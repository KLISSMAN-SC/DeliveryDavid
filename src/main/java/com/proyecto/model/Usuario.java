package com.proyecto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "USUARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer idUsuario;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "idRol", nullable = false)
  @ToString.Exclude
  private Rol rol;

  @Column(length = 45)
  private String nombres;

  @Column(length = 45)
  private String apellidos;

  @Column(length = 8, unique = true)
  private String dni;

  @Column(length = 15, unique = true)
  private String telefono;

  @Column(length = 45, name = "correoElectronico", unique = true)
  private String correoElectronico;

  @Column(length = 200)
  @JsonIgnore
  @ToString.Exclude
  private String password;

  @Column(name = "fechaCreacion", insertable = false, updatable = false)
  private LocalDateTime fechaCreacion;

  @Column(name = "intentosFallidos")
  private Integer intentosFallidos;

  @Column(name = "fechaBloqueo")
  private LocalDateTime fechaBloqueo;

  @Column(name = "tokenPassword")
  @JsonIgnore
  @ToString.Exclude
  private String tokenPassword;

  @JsonIgnore
  @Column(name = "tokenPasswordExpira")
  private Instant tokenPasswordExpira;

  @JsonIgnore
  @Column(name = "securityVersion", nullable = false)
  private Long securityVersion = 0L;
}
