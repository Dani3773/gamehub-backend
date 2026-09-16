CREATE TABLE usuario (
                         id             BIGSERIAL PRIMARY KEY,
                         nome           VARCHAR(120)  NOT NULL,
                         email          VARCHAR(160)  NOT NULL,
                         senha_hash     VARCHAR(255)  NOT NULL,
                         perfil         VARCHAR(20)   NOT NULL DEFAULT 'CLIENTE',
                         ativo          BOOLEAN       NOT NULL DEFAULT TRUE,
                         data_cadastro  TIMESTAMP     NOT NULL DEFAULT now(),

                         CONSTRAINT uq_usuario_email UNIQUE (email),
                         CONSTRAINT ck_usuario_perfil CHECK (perfil IN ('CLIENTE', 'ADMIN'))
);

CREATE INDEX idx_usuario_email ON usuario (email);