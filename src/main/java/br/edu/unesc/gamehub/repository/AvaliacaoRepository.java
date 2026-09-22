package br.edu.unesc.gamehub.repository;

import br.edu.unesc.gamehub.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoRepository
        extends JpaRepository<Avaliacao, Long> {

    boolean existsByUsuarioIdAndJogoId(
            Long usuarioId,
            Long jogoId
    );

    boolean existsByUsuarioIdAndJogoIdAndIdNot(
            Long usuarioId,
            Long jogoId,
            Long id
    );

    List<Avaliacao> findByJogoId(Long jogoId);

    List<Avaliacao> findByUsuarioId(Long usuarioId);
}