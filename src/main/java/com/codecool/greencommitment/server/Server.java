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
                outWriter = new PrintWriter(outputStream);
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
            System.out.println("Client " + clientId + " identified and connected");
            String in;
            while (true) {
                try {
                    in = inReader.readLine();
                    if (in.equals("measurement")) {
                        readMeasurement();
                    } else if (in.equals("request")) {
                        sendData();
                    } else if (in.equals("logout")) {
                        System.out.println("Logged out " + clientId);
                        outWriter.println("closed");
                        measurementsSaveToXml();
                        return;
                    }
                } catch (IOException | ClassNotFoundException e) {
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
                    outWriter.println("ok");
                    clientId = id;
                    System.out.println("Id " + id + " recognised");
                } else if (id == 0) {
                    generateNewId();
                }
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
            System.out.println("Assigned new id " + id + "to client");
        }

        private void sendData() {
            outWriter.println(data.get(clientId));
        }

        private void readMeasurement() throws IOException, ClassNotFoundException {
            Document document = (Document) inputStream.readObject();
            Element measurement = (Element) document.getElementsByTagName("measurement").item(0);
            System.out.println(document);
            int id = Integer.parseInt(measurement.getAttribute("id"));
            if (!data.get(clientId).containsKey(id)) {
                data.get(clientId).put(id, new ArrayList<>());
            }
            data.get(clientId).get(id).add(measurement);
        }
    }
}
