package br.edu.unesc.gamehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequestDTO(

        @NotBlank(message = "O nome da categoria é obrigatório")
        @Size(max = 100, message = "O nome deve possuir no máximo 100 caracteres")
        String nome,

        @Size(max = 500, message = "A descrição deve possuir no máximo 500 caracteres")
        String descricao

) {}