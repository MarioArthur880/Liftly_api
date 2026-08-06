package com.cefet.liftly.controller;

import com.cefet.liftly.dto.ConvidarRequest;
import com.cefet.liftly.dto.GrupoDetalheResponse;
import com.cefet.liftly.dto.GrupoResponse;
import com.cefet.liftly.dto.MembroRankingResponse;
import com.cefet.liftly.model.Convite;
import com.cefet.liftly.model.Grupo;
import com.cefet.liftly.model.Historico;
import com.cefet.liftly.model.Usuario;
import com.cefet.liftly.repository.ConviteRepository;
import com.cefet.liftly.repository.GrupoRepository;
import com.cefet.liftly.repository.HistoricoRepository;
import com.cefet.liftly.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grupos")
@RequiredArgsConstructor
public class GrupoController {
    private final GrupoRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConviteRepository conviteRepository;
    private final HistoricoRepository historicoRepository;

    @GetMapping
    public List<GrupoResponse> listarPorUsuario(@RequestParam String usuarioId) {
        return grupoRepository.findByMembro(usuarioId).stream()
                .map(this::paraResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoDetalheResponse> buscarDetalhe(@PathVariable String id) {
        return grupoRepository.findById(id)
                .map(grupo -> ResponseEntity.ok(montarDetalhe(grupo)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<GrupoResponse> criar(@Valid @RequestBody Grupo grupo) {
        grupo.setId(UUID.randomUUID().toString().substring(0, 8));
        grupo.setCodigoConvite(UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        grupo.setDataCriacao(Instant.now().toString());
        grupo.setMembrosIds(new ArrayList<>(List.of(grupo.getCriadorId())));
        return ResponseEntity.status(201).body(paraResponse(grupoRepository.save(grupo)));
    }

    @PostMapping("/entrar")
    public ResponseEntity<GrupoResponse> entrarPorCodigo(@RequestParam String codigo, @RequestParam String usuarioId) {
        return grupoRepository.findByCodigoConvite(codigo.toUpperCase())
                .map(grupo -> {
                    if (!grupo.getMembrosIds().contains(usuarioId)) {
                        grupo.getMembrosIds().add(usuarioId);
                        grupoRepository.save(grupo);
                    }
                    return ResponseEntity.ok(paraResponse(grupo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/convidar")
    public ResponseEntity<Void> convidar(@PathVariable String id, @Valid @RequestBody ConvidarRequest request) {
        Optional<Grupo> grupoOpt = grupoRepository.findById(id);
        Optional<Usuario> convidadoOpt = usuarioRepository.findByEmail(request.email());
        Optional<Usuario> convidanteOpt = usuarioRepository.findById(request.deUsuarioId());

        if (grupoOpt.isEmpty() || convidadoOpt.isEmpty() || convidanteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Grupo grupo = grupoOpt.get();
        Usuario convidado = convidadoOpt.get();

        if (grupo.getMembrosIds().contains(convidado.getId())) {
            return ResponseEntity.status(409).build();
        }

        Convite convite = new Convite();
        convite.setId(UUID.randomUUID().toString().substring(0, 8));
        convite.setGrupoId(grupo.getId());
        convite.setGrupoNome(grupo.getNome());
        convite.setDeUsuarioId(request.deUsuarioId());
        convite.setDeUsuarioNome(convidanteOpt.get().getNome());
        convite.setParaUsuarioId(convidado.getId());
        convite.setStatus("PENDENTE");
        convite.setDataEnvio(Instant.now().toString());
        conviteRepository.save(convite);

        return ResponseEntity.status(201).build();
    }

    private GrupoResponse paraResponse(Grupo grupo) {
        return new GrupoResponse(grupo.getId(), grupo.getNome(), grupo.getCodigoConvite(),
                grupo.getCriadorId(), grupo.getMembrosIds().size());
    }

    private GrupoDetalheResponse montarDetalhe(Grupo grupo) {
        LocalDate hoje = LocalDate.now(ZoneOffset.UTC);
        LocalDate inicioSemana = hoje.minusDays(6);

        List<MembroRankingResponse> ranking = grupo.getMembrosIds().stream()
                .map(usuarioId -> {
                    String nome = usuarioRepository.findById(usuarioId)
                            .map(Usuario::getNome)
                            .orElse("Usuário");

                    List<Historico> historicos = historicoRepository.findByUsuarioIdOrderByDataInicioDesc(usuarioId);

                    Set<LocalDate> diasTreinados = historicos.stream()
                            .map(h -> converterData(h.getDataInicio()))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

                    int treinosNaSemana = (int) diasTreinados.stream()
                            .filter(d -> !d.isBefore(inicioSemana) && !d.isAfter(hoje))
                            .count();

                    int streak = calcularStreak(diasTreinados, hoje);

                    return new MembroRankingResponse(usuarioId, nome, streak, treinosNaSemana);
                })
                .sorted(Comparator.comparingInt(MembroRankingResponse::streakAtual).reversed())
                .toList();

        return new GrupoDetalheResponse(grupo.getId(), grupo.getNome(), grupo.getCodigoConvite(), ranking);
    }

    private LocalDate converterData(String data) {
        if (data == null || data.isBlank()) return null;
        try {
            return Instant.parse(data).atZone(ZoneOffset.UTC).toLocalDate();
        } catch (Exception e) {
            try {
                return LocalDate.parse(data.substring(0, 10));
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private int calcularStreak(Set<LocalDate> dias, LocalDate hoje) {
        int streak = 0;
        LocalDate atual = dias.contains(hoje) ? hoje : hoje.minusDays(1);
        while (dias.contains(atual)) {
            streak++;
            atual = atual.minusDays(1);
        }
        return streak;
    }
}
