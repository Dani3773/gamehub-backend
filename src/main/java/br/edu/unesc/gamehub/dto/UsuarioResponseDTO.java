package br.edu.unesc.gamehub.dto;

import br.edu.unesc.gamehub.entity.Perfil;
import br.edu.unesc.gamehub.entity.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        Perfil perfil,
        Boolean ativo,
        LocalDateTime dataCadastro
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