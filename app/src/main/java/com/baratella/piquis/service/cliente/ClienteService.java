package com.baratella.piquis.service.cliente;

import com.baratella.piquis.dto.ClienteDTO;
import java.util.List;

public interface ClienteService {

  ClienteDTO cadastrarCliente(ClienteDTO cliente);

  List<ClienteDTO> listarClientes();

  ClienteDTO buscarCliente(String numero_conta);
}
