CREATE TABLE compra (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    jogo_id BIGINT NOT NULL,
    valor_pago NUMERIC(10,2) NOT NULL,
    data_compra TIMESTAMP NOT NULL DEFAULT now(),
 
    CONSTRAINT fk_compra_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_compra_jogo FOREIGN KEY (jogo_id) REFERENCES jogo(id),
 
    -- RN05: impede que o mesmo usuário compre o mesmo jogo mais de uma vez
    CONSTRAINT uk_compra_usuario_jogo UNIQUE (usuario_id, jogo_id)
);
 
CREATE INDEX idx_compra_usuario ON compra(usuario_id);
CREATE INDEX idx_compra_jogo ON compra(jogo_id);
