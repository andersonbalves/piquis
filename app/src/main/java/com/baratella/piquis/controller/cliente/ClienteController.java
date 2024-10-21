package com.baratella.piquis.controller.cliente;

import com.baratella.piquis.dto.ClienteDTO;
import com.baratella.piquis.service.cliente.ClienteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ClienteController {

  private final ClienteService clienteService;

  @PostMapping
  public ResponseEntity<ClienteDTO> cadastrarCliente(@Valid @RequestBody ClienteDTO request) {
    var cliente = clienteService.cadastrarCliente(request);
    ClienteDTO.builder()
        .idCliente(cliente.idCliente())
        .numeroConta(cliente.numeroConta())
        .saldoConta(cliente.saldoConta())
        .build();
    return ResponseEntity
        .created(ServletUriComponentsBuilder
            .fromCurrentRequest()
            .queryParam("numeroConta", cliente.numeroConta())
            .buildAndExpand(cliente.numeroConta())
            .toUri())
        .body(cliente);
  }

  @GetMapping(params = "numeroConta")
  public ResponseEntity<ClienteDTO> buscarCliente(
      @Valid
      @RequestParam("numeroConta")
      @NotBlank(message = "Número da conta não pode ser vazio.")
      String numeroConta) {
    return ResponseEntity.ok(clienteService.buscarCliente(numeroConta));
  }

  @GetMapping
  public ResponseEntity<List<ClienteDTO>> listarClientes() {
    return ResponseEntity.ok(clienteService.listarClientes());
  }
}