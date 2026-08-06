package com.cefet.liftly.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ExercicioTreino {
    @Id
    private String id;
    private String nome;
    private String grupoMuscular;
    private Integer series;
    private Integer repeticoes;
    private Double carga;
    private String observacao;
}
