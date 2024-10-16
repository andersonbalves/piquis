package com.baratella.piquis.adapter.dynamodb.transferencia;

import com.baratella.piquis.dto.TransferenciaDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class HistoricoTransferenciaImpl implements HistoricoTransferencia {

  @Override
  public Optional<TransferenciaDTO> salvarTransferencia(TransferenciaDTO transferencia) {
    return Optional.empty();
  }

  @Override
  public Optional<List<TransferenciaDTO>> listarTransferencias(String numeroConta) {
    return Optional.empty();
  }
}
