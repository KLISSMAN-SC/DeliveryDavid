package com.proyecto.dto;

import jakarta.validation.constraints.*;

public record RegistroRequest(
    @NotBlank @Size(max = 45) String nombres,
    @NotBlank @Size(max = 45) String apellidos,
    @NotBlank @Pattern(regexp = "[0-9]{9,15}") String telefono,
    @NotBlank @Email @Size(max = 45) String correoElectronico,
    @NotBlank String password) {}
