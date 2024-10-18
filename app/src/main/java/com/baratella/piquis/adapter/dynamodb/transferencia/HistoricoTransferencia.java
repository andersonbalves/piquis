package com.baratella.piquis.adapter.dynamodb.transferencia;

import com.baratella.piquis.dto.ComprovanteTransferenciaDTO;
import com.baratella.piquis.dto.TransferenciaDTO;
import java.util.List;
import java.util.Optional;

public interface HistoricoTransferencia {

  void salvarTransferencia(ComprovanteTransferenciaDTO transferencia);

  Optional<List<ComprovanteTransferenciaDTO>> listarTransferencias(String numeroConta);
}
