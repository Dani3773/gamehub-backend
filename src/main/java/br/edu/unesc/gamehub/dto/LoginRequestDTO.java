package br.edu.unesc.gamehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais para autenticação na GameHub.")
public record LoginRequestDTO(
        @Schema(description = "E-mail cadastrado", example = "cliente@gamehub.com")
        @NotBlank String email,
        @Schema(description = "Senha da conta", example = "SenhaDeExemplo123")
        @NotBlank String senha
) {
}
