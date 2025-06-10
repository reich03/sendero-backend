package com.sendero.backend.service;

import com.sendero.backend.model.Zona;
import com.sendero.backend.model.AuditLog;
import com.sendero.backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZonaVisitadaService {
    @Lazy
    private final ZonaService zonaService;
    private final AuditLogRepository auditLogRepository;

    // Modificado: Incluir los logs de auditoría junto con las zonas
    public List<ZonaVisitadaResponse> obtenerZonasVisitadas() {
        // Obtener los IDs de las zonas visitadas de los logs
        List<AuditLog> logs = auditLogRepository.findAll().stream()
                .filter(log -> log.getEntityId() != null)  // Filtrar registros con entityId nulo
                .collect(Collectors.toList());

        // Crear una lista de respuestas que incluye tanto la zona como la auditoría
        return logs.stream().map(log -> {
                    // Obtener el ID de la zona visitada
                    Long zonaId = Long.valueOf(log.getEntityId());
                    try {
                        // Intentar obtener la zona correspondiente
                        Zona zona = zonaService.obtenerZona(zonaId);
                        // Devolver una respuesta que incluye tanto la zona como los detalles de la auditoría
                        return new ZonaVisitadaResponse(zona, log);
                    } catch (RuntimeException e) {
                        // Log de la excepción si no se encuentra la zona
                        System.err.println("Error al obtener la zona con ID: " + zonaId);
                        // Devolver null o alguna otra respuesta adecuada (opcional)
                        return null;
                    }
                })
                .filter(response -> response != null) // Filtrar respuestas nulas
                .collect(Collectors.toList());
    }
}
