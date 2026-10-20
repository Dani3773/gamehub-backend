package br.edu.unesc.gamehub.controller;

import br.edu.unesc.gamehub.dto.CompraRequestDTO;
import br.edu.unesc.gamehub.dto.CompraResponseDTO;
import br.edu.unesc.gamehub.service.CompraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/compras")
@RequiredArgsConstructor
@Tag(name = "Compras", description = "Compras de jogos. Cada usuário acessa apenas as próprias compras; ADMIN acessa todas.")
public class CompraController {

    private final CompraService compraService;

    @PostMapping
    @Operation(summary = "Comprar jogo", description = "O comprador é obtido do token. O valor pago é o preço atual do jogo.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compra realizada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária"),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Usuário já comprou este jogo")
    })
    public ResponseEntity<CompraResponseDTO> comprar(
            @Valid @RequestBody CompraRequestDTO dto
    ) {
        CompraResponseDTO criada = compraService.comprar(dto);

        return ResponseEntity
                .created(URI.create("/compras/" + criada.id()))
                .body(criada);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar compra", description = "Somente o dono da compra ou ADMIN.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Compra encontrada"), @ApiResponse(responseCode = "401", description = "Autenticação necessária"), @ApiResponse(responseCode = "403", description = "Compra pertence a outro usuário"), @ApiResponse(responseCode = "404", description = "Compra não encontrada")})
    public ResponseEntity<CompraResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar compras", description = "Sem usuarioId, lista as compras do usuário autenticado. Informar o usuarioId de outro usuário exige ADMIN.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista de compras"), @ApiResponse(responseCode = "401", description = "Autenticação necessária"), @ApiResponse(responseCode = "403", description = "Compras de outro usuário exigem ADMIN")})
    public ResponseEntity<List<CompraResponseDTO>> listarPorUsuario(
            @RequestParam(required = false) Long usuarioId
    ) {
        return ResponseEntity.ok(compraService.listarPorUsuario(usuarioId));
    }
}
