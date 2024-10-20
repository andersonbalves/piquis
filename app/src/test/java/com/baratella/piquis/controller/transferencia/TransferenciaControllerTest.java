package com.baratella.piquis.controller.transferencia;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.baratella.piquis.dto.ComprovanteTransferenciaDTO;
import com.baratella.piquis.dto.TransferenciaDTO;
import com.baratella.piquis.service.transferencia.TransferenciaService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@WebMvcTest(TransferenciaController.class)
class TransferenciaControllerTest {

  private static final PodamFactory podam = new PodamFactoryImpl();

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TransferenciaService transferenciaService;

  @Test
  void transferirEntreContas_sucesso() throws Exception {
    TransferenciaDTO request = podam.manufacturePojo(TransferenciaDTO.class);
    ComprovanteTransferenciaDTO response = ComprovanteTransferenciaDTO.builder()
        .comprovante(UUID.randomUUID().toString())
        .contaOrigem(request.contaOrigem())
        .contaDestino(request.contaDestino())
        .valorTransferido(request.valor())
        .dataHoraTransferencia(LocalDateTime.now())
        .comprovante(UUID.randomUUID().toString())
        .build();

    doReturn(response).when(transferenciaService).efetuarTransferencia(any(TransferenciaDTO.class));

    mockMvc.perform(post("/api/v1/transferencias")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "contaOrigem": "%s",
                  "contaDestino": "%s",
                  "valor": %s
                }
                """.formatted(request.contaOrigem(), request.contaDestino(), request.valor()))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.comprovante").value(response.comprovante()))
        .andExpect(jsonPath("$.contaOrigem").value(response.contaOrigem()))
        .andExpect(jsonPath("$.contaDestino").value(response.contaDestino()))
        .andExpect(jsonPath("$.valorTransferido").value(response.valorTransferido()))
        .andExpect(jsonPath("$.comprovante").value(response.comprovante()));
  }

  @Test
  void listarTransferencias_sucesso() throws Exception {
    String numeroConta = UUID.randomUUID().toString();
    List<ComprovanteTransferenciaDTO> response = List.of(
        podam.manufacturePojo(ComprovanteTransferenciaDTO.class),
        podam.manufacturePojo(ComprovanteTransferenciaDTO.class)
    );

    doReturn(response).when(transferenciaService).listarTransferencias(numeroConta);

    mockMvc.perform(get("/api/v1/transferencias")
            .param("numeroConta", numeroConta))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(response.size()));
  }
}