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
    }

    // Method(s)
    private void handleClientId(){
        String pathToId = "resources/clientid";
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
            //Boo!
        }
    }

    private void writeId(String pathToId, String id){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(pathToId));
            bw.write(id);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            //Boo!
        }
    }

    private void sendId(){

    }

    private String getId(){

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
