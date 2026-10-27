package com.educacioninnovacion.ms_solicitudes.controller;

import com.educacioninnovacion.ms_solicitudes.entity.SolicitudRecuperacion;
import com.educacioninnovacion.ms_solicitudes.service.SolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*") // Permite que Angular se conecte sin problemas de CORS
public class SolicitudController {

    @Autowired
    private SolicitudService service;

    // ==========================================
    // 1. Endpoint: Crear una solicitud (Estudiante)
    // Método: POST
    // Ruta: /api/solicitudes
    // ==========================================
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> request) {
        try {
            Long estudianteId = Long.valueOf(request.get("estudianteId").toString());
            Long asignaturaId = Long.valueOf(request.get("asignaturaId").toString());
            BigDecimal nota = new BigDecimal(request.get("notaOriginal").toString());

            SolicitudRecuperacion nueva = service.crearSolicitud(estudianteId, asignaturaId, nota);
            return ResponseEntity.ok(nueva);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // 2. Endpoint: Calificar una solicitud (Docente)
    // Método: PUT
    // Ruta: /api/solicitudes/{id}/calificar
    // ==========================================
    @PutMapping("/{id}/calificar")
    public ResponseEntity<?> calificar(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            BigDecimal notaEvaluacion = new BigDecimal(request.get("notaEvaluacion").toString());
            String comentario = request.get("comentario").toString();

            SolicitudRecuperacion resultado = service.calificarSolicitud(id, notaEvaluacion, comentario);
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==========================================
    // 3. Endpoint: Listar solicitudes por estudiante
    // Método: GET
    // Ruta: /api/solicitudes/estudiante/{estudianteId}
    // ==========================================
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<SolicitudRecuperacion>> listar(@PathVariable Long estudianteId) {
        return ResponseEntity.ok(service.listarPorEstudiante(estudianteId));
    }
}