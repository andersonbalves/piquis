# Piquis API

Este repositório contém uma API RESTful construída em Java 21, utilizando Spring Boot e H2 como banco de dados em memória. O projeto contém funcionalidades para o gerenciamento de clientes e transferências entre contas.

## Funcionalidades

- **Clientes**: Cadastro e listagem de clientes.
- **Transferências**: Realização e consulta de transferências entre contas.

## Tecnologias Utilizadas

- [Java 21](https://openjdk.org/projects/jdk/21/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/)
- [H2 Database](https://www.h2database.com/html/main.html)
- [DynamoDBLocal](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html)
- [JUnit 5](https://junit.org/junit5/)
- [Cucumber](https://cucumber.io/)

## Requisitos

Certifique-se de ter as seguintes ferramentas instaladas em seu ambiente de desenvolvimento:

- [Java 21](https://jdk.java.net/java-se-ri/21)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)

## Clonando o Repositório

Para clonar este repositório, utilize o comando:

```bash
git clone https://github.com/andersonbalves/piquis.git
```

vá para o diretório do projeto:

```bash
cd piquis/app
```

## Instalando Dependências

Para instalar as dependências do projeto, execute o comando:

```bash
mvn clean package
```

## Executando a Aplicação

Para executar a aplicação localmente:

```bash
mvn spring-boot:run
```

A aplicação estará disponível em http://localhost:8080.

## Testes

O projeto possui testes unitários e integrados configurados.

Para rodar os testes unitários:

```bash
mvn test
```

Para rodar os testes de integração:

```bash
mvn verify
```

## Endpoints Disponíveis

### Clientes

#### Listar Clientes

- **Endpoint:** `GET /api/v1/clientes`
- **Exemplo de Requisição:**
  ```bash
  curl -X GET "http://localhost:8080/api/v1/clientes"
  ```

#### Buscar Cliente

- **Endpoint:** `GET /api/v1/clientes`
- **Parâmetros de Query:**
  - `numeroConta` (obrigatório): Número da conta do cliente.
- **Exemplo de Requisição:**
  ```bash
  curl -X GET "http://localhost:8080/api/v1/clientes?numeroConta=123456"
  ```

#### Cadastrar Cliente

- **Endpoint:** `POST /api/v1/clientes`
- **Corpo da Requisição (JSON):**
  ```json
  {
    "idCliente": "123",
    "nomeCliente": "Son Goku",
    "numeroConta": "123456",
    "saldoConta": 1000
  }
  ```
  - **Exemplo de Requisição:**
  ```bash
  curl -X POST "http://localhost:8080/api/v1/clientes" \
    -H "Content-Type: application/json" \
    -d '{
      "idCliente": "123",
      "nomeCliente": "Son Goku",
      "numeroConta": "123456",
      "saldoConta": 1000
    }'
  ```

### Transferências

#### Listar Transferências

- **Endpoint:** `GET /api/v1/transferencias`
- **Parâmetros de Query:**
  - `numeroConta` (obrigatório): Número da conta para consulta das transferências.
- **Exemplo de Requisição:**
  ```bash
  curl -X GET "http://localhost:8080/api/v1/transferencias?numeroConta=123456"
  ```

#### Realizar Transferência

- **Endpoint:** POST /api/v1/transferencias
- **Corpo da Requisição (JSON):**
  ```json
  {
    "contaOrigem": "123456",
    "contaDestino": "654321",
    "valor": 500
  }
  ```
- **Exemplo de Requisição:**
  ```bash
  curl -X POST "http://localhost:8080/api/v1/transferencias" \
  -H "Content-Type: application/json" \
  -d '{
  "contaOrigem": "123456",
  "contaDestino": "654321",
  "valor": 500
  }'
  ```

## Banco de Dados

A aplicação utiliza o banco de dados H2 em memória. Para acessar o console do H2 e inspecionar o banco de dados:

- **URL:** http://localhost:8080/h2-console
- **JDBC URL:** jdbc:h2:mem:testdb
- **Usuário:** sa
- **Senha:** password

## Licença

Este projeto está licenciado sob os termos da licença MIT. Para mais detalhes, consulte o arquivo LICENSE no repositório.
