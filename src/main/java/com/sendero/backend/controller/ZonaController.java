package com.sendero.backend.controller;

import com.sendero.backend.dto.ZonaRequest;
import com.sendero.backend.model.Zona;
import com.sendero.backend.model.ZonaTipo;
import com.sendero.backend.security.JwtUtil;
import com.sendero.backend.service.ZonaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/zonas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ZonaController {

    private final ZonaService zonaService;
    private final JwtUtil jwtUtil;

   /* @PostMapping
    public ResponseEntity<?> crearZona(@RequestBody ZonaRequest request,
                                       @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);

        Zona nuevaZona = zonaService.crearZona(request, username);
        return ResponseEntity.ok(nuevaZona);
    }*/

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> crearZona(
            @RequestPart("zona") ZonaRequest request,
            @RequestPart("imagenes") List<MultipartFile> imagenes,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(token);

        // Guardar las imágenes y obtener las rutas
        List<String> rutasImagenes = zonaService.guardarImagenes(imagenes);

        // Asociar las rutas de las imágenes con la zona
        request.setImagenes(rutasImagenes);

        // Crear la zona
        Zona nuevaZona = zonaService.crearZona(request, username);
        return ResponseEntity.ok(nuevaZona);
    }


    @GetMapping
    public ResponseEntity<List<Zona>> listarZonas() {
        return ResponseEntity.ok(zonaService.listarZonas());
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Zona>> listarPorTipo(@PathVariable ZonaTipo tipo) {
        return ResponseEntity.ok(zonaService.listarPorTipo(tipo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Zona> obtenerZona(@PathVariable Long id) {
        return ResponseEntity.ok(zonaService.obtenerZona(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarZona(@PathVariable Long id) {
        zonaService.eliminarZona(id);
        return ResponseEntity.ok("Zona eliminada correctamente");
    }

    /*@PutMapping("/{id}")
    public ResponseEntity<?> actualizarZona(@PathVariable Long id, @RequestBody Zona zonaActualizada) {
        Zona zona = zonaService.actualizarZona(id, zonaActualizada);
        return ResponseEntity.ok(zona);
    }*/

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> actualizarZona(
            @PathVariable Long id,
            @RequestPart("zona") ZonaRequest request,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes) {

        // Si se proporcionan nuevas imágenes, las guardamos
        if (imagenes != null && !imagenes.isEmpty()) {
            List<String> rutasImagenes = zonaService.guardarImagenes(imagenes);
            request.setImagenes(rutasImagenes);
        }

        // Actualizar la zona
        Zona zonaActualizada = zonaService.actualizarZona(id, request);
        return ResponseEntity.ok(zonaActualizada);
    }

}
