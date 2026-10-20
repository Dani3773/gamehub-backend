package br.edu.unesc.gamehub.dto;

import br.edu.unesc.gamehub.entity.Avaliacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Avaliação registrada por um usuário que comprou o jogo.")
public record AvaliacaoResponseDTO(
        @Schema(description = "Identificador da avaliação", example = "7") Long id,
        @Schema(description = "Identificador do autor", example = "42") Long usuarioId,
        @Schema(description = "Identificador do jogo", example = "12") Long jogoId,
        @Schema(description = "Nota de 1 a 5", example = "5") Integer nota,
        @Schema(description = "Comentário", example = "Ótimo jogo, recomendo!") String comentario,
        @Schema(description = "Data da avaliação", example = "2026-10-20T14:30:00") LocalDateTime dataAvaliacao
) {

    public static AvaliacaoResponseDTO from(Avaliacao avaliacao) {
        return new AvaliacaoResponseDTO(
                avaliacao.getId(),
                avaliacao.getUsuario().getId(),
                avaliacao.getJogo().getId(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getDataAvaliacao()
        );
    }
}
