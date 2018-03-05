package com.codecool.greencommitment.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {

    private int portNumber;
    private HashMap<Integer, List<Element>> data;

    public Server(int portNumber) {
        this.portNumber = portNumber;
    }

    public void start() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectInputStream inputStream;
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                inputStream = new ObjectInputStream(clientSocket.getInputStream());
                new Thread(new Protocol((Document) inputStream.readObject())).start();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    class Protocol implements Runnable {

        private Document document;

        Protocol(Document document) {
            this.document = document;
        }

        public void run() {
            Element measurement = (Element) document.getElementsByTagName("measurement").item(0);
            int id = Integer.parseInt(measurement.getAttribute("id"));
            if (!data.containsKey(id)) {
                data.put(id, new ArrayList<>());
            }
            data.get(id).add(measurement);
        }
    }
}
