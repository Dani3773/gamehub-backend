package br.edu.unesc.gamehub.dto;

import br.edu.unesc.gamehub.entity.Avaliacao;

import java.time.LocalDateTime;

public record AvaliacaoResponseDTO(
        Long id,
        Long usuarioId,
        Long jogoId,
        Integer nota,
        String comentario,
        LocalDateTime dataAvaliacao
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