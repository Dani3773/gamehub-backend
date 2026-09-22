CREATE TABLE categoria (
                           id BIGSERIAL PRIMARY KEY,
                           nome VARCHAR(100) NOT NULL,
                           descricao VARCHAR(500),
                           CONSTRAINT uk_categoria_nome UNIQUE (nome)
);