package com.ecogrid.mapper.model;

import jakarta.persistence.*;
import lombok.*;
import java.awt.*;
import java.util.UUID;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class Subestacao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String idInstalacao;
    private String nome;
    private String idAgentePrincipal;
    private String agentePrincipal;
    @Column(columnDefinition = "geometry(Point, 4326)") // EPSG:4326 para longitude/latitude
    private Point coordenadas;
}
