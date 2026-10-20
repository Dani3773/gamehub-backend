package br.edu.unesc.gamehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

// RN08: o comprador é sempre o usuário do token, nunca um id vindo no corpo
@Schema(description = "Dados de uma compra. O comprador é identificado pelo token de acesso.")
public record CompraRequestDTO(
        @Schema(description = "Identificador do jogo comprado", example = "12")
        @NotNull(message = "jogoId é obrigatório") Long jogoId
) {}
