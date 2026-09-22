CREATE TABLE jogo (
    id               BIGSERIAL PRIMARY KEY,
    nome             VARCHAR(160) NOT NULL,
    descricao        TEXT,
    preco            NUMERIC(10,2) NOT NULL,
    data_lancamento  DATE,
    ativo            BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT ck_jogo_preco_positivo CHECK (preco > 0)
);

CREATE INDEX idx_jogo_nome ON jogo (nome);

CREATE TABLE jogo_categoria (
    jogo_id       BIGINT NOT NULL,
    categoria_id  BIGINT NOT NULL,

    CONSTRAINT pk_jogo_categoria PRIMARY KEY (jogo_id, categoria_id),
    CONSTRAINT fk_jogo_categoria_jogo
        FOREIGN KEY (jogo_id) REFERENCES jogo (id),
    CONSTRAINT fk_jogo_categoria_categoria
        FOREIGN KEY (categoria_id) REFERENCES categoria (id)
);

CREATE INDEX idx_jogo_categoria_categoria_id
    ON jogo_categoria (categoria_id);
