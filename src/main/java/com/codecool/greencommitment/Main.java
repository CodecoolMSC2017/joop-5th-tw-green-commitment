package com.codecool.greencommitment;

import com.codecool.greencommitment.client.ClientMenu;
import com.codecool.greencommitment.server.Server;
import com.codecool.greencommitment.client.Client;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("That's no good mate! Use it like this: Main <port> <IP>");
            /*try {
                new ClientMenu(new Client(45454, "192.168.150.35"));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }*/
        }
        else if (args.length == 1) {
            new Server(Integer.valueOf(args[0])).start();
        }
        else if (args.length == 2) {
            try {
                new Client(Integer.valueOf(args[0]), args[1]).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Usage: <port> to start server, <port> <server> to start client");
        }
    }
}
