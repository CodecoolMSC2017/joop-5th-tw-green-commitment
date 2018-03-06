package com.codecool.greencommitment.client;

import org.w3c.dom.Document;

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

    public void start() throws IOException {
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        inReader = new BufferedReader(new InputStreamReader(inputStream));
        outWriter = new PrintWriter(outputStream, true);

        if (handleLogin()) {
            sendData(new TemperatureSensor());
        } else {
            System.out.println("Login unsuccessful!");
            logOut();
        }

    }

    // Method(s)
    private boolean handleLogin() throws IOException {
        String pathToId = System.getProperty("user.home") + "/clientid";
        File idFile = new File(pathToId);
        String ok;
        if (idFile.exists()) {
            readId(pathToId);
            ok = sendId();
        } else if (!(idFile.exists())) {
            writeId(pathToId, getId());
            ok = "ok";
        } else {
            ok = "no";
        }
        return ok.equals("ok");
    }

    private void readId(String pathToId) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(pathToId));
        this.clientId = br.readLine();
        br.close();
    }

    private void writeId(String pathToId, String id) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(pathToId));
        bw.write(id);
        bw.flush();
        bw.close();
    }

    private String sendId() throws IOException {
        outWriter.println(clientId);
        return inReader.readLine();
    }

    private String getId() throws IOException {
        String clientId = "0";
        outWriter.println(clientId);
        clientId = inReader.readLine();
        return clientId;
    }

    private String logOut() throws IOException {
        String logOut = "logout";
        outWriter.println(logOut);
        return inReader.readLine();
    }

    private String sendData(Sensor sensor) throws IOException {

        Document doc = sensor.readData();
        outWriter.println("measurement");
        String ok = inReader.readLine();
        if (ok.equals("ok")) {
            outputStream.writeObject(doc);
        }
        return inReader.readLine();
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }
}

