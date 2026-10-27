package com.educacioninnovacion.ms_solicitudes.repository;

import com.educacioninnovacion.ms_solicitudes.entity.SolicitudRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<SolicitudRecuperacion, Long> {
    List<SolicitudRecuperacion> findByEstudianteId(Long estudianteId);
    
    // Contar cuántas solicitudes tiene el estudiante para una materia y tipo específico
    long countByEstudianteIdAndAsignaturaIdAndTipoRecuperacionId(
        Long estudianteId, Long asignaturaId, Long tipoRecuperacionId);
}