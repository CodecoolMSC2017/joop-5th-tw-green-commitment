package com.codecool.greencommitment;

import com.codecool.greencommitment.server.Server;
import com.codecool.greencommitment.client.Client;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        System.out.println("Usage: Main <port> <IP>");
        if (args.length == 0) {
            System.out.println("That's no good mate! Give me some ports and addresses!");
        }
        else if (args.length == 1) {
            new Server(Integer.valueOf(args[0])).start();
        }
        else if (args.length == 2) {
            try {
                new Client(Integer.valueOf(args[0]), args[1]).start();
            } catch (IOException e) {
                System.out.println("Couldn't create socket!");
            }
        }
        else {
            System.out.println("Usage: no args to start client, <server> to start server");
        }
    }
}
