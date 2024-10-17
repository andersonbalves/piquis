package com.baratella.piquis.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

  @Id
  @Column(name = "id_cliente")
  @Setter(lombok.AccessLevel.NONE)
  private String idCliente;

  @Column(name = "nome_cliente")
  @Setter(lombok.AccessLevel.NONE)
  private String nomeCliente;

  @Column(name = "numero_conta", unique = true)
  @Setter(lombok.AccessLevel.NONE)
  private String numeroConta;

  @Column(name = "saldo")
  @Setter(lombok.AccessLevel.NONE)
  private BigDecimal saldo;

  @Version
  @Column(name = "version")
  private Long version;

}
