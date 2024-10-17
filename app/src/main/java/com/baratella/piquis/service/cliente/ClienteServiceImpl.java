package com.baratella.piquis.service.cliente;

import com.baratella.piquis.dto.ClienteDTO;
import com.baratella.piquis.model.Cliente;
import com.baratella.piquis.repository.ClienteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

  private final ClienteRepository clienteRepository;

  @Override
  public ClienteDTO cadastrarCliente(ClienteDTO request) {
    if (clienteRepository.existsById(request.idCliente())) {
      throw new DataIntegrityViolationException(
          "Cliente com ID " + request.idCliente() + " já existe.");
    }
    var cliente = clienteRepository.save(Cliente.builder()
        .idCliente(request.idCliente())
        .nomeCliente(request.nomeCliente())
        .numeroConta(request.numeroConta())
        .saldo(request.saldoConta())
        .build());
    return ClienteDTO.builder()
        .idCliente(cliente.getIdCliente())
        .nomeCliente(cliente.getNomeCliente())
        .numeroConta(cliente.getNumeroConta())
        .saldoConta(cliente.getSaldo())
        .build();
  }

  @Override
  public List<ClienteDTO> listarClientes() {
    return clienteRepository.findAll().stream()
        .map(cliente -> ClienteDTO.builder()
            .idCliente(cliente.getIdCliente())
            .nomeCliente(cliente.getNomeCliente())
            .numeroConta(cliente.getNumeroConta())
            .saldoConta(cliente.getSaldo())
            .build())
        .toList();
  }

  @Override
  public ClienteDTO buscarCliente(String numeroConta) {
    return clienteRepository.findByNumeroConta(numeroConta)
        .map(cliente -> ClienteDTO.builder()
            .idCliente(cliente.getIdCliente())
            .nomeCliente(cliente.getNomeCliente())
            .numeroConta(cliente.getNumeroConta())
            .saldoConta(cliente.getSaldo())
            .build())
        .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
  }
}