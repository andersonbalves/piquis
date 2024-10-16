package com.baratella.piquis.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record TransferenciaDTO(String contaOrigem, String contaDestino, BigDecimal valor) {

}
