package com.codecool.greencommitment.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private String clientId;
    private Socket socket;
    private List<Sensor> sensors;


    // Constructor(s)
    public Client(int port, String host) throws IOException{
        socket = new Socket(host, port);
        sensors = new ArrayList<>();
    }
    public void start(){
        handleClientId();
        try {
            sendData(new TemperatureSensor());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method(s)
    private void handleClientId(){
        String pathToId = "src/main/resources/clientid";
        File idFile = new File(pathToId);
        if (idFile.exists()){
            readId(pathToId);
            sendId();
        } else {
            writeId(pathToId, getId());
        }
    }

    private void readId(String pathToId){
        try {
            BufferedReader br = new BufferedReader(new FileReader(pathToId));
            this.clientId = br.readLine();
            br.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void writeId(String pathToId, String id){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(pathToId));
            bw.write(id);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendId(){
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(clientId);
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private String getId(){
        String clientId = "0";
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(clientId);
            clientId = (String) in.readObject();

            out.close();
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return clientId;
    }

    public void sendData(Sensor sensor) throws IOException {

        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            out.writeObject("measurement");
            out.writeObject(sensor.readData());

            out.close();
        } finally {
            //socket.close();
        }
    }
}
