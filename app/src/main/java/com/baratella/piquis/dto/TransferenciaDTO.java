package com.baratella.piquis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record TransferenciaDTO(
    @NotBlank(message = "Conta de origem não pode ser vazia.")
    String contaOrigem,
    
    @NotBlank(message = "Conta de destino não pode ser vazia.")
    String contaDestino,

    @NotNull(message = "Valor da transferência não pode ser vazio.")
    BigDecimal valor) {

}
