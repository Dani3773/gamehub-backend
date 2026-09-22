package br.edu.unesc.gamehub.dto;

import jakarta.validation.constraints.NotNull;

public record CompraRequestDTO(
        @NotNull(message = "usuarioId é obrigatório") Long usuarioId,
        @NotNull(message = "jogoId é obrigatório") Long jogoId
) {}