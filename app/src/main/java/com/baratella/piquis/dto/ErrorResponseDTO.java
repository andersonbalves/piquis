package com.baratella.piquis.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record ErrorResponseDTO(String code, String message, List<ErrorFieldResponseDTO> errors) {

  @Builder
  public record ErrorFieldResponseDTO(String field, String error) {

  }

}

