package com.codecool.greencommitment.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ServerProtocol implements Runnable {

    private Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private BufferedReader inReader;
    private PrintWriter outWriter;
    private int clientId;
    private HashMap<Integer, HashMap<Integer, List<Element>>> data;
    private Logger logger;

    public ServerProtocol(
            Socket clientSocket,
            HashMap<Integer, HashMap<Integer, List<Element>>> data,
            Logger logger) {
        this.clientSocket = clientSocket;
        this.data = data;
        this.logger = logger;
    }

    public void run() {
        try {
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inReader = new BufferedReader(new InputStreamReader(inputStream));
            outWriter = new PrintWriter(outputStream, true);
        } catch (IOException e) {
            logger.log("Could not open streams");
            e.printStackTrace();
            return;
        }
        try {
            identify();
        } catch (NumberFormatException e) {
            logger.log("Identification failed");
            return;
        }
        String in;
        while (true) {
            try {
                in = inReader.readLine();
                if (in == null) {
                    logger.log("Client " + clientId + " disconnected");
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
                        logger.log("Client " + clientId + " logged out");
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
                logger.log("New client " + clientId + " logged in");
                return;
            }
            clientId = id;
            if (!data.containsKey(id)) {
                data.put(id, new HashMap<>());
                logger.log("Unknown client " + clientId + " logged in");
            } else {
                logger.log("Client " + clientId + " logged in");
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
            logger.log("Error receiving data from client " + clientId);
            outWriter.println("error");
            return;
        }
        if (document == null) {
            logger.log("Error receiving data from client " + clientId + " : document is null");
            outWriter.println("error");
            return;
        }
        logger.log("Data received from client " + clientId);
        outWriter.println("ok");

        Element measurement = (Element) document.getElementsByTagName("measurement").item(0);
        int id = Integer.parseInt(measurement.getAttribute("id"));
        if (!data.get(clientId).containsKey(id)) {
            data.get(clientId).put(id, new ArrayList<>());
        }
        logger.log(", from sensor " + id);
        data.get(clientId).get(id).add(measurement);
    }
}