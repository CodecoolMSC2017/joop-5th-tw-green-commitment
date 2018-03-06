package com.codecool.greencommitment.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    private String clientId;
    private Socket socket;
    private List<Sensor> sensors;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;


    // Constructor(s)
    public Client(int port, String host) throws IOException {
        socket = new Socket(host, port);
        sensors = new ArrayList<>();
    }

    public void start() {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        handleClientId();
        try {
            sendData(new TemperatureSensor());
            new Scanner(System.in).nextLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method(s)
    private void handleClientId() {
        String pathToId = "src/main/resources/clientid.txt";
        File idFile = new File(pathToId);
        if (idFile.exists()) {
            readId(pathToId);
            sendId();
        } else {
            writeId(pathToId, getId());
        }
    }

    private void readId(String pathToId) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(pathToId));
            this.clientId = br.readLine();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeId(String pathToId, String id) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(pathToId));
            bw.write(id);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendId() {
        try {
            outputStream.writeObject(clientId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getId() {
        String clientId = "0";
        try {
            outputStream.writeObject(clientId);
            clientId = (String) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clientId;
    }

    private void sendData(Sensor sensor) throws IOException {
        outputStream.writeObject("measurement");
        outputStream.writeObject(sensor.readData());
    }
}

