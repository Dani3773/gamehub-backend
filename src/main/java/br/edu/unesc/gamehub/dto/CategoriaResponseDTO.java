
package br.edu.unesc.gamehub.dto;

import br.edu.unesc.gamehub.entity.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;

public record CategoriaResponseDTO(

        @Schema(example = "1")
        Long id,
        @Schema(example = "Ação")
        String nome,
        @Schema(example = "Jogos que envolvem combate, desafios e ação em tempo real")
        String descricao
) {
    public static CategoriaResponseDTO from(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome(),
                categoria.getDescricao()
        );
    }
}
