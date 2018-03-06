package com.codecool.greencommitment.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private String clientId;
    private Socket socket;
    private List<Sensor> sensors;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private BufferedReader inReader;
    private PrintWriter outWriter;

    // Constructor(s)
    public Client(int port, String host) throws IOException {
        socket = new Socket(host, port);
        sensors = new ArrayList<>();
    }

    public void start() {
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            inReader = new BufferedReader(new InputStreamReader(inputStream));
            outWriter = new PrintWriter(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        handleClientId();
        try {
            sendData(new TemperatureSensor());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method(s)
    private void handleClientId() {
        String pathToId = System.getProperty("user.home") + "/clientid";
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
        outWriter.println(clientId);
    }

    private String getId() {
        String clientId = "0";
        try {
            outWriter.println(clientId);
            clientId = inReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clientId;
    }

    private void sendData(Sensor sensor) throws IOException {
        outWriter.println("measurement");
        outputStream.writeObject(sensor.readData());
    }
}

