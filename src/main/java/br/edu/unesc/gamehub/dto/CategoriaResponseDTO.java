package br.edu.unesc.gamehub.dto;

import br.edu.unesc.gamehub.entity.Categoria;

public record CategoriaResponseDTO(
        Long id,
        String nome,
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