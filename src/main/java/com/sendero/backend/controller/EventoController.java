package com.sendero.backend.controller;

import com.sendero.backend.dto.EventoRequest;
import com.sendero.backend.model.Evento;
import com.sendero.backend.service.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EventoController {

    private final EventoService eventoService;

    @PostMapping
    public ResponseEntity<?> crearEvento(@RequestBody EventoRequest request) {
        Evento nuevoEvento = eventoService.crearEvento(request);
        return ResponseEntity.ok(nuevoEvento);
    }

    @GetMapping
    public ResponseEntity<List<Evento>> listarEventos() {
        return ResponseEntity.ok(eventoService.listarEventos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> obtenerEvento(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.obtenerEvento(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEvento(@PathVariable Long id, @RequestBody EventoRequest request) {
        Evento eventoActualizado = eventoService.actualizarEvento(id, request);
        return ResponseEntity.ok(eventoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEvento(@PathVariable Long id) {
        eventoService.eliminarEvento(id);
        return ResponseEntity.ok("Evento eliminado correctamente");
    }
}
