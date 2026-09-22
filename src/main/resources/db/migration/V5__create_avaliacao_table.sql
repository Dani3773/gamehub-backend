CREATE TABLE avaliacao (
                           id BIGSERIAL PRIMARY KEY,
                           usuario_id BIGINT NOT NULL,
                           jogo_id BIGINT NOT NULL,
                           nota INTEGER NOT NULL,
                           comentario VARCHAR(1000),
                           data_avaliacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT fk_avaliacao_usuario
                               FOREIGN KEY (usuario_id)
                                   REFERENCES usuario (id),

                           CONSTRAINT fk_avaliacao_jogo
                               FOREIGN KEY (jogo_id)
                                   REFERENCES jogo (id),

                           CONSTRAINT ck_avaliacao_nota
                               CHECK (nota BETWEEN 1 AND 5),

                           CONSTRAINT uk_avaliacao_usuario_jogo
                               UNIQUE (usuario_id, jogo_id)
);

CREATE INDEX idx_avaliacao_jogo_id
    ON avaliacao (jogo_id);

CREATE INDEX idx_avaliacao_usuario_id
    ON avaliacao (usuario_id);