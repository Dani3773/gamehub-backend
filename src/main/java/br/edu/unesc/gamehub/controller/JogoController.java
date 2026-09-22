package br.edu.unesc.gamehub.controller;

import br.edu.unesc.gamehub.dto.JogoRequestDTO;
import br.edu.unesc.gamehub.dto.JogoResponseDTO;
import br.edu.unesc.gamehub.service.JogoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/jogos")
@RequiredArgsConstructor
public class JogoController {

    private final JogoService jogoService;

    @PostMapping
    public ResponseEntity<JogoResponseDTO> cadastrar(
            @Valid @RequestBody JogoRequestDTO dto
    ) {
        JogoResponseDTO criado = jogoService.cadastrar(dto);

        return ResponseEntity
                .created(URI.create("/jogos/" + criado.id()))
                .body(criado);
    }

    @GetMapping
    public ResponseEntity<List<JogoResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Long categoriaId
    ) {
        return ResponseEntity.ok(jogoService.listar(nome, categoriaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JogoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(jogoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JogoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody JogoRequestDTO dto
    ) {
        return ResponseEntity.ok(jogoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        jogoService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
