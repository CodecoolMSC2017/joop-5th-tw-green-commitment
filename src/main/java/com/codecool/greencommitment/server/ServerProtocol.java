package com.codecool.greencommitment.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ServerProtocol implements Runnable {

    private Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private BufferedReader inReader;
    private PrintWriter outWriter;
    private String clientId;
    private HashMap<String, HashMap<Integer, List<Element>>> data;
    private Logger logger;
    private List<String> loggedInClients;

    public ServerProtocol(
            Socket clientSocket,
            HashMap<String, HashMap<Integer, List<Element>>> data,
            Logger logger,
            List<String> loggedInClients) {
        this.clientSocket = clientSocket;
        this.data = data;
        this.logger = logger;
        this.loggedInClients = loggedInClients;
    }

    public void run() {
        try {
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inReader = new BufferedReader(new InputStreamReader(inputStream));
            outWriter = new PrintWriter(outputStream, true);
        } catch (IOException e) {
            logger.log("Server", "Could not open streams");
            e.printStackTrace();
            return;
        }
        try {
            identify();
        } catch (NumberFormatException e) {
            logger.log("Server", "Identification failed");
            return;
        } catch (AlreadyLoggedInException e) {
            logger.log("Server", "Client " + clientId + " is already logged in");
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String in;
        while (true) {
            try {
                in = inReader.readLine();
                if (in == null) {
                    logger.log("Server", "Client " + clientId + " disconnected");
                    loggedInClients.remove(clientId);
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
                        logger.log("Server", "Client " + clientId + " logged out");
                        loggedInClients.remove(clientId);
                        return;
                }
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private boolean alreadyLoggedIn() {
        return loggedInClients.contains(clientId);
    }

    private void identify() throws NumberFormatException, AlreadyLoggedInException, IOException {
        String id = inReader.readLine();
        if (id.equals("0")) {
            clientId = generateNewId();
            logger.log("Server", "New client " + clientId + " logged in");
            return;
        }
        clientId = id;
        if (alreadyLoggedIn()) {
            outWriter.println("error");
            throw new AlreadyLoggedInException();
        }
        if (!data.containsKey(id)) {
            data.put(id, new HashMap<>());
            logger.log("Server", "Unknown client " + clientId + " logged in");
        } else {
            logger.log("Server", "Client " + clientId + " logged in");
        }
        loggedInClients.add(String.valueOf(clientId));
        outWriter.println("ok");
    }

    private String generateNewId() {
        int id;
        do {
            id = new Random().nextInt((999 - 100) + 1) + 100;
        } while (data.containsKey(String.valueOf(id)));
        data.put(String.valueOf(id), new HashMap<>());
        outWriter.println(id);
        loggedInClients.add(String.valueOf(clientId));
        return String.valueOf(id);
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
            logger.log("Server", "Error receiving data from client " + clientId);
            outWriter.println("error");
            return;
        }
        if (document == null) {
            logger.log("Server", "Error receiving data from client " + clientId + " : document is null");
            outWriter.println("error");
            return;
        }
        outWriter.println("ok");

        Element measurement = (Element) document.getElementsByTagName("measurement").item(0);
        int id = Integer.parseInt(measurement.getAttribute("id"));
        if (!data.get(clientId).containsKey(id)) {
            data.get(clientId).put(id, new ArrayList<>());
        }
        logger.log("Client " + clientId, "Sent data from sensor " + id);
        data.get(clientId).get(id).add(measurement);
    }
}