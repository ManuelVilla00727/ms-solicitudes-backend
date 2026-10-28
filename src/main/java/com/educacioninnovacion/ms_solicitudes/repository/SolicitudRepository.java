package com.educacioninnovacion.ms_solicitudes.repository;

import com.educacioninnovacion.ms_solicitudes.entity.SolicitudRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<SolicitudRecuperacion, Long> {
    
    // 1. Buscar las solicitudes de un estudiante específico
    List<SolicitudRecuperacion> findByEstudianteId(Long estudianteId);
    
    // 2. Contar cuántas solicitudes tiene el estudiante (para validar límites)
    long countByEstudianteIdAndAsignaturaIdAndTipoRecuperacionId(
        Long estudianteId, Long asignaturaId, Long tipoRecuperacionId);
        
    // 3. NUEVO: Buscar todas las solicitudes por su estado (para el docente)
    List<SolicitudRecuperacion> findByEstadoId(Long estadoId);
}