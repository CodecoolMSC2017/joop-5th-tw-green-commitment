package com.codecool.greencommitment.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class ServerInputHandler implements Runnable {

    private HashMap<String, HashMap<Integer, List<Element>>> data;
    private String xmlFilePath;
    protected Logger logger;

    ServerInputHandler(
            HashMap<String, HashMap<Integer, List<Element>>> data,
            String xmlFilePath,
            Logger logger) {
        this.data = data;
        this.xmlFilePath = xmlFilePath;
        this.logger = logger;
    }

    public void run() {
        String command;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scanner.hasNext()) {
                command = scanner.nextLine().toLowerCase();
                switch (command) {
                    case "save":
                        save();
                        break;
                    case "exit":
                        exit();
                    default:
                        logger.log("Server", "Unknown command: " + command);
                }
            }
        }
    }

    void exit() {
        save();
        System.exit(0);
    }

    public void save() {
        try {
            saveXml();
            logger.log("Server", "Data saved");
        } catch (TransformerException | ParserConfigurationException e) {
            logger.log("Server", "Saving failed");
        }
    }

    protected void saveXml() throws TransformerException, ParserConfigurationException {
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element rootElement = doc.createElement("Clients");
        doc.appendChild(rootElement);

        for (String clientId : data.keySet()) {
            Element client = doc.createElement("Client");
            client.setAttribute("id", clientId);
            rootElement.appendChild(client);

            HashMap<Integer, List<Element>> clientData = data.get(clientId);

            if (clientData.size() > 0) {
                Element sensors = doc.createElement("Sensors");
                client.appendChild(sensors);
                Element sensor;
                for (Integer sensorId : clientData.keySet()) {
                    sensor = doc.createElement("Sensor");
                    sensor.setAttribute("id", sensorId.toString());
                    sensors.appendChild(sensor);
                    for (Element measurement : clientData.get(sensorId)) {
                        Element measurementCopy = doc.createElement("measurement");
                        measurementCopy.setAttribute("id", measurement.getAttribute("id"));
                        measurementCopy.setAttribute("time", measurement.getAttribute("time"));
                        measurementCopy.setAttribute("value", measurement.getAttribute("value"));
                        measurementCopy.setAttribute("type", measurement.getAttribute("type"));

                        sensor.appendChild(measurementCopy);
                    }
                }
            }
        }
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(xmlFilePath));
        transformer.transform(source, result);
    }
}