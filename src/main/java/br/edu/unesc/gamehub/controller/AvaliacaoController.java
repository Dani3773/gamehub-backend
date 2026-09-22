package br.edu.unesc.gamehub.controller;

import br.edu.unesc.gamehub.dto.AvaliacaoRequestDTO;
import br.edu.unesc.gamehub.dto.AvaliacaoResponseDTO;
import br.edu.unesc.gamehub.service.AvaliacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @PostMapping
    public ResponseEntity<AvaliacaoResponseDTO> cadastrar(
            @Valid @RequestBody AvaliacaoRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(avaliacaoService.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<AvaliacaoResponseDTO>> listar() {
        return ResponseEntity.ok(
                avaliacaoService.listar()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                avaliacaoService.buscarPorId(id)
        );
    }

    @GetMapping("/jogo/{jogoId}")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarPorJogo(
            @PathVariable Long jogoId
    ) {
        return ResponseEntity.ok(
                avaliacaoService.listarPorJogo(jogoId)
        );
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarPorUsuario(
            @PathVariable Long usuarioId
    ) {
        return ResponseEntity.ok(
                avaliacaoService.listarPorUsuario(usuarioId)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AvaliacaoRequestDTO dto
    ) {
        return ResponseEntity.ok(
                avaliacaoService.atualizar(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {
        avaliacaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}