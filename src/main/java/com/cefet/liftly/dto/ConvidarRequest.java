package com.cefet.liftly.dto;

import jakarta.validation.constraints.NotBlank;

public record ConvidarRequest(
        @NotBlank String email,
        @NotBlank String deUsuarioId
) { }
