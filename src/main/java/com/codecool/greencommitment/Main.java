package com.codecool.greencommitment;

import com.codecool.greencommitment.server.Server;
//import com.codecool.greencommitment.client.Client;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            //new Client().start();
        } else if (args[0].equals("server")) {
            new Server().start();
        } else {
            System.out.println("Usage: no args to start client, <server> to start server");
        }
    }
}
