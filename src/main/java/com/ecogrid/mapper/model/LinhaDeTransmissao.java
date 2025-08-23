package com.ecogrid.mapper.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
public class LinhaDeTransmissao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "subestacao_a_id")
    private Subestacao subestacaoA;
    @ManyToOne
    @JoinColumn(name = "subestacao_b_id")
    private Subestacao subestacaoB;
    private String idEquipamento;
    private double tensao;
    private String proprietario;
    private double CSPT_longa_sL;
    private double CSPT_curta_sl;
    private double CSPT_longa_cL;
    private double CSPT_curta_cL;
    private double resistencia;
    private double reatancia;
    private double comprimento;
}
