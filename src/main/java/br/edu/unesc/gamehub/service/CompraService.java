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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

        // RN08: comprador vem do token, nunca do corpo da requisição
        Usuario usuario = buscarUsuarioAutenticado();

        // RN05: impede compra duplicada
        if (compraRepository.existsByUsuarioIdAndJogoId(usuario.getId(), dto.jogoId())) {
                throw new CompraDuplicadaException(
                    "Compra duplicada para usuário " + usuario.getId() + " e jogo " + dto.jogoId()
                );
        }

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

    @Transactional(readOnly = true)
    public CompraResponseDTO buscarPorId(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Compra não encontrada: " + id));

        // RN08: só o dono da compra ou ADMIN acessa
        validarAcesso(compra.getUsuario().getId());

        return toResponseDTO(compra);
    }

    @Transactional(readOnly = true)
    public List<CompraResponseDTO> listarPorUsuario(Long usuarioId) {
        // RN08: sem usuarioId, lista as compras do próprio usuário autenticado
        Long alvo = usuarioId != null ? usuarioId : buscarUsuarioAutenticado().getId();
        validarAcesso(alvo);

        return compraRepository.findByUsuarioId(alvo).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private Usuario buscarUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getName() == null || "anonymousUser".equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticação necessária");
        }
        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário autenticado não encontrado: " + authentication.getName()
                ));
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    private void validarAcesso(Long donoId) {
        Usuario usuario = buscarUsuarioAutenticado();
        if (!usuario.getId().equals(donoId) && !isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Somente o dono da compra ou ADMIN pode acessá-la");
        }
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
