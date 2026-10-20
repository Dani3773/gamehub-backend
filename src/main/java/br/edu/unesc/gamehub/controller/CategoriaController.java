
package br.edu.unesc.gamehub.controller;

import br.edu.unesc.gamehub.dto.CategoriaRequestDTO;
import br.edu.unesc.gamehub.dto.CategoriaResponseDTO;
import br.edu.unesc.gamehub.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
@Tag(
        name = "Categorias",
        description = "Gerenciamento das categorias de jogos"
)
public class CategoriaController {

    private final CategoriaService categoriaService;

    // Cadastrar categoria
    @Operation(
            summary = "Cadastrar categoria",
            description = "Cadastra uma nova categoria de jogos. Requer permissão de administrador."
    )
    @ApiResponse(responseCode = "201", description = "Categoria cadastrada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "403", description = "Acesso negado")
    @ApiResponse(responseCode = "409", description = "Categoria já cadastrada")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponseDTO> cadastrar(
            @Valid @RequestBody CategoriaRequestDTO dto
    ) {
        CategoriaResponseDTO categoria = categoriaService.cadastrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoria);
    }

    // Listar categorias
    @Operation(
            summary = "Listar categorias",
            description = "Retorna todas as categorias de jogos cadastradas."
    )
    @ApiResponse(responseCode = "200", description = "Categorias listadas com sucesso")
    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> listar() {
        return ResponseEntity.ok(categoriaService.listar());
    }

    // Buscar categoria por ID
    @Operation(
            summary = "Buscar categoria por ID",
            description = "Busca uma categoria de jogos pelo seu identificador."
    )
    @ApiResponse(responseCode = "200", description = "Categoria encontrada")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    // Atualizar categoria
    @Operation(
            summary = "Atualizar categoria",
            description = "Atualiza os dados de uma categoria existente. Requer permissão de administrador."
    )
    @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "403", description = "Acesso negado")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @ApiResponse(responseCode = "409", description = "Nome de categoria já cadastrado")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaRequestDTO dto
    ) {
        return ResponseEntity.ok(
                categoriaService.atualizar(id, dto)
        );
    }

    // Excluir categoria
    @Operation(
            summary = "Excluir categoria",
            description = "Exclui uma categoria pelo ID. Requer permissão de administrador."
    )
    @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso")
    @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    @ApiResponse(responseCode = "403", description = "Acesso negado")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        categoriaService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
