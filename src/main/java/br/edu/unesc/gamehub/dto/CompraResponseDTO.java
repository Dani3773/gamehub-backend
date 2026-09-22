package br.edu.unesc.gamehub.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompraResponseDTO(
        Long id,
        Long usuarioId,
        Long jogoId,
        BigDecimal valorPago,   // RN06
        LocalDateTime dataCompra
) {}