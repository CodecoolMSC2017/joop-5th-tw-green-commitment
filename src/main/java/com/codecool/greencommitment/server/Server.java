package com.codecool.greencommitment.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.print.Doc;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
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
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(new Protocol(clientSocket)).start();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }

    class Protocol implements Runnable {

        private Socket clientSocket;
        private Document document;
        private ObjectInputStream inputStream;
        private ObjectOutputStream outputStream;

        public Protocol(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            String in = null;
            try {
                inputStream = new ObjectInputStream(clientSocket.getInputStream());
                outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                in = (String) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                if (in.equals("measurement")) {
                    readMeasurement();
                } else if (in.split(" ")[0].equals("request")) {
                    sendData(in.split(" ")[1]);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void sendData(String id) throws IOException {
            int idAsInt = Integer.parseInt(id);
            if (data.containsKey(idAsInt)) {
                outputStream.writeObject(data.get(idAsInt));
                return;
            }
            outputStream.writeObject(new ArrayList<Integer>());
        }

        private void readMeasurement() throws IOException, ClassNotFoundException {
            document = (Document) inputStream.readObject();
            Element measurement = (Element) document.getElementsByTagName("measurement").item(0);
            int id = Integer.parseInt(measurement.getAttribute("id"));
            if (!data.containsKey(id)) {
                data.put(id, new ArrayList<>());
            }
            data.get(id).add(measurement);
        }
    }
}
