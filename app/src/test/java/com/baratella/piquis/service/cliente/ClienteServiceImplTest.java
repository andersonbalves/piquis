package com.baratella.piquis.service.cliente;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import com.baratella.piquis.dto.ClienteDTO;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@RunWith(MockitoJUnitRunner.class)
class ClienteServiceImplTest {

  private static final PodamFactory podam = new PodamFactoryImpl();
  private static final RandomGenerator randomGenerator = new Random();

  @Mock
  private ClienteRepository clienteRepository;

  @InjectMocks
  private ClienteServiceImpl clienteService;

  @BeforeEach
  void setUp() {
    openMocks(this);
  }

  @Test
  void cadastrarCliente_sucesso() {
    var request = podam.manufacturePojo(ClienteDTO.class);

    var cliente = Cliente.builder()
        .idCliente(UUID.randomUUID().toString())
        .nomeCliente(UUID.randomUUID().toString())
        .numeroConta(String.valueOf(randomGenerator.nextInt(100000000, 999999999)))
        .saldo(BigDecimal.valueOf(randomGenerator.nextDouble(1000, 9999)))
        .build();

    doReturn(false)
        .when(clienteRepository).existsById(request.idCliente());
    doAnswer(invocation -> cliente)
        .when(clienteRepository).save(any());

    var result = clienteService.cadastrarCliente(request);
    ArgumentCaptor<Cliente> clienteCaptor = forClass(Cliente.class);

    assertNotNull(result);
    assertEquals(cliente.getIdCliente(), result.idCliente());
    assertEquals(cliente.getNomeCliente(), result.nomeCliente());
    assertEquals(cliente.getNumeroConta(), result.numeroConta());
    assertEquals(cliente.getSaldo(), result.saldoConta());

    verify(clienteRepository).existsById(request.idCliente());
    verify(clienteRepository).save(clienteCaptor.capture());

    assertEquals(request.idCliente(), clienteCaptor.getValue().getIdCliente());
    assertEquals(request.nomeCliente(), clienteCaptor.getValue().getNomeCliente());
    assertEquals(request.numeroConta(), clienteCaptor.getValue().getNumeroConta());
    assertEquals(request.saldoConta(), clienteCaptor.getValue().getSaldo());
  }

  @Test
  void cadastrarCliente_clienteJaExiste() {
    ClienteDTO request = podam.manufacturePojo(ClienteDTO.class);

    doReturn(true)
        .when(clienteRepository).existsById(request.idCliente());

    DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class,
        () -> clienteService.cadastrarCliente(request),
        String.format("Cliente com ID %s já existe.", request.idCliente()));

    verify(clienteRepository).existsById(request.idCliente());
    verify(clienteRepository, never()).save(any());
  }

  @Test
  void listarClientes_listaNaoVazia() {
    var expectedClientes = new ArrayList<Cliente>();
    int quantidade = 3;
    for (int i = 0; i < quantidade; i++) {
      expectedClientes.add(
          Cliente.builder()
              .idCliente(UUID.randomUUID().toString())
              .nomeCliente(UUID.randomUUID().toString())
              .numeroConta(String.valueOf(randomGenerator.nextInt(100000000, 999999999)))
              .saldo(BigDecimal.valueOf(randomGenerator.nextDouble(1000, 9999)))
              .build());
    }
    doAnswer(invocation -> expectedClientes)
        .when(clienteRepository).findAll();

    List<ClienteDTO> result = clienteService.listarClientes();

    assertEquals(quantidade, result.size());
    expectedClientes.forEach(cliente -> {
      assertTrue(result.stream()
          .anyMatch(dto -> dto.idCliente().equals(cliente.getIdCliente())
              && dto.nomeCliente().equals(cliente.getNomeCliente())
              && dto.numeroConta().equals(cliente.getNumeroConta())
              && dto.saldoConta().equals(cliente.getSaldo())));
    });

    verify(clienteRepository).findAll();
  }

  @Test
  void listarClientes_listaVazia() {
    doAnswer(invocation -> Collections.emptyList())
        .when(clienteRepository).findAll();

    List<ClienteDTO> result = clienteService.listarClientes();
    assertTrue(result.isEmpty());
    verify(clienteRepository).findAll();
  }

  @Test
  void buscarCliente_clienteEncontrado() {
    var cliente = Cliente.builder()
        .idCliente(UUID.randomUUID().toString())
        .nomeCliente(UUID.randomUUID().toString())
        .numeroConta(String.valueOf(randomGenerator.nextInt(100000000, 999999999)))
        .saldo(BigDecimal.valueOf(randomGenerator.nextDouble(1000, 9999)))
        .build();

    doAnswer(invocation -> Optional.of(cliente))
        .when(clienteRepository).findByNumeroConta(cliente.getNumeroConta());

    ClienteDTO result = clienteService.buscarCliente(cliente.getNumeroConta());

    assertEquals(cliente.getIdCliente(), result.idCliente());
    assertEquals(cliente.getNomeCliente(), result.nomeCliente());
    assertEquals(cliente.getNumeroConta(), result.numeroConta());
    assertEquals(cliente.getSaldo(), result.saldoConta());
    verify(clienteRepository).findByNumeroConta(cliente.getNumeroConta());
  }

  @Test
  void buscarCliente_clienteNaoEncontrado() {
    String numeroConta = UUID.randomUUID().toString();
    doAnswer(invocation -> Optional.empty())
        .when(clienteRepository).findByNumeroConta(numeroConta);

    assertThrows(NotFoundException.class, () -> clienteService.buscarCliente(numeroConta),
        "Cliente não encontrado.");
    verify(clienteRepository).findByNumeroConta(numeroConta);
  }

}

