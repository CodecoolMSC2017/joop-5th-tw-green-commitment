package com.codecool.greencommitment.common;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

public class UdpDiscovery implements Runnable{
    private int serverPort;

    public UdpDiscovery() {
    }

    public UdpDiscovery(int serverPort) {
        this.serverPort = serverPort;
    }

    //Run for server
    public void run() {

        String hostname= "192.168.150.255";
        int port=1234;
        InetAddress host;
        DatagramSocket socket = null;
        DatagramPacket packet = null;

        try {
            host = InetAddress.getByName(hostname);
            socket = new DatagramSocket(null);
            packet = new DatagramPacket(intToBytes(serverPort), 0, host, port);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        while(true) {
            try {
                socket.send(packet);
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] intToBytes(final int i) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
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
            serverData = new String[]{packet.getAddress().getHostAddress(), String.valueOf(packet.getPort())};
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        return serverData;
    }
}
