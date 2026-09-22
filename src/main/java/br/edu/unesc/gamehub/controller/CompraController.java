package br.edu.unesc.gamehub.controller;

import br.edu.unesc.gamehub.dto.CompraRequestDTO;
import br.edu.unesc.gamehub.dto.CompraResponseDTO;
import br.edu.unesc.gamehub.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @PostMapping
    public ResponseEntity<CompraResponseDTO> comprar(
            @Valid @RequestBody CompraRequestDTO dto
    ) {
        CompraResponseDTO criada = compraService.comprar(dto);

        return ResponseEntity
                .created(URI.create("/compras/" + criada.id()))
                .body(criada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<CompraResponseDTO>> listarPorUsuario(
            @RequestParam Long usuarioId
    ) {
        return ResponseEntity.ok(compraService.listarPorUsuario(usuarioId));
    }
}