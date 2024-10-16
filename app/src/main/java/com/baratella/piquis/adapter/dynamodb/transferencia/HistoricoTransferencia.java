package com.baratella.piquis.adapter.dynamodb.transferencia;

import com.baratella.piquis.dto.TransferenciaDTO;
import java.util.List;
import java.util.Optional;

public interface HistoricoTransferencia {

  Optional<TransferenciaDTO> salvarTransferencia(TransferenciaDTO transferencia);

  Optional<List<TransferenciaDTO>> listarTransferencias(String numeroConta);
}
