package com.ecogrid.mapper.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.locationtech.jts.geom.Polygon;

import java.util.UUID;

@Entity @Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class AreaProtegida {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String nome;
    private String uf;
    private double medidaArea;
    private Polygon area;
}
