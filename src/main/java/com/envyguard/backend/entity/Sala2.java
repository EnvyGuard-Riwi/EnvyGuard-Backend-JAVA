package com.envyguard.backend.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a computer in Sala 2.
 */
@Entity
@Table(name = "sala_2")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Computadora en Sala 2")
public class Sala2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la computadora", example = "1")
    private Long id;

    @Column(name = "nombre_pc", nullable = false, length = 50)
    @Schema(description = "Nombre del PC", example = "PC 1")
    private String nombrePc;

    @Column(name = "ip", length = 50)
    @Schema(description = "Dirección IP del PC", example = "192.168.1.10")
    private String ip;

    @Column(name = "mac", length = 50)
    @Schema(description = "Dirección MAC del PC", example = "00:11:22:33:44:55")
    private String mac;
}
