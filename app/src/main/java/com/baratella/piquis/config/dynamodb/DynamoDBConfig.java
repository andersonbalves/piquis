package com.baratella.piquis.config.dynamodb;


import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DynamoDBConfig {

  @Value("${aws.accessKeyId:accessKeyId}")
  private String accessKeyId;
  @Value("${aws.secretAccessKey:secretAccessKey}")
  private String secretAccessKey;
  @Value("${aws.region:us-east-1}")
  private String region;
  @Value("${aws.dynamodb.endpoint:http://localhost:8000}")
  private String dynamodbEndpoint;
  @Value("${aws.dynamodb.table:transferencias}")
  private String tableName;

  @PostConstruct
  public void createDynamoDBServer() throws Exception {
    System.setProperty("sqlite4java.library.path", "target/dependencies");
    String[] localArgs = {"-inMemory", "-sharedDb"};
    DynamoDBProxyServer server = ServerRunner.createServerFromCommandLineArgs(localArgs);
    server.start();
  }

  @Bean
  @SneakyThrows
  public DynamoDbClient dynamoDB() {
    var builder = DynamoDbClient
        .builder()
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKeyId, secretAccessKey)));

    builder.region(Region.of(region)).endpointOverride(URI.create(dynamodbEndpoint));
    log.info("DynamoDB Client initialized in region {} with ENDPOINT {}", region, dynamodbEndpoint);

    DynamoDbClient client = builder.build();
    this.createTable(client);
    log.info("Table {} created", tableName);
    return client;
  }

  @SneakyThrows
  private void createTable(DynamoDbClient client) {
    var request = CreateTableRequest.builder()
        .tableName(tableName)
        .keySchema(
            KeySchemaElement.builder().attributeName("contaOrigem").keyType(KeyType.HASH).build(),
            KeySchemaElement.builder().attributeName("dataHora").keyType(KeyType.RANGE).build())
        .attributeDefinitions(
            AttributeDefinition.builder().attributeName("contaOrigem").attributeType(
                ScalarAttributeType.S).build(),
            AttributeDefinition.builder().attributeName("dataHora")
                .attributeType(ScalarAttributeType.S).build(),
            AttributeDefinition.builder().attributeName("contaDestino")
                .attributeType(ScalarAttributeType.S).build())
        .provisionedThroughput(
            ProvisionedThroughput.builder()
                .readCapacityUnits(10L)
                .writeCapacityUnits(10L).build())
        .globalSecondaryIndexes(
            GlobalSecondaryIndex.builder()
                .indexName("contaDestino-dataHora-index")
                .keySchema(
                    KeySchemaElement.builder()
                        .attributeName("contaDestino").keyType(KeyType.HASH).build(),
                    KeySchemaElement.builder()
                        .attributeName("dataHora").keyType(KeyType.RANGE).build())
                .projection(
                    Projection.builder()
                        .projectionType(ProjectionType.ALL).build())
                .provisionedThroughput(
                    ProvisionedThroughput.builder()
                        .readCapacityUnits(10L)
                        .writeCapacityUnits(10L).build())
                .build())
        .build();
    client.createTable(request);
  }
}