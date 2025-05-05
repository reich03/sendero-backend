package com.sendero.backend.dto;
import com.sendero.backend.model.ZonaTipo;
import lombok.Data;

import java.util.List;

@Data
public class ZonaRequest {
    private String nombre;
    private ZonaTipo tipo;
    private String descripcion;
    private double latitud;
    private double longitud;
    private List<String> imagenes;
}
