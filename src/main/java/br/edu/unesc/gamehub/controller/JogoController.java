package br.edu.unesc.gamehub.controller;

import br.edu.unesc.gamehub.dto.JogoRequestDTO;
import br.edu.unesc.gamehub.dto.JogoResponseDTO;
import br.edu.unesc.gamehub.service.JogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/jogos")
@RequiredArgsConstructor
@Tag(name = "Jogos", description = "Catálogo e gerenciamento de jogos da GameHub.")
public class JogoController {

    private final JogoService jogoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Cadastrar jogo",
            description = "Cadastra um novo jogo no catálogo. Operação restrita ao perfil ADMIN."
    )
    @ApiResponse(responseCode = "201", description = "Jogo cadastrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou categoria inexistente")
    @ApiResponse(responseCode = "401", description = "Autenticação necessária")
    @ApiResponse(responseCode = "403", description = "Acesso permitido apenas para ADMIN")
    public ResponseEntity<JogoResponseDTO> cadastrar(
            @Valid @RequestBody JogoRequestDTO dto
    ) {
        JogoResponseDTO criado = jogoService.cadastrar(dto);

        return ResponseEntity
                .created(URI.create("/jogos/" + criado.id()))
                .body(criado);
    }

    @GetMapping
    @Operation(
            summary = "Listar jogos",
            description = "Lista os jogos cadastrados e permite filtrar por parte do nome e/ou identificador da categoria."
    )
    @ApiResponse(responseCode = "200", description = "Lista de jogos")
    @ApiResponse(responseCode = "401", description = "Autenticação necessária")
    public ResponseEntity<List<JogoResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Long categoriaId
    ) {
        return ResponseEntity.ok(jogoService.listar(nome, categoriaId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar jogo", description = "Busca um jogo pelo identificador.")
    @ApiResponse(responseCode = "200", description = "Jogo encontrado")
    @ApiResponse(responseCode = "401", description = "Autenticação necessária")
    @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
    public ResponseEntity<JogoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(jogoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Atualizar jogo",
            description = "Atualiza os dados de um jogo existente. Operação restrita ao perfil ADMIN."
    )
    @ApiResponse(responseCode = "200", description = "Jogo atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou categoria inexistente")
    @ApiResponse(responseCode = "401", description = "Autenticação necessária")
    @ApiResponse(responseCode = "403", description = "Acesso permitido apenas para ADMIN")
    @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
    public ResponseEntity<JogoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody JogoRequestDTO dto
    ) {
        return ResponseEntity.ok(jogoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Desativar jogo",
            description = "Realiza a inativação lógica de um jogo. Operação restrita ao perfil ADMIN."
    )
    @ApiResponse(responseCode = "204", description = "Jogo desativado")
    @ApiResponse(responseCode = "401", description = "Autenticação necessária")
    @ApiResponse(responseCode = "403", description = "Acesso permitido apenas para ADMIN")
    @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        jogoService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
