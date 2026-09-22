package br.edu.unesc.gamehub.service;

import br.edu.unesc.gamehub.dto.CompraRequestDTO;
import br.edu.unesc.gamehub.dto.CompraResponseDTO;
import br.edu.unesc.gamehub.entity.Compra;
import br.edu.unesc.gamehub.entity.Jogo;
import br.edu.unesc.gamehub.entity.Usuario;
import br.edu.unesc.gamehub.exception.CompraDuplicadaException;
import br.edu.unesc.gamehub.repository.CompraRepository;
import br.edu.unesc.gamehub.repository.JogoRepository;
import br.edu.unesc.gamehub.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;
    private final UsuarioRepository usuarioRepository;
    private final JogoRepository jogoRepository;

    @Transactional
    public CompraResponseDTO comprar(CompraRequestDTO dto) {

        // RN05: impede compra duplicada
        if (compraRepository.existsByUsuarioIdAndJogoId(dto.usuarioId(), dto.jogoId())) {
            throw new CompraDuplicadaException(dto.usuarioId(), dto.jogoId());
        }

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + dto.usuarioId()));

        Jogo jogo = jogoRepository.findById(dto.jogoId())
                .orElseThrow(() -> new EntityNotFoundException("Jogo não encontrado: " + dto.jogoId()));

        // RN06: valor pago é o preço do jogo NO MOMENTO da compra,
        Compra compra = Compra.builder()
                .usuario(usuario)
                .jogo(jogo)
                .valorPago(jogo.getPreco())
                .dataCompra(LocalDateTime.now())
                .build();

        Compra salva = compraRepository.save(compra);

        return toResponseDTO(salva);
    }

    public CompraResponseDTO buscarPorId(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Compra não encontrada: " + id));
        return toResponseDTO(compra);
    }

    public List<CompraResponseDTO> listarPorUsuario(Long usuarioId) {
        return compraRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private CompraResponseDTO toResponseDTO(Compra compra) {
        return new CompraResponseDTO(
                compra.getId(),
                compra.getUsuario().getId(),
                compra.getJogo().getId(),
                compra.getValorPago(),
                compra.getDataCompra()
        );
    }
}