package com.sendero.backend.service;
import com.sendero.backend.model.AuditLog;
import com.sendero.backend.model.Zona;
import com.sendero.backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditLogRepository auditLogRepository;


    public void registrar(String username, String action, String entity, String entityId, String details) {
        AuditLog log = AuditLog.builder()
                .username(username)
                .action(action)
                .entity(entity)
                .entityId(entityId)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(log);
    }
    public List<AuditLog> obtenerTodos() {
        return auditLogRepository.findAll();
    }

    public void registrarZonaVisitada(String username, String ip, Long zonaId) {
        String action = "ZONA_VISITADA";
        String entity = "Zona";
        String entityId = zonaId.toString();
        String details = "Zona visitada por el usuario: " + (username != null ? username : "Invitado") + " desde IP: " + ip;

        AuditLog log = AuditLog.builder()
                .username(username != null ? username : "Invitado")
                .action(action)
                .entity(entity)
                .entityId(entityId)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(log);
    }
}
