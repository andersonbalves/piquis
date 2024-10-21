package com.baratella.piquis.service.transferencia;

import com.baratella.piquis.dto.ComprovanteTransferenciaDTO;
import com.baratella.piquis.dto.TransferenciaDTO;

import java.util.List;

public interface TransferenciaService {

  ComprovanteTransferenciaDTO efetuarTransferencia(TransferenciaDTO transferencia);

  List<ComprovanteTransferenciaDTO> listarTransferencias(String numeroConta);
}
