package com.sandhata.converter.util;

import com.sandhata.converter.constants.PegaToCamundaConverterConstants;
import com.sandhata.converter.exception.ConverterCustomException;
import com.sandhata.converter.exception.ConverterExceptionEnums;
import com.sandhata.converter.exception.CustomExceptionHandler;
import io.camunda.zeebe.model.bpmn.Bpmn;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.util.Objects;

import static java.lang.System.*;

public class ConverterUtil {

    private ConverterUtil(){}

    public static File getFilePath(Label filePath, String fileType){
        FileChooser fileChooser = new FileChooser();
        if(fileType.equals(PegaToCamundaConverterConstants.PEGA)) {
            fileChooser.setTitle("Pega XML File Chooser");
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("XML File", PegaToCamundaConverterConstants.EXTENSION_XML)
            );
            var file = fileChooser.showOpenDialog(null);
            if (Objects.nonNull(file)) {
                filePath.setText(file.getName());
                out.println("Selected file path : " + file.getName());
            }
            return file;
        }
        if(fileType.equals(PegaToCamundaConverterConstants.CAMUNDA)) {
            fileChooser.setTitle("Camunda BPMN Output File Chooser");
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("BPMN File", PegaToCamundaConverterConstants.EXTENSION_BPMN)
            );
            var file = fileChooser.showSaveDialog(null);
            if (Objects.nonNull(file)) {
                filePath.setText(file.getName());
                out.println("Selected Camunda file path : " + file.getName());
            }
            return file;
        }
        return null;
    }

    public static void logStartEnd(String className, String methodName, String operation){
        out.println("Class Name : "+className+" :: Method Name : "+methodName+" :: Operation :"+operation);
    }

    public static void logCustomErrorInfo(ConverterCustomException converterCustomException){
        out.println("Class Name : "+converterCustomException.getClassName()+" :: Method Name : "+converterCustomException.getMethodName()+" :: Exception :"+converterCustomException.getErrorMessage());
    }

    public static void logUnknownErrorInfo(Exception exception){
        out.println("Exception : "+exception.getLocalizedMessage());
    }

    public static void writeDataToFile(File camundaBPMNFilePath, Object modelInstance, String camundaVersion){
        if(camundaVersion.equals(PegaToCamundaConverterConstants.CAMUNDA_8))
            Bpmn.writeModelToFile(camundaBPMNFilePath, (io.camunda.zeebe.model.bpmn.BpmnModelInstance)modelInstance);
        if(camundaVersion.equals(PegaToCamundaConverterConstants.CAMUNDA_7))
            org.camunda.bpm.model.bpmn.Bpmn.writeModelToFile(camundaBPMNFilePath, (org.camunda.bpm.model.bpmn.BpmnModelInstance)modelInstance);
        out.println("BPMN File created for Camunda Version  "+camundaVersion+" can be found in "+camundaBPMNFilePath.getAbsolutePath());
    }

    public static String readConvertedData(Object modelInstance, String camundaVersion){
        if(camundaVersion.equals(PegaToCamundaConverterConstants.CAMUNDA_8))
            return Bpmn.convertToString((io.camunda.zeebe.model.bpmn.BpmnModelInstance)modelInstance);
        if(camundaVersion.equals(PegaToCamundaConverterConstants.CAMUNDA_7))
            return org.camunda.bpm.model.bpmn.Bpmn.convertToString((org.camunda.bpm.model.bpmn.BpmnModelInstance)modelInstance);
        return null;
    }

    public static boolean validateConversionData(File pegaXMLFilePath, File camundaBPMNFilePath){
        boolean pegaValidationStatus=true;
        boolean camundaValidationStatus=true;
        if(Objects.isNull(pegaXMLFilePath)) {
            new CustomExceptionHandler().handle(new ConverterCustomException(PegaToCamundaConverterConstants.CONVERTER_UTIL, PegaToCamundaConverterConstants.VALIDATE_CONVERSION_DATA, ConverterExceptionEnums.MISSING_INPUT_FILE));
            pegaValidationStatus=false;
        }
        if(Objects.isNull(camundaBPMNFilePath)) {
            new CustomExceptionHandler().handle(new ConverterCustomException(PegaToCamundaConverterConstants.CONVERTER_UTIL, PegaToCamundaConverterConstants.VALIDATE_CONVERSION_DATA, ConverterExceptionEnums.MISSING_OUTPUT_FILE));
            camundaValidationStatus=false;
        }
        return pegaValidationStatus&&camundaValidationStatus;
    }

    public static void reInitialize(Label pegaInputFieldPathLabel, Label camundaInputFieldPathLabel, Button downloadButton, Button convertButton, RadioButton camunda7RadioButton, TextArea outputTextArea){
        pegaInputFieldPathLabel.setText("");
        camundaInputFieldPathLabel.setText("");
        downloadButton.setDisable(true);
        convertButton.setDisable(false);
        camunda7RadioButton.setSelected(true);
        outputTextArea.setText("");
    }
}
