package com.ecogrid.mapper.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class Grafo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Map<Subestacao, List<LinhaDeTransmissao>> adjacencias;
}
