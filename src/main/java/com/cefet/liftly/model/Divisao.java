package com.cefet.liftly.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Divisao {
    @Id
    private String id;

    @NotBlank
    private String usuarioId;

    @NotBlank
    private String nome;

    private String descricao;
    private String dataCriacao;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "divisao_id")
    private List<ExercicioTreino> exercicios = new ArrayList<>();
}
