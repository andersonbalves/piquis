package com.baratella.piquis.adapter.dynamodb.transferencia;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import com.baratella.piquis.dto.ComprovanteTransferenciaDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.random.RandomGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@RunWith(MockitoJUnitRunner.class)
class HistoricoTransferenciaImplTest {

  private static final PodamFactory podam = new PodamFactoryImpl();
  private static final RandomGenerator randomGenerator = new Random();
  private static final String tableName = UUID.randomUUID().toString();

  @Mock
  private DynamoDbClient dynamoDBClient;

  @InjectMocks
  private HistoricoTransferenciaImpl historicoTransferencia;

  @BeforeEach
  void setUp() {
    openMocks(this);
    ReflectionTestUtils.setField(historicoTransferencia, "tableName", tableName);
  }

  @Test
  void salvarTransferencia_sucesso() {
    var transferencia = podam.manufacturePojo(ComprovanteTransferenciaDTO.class);
    var expected = new HashMap<String, AttributeValue>();

    expected.put("contaOrigem", AttributeValue.builder().s(transferencia.contaOrigem()).build());
    expected.put("contaDestino", AttributeValue.builder().s(transferencia.contaDestino()).build());
    expected.put("comprovante", AttributeValue.builder().s(transferencia.comprovante()).build());
    expected.put("valor",
        AttributeValue.builder().n(transferencia.valorTransferido().toString()).build());
    expected.put("dataHora",
        AttributeValue.builder().s(transferencia.dataHoraTransferencia().toString()).build());

    ArgumentCaptor<PutItemRequest> captor = ArgumentCaptor.forClass(PutItemRequest.class);

    historicoTransferencia.salvarTransferencia(transferencia);

    verify(dynamoDBClient).putItem(captor.capture());

    assertEquals(tableName, captor.getValue().tableName());
    assertThat(captor.getValue().item()).containsAllEntriesOf(expected);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 5, 10})
  void listarTransferencias_sucesso(int quantidade) {
    String numeroConta = UUID.randomUUID().toString();
    var itemsOrigem = new ArrayList<Map<String, AttributeValue>>();
    var itemsDestino = new ArrayList<Map<String, AttributeValue>>();
    var transferenciasOrigem = new ArrayList<ComprovanteTransferenciaDTO>();
    var transferenciasDestino = new ArrayList<ComprovanteTransferenciaDTO>();

    for (int i = 0; i < quantidade; i++) {
      var transferenciaOrigem = ComprovanteTransferenciaDTO.builder()
          .contaOrigem(numeroConta)
          .contaDestino("destino-" + i)
          .valorTransferido(BigDecimal.valueOf(randomGenerator.nextDouble(100, 999)))
          .dataHoraTransferencia(LocalDateTime.now())
          .comprovante(UUID.randomUUID().toString())
          .build();
      transferenciasDestino.add(transferenciaOrigem);
      var transferenciaDestino = ComprovanteTransferenciaDTO.builder()
          .contaOrigem("origem-" + i)
          .contaDestino(numeroConta)
          .valorTransferido(BigDecimal.valueOf(randomGenerator.nextDouble(100, 999)))
          .dataHoraTransferencia(LocalDateTime.now())
          .comprovante(UUID.randomUUID().toString())
          .build();
      transferenciasOrigem.add(transferenciaDestino);

      itemsOrigem.add(Map.of(
          "contaOrigem", AttributeValue.builder().s(transferenciaOrigem.contaOrigem()).build(),
          "contaDestino", AttributeValue.builder().s(transferenciaOrigem.contaDestino()).build(),
          "valor", AttributeValue.builder().n(transferenciaOrigem.valorTransferido().toString())
              .build(),
          "dataHora",
          AttributeValue.builder().s(transferenciaOrigem.dataHoraTransferencia().toString())
              .build(),
          "comprovante", AttributeValue.builder().s(transferenciaOrigem.comprovante()).build()
      ));

      itemsDestino.add(Map.of(
          "contaOrigem", AttributeValue.builder().s(transferenciaDestino.contaOrigem()).build(),
          "contaDestino", AttributeValue.builder().s(transferenciaDestino.contaDestino()).build(),
          "valor", AttributeValue.builder().n(transferenciaDestino.valorTransferido().toString())
              .build(),
          "dataHora",
          AttributeValue.builder().s(transferenciaDestino.dataHoraTransferencia().toString())
              .build(),
          "comprovante", AttributeValue.builder().s(transferenciaDestino.comprovante()).build()
      ));
    }

    QueryResponse responseOrigem = QueryResponse.builder().items(itemsOrigem).build();
    QueryResponse responseDestino = QueryResponse.builder().items(itemsDestino).build();

    doAnswer(invocation -> invocation.toString()
        .contains("contaDestino = :conta") ? responseOrigem : responseDestino)
        .when(dynamoDBClient).query(any(QueryRequest.class));

    var result = historicoTransferencia.listarTransferencias(numeroConta);

    assertTrue(result.isPresent());
    assertEquals(quantidade * 2, result.get().size());
    verify(dynamoDBClient, times(2)).query(any(QueryRequest.class));

    transferenciasOrigem.forEach(expected -> {
      var actual = result.get().stream()
          .filter(dto ->
              dto.comprovante().equals(expected.comprovante())
                  && dto.dataHoraTransferencia().equals(expected.dataHoraTransferencia())
          ).findFirst().orElseGet(() -> fail("Transferência não encontrada"));
      assertEquals(expected.contaOrigem(), actual.contaOrigem());
      assertEquals(expected.contaDestino(), actual.contaDestino());
      assertThat(actual.valorTransferido()).isEqualTo(expected.valorTransferido().negate());
    });
    transferenciasDestino.forEach(expected -> {
      var actual = result.get().stream()
          .filter(dto ->
              dto.comprovante().equals(expected.comprovante())
                  && dto.dataHoraTransferencia().equals(expected.dataHoraTransferencia())
          ).findFirst().orElseGet(() -> fail("Transferência não encontrada"));
      assertEquals(expected.contaOrigem(), actual.contaOrigem());
      assertEquals(expected.contaDestino(), actual.contaDestino());
      assertThat(actual.valorTransferido()).isEqualTo(expected.valorTransferido());
    });
  }

  @Test
  void listarTransferencias_semTransferencias() {
    String numeroConta = UUID.randomUUID().toString();

    QueryResponse emptyResponse = QueryResponse.builder().items(new ArrayList<>()).build();

    doAnswer(invocation -> emptyResponse)
        .when(dynamoDBClient).query(any(QueryRequest.class));

    Optional<List<ComprovanteTransferenciaDTO>> result = historicoTransferencia.listarTransferencias(
        numeroConta);

    assertTrue(result.isPresent());
    assertTrue(result.get().isEmpty());
    verify(dynamoDBClient, times(2)).query(any(QueryRequest.class));
  }
}
