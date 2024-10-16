package com.baratella.piquis.service.transferencia;

import com.baratella.piquis.adapter.dynamodb.transferencia.HistoricoTransferencia;
import com.baratella.piquis.dto.TransferenciaDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferenciaServiceImpl implements TransferenciaService {

  private final HistoricoTransferencia historicoTransferencia;

  @Override
  public TransferenciaDTO efetuarTransferencia(TransferenciaDTO transferencia) {
    return null;
  }

  @Override
  public List<TransferenciaDTO> listarTransferencias(String numeroConta) {
    return null;
  }
}
