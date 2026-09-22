package br.edu.unesc.gamehub.exception;

// RN05
public class CompraDuplicadaException extends RuntimeException {
    public CompraDuplicadaException(Long usuarioId, Long jogoId) {
        super("Usuário " + usuarioId + " já possui uma compra do jogo " + jogoId);
    }
}