package com.baratella.piquis.service.transferencia;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import com.baratella.piquis.adapter.dynamodb.transferencia.HistoricoTransferencia;
import com.baratella.piquis.dto.ComprovanteTransferenciaDTO;
import com.baratella.piquis.dto.TransferenciaDTO;
import com.baratella.piquis.exception.InvalidParameterException;
import com.baratella.piquis.exception.NotFoundException;
import com.baratella.piquis.model.Cliente;
import com.baratella.piquis.repository.ClienteRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@RunWith(MockitoJUnitRunner.class)
class TransferenciaServiceImplTest {

  private static final PodamFactory podam = new PodamFactoryImpl();
  private static final RandomGenerator randomGenerator = new Random();

  @Mock
  private ClienteRepository clienteRepository;

  @Mock
  private HistoricoTransferencia historicoTransferencia;

  @InjectMocks
  private TransferenciaServiceImpl transferenciaService;

  @BeforeEach
  void setUp() {
    openMocks(this);
    ReflectionTestUtils.setField(transferenciaService, "minTransferencia", 0);
    ReflectionTestUtils.setField(transferenciaService, "maxTransferencia", 10000);
  }

  @ParameterizedTest
  @MethodSource("provideTransferencias")
  void efetuarTransferencia_sucesso(
      BigDecimal saldoOrigem, BigDecimal saldoDestino, BigDecimal valorTransferencia,
      BigDecimal saldoEsperadoOrigem, BigDecimal saldoEsperadoDestino) {

    var contaOrigem = Cliente.builder()
        .idCliente(UUID.randomUUID().toString())
        .nomeCliente("Origem")
        .numeroConta(String.valueOf(randomGenerator.nextInt(100000000, 999999999)))
        .saldo(saldoOrigem)
        .build();
    var contaDestino = Cliente.builder()
        .idCliente(UUID.randomUUID().toString())
        .nomeCliente("Destino")
        .numeroConta(String.valueOf(randomGenerator.nextInt(100000000, 999999999)))
        .saldo(saldoDestino)
        .build();
    var transferencia = TransferenciaDTO.builder()
        .contaOrigem(contaOrigem.getNumeroConta())
        .contaDestino(contaDestino.getNumeroConta())
        .valor(valorTransferencia)
        .build();

    doAnswer(invocation -> Optional.of(contaOrigem))
        .when(clienteRepository).findByNumeroConta(contaOrigem.getNumeroConta());
    doAnswer(invocation -> Optional.of(contaDestino))
        .when(clienteRepository).findByNumeroConta(contaDestino.getNumeroConta());

    var clienteCaptor = ArgumentCaptor.forClass(Cliente.class);
    var comprovanteCaptor = ArgumentCaptor.forClass(ComprovanteTransferenciaDTO.class);

    ComprovanteTransferenciaDTO result = transferenciaService.efetuarTransferencia(transferencia);

    assertEquals(contaOrigem.getNumeroConta(), result.contaOrigem());
    assertEquals(contaDestino.getNumeroConta(), result.contaDestino());
    assertThat(result.valorTransferido())
        .usingComparator(BigDecimal::compareTo)
        .isEqualTo(valorTransferencia);

    verify(clienteRepository).findByNumeroConta(contaOrigem.getNumeroConta());
    verify(clienteRepository).findByNumeroConta(contaDestino.getNumeroConta());

    verify(clienteRepository, times(2)).save(clienteCaptor.capture());
    Cliente capturedOrigem = clienteCaptor.getAllValues().get(0);
    Cliente capturedDestino = clienteCaptor.getAllValues().get(1);

    assertEquals(contaOrigem.getIdCliente(), capturedOrigem.getIdCliente());
    assertEquals(contaOrigem.getNomeCliente(), capturedOrigem.getNomeCliente());
    assertEquals(contaOrigem.getNumeroConta(), capturedOrigem.getNumeroConta());
    assertThat(capturedOrigem.getSaldo())
        .usingComparator(BigDecimal::compareTo)
        .isEqualTo(saldoEsperadoOrigem);

    assertEquals(contaDestino.getIdCliente(), capturedDestino.getIdCliente());
    assertEquals(contaDestino.getNomeCliente(), capturedDestino.getNomeCliente());
    assertEquals(contaDestino.getNumeroConta(), capturedDestino.getNumeroConta());
    assertThat(capturedDestino.getSaldo())
        .usingComparator(BigDecimal::compareTo)
        .isEqualTo(saldoEsperadoDestino);

    verify(historicoTransferencia).salvarTransferencia(comprovanteCaptor.capture());
    ComprovanteTransferenciaDTO capturedComprovante = comprovanteCaptor.getValue();
    assertEquals(result.dataHoraTransferencia(), capturedComprovante.dataHoraTransferencia());
    assertEquals(result.comprovante(), capturedComprovante.comprovante());
    assertEquals(result.contaOrigem(), capturedComprovante.contaOrigem());
    assertEquals(result.contaDestino(), capturedComprovante.contaDestino());
  }

  private static Stream<Arguments> provideTransferencias() {
    return Stream.of(
        Arguments.of(
            BigDecimal.valueOf(1000.55), BigDecimal.valueOf(800), BigDecimal.valueOf(100.55),
            BigDecimal.valueOf(900), BigDecimal.valueOf(900.55)),
        Arguments.of(
            BigDecimal.valueOf(1000), BigDecimal.valueOf(800), BigDecimal.valueOf(250.5),
            BigDecimal.valueOf(749.5), BigDecimal.valueOf(1050.5))
    );
  }

  @Test
  void efetuarTransferencia_contaOrigemNaoEncontrada() {
    var transferencia = TransferenciaDTO.builder()
        .contaOrigem(String.valueOf(randomGenerator.nextInt(100000000, 999999999)))
        .contaDestino(String.valueOf(randomGenerator.nextInt(100000000, 999999999)))
        .valor(BigDecimal.valueOf(randomGenerator.nextDouble(1, 999)))
        .build();

    doReturn(Optional.empty())
        .when(clienteRepository).findByNumeroConta(transferencia.contaOrigem());

    assertThrows(NotFoundException.class,
        () -> transferenciaService.efetuarTransferencia(transferencia)
        , "Conta de origem não encontrada."
    );
  }

  @Test
  void efetuarTransferencia_contaDestinoNaoEncontrada() {
    var contaOrigem = Cliente.builder()
        .idCliente(UUID.randomUUID().toString())
        .nomeCliente("Origem")
        .numeroConta(String.valueOf(randomGenerator.nextInt(100000000, 999999999)))
        .saldo(BigDecimal.valueOf(randomGenerator.nextDouble(1000, 9999)))
        .build();
    var numeroContaDestino = String.valueOf(randomGenerator.nextInt(100000000, 999999999));

    var transferencia = TransferenciaDTO.builder()
        .contaOrigem(contaOrigem.getNumeroConta())
        .contaDestino(numeroContaDestino)
        .valor(BigDecimal.valueOf(randomGenerator.nextDouble(1, 999)))
        .build();

    doReturn(Optional.of(contaOrigem))
        .when(clienteRepository).findByNumeroConta(contaOrigem.getNumeroConta());
    doReturn(Optional.empty())
        .when(clienteRepository).findByNumeroConta(numeroContaDestino);

    assertThrows(NotFoundException.class,
        () -> transferenciaService.efetuarTransferencia(transferencia),
        "Conta de destino não encontrada."
    );
  }

  @ParameterizedTest
  @ValueSource(doubles = {-1, -999, 10001, 99999})
  void efetuarTransferencia_valorInvalido(double valor) {
    var contaOrigem = Cliente.builder()
        .idCliente(UUID.randomUUID().toString())
        .nomeCliente("Origem")
        .numeroConta(String.valueOf(randomGenerator.nextInt(100000000, 999999999)))
        .saldo(BigDecimal.valueOf(randomGenerator.nextDouble(1000, 9999)))
        .build();

    var contaDestino = Cliente.builder()
        .idCliente(UUID.randomUUID().toString())
        .nomeCliente("Destino")
        .numeroConta(String.valueOf(randomGenerator.nextInt(100000000, 999999999)))
        .saldo(BigDecimal.valueOf(randomGenerator.nextDouble(1000, 9999)))
        .build();

    var transferencia = TransferenciaDTO.builder()
        .contaOrigem(contaOrigem.getNumeroConta())
        .contaDestino(contaDestino.getNumeroConta())
        .valor(BigDecimal.valueOf(valor))
        .build();
    doReturn(Optional.of(contaOrigem))
        .when(clienteRepository).findByNumeroConta(contaOrigem.getNumeroConta());
    doReturn(Optional.of(contaDestino))
        .when(clienteRepository).findByNumeroConta(contaDestino.getNumeroConta());

    assertThrows(InvalidParameterException.class,
        () -> transferenciaService.efetuarTransferencia(transferencia));
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 5, 10})
  void listarTransferencias_contaEncontrada(int quantidade) {
    String numeroConta = UUID.randomUUID().toString();
    var transferencias = new ArrayList<ComprovanteTransferenciaDTO>();
    var cliente = Cliente.builder()
        .idCliente(UUID.randomUUID().toString())
        .nomeCliente("Cliente")
        .numeroConta(numeroConta)
        .saldo(BigDecimal.valueOf(1000))
        .build();

    for (int i = 0; i < quantidade; i++) {
      transferencias.add(podam.manufacturePojo(ComprovanteTransferenciaDTO.class));
    }
    doAnswer(invocation -> Optional.of(cliente))
        .when(clienteRepository).findByNumeroConta(numeroConta);
    doAnswer(invocation -> Optional.of(transferencias))
        .when(historicoTransferencia).listarTransferencias(numeroConta);

    List<ComprovanteTransferenciaDTO> result = transferenciaService.listarTransferencias(
        numeroConta);

    assertEquals(quantidade, result.size());

    assertThat(result)
        .containsExactlyInAnyOrderElementsOf(transferencias);

    verify(clienteRepository).findByNumeroConta(numeroConta);
    verify(historicoTransferencia).listarTransferencias(numeroConta);
  }

  @Test
  void listarTransferencias_contaNaoEncontrada() {
    String numeroConta = UUID.randomUUID().toString();

    doReturn(Optional.empty())
        .when(clienteRepository).findByNumeroConta(numeroConta);

    assertThrows(NotFoundException.class,
        () -> transferenciaService.listarTransferencias(numeroConta),
        "Conta não encontrada.");

    verify(clienteRepository).findByNumeroConta(numeroConta);
  }

  @Test
  void listarTransferencias_semTransferencias() {
    String numeroConta = UUID.randomUUID().toString();
    var cliente = Cliente.builder()
        .idCliente(UUID.randomUUID().toString())
        .nomeCliente("Cliente")
        .numeroConta(numeroConta)
        .saldo(BigDecimal.valueOf(randomGenerator.nextDouble(1000, 9999)))
        .build();
    doAnswer(invocation -> Optional.of(cliente))
        .when(clienteRepository).findByNumeroConta(numeroConta);
    doAnswer(invocation -> Optional.of(Collections.emptyList()))
        .when(historicoTransferencia).listarTransferencias(numeroConta);

    var result = transferenciaService.listarTransferencias(numeroConta);

    assertTrue(result.isEmpty());

    verify(clienteRepository).findByNumeroConta(numeroConta);
    verify(historicoTransferencia).listarTransferencias(numeroConta);
  }

}