package com.educacioninnovacion.ms_solicitudes.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "solicitudes_recuperacion")
@Data
public class SolicitudRecuperacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estudiante_id", nullable = false)
    private Long estudianteId;

    @Column(name = "asignatura_id", nullable = false)
    private Long asignaturaId;

    // NUEVO CAMPO: ID de la Actividad
    @Column(name = "actividad_id")
    private Long actividadId;

    @Column(name = "tipo_recuperacion_id", nullable = false)
    private Long tipoRecuperacionId;

    @Column(name = "estado_id", nullable = false)
    private Long estadoId;

    @Column(name = "nota_obtenida_original", nullable = false)
    private BigDecimal notaObtenidaOriginal;

    @Column(name = "nota_nueva_propuesta")
    private BigDecimal notaNuevaPropuesta;

    @Column(name = "fecha_solicitud")
    private ZonedDateTime fechaSolicitud;

    @Column(name = "fecha_evaluacion")
    private ZonedDateTime fechaEvaluacion;

    @Column(name = "comentario_docente")
    private String comentarioDocente;

    @Column(name = "actividad_recuperar")
    private String actividadRecuperar;

    @Column(name = "ruta_archivo_solicitud")
    private String rutaArchivoSolicitud;
}