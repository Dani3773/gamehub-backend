package br.edu.unesc.gamehub.service;

import br.edu.unesc.gamehub.dto.AvaliacaoRequestDTO;
import br.edu.unesc.gamehub.dto.AvaliacaoResponseDTO;
import br.edu.unesc.gamehub.entity.Avaliacao;
import br.edu.unesc.gamehub.entity.Jogo;
import br.edu.unesc.gamehub.entity.Usuario;
import br.edu.unesc.gamehub.exception.AvaliacaoDuplicadaException;
import br.edu.unesc.gamehub.exception.AvaliacaoNaoPermitidaException;
import br.edu.unesc.gamehub.repository.AvaliacaoRepository;
import br.edu.unesc.gamehub.repository.CompraRepository;
import br.edu.unesc.gamehub.repository.JogoRepository;
import br.edu.unesc.gamehub.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final JogoRepository jogoRepository;
    private final CompraRepository compraRepository;

    @Transactional
    public AvaliacaoResponseDTO cadastrar(
            AvaliacaoRequestDTO dto
    ) {
        Usuario usuario = buscarUsuario(dto.usuarioId());
        Jogo jogo = buscarJogo(dto.jogoId());

        // RN09: somente quem comprou o jogo pode avaliá-lo
        if (!compraRepository.existsByUsuarioIdAndJogoId(
                usuario.getId(),
                jogo.getId()
        )) {
            throw new AvaliacaoNaoPermitidaException(
                    usuario.getId(),
                    jogo.getId()
            );
        }

        // RN11: somente uma avaliação por usuário/jogo
        if (avaliacaoRepository.existsByUsuarioIdAndJogoId(
                usuario.getId(),
                jogo.getId()
        )) {
            throw new AvaliacaoDuplicadaException(
                    usuario.getId(),
                    jogo.getId()
            );
        }

        Avaliacao avaliacao = Avaliacao.builder()
                .usuario(usuario)
                .jogo(jogo)
                .nota(dto.nota())
                .comentario(dto.comentario())
                .build();

        return AvaliacaoResponseDTO.from(
                avaliacaoRepository.save(avaliacao)
        );
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponseDTO> listar() {
        return avaliacaoRepository.findAll()
                .stream()
                .map(AvaliacaoResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AvaliacaoResponseDTO buscarPorId(Long id) {
        return AvaliacaoResponseDTO.from(
                buscarAvaliacao(id)
        );
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponseDTO> listarPorJogo(
            Long jogoId
    ) {
        buscarJogo(jogoId);

        return avaliacaoRepository.findByJogoId(jogoId)
                .stream()
                .map(AvaliacaoResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponseDTO> listarPorUsuario(
            Long usuarioId
    ) {
        buscarUsuario(usuarioId);

        return avaliacaoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(AvaliacaoResponseDTO::from)
                .toList();
    }

    @Transactional
    public AvaliacaoResponseDTO atualizar(
            Long id,
            AvaliacaoRequestDTO dto
    ) {
        Avaliacao avaliacao = buscarAvaliacao(id);
        Usuario usuario = buscarUsuario(dto.usuarioId());
        Jogo jogo = buscarJogo(dto.jogoId());

        if (!compraRepository.existsByUsuarioIdAndJogoId(
                usuario.getId(),
                jogo.getId()
        )) {
            throw new AvaliacaoNaoPermitidaException(
                    usuario.getId(),
                    jogo.getId()
            );
        }

        if (avaliacaoRepository
                .existsByUsuarioIdAndJogoIdAndIdNot(
                        usuario.getId(),
                        jogo.getId(),
                        id
                )) {
            throw new AvaliacaoDuplicadaException(
                    usuario.getId(),
                    jogo.getId()
            );
        }

        avaliacao.setUsuario(usuario);
        avaliacao.setJogo(jogo);
        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(dto.comentario());

        return AvaliacaoResponseDTO.from(
                avaliacaoRepository.save(avaliacao)
        );
    }

    @Transactional
    public void excluir(Long id) {
        Avaliacao avaliacao = buscarAvaliacao(id);
        avaliacaoRepository.delete(avaliacao);
    }

    private Avaliacao buscarAvaliacao(Long id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Avaliação não encontrada: " + id
                        )
                );
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Usuário não encontrado: " + id
                        )
                );
    }

    private Jogo buscarJogo(Long id) {
        return jogoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Jogo não encontrado: " + id
                        )
                );
    }
}