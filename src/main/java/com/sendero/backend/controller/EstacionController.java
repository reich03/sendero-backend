    package com.sendero.backend.controller;

    import com.sendero.backend.dto.EstudioRequest;
    import com.sendero.backend.model.Estacion;
    import com.sendero.backend.security.JwtUtil;
    import com.sendero.backend.service.EstacionService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.UUID;

    @RestController
    @RequestMapping("/api/estaciones")
    @RequiredArgsConstructor
    @CrossOrigin(origins = "*")
    public class EstacionController {

        private final EstacionService estacionService;
        private final JwtUtil jwtUtil;

        @Value("${app.upload.dir:uploads/zonas}")
        private String uploadDir;

        @PostMapping("/imagenes")
        public List<String> guardarImagenEstacion(@RequestParam("imagenes") List<MultipartFile> imagenes) {
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

        @PostMapping(consumes = "multipart/form-data")
        public ResponseEntity<Estacion> crearEstacion(@RequestPart("estacion") EstudioRequest request,
                                                      @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes,
                                                      @RequestHeader("Authorization") String authHeader) {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(token);

            List<String> rutasImagenes = (imagenes != null) ? estacionService.guardarImagenEstacion(imagenes) : new ArrayList<>();

            request.setImagenPortada(rutasImagenes.isEmpty() ? null : rutasImagenes.get(0)); // Asumimos que es la primera imagen como portada

            Estacion nuevaEstacion = estacionService.crearEstacion(request, username, rutasImagenes);
            return ResponseEntity.ok(nuevaEstacion);
        }

        @PutMapping("/{id}/zonas")
        public ResponseEntity<Estacion> asociarZonasAEstacion(@PathVariable Long id, @RequestBody List<Long> zonaIds) {
            Estacion estacionActualizada = estacionService.asociarZonasAEstacion(id, zonaIds);
            return ResponseEntity.ok(estacionActualizada);
        }

        @GetMapping
        public ResponseEntity<List<Estacion>> listarEstaciones() {
            return ResponseEntity.ok(estacionService.listarEstaciones());
        }

        @GetMapping("/{id}")
        public ResponseEntity<Estacion> obtenerEstacion(@PathVariable Long id) {
            return ResponseEntity.ok(estacionService.obtenerEstacion(id));
        }

        @PutMapping(value = "/{id}", consumes = "multipart/form-data")
        public ResponseEntity<Estacion> actualizarEstacion(@PathVariable Long id,
                                                           @RequestPart("estacion") EstudioRequest request,
                                                           @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes) {
            List<String> rutasImagenes = (imagenes != null) ? estacionService.guardarImagenEstacion(imagenes) : new ArrayList<>();

            request.setImagenPortada(rutasImagenes.isEmpty() ? null : rutasImagenes.get(0));

            Estacion estacionActualizada = estacionService.actualizarEstacion(id, request, rutasImagenes);
            return ResponseEntity.ok(estacionActualizada);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<String> eliminarEstacion(@PathVariable Long id) {
            estacionService.eliminarEstacion(id);
            return ResponseEntity.ok("Estación eliminada correctamente");
        }
    }
