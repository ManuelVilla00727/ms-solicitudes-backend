package com.educacioninnovacion.ms_solicitudes.service;

import com.educacioninnovacion.ms_solicitudes.entity.SolicitudRecuperacion;
import com.educacioninnovacion.ms_solicitudes.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class SolicitudService {

    @Autowired
    private SolicitudRepository repository;

    // 1. CREAR SOLICITUD (Con Validación de Límites)
    public SolicitudRecuperacion crearSolicitud(Long estudianteId, Long asignaturaId, BigDecimal notaOriginal) {
        
        // Validar rango de nota
        if (notaOriginal.compareTo(new BigDecimal("0.01")) < 0 || 
           (notaOriginal.compareTo(new BigDecimal("6.99")) > 0 && notaOriginal.compareTo(new BigDecimal("7.01")) < 0) || 
            notaOriginal.compareTo(new BigDecimal("8.99")) > 0) {
            throw new RuntimeException("Nota no elegible para recuperación (Acuerdo 00082-A).");
        }

        // Determinar tipo (1 = Refuerzo, 2 = Mejora)
        Long tipoId = notaOriginal.compareTo(new BigDecimal("6.99")) <= 0 ? 1L : 2L;
        
        // VALIDACIÓN DE LÍMITES (Artículos 12 y 13)
        long solicitudesPrevias = repository.countByEstudianteIdAndAsignaturaIdAndTipoRecuperacionId(estudianteId, asignaturaId, tipoId);
        
        if (tipoId == 1L && solicitudesPrevias >= 2) {
            throw new RuntimeException("Límite excedido: Máximo 2 recuperaciones por Refuerzo Pedagógico para esta asignatura.");
        }
        if (tipoId == 2L && solicitudesPrevias >= 1) {
            throw new RuntimeException("Límite excedido: Máximo 1 recuperación por Mejora Directa para esta asignatura.");
        }

        SolicitudRecuperacion solicitud = new SolicitudRecuperacion();
        solicitud.setEstudianteId(estudianteId);
        solicitud.setAsignaturaId(asignaturaId);
        solicitud.setTipoRecuperacionId(tipoId);
        solicitud.setEstadoId(1L); // PENDIENTE
        solicitud.setNotaObtenidaOriginal(notaOriginal);
        solicitud.setFechaSolicitud(ZonedDateTime.now());

        return repository.save(solicitud);
    }

    // 2. CALIFICAR SOLICITUD (Flujo del Docente)
    public SolicitudRecuperacion calificarSolicitud(Long solicitudId, BigDecimal notaEvaluacion, String comentario) {
        SolicitudRecuperacion solicitud = repository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada."));

        if (solicitud.getEstadoId() == 4L) {
            throw new RuntimeException("Esta solicitud ya fue calificada y completada.");
        }

        // Calcular promedio: (Nota Original + Nota Evaluación) / 2
        BigDecimal suma = solicitud.getNotaObtenidaOriginal().add(notaEvaluacion);
        BigDecimal promedio = suma.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);

        // Aplicar el tope del Acuerdo Ministerial
        BigDecimal notaFinal;
        if (solicitud.getTipoRecuperacionId() == 1L) {
            // Refuerzo: tope 7.00
            notaFinal = promedio.min(new BigDecimal("7.00"));
        } else {
            // Mejora Directa: tope 9.00
            notaFinal = promedio.min(new BigDecimal("9.00"));
        }

        solicitud.setNotaNuevaPropuesta(notaFinal);
        solicitud.setEstadoId(4L); // COMPLETADA
        solicitud.setFechaEvaluacion(ZonedDateTime.now());
        solicitud.setComentarioDocente(comentario);

        return repository.save(solicitud);
    }

    public List<SolicitudRecuperacion> listarPorEstudiante(Long estudianteId) {
        return repository.findByEstudianteId(estudianteId);
    }
}