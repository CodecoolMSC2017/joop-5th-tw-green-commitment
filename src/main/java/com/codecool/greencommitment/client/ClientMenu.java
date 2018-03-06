package com.codecool.greencommitment.client;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ClientMenu {
    private Scanner cmdscan = new Scanner(System.in);
    private String line;
    private Client client;
    private boolean tempSens, airSens, windSens, isTransferring;

    public ClientMenu(Client client) throws IOException, InterruptedException {
        this.client = client;
        System.out.println(client.start());
        start();
    }

    public void start() throws IOException, InterruptedException {
        while(true) {
            System.out.println("\nWelcome to the Client!");
            System.out.println("----------------------");
            System.out.println("(1) Start/Stop Temperature sensor");
            System.out.println("(2) Start/Stop Air pressure sensor");
            System.out.println("(3) Start/Stop Wind speed sensor");
            System.out.println("(4) Send 50 second data burst");
            System.out.println("(5) Request Chart from server");
            System.out.println("(6) Exit");
            line = cmdscan.nextLine();

            switch (line) {
                case "1":
                    handleTempSens();
                    break;
                case "2":
                    handleAirSens();
                    break;
                case "3":
                    handleWindSens();
                    break;
                case "4":
                    handleDataTransferToServer();
                    break;
                case "5":
                    break;
                case "6":
                    client.logOut();
                    System.exit(0);
                    break;
                default:
                    System.out.println("No such choice!");
            }
        }
    }

    private void handleTempSens(){
        if (tempSens) {
            tempSens = false;
            System.out.println(client.removeSensors("Temperature"));
        } else {
            tempSens = true;
            System.out.println(client.addSensors("Temperature"));
        }
    }

    private void handleAirSens(){
        if (airSens) {
            airSens = false;
            System.out.println(client.removeSensors("Air pressure"));
        } else {
            airSens = true;
            System.out.println(client.addSensors("Air pressure"));
        }
    }

    private void handleWindSens(){
        if (windSens) {
            windSens = false;
            System.out.println(client.removeSensors("Windspeed"));
        } else {
            windSens = true;
            System.out.println(client.addSensors("Windspeed"));
        }
    }

    private void handleDataTransferToServer() throws IOException, InterruptedException {
        int interval = 5; //seconds
        System.out.print("\nSending data to server");
        for (int i=0;i<10;i++) {
            System.out.print(".");
            client.sendData();
            TimeUnit.SECONDS.sleep(interval);
        }
    }
}
