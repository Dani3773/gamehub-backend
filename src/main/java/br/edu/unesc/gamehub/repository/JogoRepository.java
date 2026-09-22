package br.edu.unesc.gamehub.repository;

import br.edu.unesc.gamehub.entity.Jogo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JogoRepository extends JpaRepository<Jogo, Long> {

    List<Jogo> findByNomeContainingIgnoreCase(String nome);

    List<Jogo> findDistinctByCategorias_Id(Long categoriaId);

    List<Jogo> findDistinctByNomeContainingIgnoreCaseAndCategorias_Id(
            String nome,
            Long categoriaId
    );
}
