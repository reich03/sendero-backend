package com.sendero.backend.service;

import com.sendero.backend.model.AuditLog;
import com.sendero.backend.model.Zona;

public class ZonaVisitadaResponse {
    private Zona zona;
    private AuditLog log;

    // Constructor
    public ZonaVisitadaResponse(Zona zona, AuditLog log) {
        this.zona = zona;
        this.log = log;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public AuditLog getLog() {
        return log;
    }

    public void setLog(AuditLog log) {
        this.log = log;
    }
}
