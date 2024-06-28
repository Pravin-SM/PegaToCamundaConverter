package com.sandhata.converter.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
public class ConverterCustomException extends RuntimeException{
    private final String className;
    private final String methodName;
    private final String errorMessage;
    private final String errorCode;
    private final Instant timestamp;

    public ConverterCustomException(String className, String methodName, ConverterExceptionEnums converterExceptionEnums) {
        this.className = className;
        this.methodName = methodName;
        this.errorCode= converterExceptionEnums.getErrorCode();
        this.errorMessage= converterExceptionEnums.getErrorMessage();
        timestamp= Instant.now();
    }
}
