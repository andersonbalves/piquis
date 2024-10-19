CREATE TABLE CLIENTE (
    id_cliente VARCHAR(255) PRIMARY KEY,
    nome_cliente VARCHAR(255) NOT NULL,
    numero_conta VARCHAR(255) NOT NULL,
    saldo DECIMAL(19, 2) NOT NULL,
    version BIGINT NOT NULL,
    CONSTRAINT unique_numero_conta UNIQUE (numero_conta)
);

CREATE INDEX idx_numero_conta ON CLIENTE (numero_conta);