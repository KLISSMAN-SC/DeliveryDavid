package com.proyecto.dto;

import jakarta.validation.constraints.*;

public record PerfilRequest(
    @NotBlank @Size(max = 45) String nombres,
    @NotBlank @Size(max = 45) String apellidos,
    @NotBlank @Pattern(regexp = "[0-9]{9,15}") String telefono,
    String password,
    String passwordActual) {}
