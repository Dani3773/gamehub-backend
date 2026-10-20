package br.edu.unesc.gamehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token de acesso JWT emitido após autenticação.")
public record TokenResponseDTO(
        @Schema(description = "JWT para o cabeçalho Authorization", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjbGllbnRlQGdhbWVodWIuY29tIn0.assinatura")
        String token
) {
}
