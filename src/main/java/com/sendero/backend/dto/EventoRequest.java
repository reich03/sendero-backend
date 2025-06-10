package com.sendero.backend.dto;

import lombok.Data;

import java.util.Date;

@Data
public class EventoRequest {
    private String nombre;
    private String descripcion;
    private Date fechaEvento;
    private Long estacionId;
    private int usuarioId;
}
