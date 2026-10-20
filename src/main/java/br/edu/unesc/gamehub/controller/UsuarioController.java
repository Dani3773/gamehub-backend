package br.edu.unesc.gamehub.controller;

import br.edu.unesc.gamehub.dto.UsuarioRequestDTO;
import br.edu.unesc.gamehub.dto.UsuarioResponseDTO;
import br.edu.unesc.gamehub.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Cadastro e consulta de usuários da GameHub.")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @SecurityRequirements
    @Operation(summary = "Cadastrar usuário", description = "Cria uma conta de cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou e-mail já cadastrado")
    })
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO criado = usuarioService.cadastrar(dto);
        return ResponseEntity.created(URI.create("/api/usuarios/" + criado.id())).body(criado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário", description = "Busca um usuário pelo identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária"),
            @ApiResponse(responseCode = "403", description = "Acesso não permitido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar usuários")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários"),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária"),
            @ApiResponse(responseCode = "403", description = "Acesso não permitido")
    })
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária"),
            @ApiResponse(responseCode = "403", description = "Acesso não permitido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário desativado"),
            @ApiResponse(responseCode = "401", description = "Autenticação necessária"),
            @ApiResponse(responseCode = "403", description = "Acesso não permitido"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        usuarioService.desativar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
