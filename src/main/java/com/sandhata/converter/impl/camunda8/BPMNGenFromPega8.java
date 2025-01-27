package com.sandhata.converter.impl.camunda8;

import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import io.camunda.zeebe.model.bpmn.instance.*;
import io.camunda.zeebe.model.bpmn.instance.Process;
import io.camunda.zeebe.model.bpmn.instance.bpmndi.BpmnDiagram;
import io.camunda.zeebe.model.bpmn.instance.bpmndi.BpmnPlane;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import static io.camunda.zeebe.model.bpmn.impl.BpmnModelConstants.BPMN20_NS;

public class BPMNGenFromPega8 {

    public BpmnModelInstance camunda8ConversionLogic(File pegaXMLFilePath) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {

        // Read in Pega xml file
        File file = pegaXMLFilePath;

        // Create hash map to map ids in old file node objects with ids in new file
        HashMap<String, Object> idMap = new HashMap<>();

        // Create hash of flow nodes for drawing of sequence flows later
        HashMap<String, Object> flowNodesMap = new HashMap<>();

        // Create hash map of Pega elements to search for
        HashMap<String, Object> pegaElementsMap = new HashMap<>();
        PegaToBPMNElement8 bpmnElement = new PegaToBPMNElement8(StartEvent.class, 36d, 36d);
        pegaElementsMap.put("Data-MO-Event-Start", bpmnElement);
        bpmnElement = new PegaToBPMNElement8(UserTask.class, 80d, 100d);
        pegaElementsMap.put("Data-MO-Activity-Assignment", bpmnElement);
        bpmnElement = new PegaToBPMNElement8(CallActivity.class, 80d, 100d);
        pegaElementsMap.put("Data-MO-Activity-SubProcess", bpmnElement);
        bpmnElement = new PegaToBPMNElement8(ServiceTask.class, 80d, 100d);
        pegaElementsMap.put("Data-MO-Activity-Utility", bpmnElement);
        bpmnElement = new PegaToBPMNElement8(EndEvent.class, 36d, 36d);
        pegaElementsMap.put("Data-MO-Event-End", bpmnElement);
        pegaElementsMap.put("Data-MO-Event-Exception", bpmnElement);
        bpmnElement = new PegaToBPMNElement8(ExclusiveGateway.class, 50d, 50d);
        pegaElementsMap.put("Data-MO-Gateway-Decision", bpmnElement);
        //bpmnElement = new PegaToBPMNElement8(SequenceFlow.class, 0d,0d);
        //pegaElementsMap.put("Data-MO-Connector-Transition", bpmnElement);

        // Read document in preparation for Xpath searches
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);

        doc.getDocumentElement().normalize();

        // Create BPMN model using Camunda Model APIs
        BpmnModelInstance modelInstance = Bpmn.createEmptyModel();

        Definitions definitions = modelInstance.newInstance(Definitions.class);
        definitions.setTargetNamespace(BPMN20_NS);
        definitions.setAttributeValueNs("http://camunda.org/schema/modeler/1.0", "modeler:executionPlatform", "Camunda Cloud");
        modelInstance.setDefinitions(definitions);

        Process process = modelInstance.newInstance(Process.class);
        process.setExecutable(true); // Want to make sure it is executable by default in Modeler
        definitions.addChildElement(process);


        // For the diagram, a diagram and a plane element needs to be created. The plane is set in a diagram object and the diagram is added as a child element
        BpmnDiagram bpmnDiagram = modelInstance.newInstance(BpmnDiagram.class);
        BpmnPlane plane = modelInstance.newInstance(BpmnPlane.class);

        plane.setBpmnElement(process);

        bpmnDiagram.setBpmnPlane(plane);
        definitions.addChildElement(bpmnDiagram);

        NodeList pyShapeTypeList = doc.getElementsByTagName("pyShapeType");

        XPathExpression searchRequest = null;
        XPath xpath = XPathFactory.newInstance().newXPath();

        Iterator iter = pegaElementsMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            bpmnElement = (PegaToBPMNElement8) pegaElementsMap.get(key);
            searchRequest = xpath.compile("//pyShapeType[text() = '" + key + "']");
            pyShapeTypeList = (NodeList) searchRequest.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < pyShapeTypeList.getLength(); i++) {
                Node parentNode = pyShapeTypeList.item(i).getParentNode();
                searchRequest = xpath.compile("pyCoordX");
                NodeList xCoordList = (NodeList) searchRequest.evaluate(parentNode, XPathConstants.NODESET);
                searchRequest = xpath.compile("pyCoordY");
                NodeList yCoordList = (NodeList) searchRequest.evaluate(parentNode, XPathConstants.NODESET);
                Double newX = (Double.valueOf(xCoordList.item(0).getTextContent()) + 5) * 120;
                Double newY = (Double.valueOf(yCoordList.item(0).getTextContent()) + 5) * 120;

                searchRequest = xpath.compile("pyMOName");
                NodeList nameList = (NodeList) searchRequest.evaluate(parentNode, XPathConstants.NODESET);

                BpmnModelElementInstance element = (BpmnModelElementInstance) modelInstance.newInstance(bpmnElement.getType());
                if (nameList.getLength() > 0) {
                    element.setAttributeValue("name", nameList.item(0).getTextContent());
                }
                process.addChildElement(element);
                plane = DrawShape8.drawShape(plane, modelInstance, element, newX, newY, bpmnElement.getHeight(), bpmnElement.getWidth(), true, false);

                FlowNodeInfo8 fni = new FlowNodeInfo8(element.getAttributeValue("id"), Double.valueOf(xCoordList.item(0).getTextContent()), Double.valueOf(yCoordList.item(0).getTextContent()), newX, newY, bpmnElement.getType().toString(), bpmnElement.getHeight(), bpmnElement.getWidth());
                searchRequest = xpath.compile("pyMOId");
                NodeList idList = (NodeList) searchRequest.evaluate(parentNode, XPathConstants.NODESET);
                flowNodesMap.put(idList.item(0).getTextContent(), fni);
            }
        }

        // Find end events
        searchRequest = xpath.compile("//pyEndingActivities/rowdata");
        NodeList endingActivitiesList = (NodeList) searchRequest.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < endingActivitiesList.getLength(); i++) {
            Node endElement = endingActivitiesList.item(i);
            searchRequest = xpath.compile("//pyMOId[text() = '" + endElement.getTextContent() + "']");
            NodeList endingActivityList = (NodeList) searchRequest.evaluate(doc, XPathConstants.NODESET);
            Node parentNode = endingActivityList.item(0).getParentNode();
            searchRequest = xpath.compile("pyCoordX");
            NodeList xCoordList = (NodeList) searchRequest.evaluate(parentNode, XPathConstants.NODESET);
            searchRequest = xpath.compile("pyCoordY");
            NodeList yCoordList = (NodeList) searchRequest.evaluate(parentNode, XPathConstants.NODESET);
            Double newX = (Double.valueOf(xCoordList.item(0).getTextContent()) + 5) * 120;
            Double newY = (Double.valueOf(yCoordList.item(0).getTextContent()) + 5) * 120;
            BpmnModelElementInstance element = (BpmnModelElementInstance) modelInstance.newInstance(EndEvent.class);

            searchRequest = xpath.compile("pyMOName");
            NodeList nameList = (NodeList) searchRequest.evaluate(parentNode, XPathConstants.NODESET);
            if (nameList.getLength() > 0) {
                element.setAttributeValue("name", nameList.item(0).getTextContent());
            }
            process.addChildElement(element);
            plane = DrawShape8.drawShape(plane, modelInstance, element, newX, newY, 36d, 36d, true, false);

            FlowNodeInfo8 fni = new FlowNodeInfo8(element.getAttributeValue("id"), Double.valueOf(xCoordList.item(0).getTextContent()), Double.valueOf(yCoordList.item(0).getTextContent()), newX, newY, EndEvent.class.toString(), 36d, 36d);
            searchRequest = xpath.compile("pyMOId");
            NodeList idList = (NodeList) searchRequest.evaluate(parentNode, XPathConstants.NODESET);
            flowNodesMap.put(idList.item(0).getTextContent(), fni);

        }

        // Now draw the sequence flows
        searchRequest = xpath.compile("//pxObjClass[text() = 'Data-MO-Connector-Transition']");
        NodeList sequenceList = (NodeList) searchRequest.evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < sequenceList.getLength(); i++) {
            Node sequenceElement = sequenceList.item(i);
            Node parentNode = sequenceElement.getParentNode();
            searchRequest = xpath.compile("pyFrom");
            NodeList fromList = (NodeList) searchRequest.evaluate(parentNode, XPathConstants.NODESET);
            String fromId = fromList.item(0).getTextContent();
            searchRequest = xpath.compile("pyTo");
            NodeList toList = (NodeList) searchRequest.evaluate(parentNode, XPathConstants.NODESET);
            String toId = toList.item(0).getTextContent();
            FlowNodeInfo8 fromFNI = (FlowNodeInfo8) flowNodesMap.get(fromId);
            FlowNodeInfo8 toFNI = (FlowNodeInfo8) flowNodesMap.get(toId);
            SequenceFlow sf = modelInstance.newInstance(SequenceFlow.class);
            process.addChildElement(sf);

            // Get sequence flow name
            searchRequest = xpath.compile("pyMOName");
            NodeList nameList = (NodeList) searchRequest.evaluate(parentNode, XPathConstants.NODESET);
            if (nameList.getLength() > 0) {
                sf.setAttributeValue("name", nameList.item(0).getTextContent());
            }

            // Get sequence flow waypoint list - currently not used
            searchRequest = xpath.compile("pyConnectorPoints");
            NodeList connectorPointList = (NodeList) searchRequest.evaluate(parentNode, XPathConstants.NODESET);

            String targetId = toFNI.getNewId();
            String sourceId = fromFNI.getNewId();

            FlowNode targetFlowNode = modelInstance.getModelElementById(targetId);
            FlowNode sourceFlowNode = modelInstance.getModelElementById(sourceId);

            sf.setSource(sourceFlowNode);
            sf.setTarget(targetFlowNode);

            plane = DrawFlow8.drawFlow(plane, modelInstance, sf, fromFNI, toFNI, connectorPointList, 0d, 0d);
        }

        Bpmn.validateModel(modelInstance);

        return modelInstance;


        /*} catch (Exception e) {
            System.out.println("Exception during processing "+e.getMessage());
        } */
    }
}
