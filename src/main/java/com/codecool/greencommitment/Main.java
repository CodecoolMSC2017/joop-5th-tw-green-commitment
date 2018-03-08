package com.codecool.greencommitment;

import com.codecool.greencommitment.client.ClientMenu;
import com.codecool.greencommitment.gui.GCWindow;
import com.codecool.greencommitment.server.Server;

public class Main {

    public static void main(String[] args) {
        if (args[0].equals("gui")) {
            System.out.println("Starting GUI, please wait!");
            new GCWindow().startWindow();
            //new Server(20000).start();
            //new ClientMenu();
        }
        else if (args[0].equals("client")) {
            new ClientMenu();
        }
        else if (args[0].equals("server")) {
            new Server(Integer.valueOf(args[1])).start();
        }
        else {
            System.out.println("Usage: <gui> to start gui (both Client and Server),  " +
                                "<client> to start client, <server> <port> to start server");
        }
    }
}
