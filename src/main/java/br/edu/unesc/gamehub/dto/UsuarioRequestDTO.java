package br.edu.unesc.gamehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro ou atualização de usuário.")
public record UsuarioRequestDTO(

        @Schema(description = "Nome do usuário", example = "Maria Silva")
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 2, max = 120, message = "O nome deve ter entre 2 e 120 caracteres")
        String nome,

        @Schema(description = "E-mail do usuário", example = "maria@gamehub.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail em formato inválido")
        @Size(max = 160, message = "O e-mail deve ter no máximo 160 caracteres")
        String email,

        @Schema(description = "Senha da conta; exemplo ilustrativo, não use em produção", example = "SenhaDeExemplo123")
        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
        String senha
) {
}
