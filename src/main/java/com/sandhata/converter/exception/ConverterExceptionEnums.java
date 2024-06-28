package com.sandhata.converter.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConverterExceptionEnums {

    MISSING_INPUT_FILE("E001", "Missing file path for Pega xml file"),
    MISSING_OUTPUT_FILE("E002", "Missing file path for camunda output BPMN file"),
    UNKNOWN_EXCEPTION("E100", "Unknown Exception");

    private final String errorCode;
    private final String errorMessage;

}
