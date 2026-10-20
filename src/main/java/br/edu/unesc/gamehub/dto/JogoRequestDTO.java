package br.edu.unesc.gamehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Schema(description = "Dados para cadastro ou atualização de um jogo.")
public record JogoRequestDTO(

        @Schema(description = "Nome do jogo", example = "UNESC Adventure")
        @NotBlank(message = "O nome do jogo é obrigatório")
        @Size(max = 160, message = "O nome deve possuir no máximo 160 caracteres")
        String nome,

        @Schema(description = "Descrição do jogo", example = "Jogo de aventura em um universo acadêmico.")
        @Size(max = 3000, message = "A descrição deve possuir no máximo 3000 caracteres")
        String descricao,

        @Schema(description = "Preço do jogo", example = "59.90")
        @NotNull(message = "O preço do jogo é obrigatório")
        @Positive(message = "O preço do jogo deve ser maior que zero")
        BigDecimal preco,

        @Schema(description = "Data de lançamento", example = "2026-09-22")
        LocalDate dataLancamento,

        @Schema(description = "Identificadores das categorias associadas ao jogo", example = "[1, 2]")
        Set<Long> categoriaIds
) {
}
