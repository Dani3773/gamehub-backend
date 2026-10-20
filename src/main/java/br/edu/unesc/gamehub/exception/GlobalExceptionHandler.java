
package br.edu.unesc.gamehub.exception;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // RN05: Compra duplicada - HTTP 409
    @ExceptionHandler(CompraDuplicadaException.class)
    public ResponseEntity<String> handleDuplicada(
            CompraDuplicadaException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    // RN11: Avaliação duplicada - HTTP 409
    @ExceptionHandler(AvaliacaoDuplicadaException.class)
    public ResponseEntity<String> handleAvaliacaoDuplicada(
            AvaliacaoDuplicadaException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    // RN09: Avaliação não permitida - HTTP 403
    @ExceptionHandler(AvaliacaoNaoPermitidaException.class)
    public ResponseEntity<String> handleAvaliacaoNaoPermitida(
            AvaliacaoNaoPermitidaException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }

    // Entidade não encontrada - HTTP 404
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNaoEncontrado(
            EntityNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    // Erros de validação com @Valid - HTTP 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidacao(
            MethodArgumentNotValidException ex) {

        String mensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": "
                        + erro.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining("; "));

        if (mensagem.isBlank()) {
            mensagem = "Dados inválidos";
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(mensagem);
    }

    // Acesso negado - HTTP 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAcessoNegado(
            AccessDeniedException ex) {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("Acesso negado");
    }

    // Fallback: erros inesperados - HTTP 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleErroInterno(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocorreu um erro interno no servidor");
    }
}
