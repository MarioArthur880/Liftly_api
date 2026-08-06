package com.cefet.liftly.controller;

import com.cefet.liftly.dto.ConviteResponse;
import com.cefet.liftly.repository.ConviteRepository;
import com.cefet.liftly.repository.GrupoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/convites")
@RequiredArgsConstructor
public class ConviteController {
    private final ConviteRepository conviteRepository;
    private final GrupoRepository grupoRepository;

    @GetMapping
    public List<ConviteResponse> listarPendentes(@RequestParam String usuarioId) {
        return conviteRepository.findByParaUsuarioIdAndStatusOrderByDataEnvioDesc(usuarioId, "PENDENTE")
                .stream()
                .map(c -> new ConviteResponse(c.getId(), c.getGrupoId(), c.getGrupoNome(), c.getDeUsuarioNome(), c.getDataEnvio()))
                .toList();
    }

    @PutMapping("/{id}/aceitar")
    public ResponseEntity<Void> aceitar(@PathVariable String id) {
        return conviteRepository.findById(id).map(convite -> {
            grupoRepository.findById(convite.getGrupoId()).ifPresent(grupo -> {
                if (!grupo.getMembrosIds().contains(convite.getParaUsuarioId())) {
                    grupo.getMembrosIds().add(convite.getParaUsuarioId());
                    grupoRepository.save(grupo);
                }
            });
            convite.setStatus("ACEITO");
            conviteRepository.save(convite);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/recusar")
    public ResponseEntity<Void> recusar(@PathVariable String id) {
        return conviteRepository.findById(id).map(convite -> {
            convite.setStatus("RECUSADO");
            conviteRepository.save(convite);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
