package br.edu.unesc.gamehub.dto;

import br.edu.unesc.gamehub.entity.Perfil;
import br.edu.unesc.gamehub.entity.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Dados públicos de um usuário, sem a senha.")
public record UsuarioResponseDTO(
        @Schema(description = "Identificador", example = "42") Long id,
        @Schema(description = "Nome", example = "Maria Silva") String nome,
        @Schema(description = "E-mail", example = "maria@gamehub.com") String email,
        @Schema(description = "Perfil de acesso", example = "CLIENTE") Perfil perfil,
        @Schema(description = "Indica se a conta está ativa", example = "true") Boolean ativo,
        @Schema(description = "Data de cadastro", example = "2026-10-20T14:30:00") LocalDateTime dataCadastro
) {
    public static UsuarioResponseDTO from(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.getAtivo(),
                usuario.getDataCadastro()
        );
    }
}
