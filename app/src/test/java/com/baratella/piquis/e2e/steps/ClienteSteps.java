package com.baratella.piquis.e2e.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.baratella.piquis.PiquisApplication;
import com.baratella.piquis.dto.ClienteDTO;
import com.baratella.piquis.dto.ErrorResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.cucumber.spring.CucumberContextConfiguration;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@CucumberContextConfiguration
@SpringBootTest(classes = PiquisApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class ClienteSteps {

  @Autowired
  private TestRestTemplate restTemplate;

  private ClienteDTO cliente;
  private final List<ClienteDTO> clientesCadastrados = new ArrayList<>();
  private ResponseEntity<Object> response;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Dado("que eu tenho os dados do cliente")
  public void queEuTenhoOsDadosDoCliente(DataTable dataTable) {
    Map<String, String> data = dataTable.asMaps().getFirst();
    cliente = ClienteDTO.builder()
        .idCliente(tratarCampoString(data.get("idCliente")))
        .nomeCliente(tratarCampoString(data.get("nomeCliente")))
        .numeroConta(tratarCampoString(data.get("numeroConta")))
        .saldoConta(data.get("saldoConta").equals("null") ? null
            : BigDecimal.valueOf(Double.parseDouble(data.get("saldoConta"))))
        .build();
  }

  @Dado("que o cliente NÃO está cadastrado")
  public void queOClienteNaoEstaCadastrado() {
    log.debug("Cliente não está cadastrado");
  }

  @Dado("que tenho clientes cadastrados")
  public void queTenhoClientesCadastrados() {
    HttpHeaders headers = new HttpHeaders();
    for (int i = 0; i < 5; i++) {
      ClienteDTO clienteRequest = ClienteDTO.builder()
          .idCliente(UUID.randomUUID().toString())
          .nomeCliente("Cliente " + i)
          .numeroConta("1234567890" + i)
          .saldoConta(BigDecimal.valueOf(1000))
          .build();
      HttpEntity<ClienteDTO> request = new HttpEntity<>(clienteRequest, headers);
      restTemplate.exchange("/api/v1/clientes", HttpMethod.POST, request, Object.class);
      clientesCadastrados.add(clienteRequest);
    }
  }

  @Quando("eu envio uma requisição para cadastrar o cliente")
  @Dado("que o cliente já está cadastrado")
  public void euEnvioUmaRequisicaoParaCadastrarOCliente() {
    HttpHeaders headers = new HttpHeaders();
    HttpEntity<ClienteDTO> request = new HttpEntity<>(cliente, headers);
    response = restTemplate.exchange("/api/v1/clientes", HttpMethod.POST, request,
        Object.class);
  }

  @Quando("eu envio uma requisição para cadastrar o cliente com id diferente e conta já cadastrada")
  public void euEnvioUmaRequisicaoParaCadastrarOClienteComIdDiferenteEContaJaCadastrada() {
    HttpHeaders headers = new HttpHeaders();
    var novoClienteDTO = ClienteDTO.builder()
        .idCliente(UUID.randomUUID().toString())
        .nomeCliente(cliente.nomeCliente())
        .numeroConta(cliente.numeroConta())
        .saldoConta(cliente.saldoConta())
        .build();
    HttpEntity<ClienteDTO> request = new HttpEntity<>(novoClienteDTO, headers);
    response = restTemplate.exchange("/api/v1/clientes", HttpMethod.POST, request,
        Object.class);
  }

  @Quando("consulto um cliente pela conta {string}")
  public void euEnvioUmaRequisicaoParaCadastrarOCliente(String numeroConta) {
    HttpHeaders headers = new HttpHeaders();
    HttpEntity<ClienteDTO> request = new HttpEntity<>(headers);
    response = restTemplate.exchange(
        "/api/v1/clientes?numeroConta=" + tratarCampoString(numeroConta), HttpMethod.GET,
        request, Object.class);
  }

  @Quando("consulto todos os clientes")
  public void euEnvioUmaRequisicaoParaConsultarTodosOsClientes() {
    HttpHeaders headers = new HttpHeaders();
    HttpEntity<ClienteDTO> request = new HttpEntity<>(headers);
    response = restTemplate.exchange("/api/v1/clientes", HttpMethod.GET, request, Object.class);
  }

  @Entao("o cliente deve ser cadastrado com sucesso")
  public void oClienteDeveSerCadastradoComSucesso() {
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Entao("o cliente deve ser encontrado com sucesso")
  @Entao("os clientes devem ser encontrados com sucesso")
  public void oClienteDeveSerEncontradoComSucesso() {
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Entao("a resposta deve conter todos os clientes cadastrados")
  public void aRespostaDeveConterTodosOsClientesCadastrados() {
    assertNotNull(response.getBody());
    List<ClienteDTO> responseBody = objectMapper
        .convertValue(response.getBody(), List.class).stream()
        .map(obj -> objectMapper.convertValue(obj, ClienteDTO.class)).toList();
    clientesCadastrados
        .forEach(expected -> {
          var item = responseBody.stream()
              .filter(responseItem -> expected.idCliente().equals(responseItem.idCliente()))
              .findFirst().orElse(null);
          assertNotNull(item);
          assertEquals(expected.idCliente(), item.idCliente());
          assertEquals(expected.nomeCliente(), item.nomeCliente());
          assertEquals(expected.numeroConta(), item.numeroConta());
          assertEquals(0, expected.saldoConta().compareTo(item.saldoConta()));
        });
  }

  @Entao("o cliente não deve ser cadastrado")
  @Entao("o cliente NÃO deve ser encontrado")
  public void oClienteNaoDeveSerCadastrado() {
    assertNotEquals(HttpStatus.OK, response.getStatusCode());
    assertNotEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Entao("a resposta deve conter os dados do cliente cadastrado")
  public void aRespostaDeveConterOsDadosDoClienteCadastrado() {
    assertNotNull(response.getBody());
    var responseBody = objectMapper.convertValue(response.getBody(), ClienteDTO.class);
    assertEquals(cliente.idCliente(), responseBody.idCliente());
    assertEquals(cliente.nomeCliente(), responseBody.nomeCliente());
    assertEquals(cliente.numeroConta(), responseBody.numeroConta());
    assertEquals(0, responseBody.saldoConta().compareTo(cliente.saldoConta()));
  }

  @Entao("a resposta deve conter o erro {string} e mensagem {string}")
  public void aRespostaDeveConterAMensagem(String erro, String mensagem) {
    assertEquals(HttpStatus.valueOf(erro), response.getStatusCode());
    assertNotNull(response.getBody());
    var responseBody = objectMapper.convertValue(response.getBody(), ErrorResponseDTO.class);
    assertEquals(erro, responseBody.code());
    assertEquals(mensagem, responseBody.message());
  }

  @Entao("a resposta deve conter a mensagem de erro {string} e informar o campo {string} como inválido")
  public void aRespostaDeveConterAMensagemEInformarOCampoComoInvalido(String mensagem,
      String campoInvalido) {
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    var responseBody = objectMapper.convertValue(response.getBody(), ErrorResponseDTO.class);
    assertEquals("BAD_REQUEST", responseBody.code());
    assertEquals(mensagem, responseBody.message());
    assertEquals(1, responseBody.errors().size());
    assertEquals(campoInvalido, responseBody.errors().getFirst().field());
  }

  private String tratarCampoString(String campo) {
    return "null".equalsIgnoreCase(campo) ? null : "empty".equalsIgnoreCase(campo) ? "" : campo;
  }
}