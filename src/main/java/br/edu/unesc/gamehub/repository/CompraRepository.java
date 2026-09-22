package br.edu.unesc.gamehub.repository;

import br.edu.unesc.gamehub.entity.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    boolean existsByUsuarioIdAndJogoId(Long usuarioId, Long jogoId);

    List<Compra> findByUsuarioId(Long usuarioId);
}