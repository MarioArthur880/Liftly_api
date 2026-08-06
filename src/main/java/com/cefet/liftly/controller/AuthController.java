package com.cefet.liftly.controller;

import com.cefet.liftly.dto.LoginRequest;
import com.cefet.liftly.dto.AlterarSenhaRequest;
import com.cefet.liftly.model.Usuario;
import com.cefet.liftly.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody LoginRequest request) {
        return usuarioRepository.findByEmailAndSenhaAndAtivoTrue(request.email(), request.senha())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Usuario> cadastrar(@Valid @RequestBody Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.status(409).build();
        }
        usuario.setId(UUID.randomUUID().toString().substring(0, 8));
        usuario.setAtivo(true);
        return ResponseEntity.status(201).body(usuarioRepository.save(usuario));
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable String id, @Valid @RequestBody Usuario atualizado) {
        return usuarioRepository.findById(id).map(usuario -> {
            atualizado.setId(id);
            return ResponseEntity.ok(usuarioRepository.save(atualizado));
        }).orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/usuarios/{id}/senha")
    public ResponseEntity<Void> alterarSenha(
            @PathVariable String id,
            @Valid @RequestBody AlterarSenhaRequest request) {
        return usuarioRepository.findById(id).map(usuario -> {
            if (!usuario.getSenha().equals(request.senhaAtual())) {
                return ResponseEntity.status(401).<Void>build();
            }
            usuario.setSenha(request.novaSenha());
            usuarioRepository.save(usuario);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> desativar(@PathVariable String id) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setAtivo(false);
            usuarioRepository.save(usuario);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
