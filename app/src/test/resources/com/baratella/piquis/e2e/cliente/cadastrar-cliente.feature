      # language: pt

      Funcionalidade: Cadastrar Cliente

        Delineação do Cenário: Cadastrar um novo cliente
          Dado que eu tenho os dados do cliente
            | idCliente   | nomeCliente   | numeroConta   | saldoConta   |
            | <idCliente> | <nomeCliente> | <numeroConta> | <saldoConta> |
          Quando eu envio uma requisição para cadastrar o cliente
          Então o cliente deve ser cadastrado com sucesso
          E a resposta deve conter os dados do cliente cadastrado
          Exemplos:
            | idCliente                            | nomeCliente      | numeroConta  | saldoConta |
            | 15fc85ac-6250-42aa-8844-240af9d0e934 | Bilbo Baggins    | 228349037805 | 1000.0     |
            | 424abcb6-c9f6-4cb1-8c1f-323fa6566def | Hermione Granger | 209826025142 | 500.0      |
            | 560557e4-ab83-43b6-ae0a-e55265d7ccfe | Bruce Wayne      | 849552078868 | 2000.0     |
            | 6f7bfdb8-52e7-4f63-9a66-d7a5d427aae0 | Wanda Maximoff   | 178639194745 | 3000.0     |

        Delineação do Cenário: Tentar cadastrar um cliente com id já existente
          Dado que eu tenho os dados do cliente
            | idCliente   | nomeCliente   | numeroConta   | saldoConta   |
            | <idCliente> | <nomeCliente> | <numeroConta> | <saldoConta> |
          E que o cliente já está cadastrado
          Quando eu envio uma requisição para cadastrar o cliente
          Então o cliente não deve ser cadastrado
          E a resposta deve conter o erro "CONFLICT" e mensagem "ID do cliente já cadastrado"
          Exemplos:
            | idCliente                            | nomeCliente      | numeroConta  | saldoConta |
            | e5714945-0265-44e8-b9b8-6a76903e1217 | Anakin Skywalker | 552907814303 | 1000.0     |
            | 5147ffa0-a065-401d-b8d5-772a1f21752e | Ahsoka Tano      | 650053681835 | 500.0      |
            | 58ebf4e0-39db-4acc-b673-bb4f073f634f | Lando Calrissian | 537839664083 | 2000.0     |
            | 4099d57f-6b64-4fd7-871d-41c89b2ecde5 | Rick Sanchez     | 741495230492 | 3000.0     |

        Delineação do Cenário: Tentar cadastrar um cliente com conta já cadastrada
          Dado que eu tenho os dados do cliente
            | idCliente   | nomeCliente   | numeroConta   | saldoConta   |
            | <idCliente> | <nomeCliente> | <numeroConta> | <saldoConta> |
          E que o cliente já está cadastrado
          Quando eu envio uma requisição para cadastrar o cliente com id diferente e conta já cadastrada
          Então o cliente não deve ser cadastrado
          E a resposta deve conter o erro "CONFLICT" e mensagem "Número da conta já cadastrado"
          Exemplos:
            | idCliente                            | nomeCliente       | numeroConta  | saldoConta |
            | 8f3a8a67-9118-4281-ba49-f8a99a1b966c | Son Goku          | 521769927149 | 1000.0     |
            | e34ce8d3-4b4e-4e15-b888-38aee5feafa4 | Tanjirō Kamado    | 722286182341 | 500.0      |
            | c0b6572d-f27a-4496-ba4a-87d42a426bee | Rimuru Tempest    | 340491852774 | 2000.0     |
            | 400d9d35-26f0-4df8-8ef0-c53b16098ff2 | Hitokiri Battōsai | 661718302281 | 3000.0     |

        Delineação do Cenário: Tentar cadastrar um cliente sem informar todos os campos corretamente
          Dado que eu tenho os dados do cliente
            | idCliente   | nomeCliente   | numeroConta   | saldoConta   |
            | <idCliente> | <nomeCliente> | <numeroConta> | <saldoConta> |
          Quando eu envio uma requisição para cadastrar o cliente
          Então o cliente não deve ser cadastrado
          E a resposta deve conter a mensagem de erro "Os dados de entrada são inválidos" e informar o campo "<campoInvalido>" como inválido
          Exemplos:
            | idCliente                            | nomeCliente      | numeroConta  | saldoConta | campoInvalido |
            | null                                 | Anakin Skywalker | 368253762183 | 1000.0     | idCliente     |
            | empty                                | Anakin Skywalker | 368253762183 | 1000.0     | idCliente     |
            | 5147ffa0-a065-401d-b8d5-772a1f21752e | null             | 801148182704 | 500.0      | nomeCliente   |
            | 5147ffa0-a065-401d-b8d5-772a1f21752e | empty            | 801148182704 | 500.0      | nomeCliente   |
            | 58ebf4e0-39db-4acc-b673-bb4f073f634f | Lando Calrissian | null         | 2000.0     | numeroConta   |
            | 58ebf4e0-39db-4acc-b673-bb4f073f634f | Lando Calrissian | empty        | 2000.0     | numeroConta   |
            | 4099d57f-6b64-4fd7-871d-41c89b2ecde5 | Rick Sanchez     | 650635253715 | null       | saldoConta    |