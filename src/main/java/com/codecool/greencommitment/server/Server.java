package com.codecool.greencommitment.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private int portNumber;
    private HashMap<Integer, HashMap<Integer, List<Element>>> data = new HashMap<>();
    private String xmlFilePath = System.getProperty("user.home") + "/measurements.xml";
    private Thread stopper = new Thread(new ServerStopper());
    private Thread autosaver = new Thread(new AutoSaver(10));

    public Server(int portNumber) {
        this.portNumber = portNumber;
    }

    public void start() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println("Error creating server socket");
            e.printStackTrace();
            System.exit(1);
        }
        if (new File(xmlFilePath).exists()) {
            try {
                loadXml();
            } catch (ParserConfigurationException | IOException | SAXException e) {
                System.out.println("Loading data failed");
            }
        } else {
            System.out.println("Could not find previous results");
        }
        stopper.start();
        autosaver.start();
        System.out.println("Server started on port " + portNumber);
        Socket clientSocket;
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                new Thread(new Protocol(clientSocket)).start();
            } catch (IOException e) {
                System.out.println("Could not establish connection");
            }
        }
    }

    private void loadXml() throws ParserConfigurationException, IOException, SAXException {
        System.out.print("Loading data... ");
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
        System.out.println("Data loaded");
    }

    private class Protocol implements Runnable {

        private Socket clientSocket;
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;
        private BufferedReader inReader;
        private PrintWriter outWriter;
        private int clientId;

        Protocol(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                inputStream = new ObjectInputStream(clientSocket.getInputStream());
                outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                inReader = new BufferedReader(new InputStreamReader(inputStream));
                outWriter = new PrintWriter(outputStream, true);
            } catch (IOException e) {
                System.out.println("Could not open streams");
                e.printStackTrace();
                return;
            }
            try {
                identify();
            } catch (NumberFormatException e) {
                System.out.println("Identification failed");
                return;
            }
            String in;
            while (true) {
                try {
                    in = inReader.readLine();
                    if (in == null) {
                        System.out.println("Client " + clientId + " disconnected");
                        return;
                    }
                    switch (in) {
                        case "measurement":
                            readMeasurement();
                            break;
                        case "request":
                            sendData();
                            break;
                        case "logout":
                            System.out.println("Client " + clientId + " logged out");
                            outWriter.println(in);
                            return;
                    }
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        private void identify() throws NumberFormatException {
            try {
                String in = inReader.readLine();
                int id = Integer.parseInt(in);
                if (id == 0) {
                    clientId = generateNewId();
                    System.out.println("New client " + clientId + " logged in");
                    return;
                }
                clientId = id;
                if (!data.containsKey(id)) {
                    data.put(id, new HashMap<>());
                    System.out.println("Unknown client " + clientId + " logged in");
                } else {
                    System.out.println("Client " + clientId + " logged in");
                }
                outWriter.println("ok");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private int generateNewId() {
            int id;
            do {
                id = new Random().nextInt((999 - 100) + 1) + 100;
            } while (data.containsKey(id));
            data.put(id, new HashMap<>());
            outWriter.println(id);
            return id;
        }

        private void sendData() {
            outWriter.println(data.get(clientId));
        }

        private void readMeasurement() {
            Document document;
            try {
                outWriter.println("ok");
                document = (Document) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error receiving data from client " + clientId);
                outWriter.println("error");
                return;
            }
            if (document == null) {
                System.out.println("Error receiving data from client " + clientId + " : document is null");
                outWriter.println("error");
                return;
            }
            System.out.print("Data received from client " + clientId);
            outWriter.println("ok");

            Element measurement = (Element) document.getElementsByTagName("measurement").item(0);
            int id = Integer.parseInt(measurement.getAttribute("id"));
            if (!data.get(clientId).containsKey(id)) {
                data.get(clientId).put(id, new ArrayList<>());
            }
            System.out.println(", from sensor " + id);
            data.get(clientId).get(id).add(measurement);
        }
    }

    private class ServerStopper implements Runnable {

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
                            save();
                            System.exit(0);
                        default:
                            System.out.println("Unknown command: " + command);
                    }
                }
            }
        }

        private void save() {
            try {
                saveXml();
                System.out.println("Data saved");
            } catch (TransformerException | ParserConfigurationException e) {
                System.out.println("Saving failed");
            }
        }

        protected void saveXml() throws TransformerException, ParserConfigurationException {
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("Clients");
            doc.appendChild(rootElement);

            for (Integer clientId : data.keySet()) {
                Element client = doc.createElement("Client");
                client.setAttribute("id", clientId.toString());
                rootElement.appendChild(client);

                Element sensors = doc.createElement("Sensors");
                client.appendChild(sensors);

                HashMap<Integer, List<Element>> clientData = data.get(clientId);
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
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlFilePath));
            transformer.transform(source, result);
        }
    }

    private class AutoSaver extends ServerStopper {

        private int intervals;

        public AutoSaver(int intervals) {
            this.intervals = intervals * 1000;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(intervals);
                    save();
                }
            } catch (InterruptedException e) {
                System.out.println("Autosaver stopped due to interruption");
            }
        }

        private void save() {
            try {
                saveXml();
                System.out.println("Data autosaved");
            } catch (TransformerException | ParserConfigurationException e) {
                System.out.println("Autosaving failed");
            }
        }
    }
}
