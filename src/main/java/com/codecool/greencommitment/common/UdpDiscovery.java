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
        byte[] byteArrayServerPort;
        int port=1234;
        InetAddress host;
        DatagramSocket socket = null;
        DatagramPacket packet = null;

        try {
            host = InetAddress.getByName(hostname);
            socket = new DatagramSocket(null);
            byteArrayServerPort = intToBytes(serverPort);
            packet = new DatagramPacket(byteArrayServerPort, byteArrayServerPort.length, host, port);
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

    private byte[] intToBytes(final int i) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

    private int bytesToInt(byte[] bytes){
        ByteBuffer wrappedBytes = ByteBuffer.wrap(bytes);
        return wrappedBytes.getInt();
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
            serverData = new String[]{packet.getAddress().getHostAddress(), String.valueOf(bytesToInt(packet.getData()))};
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        return serverData;
    }
}
