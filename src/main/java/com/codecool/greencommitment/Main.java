package com.codecool.greencommitment;

import com.codecool.greencommitment.client.ClientMenu;
import com.codecool.greencommitment.server.Server;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("That's no good mate! Use it like this: Main <port> <IP>");
            //new ClientMenu(45454, "192.168.150.35");
        }
        else if (args.length == 1) {
            new Server(Integer.valueOf(args[0])).start();
        }
        else if (args.length == 2) {
            new ClientMenu(Integer.valueOf(args[0]), args[1]);
        }
        else {
            System.out.println("Usage: <port> to start server, <port> <server> to start client");
        }
    }
}
