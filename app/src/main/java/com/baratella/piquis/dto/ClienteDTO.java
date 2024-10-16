package com.baratella.piquis.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ClienteDTO(String idCliente, String nomeCliente, String numeroConta,
                         BigDecimal saldoConta) {

}
