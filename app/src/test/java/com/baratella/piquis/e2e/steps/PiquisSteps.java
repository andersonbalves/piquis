package com.baratella.piquis.e2e.steps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.baratella.piquis.PiquisApplication;
import com.baratella.piquis.dto.ClienteDTO;
import com.baratella.piquis.dto.ComprovanteTransferenciaDTO;
import com.baratella.piquis.dto.ErrorResponseDTO;
import com.baratella.piquis.dto.TransferenciaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.annotation.PostConstruct;
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
public class PiquisSteps {

  private final ObjectMapper objectMapper = new ObjectMapper();
  @Autowired
  private TestRestTemplate restTemplate;

  private ClienteDTO cliente;
  private List<ClienteDTO> clientesCadastrados;
  private ResponseEntity<Object> response;

  private TransferenciaDTO transferencia;
  private List<ComprovanteTransferenciaDTO> transferenciasEfetuadas;
  private String contaConsultaTransferencia;

  @PostConstruct
  public void init() {
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Dado("que eu tenho os dados do cliente")
  @Dado("que eu tenho os dados do cliente da conta de origem")
  @Dado("que eu tenho os dados do cliente da conta de destino")
  public void queEuTenhoOsDadosDoCliente(DataTable dataTable) {
    Map<String, String> data = dataTable.asMaps().getFirst();
    cliente = ClienteDTO.builder().idCliente(tratarCampoString(data.get("idCliente")))
        .nomeCliente(tratarCampoString(data.get("nomeCliente")))
        .numeroConta(tratarCampoString(data.get("numeroConta"))).saldoConta(
            "null".equals(data.get("saldoConta")) ? null
                : BigDecimal.valueOf(Double.parseDouble(data.get("saldoConta")))).build();
  }

  @Dado("que o cliente NÃO está cadastrado")
  public void queOClienteNaoEstaCadastrado() {
    log.debug("Cliente não está cadastrado");
  }

  @Dado("que tenho clientes cadastrados")
  public void queTenhoClientesCadastrados() {
    HttpHeaders headers = new HttpHeaders();
    clientesCadastrados = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      ClienteDTO clienteRequest = ClienteDTO.builder().idCliente(UUID.randomUUID().toString())
          .nomeCliente("Cliente " + i).numeroConta("1234567890" + i)
          .saldoConta(BigDecimal.valueOf(1000)).build();
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
    response = restTemplate.exchange("/api/v1/clientes", HttpMethod.POST, request, Object.class);
  }

  @Quando("eu envio uma requisição para cadastrar o cliente com id diferente e conta já cadastrada")
  public void euEnvioUmaRequisicaoParaCadastrarOClienteComIdDiferenteEContaJaCadastrada() {
    HttpHeaders headers = new HttpHeaders();
    var novoClienteDTO = ClienteDTO.builder().idCliente(UUID.randomUUID().toString())
        .nomeCliente(cliente.nomeCliente()).numeroConta(cliente.numeroConta())
        .saldoConta(cliente.saldoConta()).build();
    HttpEntity<ClienteDTO> request = new HttpEntity<>(novoClienteDTO, headers);
    response = restTemplate.exchange("/api/v1/clientes", HttpMethod.POST, request, Object.class);
  }

  @Quando("consulto um cliente pela conta {string}")
  public void euEnvioUmaRequisicaoParaCadastrarOCliente(String numeroConta) {
    HttpHeaders headers = new HttpHeaders();
    HttpEntity<ClienteDTO> request = new HttpEntity<>(headers);
    response = restTemplate.exchange(
        "/api/v1/clientes?numeroConta=" + tratarCampoString(numeroConta), HttpMethod.GET, request,
        Object.class);
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
  @Entao("a transferência deve ser efetuada com sucesso")
  @Entao("as transferências devem ser encontradas com sucesso")
  public void oClienteDeveSerEncontradoComSucesso() {
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Entao("a resposta deve conter todos os clientes cadastrados")
  public void aRespostaDeveConterTodosOsClientesCadastrados() {
    assertNotNull(response.getBody());
    List<ClienteDTO> responseBody = objectMapper.convertValue(response.getBody(), List.class)
        .stream().map(obj -> objectMapper.convertValue(obj, ClienteDTO.class)).toList();
    clientesCadastrados.forEach(expected -> {
      var item = responseBody.stream()
          .filter(responseItem -> expected.idCliente().equals(responseItem.idCliente())).findFirst()
          .orElse(null);
      assertNotNull(item, String.format("Cliente não encontrado: %s", expected));
      assertEquals(expected.idCliente(), item.idCliente());
      assertEquals(expected.nomeCliente(), item.nomeCliente());
      assertEquals(expected.numeroConta(), item.numeroConta());
      assertThat(expected.saldoConta())
          .usingComparator(BigDecimal::compareTo)
          .isEqualTo(item.saldoConta());
    });
  }

  @Entao("o cliente não deve ser cadastrado")
  @Entao("o cliente NÃO deve ser encontrado")
  @Entao("a transferência NÃO deve ser efetuada com sucesso")
  @Entao("as transferências NÃO devem ser encontradas")
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
    assertThat(cliente.saldoConta())
        .usingComparator(BigDecimal::compareTo)
        .isEqualTo(responseBody.saldoConta());
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

  @Dado("que eu tenho os dados da transferência")
  public void queEuTenhoOsDadosDaTransferencia(DataTable dataTable) {
    Map<String, String> data = dataTable.asMaps().get(0);
    transferencia = TransferenciaDTO.builder()
        .contaOrigem(tratarCampoString(data.get("contaOrigem")))
        .contaDestino(tratarCampoString(data.get("contaDestino"))).valor(
            "null".equals(data.get("valor")) ? null
                : BigDecimal.valueOf(Double.parseDouble(data.get("valor")))).build();
  }


  @Quando("eu envio uma requisição para efetuar a transferência")
  public void euEnvioUmaRequisicaoParaEfetuarATransferencia() {
    HttpHeaders headers = new HttpHeaders();
    HttpEntity<TransferenciaDTO> request = new HttpEntity<>(transferencia, headers);
    response = restTemplate.exchange("/api/v1/transferencias", HttpMethod.POST, request,
        Object.class);
  }

  @Dado("que as transferências foram realizadas")
  public void aTransferenciaFoiEfetuada() {
    transferenciasEfetuadas = new ArrayList<>();
    HttpHeaders headers = new HttpHeaders();

    for (int i = 0; i < 5; i++) {
      var requestBody = (i % 2 == 0) ? transferencia
          : TransferenciaDTO.builder().contaOrigem(transferencia.contaDestino())
              .contaDestino(transferencia.contaOrigem()).valor(transferencia.valor()).build();
      HttpEntity<TransferenciaDTO> request = new HttpEntity<>(requestBody, headers);
      var res = restTemplate.exchange("/api/v1/transferencias", HttpMethod.POST, request,
          ComprovanteTransferenciaDTO.class);
      transferenciasEfetuadas.add(res.getBody());
    }
  }

  @Quando("consulto as transferências da conta {string}")
  public void consultoAsTransferenciasDaConta(String numeroConta) {
    contaConsultaTransferencia = tratarCampoString(numeroConta);
    HttpHeaders headers = new HttpHeaders();
    HttpEntity<Void> request = new HttpEntity<>(headers);
    response = restTemplate.exchange(
        "/api/v1/transferencias?numeroConta=" + contaConsultaTransferencia, HttpMethod.GET, request,
        Object.class);
  }


  @Entao("a resposta deve conter os dados da transferência")
  public void aRespostaDeveConterOsDadosDaTransferencia() {
    assertNotNull(response.getBody());
    var responseBody = objectMapper.convertValue(response.getBody(),
        ComprovanteTransferenciaDTO.class);
    assertEquals(transferencia.contaOrigem(), responseBody.contaOrigem());
    assertEquals(transferencia.contaDestino(), responseBody.contaDestino());
    assertThat(transferencia.valor())
        .usingComparator(BigDecimal::compareTo)
        .isEqualTo(responseBody.valorTransferido());
  }


  @Entao("a resposta deve conter os dados de todas as transferências")
  public void aRespostaDeveConterOsDadosDasTransferencias() {
    assertNotNull(response.getBody());
    List<ComprovanteTransferenciaDTO> responseBody = objectMapper.convertValue(response.getBody(),
            List.class).stream()
        .map(obj -> objectMapper.convertValue(obj, ComprovanteTransferenciaDTO.class)).toList();
    assertNotNull(responseBody);

    transferenciasEfetuadas.forEach(expected -> {
      var actual = responseBody.stream().filter(
              responseItem -> expected.comprovante().equals(responseItem.comprovante())
                  && expected.dataHoraTransferencia().equals(responseItem.dataHoraTransferencia()))
          .findFirst().orElse(null);
      assertNotNull(actual, String.format("Transferência não encontrada: %s", expected));
      assertEquals(expected.contaOrigem(), actual.contaOrigem());
      assertEquals(expected.contaDestino(), actual.contaDestino());
      assertThat(expected.valorTransferido())
          .usingComparator(BigDecimal::compareTo)
          .isEqualTo(
              actual.contaDestino().equals(contaConsultaTransferencia) ? actual.valorTransferido()
                  : actual.valorTransferido().negate());
      assertEquals(expected.comprovante(), actual.comprovante());
      assertEquals(expected.dataHoraTransferencia(), actual.dataHoraTransferencia());
    });
  }

  @Entao("a resposta deve ser uma lista vazia")
  public void aRespostaDeveSerUmaListaVazia() {
    assertNotNull(response.getBody());
    List<ComprovanteTransferenciaDTO> responseBody = objectMapper.convertValue(response.getBody(),
            List.class).stream()
        .map(obj -> objectMapper.convertValue(obj, ComprovanteTransferenciaDTO.class)).toList();
    assertNotNull(responseBody);
    assertEquals(0, responseBody.size());
  }

  private String tratarCampoString(String campo) {
    return "null".equalsIgnoreCase(campo) ? null : "empty".equalsIgnoreCase(campo) ? "" : campo;
  }

}