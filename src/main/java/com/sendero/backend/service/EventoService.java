package com.sendero.backend.service;

import com.sendero.backend.dto.EventoRequest;
import com.sendero.backend.model.Evento;
import com.sendero.backend.model.Estacion;
import com.sendero.backend.model.User;
import com.sendero.backend.repository.EventoRepository;
import com.sendero.backend.repository.EstacionRepository;
import com.sendero.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EstacionRepository estacionRepository;
    private final AuditoriaService auditoriaService;
    private final UserRepository userRepository;
    public Evento crearEvento(EventoRequest request) {
        Evento evento = new Evento();
        evento.setNombre(request.getNombre());
        evento.setDescripcion(request.getDescripcion());
        evento.setFechaEvento(request.getFechaEvento());

        Estacion estacion = estacionRepository.findById(request.getEstacionId())
                .orElseThrow(() -> new RuntimeException("Estación no encontrada con ID: " + request.getEstacionId()));

        evento.setEstacion(estacion);
        User usuario = userRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getUsuarioId()));
        evento.setUsuarioCreador(usuario);
        Evento nuevoEvento = eventoRepository.save(evento);

        auditoriaService.registrar("Sistema", "CREAR", "Evento", nuevoEvento.getId().toString(), "Evento creado: " + nuevoEvento.getNombre());

        return nuevoEvento;
    }

    public List<Evento> listarEventos() {
        return eventoRepository.findAll();
    }

    public Evento obtenerEvento(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));
    }

    public Evento actualizarEvento(Long id, EventoRequest request) {
        Evento eventoExistente = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));

        eventoExistente.setNombre(request.getNombre());
        eventoExistente.setDescripcion(request.getDescripcion());
        eventoExistente.setFechaEvento(request.getFechaEvento());

        Estacion estacion = estacionRepository.findById(request.getEstacionId())
                .orElseThrow(() -> new RuntimeException("Estación no encontrada con ID: " + request.getEstacionId()));

        eventoExistente.setEstacion(estacion);

        return eventoRepository.save(eventoExistente);
    }

    public void eliminarEvento(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));
        eventoRepository.deleteById(id);

        auditoriaService.registrar("Sistema", "ELIMINAR", "Evento", id.toString(), "Evento eliminado: " + evento.getNombre());
    }
}
