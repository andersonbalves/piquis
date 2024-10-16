package com.baratella.piquis.controller.transferencia;

import com.baratella.piquis.dto.TransferenciaDTO;
import com.baratella.piquis.service.transferencia.TransferenciaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transferencias")
@RequiredArgsConstructor
public class TransferenciaController {

  private final TransferenciaService transferenciaService;

  @PostMapping
  ResponseEntity<TransferenciaDTO> transferirEntreContas(TransferenciaDTO transferencia) {
    return null;
  }

  @GetMapping(params = "numeroConta")
  ResponseEntity<List<TransferenciaDTO>> listarTransferencias(String numeroConta) {
    return null;
  }
}
