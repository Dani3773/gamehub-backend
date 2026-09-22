package br.edu.unesc.gamehub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AvaliacaoDuplicadaException extends RuntimeException {

    public AvaliacaoDuplicadaException(
            Long usuarioId,
            Long jogoId
    ) {
        super(
                "O usuário " + usuarioId
                        + " já avaliou o jogo " + jogoId
        );
    }
}