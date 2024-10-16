package com.baratella.piquis.controller.cliente;

import com.baratella.piquis.dto.ClienteDTO;
import com.baratella.piquis.service.cliente.ClienteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

  private final ClienteService clienteService;

  @PostMapping
  public ResponseEntity<ClienteDTO> cadastrarCliente(ClienteDTO cliente) {
    return null;
  }

  @GetMapping(params = "numeroConta")
  public ResponseEntity<ClienteDTO> buscarCliente(String numeroConta) {
    return null;
  }

  @GetMapping
  public ResponseEntity<List<ClienteDTO>> listarClientes() {
    return null;
  }
}
