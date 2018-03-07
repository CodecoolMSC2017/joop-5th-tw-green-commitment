package com.codecool.greencommitment.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private int portNumber;
    private HashMap<Integer, HashMap<Integer, List<Element>>> data = new HashMap<>();
    private String xmlFilePath = System.getProperty("user.home") + "/measurements.xml";
    private ServerInputHandler serverInputHandler = new ServerInputHandler(data, xmlFilePath);
    private Thread autosaver = new Thread(new AutoSaver(data, xmlFilePath, 20));
    private Logger logger;

    public Server(int portNumber) {
        this.portNumber = portNumber;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void start() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            logger.log("Error creating server socket");
            e.printStackTrace();
            System.exit(1);
        }
        if (new File(xmlFilePath).exists()) {
            try {
                loadXml();
            } catch (ParserConfigurationException | IOException | SAXException e) {
                logger.log("Loading data failed");
            }
        } else {
            logger.log("Could not find previous results");
        }
        Thread serverInputHandlerThread = new Thread(serverInputHandler);
        serverInputHandlerThread.start();
        autosaver.start();
        logger.log("Server started on port " + portNumber);
        Socket clientSocket;
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                new Thread(new ServerProtocol(clientSocket, data)).start();
            } catch (IOException e) {
                logger.log("Could not establish connection");
            }
        }
    }

    public void exit() {
        serverInputHandler.exit();
    }

    private void loadXml() throws ParserConfigurationException, IOException, SAXException {
        logger.log("Loading data... ");
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(xmlFilePath);

        HashMap<Integer, HashMap<Integer, List<Element>>> readData = new HashMap<>();


        Element rootElement = (Element) doc.getElementsByTagName("Clients").item(0);
        NodeList clients = rootElement.getElementsByTagName("Client");
        for (int i = 0; i < clients.getLength(); i++) {
            Element client = (Element) clients.item(i);
            HashMap<Integer, List<Element>> innerMap = new HashMap<>();
            readData.put(Integer.parseInt(client.getAttribute("id")), innerMap);
            Element sensorsE = (Element) client.getElementsByTagName("Sensors").item(0);
            NodeList sensors = sensorsE.getElementsByTagName("Sensor");
            for (int j = 0; j < sensors.getLength(); j++) {
                Element sensor = (Element) sensors.item(j);
                List<Element> measurementsList = new ArrayList<>();
                innerMap.put(Integer.parseInt(sensor.getAttribute("id")), measurementsList);
                NodeList measurements = sensor.getElementsByTagName("measurement");
                for (int k = 0; k < measurements.getLength(); k++) {
                    Element measurement = (Element) measurements.item(k);
                    measurementsList.add(measurement);
                }
            }
        }
        data = readData;
        logger.log("Data loaded");
    }

}
