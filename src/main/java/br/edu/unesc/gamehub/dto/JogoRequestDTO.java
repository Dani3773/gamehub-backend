package br.edu.unesc.gamehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record JogoRequestDTO(

        @NotBlank(message = "O nome do jogo é obrigatório")
        @Size(max = 160, message = "O nome deve possuir no máximo 160 caracteres")
        String nome,

        @Size(max = 3000, message = "A descrição deve possuir no máximo 3000 caracteres")
        String descricao,

        @NotNull(message = "O preço do jogo é obrigatório")
        @Positive(message = "O preço do jogo deve ser maior que zero")
        BigDecimal preco,

        LocalDate dataLancamento,

        Set<Long> categoriaIds
) {
}
