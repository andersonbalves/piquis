      # language: pt

      Funcionalidade: Efetuar Transferência

        Delineação do Cenário: Efetuar uma transferência entre contas válidas
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
          Quando eu envio uma requisição para efetuar a transferência
          Então a transferência deve ser efetuada com sucesso
          E a resposta deve conter os dados da transferência
          Exemplos:
            | idContaOrigem                        | contaOrigem | idContaDestino                       | contaDestino | valor |
            | dfc5543f-7223-42bb-9177-f7c679e6113b | 2410985437  | 20a770ef-a6ac-48f2-a758-ee6aa2d3ee03 | 5432623833   | 100.0 |
            | e5a7857d-3e26-4f6d-9c3e-205280ad3111 | 8365956181  | 267c2500-7e8c-4de9-b87f-3c45bd36f35  | 9478613635   | 200.0 |

        Delineacao do Cenario: Tentar efetuar uma transferencia com dados de entrada invalidos
          Dado que eu tenho os dados da transferência
            | contaOrigem   | contaDestino   | valor   |
            | <contaOrigem> | <contaDestino> | <valor> |
          Quando eu envio uma requisição para efetuar a transferência
          Então a transferência NÃO deve ser efetuada com sucesso
          E a resposta deve conter a mensagem de erro "Os dados de entrada são inválidos" e informar o campo "<campoInvalido>" como inválido
          Exemplos:
            | contaOrigem | contaDestino | valor   | campoInvalido |
            | null        | 1234567890   | 100.0   | contaOrigem   |
            | empty       | 1234567890   | 100     | contaOrigem   |
            | 1234567890  | null         | 100.0   | contaDestino  |
            | 1234567890  | empty        | 100.0   | contaDestino  |
            | 1234567890  | 1234567890   | null    | valor         |
            | 1234567890  | 1234567890   | -100.0  | valor         |
            | 1234567890  | 1234567890   | 11000.0 | valor         |

        Delineação do Cenário: Tentar transferir quando a conta de origem não existe
          Dado  que eu tenho os dados do cliente
            | idCliente        | nomeCliente | numeroConta    | saldoConta |
            | <idContaDestino> | Jon Snow    | <contaDestino> | 1000       |
          E que o cliente já está cadastrado
          E que eu tenho os dados da transferência
            | contaOrigem   | contaDestino   | valor   |
            | <contaOrigem> | <contaDestino> | <valor> |
          Quando eu envio uma requisição para efetuar a transferência
          Então a transferência NÃO deve ser efetuada com sucesso
          E a resposta deve conter o erro "NOT_FOUND" e mensagem "Conta de origem não encontrada."
          Exemplos:
            | contaOrigem | idContaDestino                       | contaDestino | valor |
            | 1004261078  | 6667c41e-e783-44b6-bc75-4bd1aebfabc1 | 8547145414   | 100.0 |
            | 6575673625  | 445e7913-e57b-4f90-99d4-826025fa138b | 6821186143   | 200.0 |

        Delineação do Cenário: Tentar transferir quando a conta de destino não existe
          Dado  que eu tenho os dados do cliente
            | idCliente       | nomeCliente | numeroConta   | saldoConta |
            | <idContaOrigem> | Jon Snow    | <contaOrigem> | 1000       |
          E que o cliente já está cadastrado
          E que eu tenho os dados da transferência
            | contaOrigem   | contaDestino   | valor   |
            | <contaOrigem> | <contaDestino> | <valor> |
          Quando eu envio uma requisição para efetuar a transferência
          Então a transferência NÃO deve ser efetuada com sucesso
          E a resposta deve conter o erro "NOT_FOUND" e mensagem "Conta de destino não encontrada."
          Exemplos:
            | contaDestino | idContaOrigem                        | contaOrigem | valor |
            | 1004261078   | 6667c41e-e783-44b6-bc75-4bd1aebfabc1 | 8547145414  | 100.0 |
            | 6575673625   | 445e7913-e57b-4f90-99d4-826025fa138b | 6821186143  | 200.0 |