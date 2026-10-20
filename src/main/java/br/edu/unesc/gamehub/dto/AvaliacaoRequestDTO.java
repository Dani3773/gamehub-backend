package br.edu.unesc.gamehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados de uma avaliação. O autor é identificado pelo token de acesso.")
public record AvaliacaoRequestDTO(
        @Schema(description = "Identificador do jogo avaliado", example = "12")
        @NotNull(message = "O jogo é obrigatório")
        Long jogoId,

        @Schema(description = "Nota de 1 a 5", example = "5")
        @NotNull(message = "A nota é obrigatória")
        @Min(value = 1, message = "A nota mínima é 1")
        @Max(value = 5, message = "A nota máxima é 5")
        Integer nota,

        @Schema(description = "Comentário opcional", example = "Ótimo jogo, recomendo!")
        @Size(
                max = 1000,
                message = "O comentário deve possuir no máximo 1000 caracteres"
        )
        String comentario
) {
}
