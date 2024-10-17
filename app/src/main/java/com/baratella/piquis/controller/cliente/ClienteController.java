package com.baratella.piquis.controller.cliente;

import com.baratella.piquis.dto.ClienteDTO;
import com.baratella.piquis.service.cliente.ClienteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

  private final ClienteService clienteService;

  @PostMapping
  public ResponseEntity<ClienteDTO> cadastrarCliente(@RequestBody ClienteDTO request) {
    var cliente = clienteService.cadastrarCliente(request);
    return ResponseEntity
        .created(ServletUriComponentsBuilder
            .fromCurrentRequest()
            .queryParam("numeroConta", cliente.numeroConta())
            .buildAndExpand(cliente.numeroConta())
            .toUri())
        .body(cliente);
  }

  @GetMapping(params = "numeroConta")
  public ResponseEntity<ClienteDTO> buscarCliente(@RequestParam("numeroConta") String numeroConta) {
    return ResponseEntity.ok(clienteService.buscarCliente(numeroConta));
  }

  @GetMapping
  public ResponseEntity<List<ClienteDTO>> listarClientes() {
    return ResponseEntity.ok(clienteService.listarClientes());
  }
}
