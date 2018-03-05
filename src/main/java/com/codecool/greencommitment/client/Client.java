package com.codecool.greencommitment.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private Socket socket;
    private List<Sensor> sensors;


    // Constructor(s)
    public Client(String host, int port) throws IOException{
        socket = new Socket(host, port);
        sensors = new ArrayList<>();
    }


    // Method(s)
    public void sendData(Sensor sensor) throws IOException {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            out.writeObject("measurement");
            out.writeObject(sensor.readData());

            out.close();
        } finally {
            socket.close();
        }
    }
}
