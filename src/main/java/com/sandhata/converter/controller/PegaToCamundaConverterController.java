package com.sandhata.converter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandhata.converter.constants.PegaToCamundaConverterConstants;
import com.sandhata.converter.impl.camunda7.BPMNGenFromPega;
import com.sandhata.converter.impl.camunda8.BPMNGenFromPega8;
import com.sandhata.converter.util.ConverterUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PegaToCamundaConverterController implements Initializable {

    @FXML
    private Button downloadButton;

    @FXML
    private MenuItem closeFileMenuItem;

    @FXML
    private VBox inputPaneCamundaVBox;

    @FXML
    private MenuItem openFileMenuItem;

    @FXML
    private Menu helpMenu;

    @FXML
    private MenuItem newFileMenuItem;

    @FXML
    private AnchorPane centerContentPane;

    @FXML
    private AnchorPane inputPane;

    @FXML
    private ToggleGroup camundaVersion;

    @FXML
    private RadioButton camunda7RadioButton;

    @FXML
    private RadioButton camunda8RadioButton;

    @FXML
    private Label pegaInputFieldPathLabel;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Button pegaFileInputButton;

    @FXML
    private Button camundaFileInputButton;

    @FXML
    private VBox inputPanePegaVBox;

    @FXML
    private Button convertButton;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private Label camundaInputFieldPathLabel;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private BorderPane parentBorderpane;

    @FXML
    private VBox inputPaneVBox;

    @FXML
    private Menu fileMenu;

    @FXML
    private VBox inputPaneRootVBox;

    //Declarations
    //Filepath for storing the location of the input pega xml file
    private File pegaXMLFilePath=null;
    //Filepath for storing the location of generated camunda BPMN file
    private File camundaBPMNPath=null;
    private String camundaVersionString=PegaToCamundaConverterConstants.CAMUNDA_7;
    private BpmnModelInstance bpmnModelInstance_Camunda7=null;
    private io.camunda.zeebe.model.bpmn.BpmnModelInstance bpmnModelInstance_Camunda8=null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        downloadButton.setDisable(true);
//        downloadButton.setVisible(true);
    }

    @FXML
    void pegaFileInputButtonClicked(ActionEvent event) {
        pegaXMLFilePath=ConverterUtil.getFilePath(pegaInputFieldPathLabel, PegaToCamundaConverterConstants.PEGA);
    }

    @FXML
    void camundaFileInputButtonClicked(ActionEvent event) {
        camundaBPMNPath=ConverterUtil.getFilePath(camundaInputFieldPathLabel, PegaToCamundaConverterConstants.CAMUNDA);
    }

    @FXML
    void convertButtonClicked(ActionEvent event) throws XPathExpressionException, ParserConfigurationException, IOException, SAXException {
        if(ConverterUtil.validateConversionData(pegaXMLFilePath, camundaBPMNPath)){
            if(camunda7RadioButton.isSelected()){
                BPMNGenFromPega bpmnGenFromPega=new BPMNGenFromPega();
                bpmnModelInstance_Camunda7=bpmnGenFromPega.camunda7ConversionLogic(pegaXMLFilePath);
                outputTextArea.setEditable(false);
                outputTextArea.setText(ConverterUtil.readConvertedData(bpmnModelInstance_Camunda7, camundaVersionString));
            }else{
                BPMNGenFromPega8 bpmnGenFromPega8=new BPMNGenFromPega8();
                bpmnModelInstance_Camunda8=bpmnGenFromPega8.camunda8ConversionLogic(pegaXMLFilePath);
                camundaVersionString=PegaToCamundaConverterConstants.CAMUNDA_8;
                outputTextArea.setEditable(false);
                outputTextArea.setText(ConverterUtil.readConvertedData(bpmnModelInstance_Camunda8, camundaVersionString));
            }
            convertButton.setDisable(true);
            downloadButton.setDisable(false);
        }
    }

    @FXML
    void downloadButtonClicked(ActionEvent event) {
        if(camundaVersionString.equals(PegaToCamundaConverterConstants.CAMUNDA_7))
            ConverterUtil.writeDataToFile(camundaBPMNPath, bpmnModelInstance_Camunda7, camundaVersionString);
        if(camundaVersionString.equals(PegaToCamundaConverterConstants.CAMUNDA_8))
            ConverterUtil.writeDataToFile(camundaBPMNPath, bpmnModelInstance_Camunda8, camundaVersionString);
        initializeFilePaths();
        ConverterUtil.reInitialize(pegaInputFieldPathLabel, camundaInputFieldPathLabel, downloadButton, convertButton, camunda7RadioButton, outputTextArea);
    }

    @FXML
    void camunda7RadioButtonSelected(ActionEvent event) {

    }

    @FXML
    void camunda8RadioButtonSelected(ActionEvent event) {

    }

    @FXML
    void fileMenuSelected(ActionEvent event) {

    }

    @FXML
    void newFileMenuItemSelected(ActionEvent event) {

    }

    @FXML
    void openFileMenuItemSelected(ActionEvent event) {

    }

    @FXML
    void closeFileMenuItemSelected(ActionEvent event) {

    }

    @FXML
    void hempMenuSelected(ActionEvent event) {

    }

    @FXML
    void helpMenuItemSelected(ActionEvent event) {

    }

    private void initializeFilePaths(){
        pegaXMLFilePath=null;
        camundaBPMNPath=null;
        camundaVersionString=PegaToCamundaConverterConstants.CAMUNDA_7;
    }

}
