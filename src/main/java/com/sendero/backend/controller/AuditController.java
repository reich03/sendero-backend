package com.sendero.backend.controller;

import com.sendero.backend.model.Zona;
import com.sendero.backend.security.JwtUtil;
import com.sendero.backend.service.AuditoriaService;
import com.sendero.backend.service.ZonaService;
import com.sendero.backend.service.ZonaVisitadaResponse;
import com.sendero.backend.service.ZonaVisitadaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuditController {
    private final AuditoriaService auditoriaService;
    private final JwtUtil jwtUtil;
    private final ZonaService zonaService;
    private final ZonaVisitadaService zonaVisitadaService;

    @GetMapping("/")
    public ResponseEntity<?> listarLogs() {
        return ResponseEntity.ok(auditoriaService.obtenerTodos());
    }
    @GetMapping("/zonas-visitadas")
    public ResponseEntity<List<ZonaVisitadaResponse>> listarZonasVisitadas() {
        // Obtener las zonas visitadas junto con los logs de auditoría
        List<ZonaVisitadaResponse> zonas = zonaVisitadaService.obtenerZonasVisitadas();
        return ResponseEntity.ok(zonas);
    }
    @PostMapping("/zona/visitar")
    public ResponseEntity<?> registrarVisitaZona(@RequestParam Long zonaId, @RequestParam String ip) {
        String username = "Invitado";
        System.out.println("Zona ID: " + zonaId + ", IP: " + ip);

        auditoriaService.registrarZonaVisitada(username, ip, zonaId);

        return ResponseEntity.ok("Visita registrada correctamente");
    }


}
