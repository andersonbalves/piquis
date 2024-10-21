package com.baratella.piquis.controller.transferencia;

import com.baratella.piquis.dto.ComprovanteTransferenciaDTO;
import com.baratella.piquis.dto.TransferenciaDTO;
import com.baratella.piquis.service.transferencia.TransferenciaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transferencias")
@RequiredArgsConstructor
public class TransferenciaController {

  private final TransferenciaService transferenciaService;

  @PostMapping
  ResponseEntity<ComprovanteTransferenciaDTO> transferirEntreContas(
      @Valid @RequestBody TransferenciaDTO transferencia) {
    return ResponseEntity.ok(transferenciaService.efetuarTransferencia(transferencia));
  }

  @GetMapping(params = "numeroConta")
  ResponseEntity<List<ComprovanteTransferenciaDTO>> listarTransferencias(
      @Valid
      @RequestParam("numeroConta")
      @NotBlank(message = "Número da conta não pode ser vazio.")
      String numeroConta) {
    return ResponseEntity.ok(transferenciaService.listarTransferencias(numeroConta));
  }
}
