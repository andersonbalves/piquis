# language: pt

Funcionalidade: Consultar Cliente

  Cenario: Consultar todos clientes cadastrados
    Dado que tenho clientes cadastrados
    Quando consulto todos os clientes
    Entao os clientes devem ser encontrados com sucesso
    E a resposta deve conter todos os clientes cadastrados

  Delineação do Cenário: Consultar um cliente por conta
    Dado que eu tenho os dados do cliente
      | idCliente   | nomeCliente   | numeroConta   | saldoConta   |
      | <idCliente> | <nomeCliente> | <numeroConta> | <saldoConta> |
    E que o cliente já está cadastrado
    Quando consulto um cliente pela conta "<numeroConta>"
    Então o cliente deve ser encontrado com sucesso
    E a resposta deve conter os dados do cliente cadastrado
    Exemplos:
      | idCliente                            | nomeCliente    | numeroConta  | saldoConta |
      | 90209d37-8aa2-4b99-9af2-e2ae7f0be834 | Vivi Ornitier  | 350422384368 | 1000.0     |
      | 9d018f47-d09f-4db2-93ee-e041f0dda683 | Killua Zoldyck | 629504314453 | 500.0      |
      | 39c13631-bc7c-4ce5-b866-2672e3bcba1c | Armin Arlert   | 341206543834 | 2000.0     |
      | 311da634-103c-4fa1-92f4-fab05803ba0d | Edward Elric   | 611630428234 | 3000.0     |

  Delineação do Cenário: Consultar um cliente NÃO cadastrado por conta
    Dado que o cliente NÃO está cadastrado
    Quando consulto um cliente pela conta "<numeroConta>"
    Então o cliente NÃO deve ser encontrado
    E a resposta deve conter o erro "NOT_FOUND" e mensagem "Cliente não encontrado."
    Exemplos:
      | numeroConta  |
      | 268407003177 |
      | 341100691160 |

  Cenário: Consultar um cliente informando valores inválidos
    Dado que o cliente NÃO está cadastrado
    Quando consulto um cliente pela conta "empty"
    Então o cliente NÃO deve ser encontrado
    E a resposta deve conter o erro "BAD_REQUEST" e mensagem "Número da conta não pode ser vazio."
