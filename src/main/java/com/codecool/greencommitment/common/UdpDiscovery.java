package com.codecool.greencommitment.common;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class UdpDiscovery {

    public String runServer() {

        String hostname= "192.168.150.255";
        int port=1234;
        InetAddress host;
        DatagramSocket socket;
        DatagramPacket packet;

        try {
            host = InetAddress.getByName(hostname);
            socket = new DatagramSocket(null);
            packet = new DatagramPacket(new byte[1], 0, host, port);
        } catch (SocketException e) {
            return "Could not create socket!";
        } catch (UnknownHostException e) {
            return "Couldn't find host!";
        }

        while(true) {
            try {
                socket.send(packet);
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                return "Sleep interrupted!";
            } catch (IOException e) {
                return "Error sending packet!";
            }
        }
    }


    public String[] runClient() {
        String[] serverData = new String[]{"-1","-1"};
        final int DEFAULT_PORT = 1234;
        DatagramSocket socket = null;
        DatagramPacket packet;

        try {
            socket = new DatagramSocket(DEFAULT_PORT);
        } catch(SocketException se) {
            System.out.println("Problem creating socket on port: " + DEFAULT_PORT );
        }

        packet = new DatagramPacket (new byte[1], 1);

        try {
            socket.receive (packet);
            //System.out.println("Received from: " + packet.getAddress () + ":" + packet.getPort ());
            serverData = new String[]{String.valueOf(packet.getAddress()), String.valueOf(packet.getPort())};
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        return serverData;
    }
}
