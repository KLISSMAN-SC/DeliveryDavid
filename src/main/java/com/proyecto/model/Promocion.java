package com.proyecto.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name="PROMOCION")
@Data
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPromocion;

    private String titulo;
    private String descripcion;
    private String imagenBanner;

    // Cambiamos a LocalDate si solo usaremos la fecha, o mantenemos LocalDateTime
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    
    private LocalTime horaInicio;
    private LocalTime horaFin;
    
    private String diasActivos; // Formato: "1,3,5"
    private Boolean mostrarEnIndex;
    private Boolean estado;

    @ManyToOne
    @JoinColumn(name = "idNegocio")
    private Negocio negocio;

    // ==========================================
    // LÓGICA DE NEGOCIO: ¿Está activa AHORA MISMO?
    // ==========================================
    public boolean isActivaHoy() {
        if (this.estado != null && !this.estado) return false; // Inactiva manualmente

        LocalDateTime ahora = LocalDateTime.now();
        LocalDate hoy = ahora.toLocalDate();
        LocalTime horaActual = ahora.toLocalTime();

        // 1. Rango de Fechas
        if (this.fechaInicio != null && hoy.isBefore(this.fechaInicio.toLocalDate())) return false;
        if (this.fechaFin != null && hoy.isAfter(this.fechaFin.toLocalDate())) return false;

        // 2. Días de la semana (1=Lunes, 7=Domingo)
        if (this.diasActivos != null && !this.diasActivos.isEmpty()) {
            String diaActual = String.valueOf(hoy.getDayOfWeek().getValue());
            if (!this.diasActivos.contains(diaActual)) return false;
        }

        // 3. Rango de Horas
        if (this.horaInicio != null && horaActual.isBefore(this.horaInicio)) return false;
        if (this.horaFin != null && horaActual.isAfter(this.horaFin)) return false;

        return true;
    }
}