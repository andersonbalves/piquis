package com.baratella.piquis.adapter.dynamodb.transferencia;


import com.baratella.piquis.dto.ComprovanteTransferenciaDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

@Component
@RequiredArgsConstructor
public class HistoricoTransferenciaImpl implements HistoricoTransferencia {

  @Value("${aws.dynamodb.table:transferencias}")
  private String tableName;

  private final DynamoDbClient dynamoDBClient;

  @Override
  public void salvarTransferencia(ComprovanteTransferenciaDTO transferencia) {
    var itemValues = new HashMap<String, AttributeValue>();

    itemValues.put("contaOrigem", AttributeValue.builder().s(transferencia.contaOrigem()).build());
    itemValues.put("contaDestino",
        AttributeValue.builder().s(transferencia.contaDestino()).build());
    itemValues.put("valor",
        AttributeValue.builder().n(transferencia.valorTransferido().toString()).build());
    itemValues.put("dataHora",
        AttributeValue.builder().s(transferencia.dataHoraTransferencia().toString()).build());
    itemValues.put("comprovante", AttributeValue.builder().s(transferencia.comprovante()).build());

    PutItemRequest request = PutItemRequest.builder()
        .tableName(tableName)
        .item(itemValues)
        .build();

    dynamoDBClient.putItem(request);
  }

  public Optional<List<ComprovanteTransferenciaDTO>> listarTransferencias(String numeroConta) {
    var queryRequestOrigem = QueryRequest.builder()
        .tableName(tableName)
        .keyConditionExpression("contaOrigem = :conta")
        .expressionAttributeValues(
            Map.of(":conta", AttributeValue.builder().s(numeroConta).build()))
        .scanIndexForward(false)
        .build();

    var queryRequestDestino = QueryRequest.builder()
        .tableName(tableName)
        .indexName("contaDestino-dataHora-index")
        .keyConditionExpression("contaDestino = :conta")
        .expressionAttributeValues(
            Map.of(":conta", AttributeValue.builder().s(numeroConta).build()))
        .scanIndexForward(false)
        .build();

    var resultOrigem = dynamoDBClient.query(queryRequestOrigem);
    var resultDestino = dynamoDBClient.query(queryRequestDestino);

    var transferencias = new ArrayList<ComprovanteTransferenciaDTO>();

    resultOrigem.items().forEach(item -> transferencias.add(mapToDTO(item, true)));
    resultDestino.items().forEach(item -> transferencias.add(mapToDTO(item, false)));

    transferencias.sort(
        Comparator.comparing(ComprovanteTransferenciaDTO::dataHoraTransferencia).reversed());

    return Optional.of(transferencias);
  }

  private ComprovanteTransferenciaDTO mapToDTO(Map<String, AttributeValue> item, boolean isOrigem) {
    BigDecimal valor = new BigDecimal(item.get("valor").n());
    return ComprovanteTransferenciaDTO.builder()
        .contaOrigem(item.get("contaOrigem").s())
        .contaDestino(item.get("contaDestino").s())
        .valorTransferido(isOrigem ? valor.negate() : valor)
        .dataHoraTransferencia(LocalDateTime.parse(item.get("dataHora").s()))
        .comprovante(item.get("comprovante").s())
        .build();
  }

}
