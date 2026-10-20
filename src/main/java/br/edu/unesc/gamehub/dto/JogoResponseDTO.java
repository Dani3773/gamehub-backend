package br.edu.unesc.gamehub.dto;

import br.edu.unesc.gamehub.entity.Jogo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Dados de um jogo retornados pela API.")
public record JogoResponseDTO(
        @Schema(description = "Identificador do jogo", example = "1")
        Long id,

        @Schema(description = "Nome do jogo", example = "UNESC Adventure")
        String nome,

        @Schema(description = "Descrição do jogo", example = "Jogo de aventura em um universo acadêmico.")
        String descricao,

        @Schema(description = "Preço atual do jogo", example = "59.90")
        BigDecimal preco,

        @Schema(description = "Data de lançamento", example = "2026-09-22")
        LocalDate dataLancamento,

        @Schema(description = "Indica se o jogo está ativo no catálogo", example = "true")
        Boolean ativo,

        @Schema(
                description = "Categorias associadas ao jogo",
                example = "[{\"id\":1,\"nome\":\"Ação\",\"descricao\":\"Jogos de ação\"}]"
        )
        List<CategoriaResponseDTO> categorias
) {
    public static JogoResponseDTO from(Jogo jogo) {
        List<CategoriaResponseDTO> categorias = jogo.getCategorias()
                .stream()
                .map(CategoriaResponseDTO::from)
                .toList();

        return new JogoResponseDTO(
                jogo.getId(),
                jogo.getNome(),
                jogo.getDescricao(),
                jogo.getPreco(),
                jogo.getDataLancamento(),
                jogo.getAtivo(),
                categorias
        );
    }
}
