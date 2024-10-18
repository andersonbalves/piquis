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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferenciaServiceImpl implements TransferenciaService {

  @Value("${app.transferencia.min:0}")
  private int minTransferencia;
  @Value("${app.transferencia.max:10000}")
  private int maxTransferencia;

  private final HistoricoTransferencia historicoTransferencia;

  private final ClienteRepository clienteRepository;

  @Override
  @Transactional
  public ComprovanteTransferenciaDTO efetuarTransferencia(TransferenciaDTO transferencia) {
    if (transferencia.valor().compareTo(new BigDecimal(minTransferencia)) <= 0) {
      throw new IllegalArgumentException(
          String.format("Valor de transferência deve ser menor que R$ %s,00.", minTransferencia));
    }
    if (transferencia.valor().compareTo(new BigDecimal(maxTransferencia)) > 0) {
      throw new IllegalArgumentException(
          String.format("Valor de transferência deve ser menor que R$ %s,00.", maxTransferencia));
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

    var response = ComprovanteTransferenciaDTO.builder()
        .dataHoraTransferencia(LocalDateTime.now())
        .comprovante(UUID.randomUUID().toString())
        .contaOrigem(origem.getNumeroConta())
        .contaDestino(destino.getNumeroConta())
        .valorTransferido(transferencia.valor())
        .build();
    historicoTransferencia.salvarTransferencia(response);
    return response;
  }

  @Override
  public List<ComprovanteTransferenciaDTO> listarTransferencias(String numeroConta) {
    if (clienteRepository.findByNumeroConta(numeroConta).isEmpty()) {
      throw new IllegalArgumentException("Conta não encontrada.");
    }
    return historicoTransferencia.listarTransferencias(numeroConta).orElseThrow(
        () -> new IllegalArgumentException("Nenhuma transferência encontrada para a conta.")
    );
  }
}
