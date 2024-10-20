package com.baratella.piquis.controller.cliente;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.baratella.piquis.dto.ClienteDTO;
import com.baratella.piquis.service.cliente.ClienteService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

  private static final PodamFactory podam = new PodamFactoryImpl();

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ClienteService clienteService;

  @BeforeEach
  void setUp() {
    // Setup code if needed
  }

  @Test
  void cadastrarCliente_sucesso() throws Exception {
    ClienteDTO request = podam.manufacturePojo(ClienteDTO.class);
    ClienteDTO response = podam.manufacturePojo(ClienteDTO.class);

    doReturn(response).when(clienteService).cadastrarCliente(any(ClienteDTO.class));

    mockMvc.perform(post("/api/v1/clientes")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                "idCliente": "%s",
                  "nomeCliente": "%s",
                  "numeroConta": "%s",
                  "saldoConta": %s
                }
                """.formatted(request.idCliente(), request.nomeCliente(), request.numeroConta(),
                request.saldoConta()))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.idCliente").value(response.idCliente()))
        .andExpect(jsonPath("$.numeroConta").value(response.numeroConta()))
        .andExpect(jsonPath("$.saldoConta").value(response.saldoConta()));
  }

  @Test
  void buscarCliente_sucesso() throws Exception {
    String numeroConta = UUID.randomUUID().toString();
    ClienteDTO response = ClienteDTO.builder()
        .idCliente(UUID.randomUUID().toString())
        .numeroConta(numeroConta)
        .saldoConta(BigDecimal.valueOf(1000))
        .build();

    doReturn(response).when(clienteService).buscarCliente(numeroConta);

    mockMvc.perform(get("/api/v1/clientes")
            .param("numeroConta", numeroConta))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.idCliente").value(response.idCliente()))
        .andExpect(jsonPath("$.numeroConta").value(response.numeroConta()))
        .andExpect(jsonPath("$.saldoConta").value(response.saldoConta()));
  }

  @Test
  void listarClientes_sucesso() throws Exception {
    List<ClienteDTO> response = List.of(
        podam.manufacturePojo(ClienteDTO.class),
        podam.manufacturePojo(ClienteDTO.class)
    );

    doReturn(response).when(clienteService).listarClientes();

    mockMvc.perform(get("/api/v1/clientes"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(response.size()));
  }
}