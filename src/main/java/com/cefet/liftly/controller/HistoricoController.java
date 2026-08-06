package com.cefet.liftly.controller;

import com.cefet.liftly.model.Historico;
import com.cefet.liftly.model.HistoricoExercicio;
import com.cefet.liftly.repository.HistoricoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/historicos")
@RequiredArgsConstructor
public class HistoricoController {
    private final HistoricoRepository historicoRepository;

    @GetMapping
    public List<Historico> listarPorUsuario(@RequestParam String usuarioId) {
        return historicoRepository.findByUsuarioIdOrderByDataInicioDesc(usuarioId);
    }

    @PostMapping
    public ResponseEntity<Historico> salvar(@Valid @RequestBody Historico historico) {
        historico.setId(UUID.randomUUID().toString().substring(0, 8));
        if (historico.getExercicios() != null) {
            for (HistoricoExercicio e : historico.getExercicios()) {
                if (e.getId() == null || e.getId().isBlank()) {
                    e.setId(UUID.randomUUID().toString().substring(0, 8));
                }
            }
        }
        return ResponseEntity.status(201).body(historicoRepository.save(historico));
    }
}
