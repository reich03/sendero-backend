package com.sendero.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class EstudioRequest {
    private String nombre;
    private String descripcion;
    private String imagenPortada;
    private List<Long> zonaIds;
}
