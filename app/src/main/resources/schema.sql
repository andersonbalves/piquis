CREATE TABLE CLIENTE (
    id_cliente VARCHAR(255) PRIMARY KEY,
    nome_cliente VARCHAR(255) NOT NULL,
    numero_conta VARCHAR(255) UNIQUE NOT NULL,
    saldo DECIMAL(19, 2) NOT NULL,
    version BIGINT NOT NULL
);