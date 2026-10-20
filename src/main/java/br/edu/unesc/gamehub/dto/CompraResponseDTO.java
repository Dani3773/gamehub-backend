package br.edu.unesc.gamehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Compra de um jogo realizada por um usuário.")
public record CompraResponseDTO(
        @Schema(description = "Identificador da compra", example = "3") Long id,
        @Schema(description = "Identificador do comprador", example = "42") Long usuarioId,
        @Schema(description = "Identificador do jogo", example = "12") Long jogoId,
        @Schema(description = "Valor pago (preço do jogo no momento da compra)", example = "199.90") BigDecimal valorPago,   // RN06
        @Schema(description = "Data da compra", example = "2026-10-20T14:30:00") LocalDateTime dataCompra
) {}
