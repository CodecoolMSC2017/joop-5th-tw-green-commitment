package com.codecool.greencommitment.common;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
        } catch (Exception e) {}
        return null;
    }

    public Document createDocument(int id, long time, double value, String type) {
        Document document = initDocument();
        if (document == null) { System.exit(1); }

        // Measurement / root
        Element root = document.createElement("measurement");
        root.setAttribute("id", Integer.toString(id));

        // Time
        Element timeE = document.createElement("time");
        timeE.setTextContent(String.valueOf(time));

        // Value
        Element valueE = document.createElement("value");
        valueE.setTextContent(String.valueOf(value));

        // Type
        Element typeE = document.createElement("type");
        typeE.setTextContent(type);

        // Add it to the document
        document.appendChild(root);

        return document;
    }
}
