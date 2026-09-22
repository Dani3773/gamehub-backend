package br.edu.unesc.gamehub.service;

import br.edu.unesc.gamehub.dto.JogoRequestDTO;
import br.edu.unesc.gamehub.dto.JogoResponseDTO;
import br.edu.unesc.gamehub.entity.Categoria;
import br.edu.unesc.gamehub.entity.Jogo;
import br.edu.unesc.gamehub.repository.CategoriaRepository;
import br.edu.unesc.gamehub.repository.JogoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JogoService {

    private final JogoRepository jogoRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional
    public JogoResponseDTO cadastrar(JogoRequestDTO dto) {
        Jogo jogo = Jogo.builder()
                .nome(dto.nome())
                .descricao(dto.descricao())
                .preco(dto.preco())
                .dataLancamento(dto.dataLancamento())
                .ativo(true)
                .categorias(buscarCategorias(dto.categoriaIds()))
                .build();

        return JogoResponseDTO.from(jogoRepository.save(jogo));
    }

    @Transactional(readOnly = true)
    public List<JogoResponseDTO> listar(String nome, Long categoriaId) {
        List<Jogo> jogos;

        boolean possuiNome = nome != null && !nome.isBlank();
        boolean possuiCategoria = categoriaId != null;

        if (possuiNome && possuiCategoria) {
            jogos = jogoRepository
                    .findDistinctByNomeContainingIgnoreCaseAndCategorias_Id(nome, categoriaId);
        } else if (possuiNome) {
            jogos = jogoRepository.findByNomeContainingIgnoreCase(nome);
        } else if (possuiCategoria) {
            jogos = jogoRepository.findDistinctByCategorias_Id(categoriaId);
        } else {
            jogos = jogoRepository.findAll();
        }

        return jogos.stream()
                .map(JogoResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public JogoResponseDTO buscarPorId(Long id) {
        return JogoResponseDTO.from(buscarEntidade(id));
    }

    @Transactional
    public JogoResponseDTO atualizar(Long id, JogoRequestDTO dto) {
        Jogo jogo = buscarEntidade(id);

        jogo.setNome(dto.nome());
        jogo.setDescricao(dto.descricao());
        jogo.setPreco(dto.preco());
        jogo.setDataLancamento(dto.dataLancamento());
        jogo.setCategorias(buscarCategorias(dto.categoriaIds()));

        return JogoResponseDTO.from(jogo);
    }

    @Transactional
    public void desativar(Long id) {
        Jogo jogo = buscarEntidade(id);
        jogo.setAtivo(false);
    }

    private Jogo buscarEntidade(Long id) {
        return jogoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Jogo não encontrado com id: " + id
                ));
    }

    private Set<Categoria> buscarCategorias(Set<Long> categoriaIds) {
        if (categoriaIds == null || categoriaIds.isEmpty()) {
            return new HashSet<>();
        }

        List<Categoria> categorias = categoriaRepository.findAllById(categoriaIds);

        if (categorias.size() != categoriaIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Uma ou mais categorias informadas não existem"
            );
        }

        return new HashSet<>(categorias);
    }
}
