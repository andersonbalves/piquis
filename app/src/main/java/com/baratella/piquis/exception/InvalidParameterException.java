package com.baratella.piquis.exception;


import lombok.Getter;

@Getter
public class InvalidParameterException extends RuntimeException {

  private final String field;

  public InvalidParameterException(String message, String field) {
    super(message);
    this.field = field;
  }

}
