package com.sendero.backend.service;

import com.sendero.backend.dto.ZonaRequest;
import com.sendero.backend.model.Zona;
import com.sendero.backend.model.ZonaTipo;
import com.sendero.backend.repository.ZonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ZonaService {

    private final ZonaRepository zonaRepository;

    @Value("${app.upload.dir:uploads/zonas}")
    private String uploadDir;

    public List<String> guardarImagenes(List<MultipartFile> imagenes) {
        List<String> rutas = new ArrayList<>();

        try {
            // Crear directorio si no existe
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            for (MultipartFile imagen : imagenes) {
                if (imagen.isEmpty()) {
                    continue;
                }

                // Generar nombre único para la imagen
                String nombreArchivo = UUID.randomUUID().toString() + "_" + imagen.getOriginalFilename();
                Path rutaCompleta = dirPath.resolve(nombreArchivo);

                // Guardar la imagen
                Files.copy(imagen.getInputStream(), rutaCompleta);

                // Agregar la ruta relativa a la lista
                rutas.add("/uploads/zonas/" + nombreArchivo);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar imágenes: " + e.getMessage(), e);
        }

        return rutas;
    }

    public Zona crearZona(ZonaRequest request, String username) {
        Zona zona = new Zona();
        zona.setNombre(request.getNombre());
        zona.setDescripcion(request.getDescripcion());
        zona.setTipo(request.getTipo());
        zona.setLatitud(request.getLatitud());
        zona.setLongitud(request.getLongitud());
        zona.setCreadoPor(username);
        zona.setImagenes(request.getImagenes());

        return zonaRepository.save(zona);
    }

    public Zona actualizarZona(Long id, ZonaRequest request) {
        Zona zonaExistente = zonaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con ID: " + id));

        zonaExistente.setNombre(request.getNombre());
        zonaExistente.setDescripcion(request.getDescripcion());
        zonaExistente.setTipo(request.getTipo());
        zonaExistente.setLatitud(request.getLatitud());
        zonaExistente.setLongitud(request.getLongitud());

        // Si hay nuevas imágenes, actualizar la lista
        if (request.getImagenes() != null && !request.getImagenes().isEmpty()) {
            zonaExistente.setImagenes(request.getImagenes());
        }

        return zonaRepository.save(zonaExistente);
    }

    public List<Zona> listarZonas() {
        return zonaRepository.findAll();
    }

    public List<Zona> listarPorTipo(ZonaTipo tipo) {
        return zonaRepository.findByTipo(tipo);
    }

    public Zona obtenerZona(Long id) {
        return zonaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con ID: " + id));
    }

    public void eliminarZona(Long id) {
        zonaRepository.deleteById(id);
    }
}