package com.cefet.liftly.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class HistoricoExercicio {
    @Id
    private String id;
    private String exercicioId;
    private String nome;
    private Integer series;
    private Integer repeticoes;
    private Double carga;
    private String observacao;

    @ElementCollection
    private List<Boolean> seriesFeitas = new ArrayList<>();
}
