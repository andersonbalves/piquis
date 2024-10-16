package com.baratella.piquis.service.transferencia;

import com.baratella.piquis.dto.TransferenciaDTO;
import java.util.List;

public interface TransferenciaService {

  TransferenciaDTO efetuarTransferencia(TransferenciaDTO transferencia);

  List<TransferenciaDTO> listarTransferencias(String numeroConta);
}
