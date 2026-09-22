package br.edu.unesc.gamehub.dto;

import br.edu.unesc.gamehub.entity.Jogo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record JogoResponseDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        LocalDate dataLancamento,
        Boolean ativo,
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
