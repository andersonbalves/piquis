# language: pt

Funcionalidade: Consultar Transferência

  Delineação do Cenário:  Consultar transferências de uma conta existente
    Dado que eu tenho os dados do cliente da conta de origem
      | idCliente       | nomeCliente    | numeroConta   | saldoConta |
      | <idContaOrigem> | Katsuki Bakugo | <contaOrigem> | 1000       |
    E que o cliente já está cadastrado
    E que eu tenho os dados do cliente
      | idCliente        | nomeCliente | numeroConta    | saldoConta |
      | <idContaDestino> | Jon Snow    | <contaDestino> | 1000       |
    E que o cliente já está cadastrado
    E que eu tenho os dados da transferência
      | contaOrigem   | contaDestino   | valor   |
      | <contaOrigem> | <contaDestino> | <valor> |
    E que a transferência já foi efetuada
    Quando consulto as transferências da conta "<contaOrigem>"
    Então as transferências devem ser encontradas com sucesso
    E a resposta deve conter os dados de todas as transferências
    Exemplos:
      | idContaOrigem                        | contaOrigem | idContaDestino                       | contaDestino | valor |
      | dfc5543f-7223-42bb-9177-f7c679e6113b | 2410985437  | 20a770ef-a6ac-48f2-a758-ee6aa2d3ee03 | 5432623833   | 100.0 |
      | e5a7857d-3e26-4f6d-9c3e-205280ad3111 | 8365956181  | 267c2500-7e8c-4de9-b87f-3c45bd36f35  | 9478613635   | 200.0 |

  Delineação do Cenário: Consultar transferências de uma conta inexistente
    Dado que o cliente NÃO está cadastrado
    Quando consulto as transferências da conta "<contaOrigem>"
    Então as transferências NÃO devem ser encontradas
    E a resposta deve conter o erro "NOT_FOUND" e mensagem "Conta não encontrada."
    Exemplos:
      | contaOrigem |
      | 1004261078  |
      | 6575673625  |