package com.sendero.backend.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="zonas")
public class Zona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private ZonaTipo tipo;

    @Lob
    private String descripcion;

    private double latitud;
    private double longitud;

    @ElementCollection
    private List<String> imagenes;

    private String creadoPor;
    private LocalDateTime fechaCreacion;

    @ElementCollection
    private List<String> comentarios;
}
