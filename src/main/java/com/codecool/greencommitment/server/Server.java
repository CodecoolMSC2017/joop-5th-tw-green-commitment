package com.codecool.greencommitment.server;

import com.codecool.greencommitment.common.UdpDiscovery;
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
    private HashMap<String, HashMap<Integer, List<Element>>> data = new HashMap<>();
    private String xmlFilePath = System.getProperty("user.home") + "/measurements.xml";
    private Logger logger;
    private List<String> loggedInClients = new ArrayList<>();
    private ServerInputHandler serverInputHandler;

    public Server(int portNumber) {
        this.portNumber = portNumber;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public List<String> getLoggedInClients() {
        return loggedInClients;
    }

    public void start() {

        new Thread(new UdpDiscovery(portNumber)).start();

        if (logger == null) {
            logger = new Logger(null);
        }
        ServerSocket serverSocket = createServerSocket();
        loadData();
        initHelperThreads();

        logger.log("Server", "Server started on port " + portNumber);
        Socket clientSocket;
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                new Thread(new ServerProtocol(clientSocket, data, logger, loggedInClients)).start();
            } catch (IOException e) {
                logger.log("Server", "Could not establish connection");
            }
        }
    }

    private ServerSocket createServerSocket() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            logger.log("Server", "Error creating server socket");
            e.printStackTrace();
            System.exit(1);
        }
        return serverSocket;
    }

    private void loadData() {
        if (new File(xmlFilePath).exists()) {
            try {
                loadXml();
            } catch (ParserConfigurationException | IOException | SAXException e) {
                logger.log("Server", "Loading data failed");
            }
        } else {
            logger.log("Server", "Could not find previous results");
        }
    }

    private void initHelperThreads() {
        serverInputHandler = new ServerInputHandler(data, xmlFilePath, logger);
        new Thread(serverInputHandler).start();

        new Thread(new AutoSaver(data, xmlFilePath, logger, 20)).start();
    }

    public void exit() {
        serverInputHandler.exit();
    }

    public void save() {
        serverInputHandler.save();
    }

    private void loadXml() throws ParserConfigurationException, IOException, SAXException {
        logger.log("Server", "Loading data... ");
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(xmlFilePath);

        HashMap<String, HashMap<Integer, List<Element>>> readData = new HashMap<>();


        Element rootElement = (Element) doc.getElementsByTagName("Clients").item(0);
        NodeList clients = rootElement.getElementsByTagName("Client");
        for (int i = 0; i < clients.getLength(); i++) {
            Element client = (Element) clients.item(i);
            HashMap<Integer, List<Element>> innerMap = new HashMap<>();
            readData.put(client.getAttribute("id"), innerMap);
            if (client.hasChildNodes()) {
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
        }
        data = readData;
        logger.log("Server", "Data loaded");
    }

}
