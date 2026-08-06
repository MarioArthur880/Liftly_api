package com.cefet.liftly.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Historico {
    @Id
    private String id;

    @NotBlank
    private String usuarioId;
    private String divisaoId;
    private String divisaoNome;
    private String dataInicio;
    private String dataFim;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "historico_id")
    private List<HistoricoExercicio> exercicios = new ArrayList<>();
}
