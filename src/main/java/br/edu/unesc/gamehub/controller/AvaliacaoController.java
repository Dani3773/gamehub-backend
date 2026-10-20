package br.edu.unesc.gamehub.controller;

import br.edu.unesc.gamehub.dto.AvaliacaoRequestDTO;
import br.edu.unesc.gamehub.dto.AvaliacaoResponseDTO;
import br.edu.unesc.gamehub.service.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
@RequiredArgsConstructor
@Tag(name = "Avaliações", description = "Avaliações de jogos comprados por usuários autenticados.")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @PostMapping
    @Operation(summary = "Criar avaliação", description = "O autor é obtido do token; é necessário ter comprado o jogo.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Avaliação criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou avaliação duplicada"),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária"),
            @ApiResponse(responseCode = "403", description = "É necessário ter comprado o jogo")
    })
    public ResponseEntity<AvaliacaoResponseDTO> cadastrar(
            @Valid @RequestBody AvaliacaoRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(avaliacaoService.cadastrar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar avaliações")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista de avaliações"), @ApiResponse(responseCode = "401", description = "Autenticação necessária")})
    public ResponseEntity<List<AvaliacaoResponseDTO>> listar() {
        return ResponseEntity.ok(
                avaliacaoService.listar()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar avaliação")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Avaliação encontrada"), @ApiResponse(responseCode = "401", description = "Autenticação necessária"), @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")})
    public ResponseEntity<AvaliacaoResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                avaliacaoService.buscarPorId(id)
        );
    }

    @GetMapping("/jogo/{jogoId}")
    @Operation(summary = "Listar avaliações por jogo")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista de avaliações"), @ApiResponse(responseCode = "401", description = "Autenticação necessária"), @ApiResponse(responseCode = "404", description = "Jogo não encontrado")})
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarPorJogo(
            @PathVariable Long jogoId
    ) {
        return ResponseEntity.ok(
                avaliacaoService.listarPorJogo(jogoId)
        );
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar avaliações por usuário")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista de avaliações"), @ApiResponse(responseCode = "401", description = "Autenticação necessária"), @ApiResponse(responseCode = "404", description = "Usuário não encontrado")})
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarPorUsuario(
            @PathVariable Long usuarioId
    ) {
        return ResponseEntity.ok(
                avaliacaoService.listarPorUsuario(usuarioId)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar avaliação", description = "Somente o autor pode atualizar sua avaliação.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Avaliação atualizada"), @ApiResponse(responseCode = "400", description = "Dados inválidos"), @ApiResponse(responseCode = "401", description = "Autenticação necessária"), @ApiResponse(responseCode = "403", description = "Somente o autor pode atualizar"), @ApiResponse(responseCode = "404", description = "Avaliação ou jogo não encontrado")})
    public ResponseEntity<AvaliacaoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AvaliacaoRequestDTO dto
    ) {
        return ResponseEntity.ok(
                avaliacaoService.atualizar(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir avaliação", description = "Somente o autor pode excluir sua avaliação.")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "Avaliação excluída"), @ApiResponse(responseCode = "401", description = "Autenticação necessária"), @ApiResponse(responseCode = "403", description = "Somente o autor pode excluir"), @ApiResponse(responseCode = "404", description = "Avaliação não encontrada")})
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        avaliacaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
