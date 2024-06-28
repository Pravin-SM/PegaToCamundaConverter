package com.sandhata.converter.exception;


import com.sandhata.converter.constants.PegaToCamundaConverterConstants;
import com.sandhata.converter.util.ConverterUtil;

import java.io.IOException;
import java.util.Objects;

public class CustomExceptionHandler {

    public void handle(Exception exception){
        switch (getClassName(exception.getClass().getName())){
            case PegaToCamundaConverterConstants.CONVERTER_CUSTOM_EXCEPTION->
                    ConverterUtil.logCustomErrorInfo((ConverterCustomException) exception);
            case PegaToCamundaConverterConstants.IO_EXCEPTION ->
                   ConverterUtil.logUnknownErrorInfo((IOException)exception);
            default ->
                    ConverterUtil.logUnknownErrorInfo(exception);
        }
    }

    private String getClassName(String completeName){
        return completeName.substring(completeName.lastIndexOf(PegaToCamundaConverterConstants.DOT)+1);
    }
}
