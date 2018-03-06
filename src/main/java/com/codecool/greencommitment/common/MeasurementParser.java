package com.codecool.greencommitment.common;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MeasurementParser {

    private Document initDocument() {
        Document document;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.newDocument();
            return document;
        } catch (Exception ignored) {
        }
        return null;
    }

    public Document createDocument(int id, long time, double value, String type) {
        Document document = initDocument();
        if (document == null) {
            System.exit(1);
        }

        //Name the document
        //document.createElementNS("codecool.com", "measurement");

        // Measurement / root
        Element root = document.createElement("measurement");
        root.setAttribute("id", Integer.toString(id));
        root.setAttribute("time", String.valueOf(time));
        root.setAttribute("value", String.valueOf(value));
        root.setAttribute("type", type);

        // Add it to the document
        document.appendChild(root);

        return document;
    }

    public void saveToFile() {

    }
}
