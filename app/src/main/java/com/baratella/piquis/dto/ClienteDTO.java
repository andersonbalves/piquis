package com.baratella.piquis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;


@Builder
public record ClienteDTO(
    @NotBlank(message = "ID do cliente não pode ser vazio.")
    String idCliente,
    @NotBlank(message = "Nome do cliente não pode ser vazio.")
    String nomeCliente,
    @NotBlank(message = "Número da conta não pode ser vazio.")
    String numeroConta,
    @NotNull(message = "Saldo da conta não pode ser nulo.")
    BigDecimal saldoConta) {

}
