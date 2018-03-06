package com.codecool.greencommitment.client;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
    private TransformerFactory transformerFactory;
    private Transformer transformer;

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
            outWriter = new PrintWriter(outputStream, true);
            transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
        } catch (IOException | TransformerConfigurationException e) {
            e.printStackTrace();
        }
        handleClientId();
        /*try {
            sendData(new TemperatureSensor());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
            System.out.println(clientId);
            clientId = inReader.readLine();
            System.out.println(clientId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return clientId;
    }

    private void sendData(Sensor sensor) throws IOException, TransformerException {
        outWriter.println("measurement");
        if (inReader.readLine().equals("OK")) {
            DOMSource source = new DOMSource(sensor.readData());
            StreamResult result = new StreamResult(outputStream);

            transformer.transform(source, result);
        }
    }
}

