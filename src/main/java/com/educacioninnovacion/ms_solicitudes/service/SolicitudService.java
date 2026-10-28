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

    // 1. CREAR SOLICITUD (Ahora recibe 6 parámetros, incluyendo actividadId)
    public SolicitudRecuperacion crearSolicitud(Long estudianteId, Long asignaturaId, Long actividadId, BigDecimal notaOriginal, String actividadRecuperar, String rutaArchivoSolicitud) {
        
        if (notaOriginal.compareTo(new BigDecimal("0.01")) < 0 || 
           (notaOriginal.compareTo(new BigDecimal("6.99")) > 0 && notaOriginal.compareTo(new BigDecimal("7.01")) < 0) || 
            notaOriginal.compareTo(new BigDecimal("8.99")) > 0) {
            throw new RuntimeException("Nota no elegible para recuperación.");
        }

        Long tipoId = notaOriginal.compareTo(new BigDecimal("6.99")) <= 0 ? 1L : 2L;
        
        long solicitudesPrevias = repository.countByEstudianteIdAndAsignaturaIdAndTipoRecuperacionId(estudianteId, asignaturaId, tipoId);
        
        if (tipoId == 1L && solicitudesPrevias >= 2) {
            throw new RuntimeException("Límite excedido: Máximo 2 recuperaciones por Refuerzo Pedagógico.");
        }
        if (tipoId == 2L && solicitudesPrevias >= 1) {
            throw new RuntimeException("Límite excedido: Máximo 1 recuperación por Mejora Directa.");
        }

        SolicitudRecuperacion solicitud = new SolicitudRecuperacion();
        solicitud.setEstudianteId(estudianteId);
        solicitud.setAsignaturaId(asignaturaId);
        
        // Asignamos el nuevo campo ID de Actividad
        solicitud.setActividadId(actividadId); 
        
        solicitud.setTipoRecuperacionId(tipoId);
        solicitud.setEstadoId(1L); // PENDIENTE
        solicitud.setNotaObtenidaOriginal(notaOriginal);
        solicitud.setFechaSolicitud(ZonedDateTime.now());
        
        // Campos de texto y archivo
        solicitud.setActividadRecuperar(actividadRecuperar);
        solicitud.setRutaArchivoSolicitud(rutaArchivoSolicitud);

        return repository.save(solicitud);
    }

    // 2. CALIFICAR SOLICITUD
    public SolicitudRecuperacion calificarSolicitud(Long solicitudId, BigDecimal notaEvaluacion, String comentario) {
        SolicitudRecuperacion solicitud = repository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada."));

        if (solicitud.getEstadoId() == 4L) {
            throw new RuntimeException("Esta solicitud ya fue calificada.");
        }

        BigDecimal suma = solicitud.getNotaObtenidaOriginal().add(notaEvaluacion);
        BigDecimal promedio = suma.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);

        BigDecimal notaFinal = (solicitud.getTipoRecuperacionId() == 1L) 
            ? promedio.min(new BigDecimal("7.00")) 
            : promedio.min(new BigDecimal("9.00"));

        solicitud.setNotaNuevaPropuesta(notaFinal);
        solicitud.setEstadoId(4L); // COMPLETADA
        solicitud.setFechaEvaluacion(ZonedDateTime.now());
        solicitud.setComentarioDocente(comentario);

        return repository.save(solicitud);
    }

    // 3. LISTAR POR ESTUDIANTE
    public List<SolicitudRecuperacion> listarPorEstudiante(Long estudianteId) {
        return repository.findByEstudianteId(estudianteId);
    }

    // 4. LISTAR PENDIENTES (Para el Docente)
    public List<SolicitudRecuperacion> listarPendientes() {
        return repository.findByEstadoId(1L);
    }
}