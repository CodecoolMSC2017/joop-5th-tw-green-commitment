package com.codecool.greencommitment.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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
        System.out.println("Server started on port " + portNumber);
        Socket clientSocket;
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                new Thread(new Protocol(clientSocket)).start();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    class Protocol implements Runnable {

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
            System.out.println("Client connected, waiting for id");
            try {
                identify();
            } catch (NumberFormatException e) {
                System.out.println("Identification failed");
                return;
            }
            System.out.println("Client " + clientId + " logged in");
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
                            System.out.println(clientId + " logged out");
                            outWriter.println(in);
                            return;
                    }
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        private void measurementsSaveToXml() {
            File f = new File(System.getProperty("user.home") + "measurements.xml");
            /*if (f.exists() && !f.isDirectory()) {
                appendXmlToExistsFile("/resources/" + clientId + ".xml");
            }
            */
            createNewFile(f);
        }

        private void createNewFile(File f) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.newDocument();
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(f);

                Element rootElement = doc.createElement("Clients");
                doc.appendChild(rootElement);

                for (Integer clientId: data.keySet()) {
                    Element client = doc.createElement("Client");
                    client.setAttribute("id", Integer.toString(clientId));
                    rootElement.appendChild(client);

                    Set<Integer> sensors = data.get(clientId).keySet();
                    for (Integer sensorId: sensors) {
                        for (Element measurement: data.get(clientId).get(sensorId)) {
                            client.appendChild(measurement);
                        }
                    }
                }
                transformer.transform(source, result);

            } catch (ParserConfigurationException | TransformerException e) {
                e.printStackTrace();
            }
        }

        private void identify() throws NumberFormatException {
            try {
                String in = inReader.readLine();
                System.out.println("Received id " + in);
                int id = Integer.parseInt(in);
                if (data.containsKey(id)) {
                    System.out.println("Id " + id + " recognised");
                } else if (id == 0) {
                    generateNewId();
                    return;
                } else {
                    data.put(id, new HashMap<>());
                    System.out.println("Id " + id + " is not recognised, creating entry for it");
                }
                clientId = id;
                outWriter.println("ok");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void generateNewId() {
            int id;
            do {
                id = new Random().nextInt((999 - 100) + 1) + 100;
            } while (data.containsKey(id));
            clientId = id;
            data.put(id, new HashMap<>());
            outWriter.println(id);
            System.out.println("Assigned new id " + id + " to client");
        }

        private void sendData() {
            outWriter.println(data.get(clientId));
        }

        private void readMeasurement() {
            Document document;
            try {
                System.out.println("Receiving measurement");
                outWriter.println("ok");
                document = (Document) inputStream.readObject();
                System.out.println("Document received");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error receiving data from client " + clientId);
                outWriter.println("error");
                e.printStackTrace();
                return;
            }
            if (document == null) {
                System.out.println("Document is null");
                outWriter.println("error");
                return;
            }
            System.out.println("Data received from client " + clientId);
            outWriter.println("ok");

            Element measurement = (Element) document.getElementsByTagName("measurement").item(0);
            int id = Integer.parseInt(measurement.getAttribute("id"));
            System.out.println("Sensor id: " + id);
            System.out.println(measurement.getElementsByTagName("type").item(0).getTextContent());
            if (!data.get(clientId).containsKey(id)) {
                data.get(clientId).put(id, new ArrayList<>());
            }
            data.get(clientId).get(id).add(measurement);
        }
    }
}
