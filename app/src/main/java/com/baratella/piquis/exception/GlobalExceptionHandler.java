package com.baratella.piquis.exception;

import static java.util.Objects.requireNonNull;

import com.baratella.piquis.dto.ErrorResponseDTO;
import com.baratella.piquis.dto.ErrorResponseDTO.ErrorFieldResponseDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolationException(
      DataIntegrityViolationException ex) {
    log.error(HttpStatus.CONFLICT.getReasonPhrase(), ex);

    if (ex.getMessage().toUpperCase().contains("CLIENTE COM ID")) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(ErrorResponseDTO.builder()
              .code("CONFLICT")
              .message("ID do cliente já cadastrado")
              .build());
    }

    if (ex.getCause() instanceof ConstraintViolationException constraintEx
        && requireNonNull(constraintEx.getConstraintName()).contains("UNIQUE_NUMERO_CONTA")) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(ErrorResponseDTO.builder()
              .code("CONFLICT")
              .message("Número da conta já cadastrado")
              .build());
    }

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ErrorResponseDTO.builder()
            .code("CONFLICT")
            .message("Ocorreu um conflito ao processar a requisição")
            .build());
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ErrorResponseDTO> handleHandlerMethodValidationException(
      HandlerMethodValidationException ex) {
    log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
    return ResponseEntity.badRequest()
        .body(ErrorResponseDTO.builder()
            .code("BAD_REQUEST")
            .message(requireNonNull(ex.getDetailMessageArguments())[0].toString())
            .build());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
    return ResponseEntity.badRequest()
        .body(ErrorResponseDTO.builder()
            .code("BAD_REQUEST")
            .message("Os dados de entrada são inválidos")
            .errors(ex.getFieldErrors().stream().map(
                    error -> ErrorFieldResponseDTO.builder()
                        .field(error.getField())
                        .error(error.getDefaultMessage())
                        .build())
                .toList())
            .build());
  }

  @ExceptionHandler(InvalidParameterException.class)
  public ResponseEntity<ErrorResponseDTO> handleInvalidParameterException(
      InvalidParameterException ex) {
    log.error(HttpStatus.BAD_REQUEST.getReasonPhrase(), ex);
    return ResponseEntity.badRequest()
        .body(ErrorResponseDTO.builder()
            .code("BAD_REQUEST")
            .message("Os dados de entrada são inválidos")
            .errors(List.of(ErrorFieldResponseDTO.builder()
                .field(ex.getField())
                .error(ex.getMessage())
                .build()))
            .build());
  }

  @ExceptionHandler({NotFoundException.class})
  public ResponseEntity<ErrorResponseDTO> handleNotFoundException(
      NotFoundException ex) {
    log.error(HttpStatus.NOT_FOUND.getReasonPhrase(), ex);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponseDTO.builder()
            .code("NOT_FOUND")
            .message(ex.getMessage())
            .build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleGeneralException(Exception ex) {
    log.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ex);
    return ResponseEntity.internalServerError()
        .body(ErrorResponseDTO.builder()
            .code("INTERNAL_SERVER_ERROR")
            .message("An unexpected error occurred: " + ex.getMessage())
            .build());
  }
}