package br.edu.unesc.gamehub.service;

import br.edu.unesc.gamehub.dto.CategoriaRequestDTO;
import br.edu.unesc.gamehub.dto.CategoriaResponseDTO;
import br.edu.unesc.gamehub.entity.Categoria;
import br.edu.unesc.gamehub.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaResponseDTO cadastrar(CategoriaRequestDTO dto) {
        if (categoriaRepository.existsByNome(dto.nome())) {
            throw new RuntimeException("Já existe uma categoria com esse nome");
        }

        Categoria categoria = Categoria.builder()
                .nome(dto.nome())
                .descricao(dto.descricao())
                .build();

        Categoria categoriaSalva = categoriaRepository.save(categoria);

        return CategoriaResponseDTO.from(categoriaSalva);
    }

    public List<CategoriaResponseDTO> listar() {
        return categoriaRepository.findAll()
                .stream()
                .map(CategoriaResponseDTO::from)
                .toList();
    }

    public CategoriaResponseDTO buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Categoria não encontrada")
                );

        return CategoriaResponseDTO.from(categoria);
    }

    public CategoriaResponseDTO atualizar(
            Long id,
            CategoriaRequestDTO dto
    ) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Categoria não encontrada")
                );

        categoria.setNome(dto.nome());
        categoria.setDescricao(dto.descricao());

        Categoria categoriaAtualizada =
                categoriaRepository.save(categoria);

        return CategoriaResponseDTO.from(categoriaAtualizada);
    }

    public void excluir(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Categoria não encontrada")
                );

        categoriaRepository.delete(categoria);
    }
}