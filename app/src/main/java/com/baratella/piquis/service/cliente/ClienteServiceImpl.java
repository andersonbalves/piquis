package com.baratella.piquis.service.cliente;

import com.baratella.piquis.dto.ClienteDTO;
import com.baratella.piquis.repository.ClienteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

  private final ClienteRepository clienteRepository;

  @Override
  public ClienteDTO cadastrarCliente(ClienteDTO cliente) {
    return null;
  }

  @Override
  public List<ClienteDTO> listarClientes() {
    return List.of();
  }

  @Override
  public ClienteDTO buscarCliente(String numeroConta) {
    return null;
  }
}