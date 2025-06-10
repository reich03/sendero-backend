package com.sendero.backend.service;

import com.sendero.backend.dto.EstudioRequest;
import com.sendero.backend.model.Estacion;
import com.sendero.backend.model.Zona;
import com.sendero.backend.model.User;
import com.sendero.backend.repository.EstacionRepository;
import com.sendero.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EstacionService {

    private final EstacionRepository estacionRepository;
    private final ZonaService zonaService;
    private final UserRepository userRepository;
    private final AuditoriaService auditoriaService;

    @Value("${app.upload.dir:uploads/zonas}")
    private String uploadDir;

    public List<String> guardarImagenEstacion(List<MultipartFile> imagenes) {
        List<String> rutas = new ArrayList<>();

        try {
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            for (MultipartFile imagen : imagenes) {
                if (imagen.isEmpty()) {
                    continue;
                }

                String nombreArchivo = UUID.randomUUID().toString() + "_" + imagen.getOriginalFilename();
                Path rutaCompleta = dirPath.resolve(nombreArchivo);

                Files.copy(imagen.getInputStream(), rutaCompleta);

                rutas.add("/uploads/zonas/" + nombreArchivo);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar imágenes: " + e.getMessage(), e);
        }

        return rutas;
    }

    public Estacion crearEstacion(EstudioRequest request, String username, List<String> rutasImagenes) {
        Estacion estacion = new Estacion();
        estacion.setNombre(request.getNombre());
        estacion.setDescripcion(request.getDescripcion());
        estacion.setImagenPortada(rutasImagenes.isEmpty() ? null : rutasImagenes.get(0)); // Asumimos que la primera imagen es la portada

        User usuarioCreador = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        estacion.setUsuarioCreador(usuarioCreador);

        Estacion guardada = estacionRepository.save(estacion);

        auditoriaService.registrar(username, "CREAR", "Estacion", guardada.getId().toString(), "Estación creada: " + guardada.getNombre());

        return guardada;
    }

    public Estacion asociarZonasAEstacion(Long estacionId, List<Long> zonaIds) {
        Estacion estacion = estacionRepository.findById(estacionId)
                .orElseThrow(() -> new RuntimeException("Estación no encontrada"));

        List<Zona> zonas = zonaService.obtenerZonasPorIds(zonaIds);

        estacion.setZonas(zonas);
        Estacion estacionActualizada = estacionRepository.save(estacion);

        auditoriaService.registrar(estacion.getUsuarioCreador().getUsername(), "ACTUALIZAR", "Estacion", estacion.getId().toString(), "Zonas asociadas a estación");

        return estacionActualizada;
    }

    public List<Estacion> listarEstaciones() {
        return estacionRepository.findAll();
    }

    public Estacion obtenerEstacion(Long id) {
        return estacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estación no encontrada con ID: " + id));
    }

    public Estacion actualizarEstacion(Long id, EstudioRequest request, List<String> rutasImagenes) {
        Estacion estacionExistente = estacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estación no encontrada con ID: " + id));

        estacionExistente.setNombre(request.getNombre());
        estacionExistente.setDescripcion(request.getDescripcion());
        estacionExistente.setImagenPortada(rutasImagenes.isEmpty() ? null : rutasImagenes.get(0));

        Estacion estacionActualizada = estacionRepository.save(estacionExistente);

        auditoriaService.registrar(estacionExistente.getUsuarioCreador().getUsername(), "ACTUALIZAR", "Estacion", estacionActualizada.getId().toString(), "Estación actualizada: " + estacionActualizada.getNombre());

        return estacionActualizada;
    }

    public void eliminarEstacion(Long id) {
        Estacion estacion = estacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estación no encontrada con ID: " + id));

        estacionRepository.deleteById(id);

        auditoriaService.registrar(estacion.getUsuarioCreador().getUsername(), "ELIMINAR", "Estacion", id.toString(), "Estación eliminada: " + estacion.getNombre());
    }
}

