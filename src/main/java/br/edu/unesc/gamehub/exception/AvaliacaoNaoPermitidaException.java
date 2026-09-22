package br.edu.unesc.gamehub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AvaliacaoNaoPermitidaException extends RuntimeException {

    public AvaliacaoNaoPermitidaException(
            Long usuarioId,
            Long jogoId
    ) {
        super(
                "O usuário " + usuarioId
                        + " não comprou o jogo " + jogoId
                        + " e não pode avaliá-lo"
        );
    }
}