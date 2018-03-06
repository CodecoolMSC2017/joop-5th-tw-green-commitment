package com.codecool.greencommitment.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Server {

    private int portNumber;
    private HashMap<Integer, List<Element>> data = new HashMap<>();

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
        private int id;

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
            identify();
            System.out.println("Client " + id + " has connected");
            String in;
            while (true) {
                try {
                    in = inReader.readLine();
                    if (in.equals("measurement")) {
                        readMeasurement();
                    } else if (in.split(" ")[0].equals("request")) {
                        sendData(in.split(" ")[1]);
                    } else if (in.equals("logout")) {
                        System.out.println("Logged out " + id);
                        outWriter.println("closed");
                        return;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        private void identify() {
            try {
                String in = inReader.readLine();
                System.out.println("Id sent: " + in);
                int id = Integer.parseInt(in);
                if (data.containsKey(id)) {
                    outWriter.println("ok");
                    this.id = id;
                } else {
                    generateNewId();
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }

        private void generateNewId() throws IOException {
            int id;
            do {
                id = new Random().nextInt((999 - 100) + 1) + 100;
            } while (data.containsKey(id));
            this.id = id;
            data.put(id, new ArrayList<>());
            outWriter.println(String.valueOf(id));
        }

        private void sendData(String id) throws IOException {
            int idAsInt = Integer.parseInt(id);
            if (data.containsKey(idAsInt)) {
                outWriter.println(data.get(idAsInt));
                return;
            }
            outputStream.writeObject(new ArrayList<Integer>());
        }

        private void readMeasurement() throws IOException, ClassNotFoundException {
            Document document = (Document) inputStream.readObject();
            Element measurement = (Element) document.getElementsByTagName("measurement").item(0);
            System.out.println(document);
            int id = Integer.parseInt(measurement.getAttribute("id"));
            data.get(id).add(measurement);
        }
    }
}
