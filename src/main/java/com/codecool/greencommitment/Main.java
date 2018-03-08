package com.codecool.greencommitment;

import com.codecool.greencommitment.client.ClientMenu;
import com.codecool.greencommitment.common.UdpDiscovery;
import com.codecool.greencommitment.gui.GCWindow;
import com.codecool.greencommitment.server.Server;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Starting GUI, please wait!");
            //new GCWindow().startWindow();
            //new ClientMenu(12324, "192.168.150.35");
            //new ClientMenu(7777, "192.168.150.4");
            //new ClientMenu(20000, "127.0.0.1");
            //new Server(20000).start();
            //UdpDiscovery udp = new UdpDiscovery();
            //udp.runServer();
            //System.out.println(udp.runClient()[0] + "," + udp.runClient()[1]);
        }
        else if (args.length == 1) {
            new Server(Integer.valueOf(args[0])).start();
        }
        else if (args.length == 2) {
            new ClientMenu(Integer.valueOf(args[0]), args[1]);
        }
        else {
            System.out.println("Usage: no arguments to start GUI,  <port> to start server, <port> <server> to start client");
        }
    }
}
