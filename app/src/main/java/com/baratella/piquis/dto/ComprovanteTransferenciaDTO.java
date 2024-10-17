package com.baratella.piquis.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ComprovanteTransferenciaDTO(
    LocalDateTime dataHoraTransferencia,
    String comprovante,
    String contaOrigem,
    String contaDestino,
    BigDecimal valorTransferido) {

}
