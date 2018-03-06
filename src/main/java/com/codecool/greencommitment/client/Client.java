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

    public void start() throws IOException, TransformerException {
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
        //sendData(new TemperatureSensor());
        System.out.println(logOut());

    }

    // Method(s)
    private String handleClientId() throws IOException {
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
        return ok;
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

    private void sendData(Sensor sensor) throws IOException, TransformerException {
        outWriter.println("measurement");
        if (inReader.readLine().equals("OK")) {
            DOMSource source = new DOMSource(sensor.readData());
            StreamResult result = new StreamResult(outputStream);

            transformer.transform(source, result);
        }
    }
}

