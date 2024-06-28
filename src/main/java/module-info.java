module com.sandhata.converter.pegatocamundaconverter {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires lombok;
    requires org.apache.commons.lang3;
    requires zeebe.bpmn.model;
    requires camunda.xml.model;
    requires camunda.bpmn.model;

    opens com.sandhata.converter to javafx.fxml;
    exports com.sandhata.converter;
    exports com.sandhata.converter.controller;
    opens com.sandhata.converter.controller to javafx.fxml;
}