package com.envyguard.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a computer in Sala 4.
 */
@Entity
@Table(name = "sala_4")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Computadora en Sala 4")
public class Sala4 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la computadora", example = "1")
    private Long id;

    @Column(name = "nombre_pc", nullable = false, length = 50)
    @Schema(description = "Nombre del PC", example = "PC 1")
    private String nombrePc;

    @Column(name = "ip", length = 50)
    @Schema(description = "Dirección IP del PC", example = "10.0.120.2")
    private String ip;

    @Column(name = "mac", length = 50)
    @Schema(description = "Dirección MAC del PC", example = "08:bf:b8:03:13:0f")
    private String mac;
}
