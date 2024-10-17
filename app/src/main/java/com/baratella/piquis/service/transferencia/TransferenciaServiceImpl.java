package com.baratella.piquis.service.transferencia;

import com.baratella.piquis.adapter.dynamodb.transferencia.HistoricoTransferencia;
import com.baratella.piquis.dto.ComprovanteTransferenciaDTO;
import com.baratella.piquis.dto.TransferenciaDTO;
import com.baratella.piquis.repository.ClienteRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferenciaServiceImpl implements TransferenciaService {

  private final HistoricoTransferencia historicoTransferencia;

  private final ClienteRepository clienteRepository;

  @Override
  @Transactional
  public ComprovanteTransferenciaDTO efetuarTransferencia(TransferenciaDTO transferencia) {
    if (transferencia.valor().compareTo(new BigDecimal(0)) <= 0) {
      throw new IllegalArgumentException("Valor de transferência deve ser maior que zero.");
    }
    if (transferencia.valor().compareTo(new BigDecimal(10000)) > 0) {
      throw new IllegalArgumentException("Valor máximo de transferência é R$ 10.000,00.");
    }
    var origem = clienteRepository.findByNumeroConta(transferencia.contaOrigem())
        .orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada."));
    var destino = clienteRepository.findByNumeroConta(transferencia.contaDestino())
        .orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada."));

    clienteRepository.save(origem.toBuilder()
        .saldo(origem.getSaldo().subtract(transferencia.valor()))
        .build());
    clienteRepository.save(destino.toBuilder()
        .saldo(destino.getSaldo().add(transferencia.valor()))
        .build());

    return ComprovanteTransferenciaDTO.builder()
        .dataHoraTransferencia(LocalDateTime.now())
        .comprovante(UUID.randomUUID().toString())
        .contaOrigem(origem.getNumeroConta())
        .contaDestino(destino.getNumeroConta())
        .valorTransferido(transferencia.valor())
        .build();
  }

  @Override
  public List<ComprovanteTransferenciaDTO> listarTransferencias(String numeroConta) {
    return null;
  }
}
