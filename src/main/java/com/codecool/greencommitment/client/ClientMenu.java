package com.codecool.greencommitment.client;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ClientMenu {
    private Scanner cmdScan = new Scanner(System.in);
    private String line;
    private Client client;
    private boolean tempSens, airSens, windSens, isTransferring;

    public ClientMenu(int port, String hostName) {
        try {
            this.client = new Client(port, hostName);
        } catch (IOException e) {
            System.out.println("Couldn't open server port! Please consult your server admin! :)");
            System.exit(1);
        }
        try {
            String msg = client.start();
            if (msg.equals("Login unsuccessful!")){
                System.out.println(msg + "Exiting!");
                System.exit(1);
            } else {
                System.out.println(msg);
            }
        } catch (IOException e) {
            System.out.println("ID file couldn't be written or read in. Please do something about that! :) ");
        }
        try {
            start();
        } catch (IOException e) {
            System.out.println("Couldn't send logout signal :(");
            System.exit(1);
        }
    }

    private void start() throws IOException {
        while(true) {
            System.out.println("\nWelcome to the Client!");
            System.out.println("----------------------");
            System.out.println("(1) Start/Stop Temperature sensor");
            System.out.println("(2) Start/Stop Air pressure sensor");
            System.out.println("(3) Start/Stop Wind speed sensor");
            System.out.println("---------------------------------");
            System.out.println("(4) Start/Stop Data send");
            System.out.println("(5) Is it transferring?");
            System.out.println("(6) Request Chart from server");
            System.out.println("-----------------------------");
            System.out.println("(7) Exit");
            line = cmdScan.nextLine();

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
                    if (isTransferring){
                        isTransferring = false;
                        System.out.println("\nData transfer turned off!");
                    } else {
                        isTransferring = true;
                        dataTransfer.start();
                        System.out.println("\nData transfer turned on!");
                    }
                    break;
                case "5":
                    handleTransferQuestion();
                    break;
                case "6":
                    break;
                case "7":
                    client.logOut();
                    System.exit(0);
                    break;
                default:
                    System.out.println("No such choice!");
            }
        }
    }

    private void handleTempSens(){
        try {
            if (tempSens) {
                tempSens = false;
                System.out.println(client.removeSensors("Temperature"));
            } else {
                tempSens = true;
                System.out.println(client.addSensors("Temperature"));
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("Try again please!");
        }
    }

    private void handleAirSens(){
        try {
            if (airSens) {
                airSens = false;
                System.out.println(client.removeSensors("Air pressure"));
            } else {
                airSens = true;
                System.out.println(client.addSensors("Air pressure"));
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("Try again please!");
        }
    }

    private void handleWindSens(){
        try {
            if (windSens) {
                windSens = false;
                System.out.println(client.removeSensors("Windspeed"));
            } else {
                windSens = true;
                System.out.println(client.addSensors("Windspeed"));
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("Try again please!");
        }
    }

    private Thread dataTransfer = new Thread(){
        public void run() {
            int interval = 5; //seconds
            while(isTransferring){
                try {
                    client.sendData();
                    TimeUnit.SECONDS.sleep(interval);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                } catch (IOException | NullPointerException ioe) {
                    System.out.println("Couldn't send data to server! Something wrong on that side! Exiting!");
                    System.exit(1);
                } catch (ConcurrentModificationException cme){
                    System.out.println("Try again please!");
                }
            }
        }
    };

    private void handleTransferQuestion(){
        if (isTransferring){
            System.out.println("\nTransfer going on in the background!");
        } else {
            System.out.println("\nNo transfers!");
        }
    }
}
