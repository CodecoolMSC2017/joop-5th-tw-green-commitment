package com.codecool.greencommitment.client;

import org.w3c.dom.Document;

import java.io.*;
import java.net.Socket;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

public class Client {
    private String clientId;
    private Socket socket;
    private Map<String, Sensor> sensors;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private BufferedReader inReader;
    private PrintWriter outWriter;

    // Constructor(s)
    public Client(int port, String host) throws IOException {
        socket = new Socket(host, port);
        sensors = new HashMap<>();
    }

    public String start() throws IOException {
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream());
        inReader = new BufferedReader(new InputStreamReader(inputStream));
        outWriter = new PrintWriter(outputStream, true);

        if (handleLogin()) {
            return "Logged in to server!";
        } else {
            return"Login unsuccessful!";
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

    protected String logOut() throws IOException {
        String logOut = "logout";
        outWriter.println(logOut);
        return inReader.readLine();
    }

    protected String sendData() throws IOException, ConcurrentModificationException, NullPointerException {
        for (Sensor s:sensors.values()){
            Document doc = s.readData();
            outWriter.println("measurement");
            if (inReader.readLine().equals("ok")) {
                outputStream.writeObject(doc);
                if (inReader.readLine().equals("error")){
                    return "Server data handling error. Please restart the client!";
                }
            }
        }
        return "ok";
    }

    protected String addSensors(String type) throws ConcurrentModificationException {
        if (type.equals("Temperature")){
            sensors.put(type, new TemperatureSensor());
        }
        else if (type.equals("Air pressure")){
            sensors.put(type, new AirPressureSensor());
        }
        else if (type.equals("Windspeed")){
            sensors.put(type, new WindSpeedSensor());
        }

        return type + " sensor turned on!";
    }
    protected String removeSensors(String type) throws ConcurrentModificationException {
        for (String k:sensors.keySet()){
            if (k.equals(type)){
                sensors.remove(type);
            }
        }
        return type + " sensor turned off!";
    }
}

