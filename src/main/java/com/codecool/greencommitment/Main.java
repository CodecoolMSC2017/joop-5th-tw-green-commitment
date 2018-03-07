package com.codecool.greencommitment;

import com.codecool.greencommitment.client.ClientMenu;
import com.codecool.greencommitment.gui.GCWindow;
import com.codecool.greencommitment.server.Server;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Starting GUI, please wait!");
            new GCWindow().startWindow();
            //new ClientMenu(30000, "192.168.150.35");
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
