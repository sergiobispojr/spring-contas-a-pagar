-- Criar tabela usuario
CREATE TABLE usuario
(
    id    SERIAL PRIMARY KEY,
    nome  VARCHAR(255)   NOT NULL,
    email VARCHAR(255)   NOT NULL UNIQUE,
    senha VARCHAR(255)   NOT NULL,
    saldo DECIMAL(10, 2) NOT NULL DEFAULT 0
);

-- Criar tabela conta
CREATE TABLE conta
(
    id              SERIAL PRIMARY KEY,
    nome            VARCHAR(255)   NOT NULL,
    descricao       TEXT,
    valor_original  DECIMAL(10, 2) NOT NULL,
    data_vencimento DATE           NOT NULL,
    situacao        VARCHAR(255)   NOT NULL DEFAULT 'PENDENTE',
    data_pagamento  DATE,
    observacao      TEXT,
    usuario_id      INT            NOT NULL,
    CONSTRAINT fk_usuario
        FOREIGN KEY (usuario_id)
            REFERENCES usuario (id)
            ON DELETE CASCADE
);