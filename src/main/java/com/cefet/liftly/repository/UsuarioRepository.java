package com.cefet.liftly.repository;

import com.cefet.liftly.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailAndSenhaAndAtivoTrue(String email, String senha);
    boolean existsByEmail(String email);
}
