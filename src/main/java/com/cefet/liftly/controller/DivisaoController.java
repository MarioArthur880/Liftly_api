package com.cefet.liftly.controller;

import com.cefet.liftly.model.Divisao;
import com.cefet.liftly.model.ExercicioTreino;
import com.cefet.liftly.repository.DivisaoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/divisoes")
@RequiredArgsConstructor
public class DivisaoController {
    private final DivisaoRepository divisaoRepository;

    @GetMapping
    public List<Divisao> listarPorUsuario(@RequestParam String usuarioId) {
        return divisaoRepository.findByUsuarioId(usuarioId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Divisao> buscarPorId(@PathVariable String id) {
        return divisaoRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Divisao> criar(@Valid @RequestBody Divisao divisao) {
        divisao.setId(UUID.randomUUID().toString().substring(0, 8));
        divisao.setDataCriacao(Instant.now().toString());
        prepararIdsExercicios(divisao);
        return ResponseEntity.status(201).body(divisaoRepository.save(divisao));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Divisao> atualizar(@PathVariable String id, @Valid @RequestBody Divisao divisao) {
        if (!divisaoRepository.existsById(id)) return ResponseEntity.notFound().build();
        divisao.setId(id);
        prepararIdsExercicios(divisao);
        return ResponseEntity.ok(divisaoRepository.save(divisao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        if (!divisaoRepository.existsById(id)) return ResponseEntity.notFound().build();
        divisaoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void prepararIdsExercicios(Divisao divisao) {
        if (divisao.getExercicios() == null) return;
        for (ExercicioTreino e : divisao.getExercicios()) {
            if (e.getId() == null || e.getId().isBlank()) {
                e.setId(UUID.randomUUID().toString().substring(0, 8));
            }
        }
    }
}
