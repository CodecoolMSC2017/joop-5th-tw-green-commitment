package com.codecool.greencommitment.client;

import java.io.IOException;
import java.util.Scanner;

public class ClientMenu {
    private Scanner cmdScan = new Scanner(System.in);
    private String line;
    private Client client;

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
                System.out.println(msg + " Exiting!");
                System.exit(1);
            } else {
                System.out.println(msg);
            }
        } catch (IOException e) {
            System.out.println("ID file couldn't be written or read in. Please do something about that! :) ");
        }
            start();
    }

    private void start() {

        client.dataTransfer.start();

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
                    handleSensors("TemperatureSensor");
                    break;
                case "2":
                    handleSensors("AirPressureSensor");
                    break;
                case "3":
                    handleSensors("WindSpeedSensor");
                    break;
                case "4":
                    if (client.isTransferring()){
                        client.setIsTransferring(false);
                        System.out.println("\nData transfer turned off!");
                    } else {
                        client.setIsTransferring(true);
                        System.out.println("\nData transfer turned on!");
                    }
                    break;
                case "5":
                    handleTransferQuestion();
                    break;
                case "6":
                    requestChart();
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

    private void handleSensors(String sensorClass){
        for (Sensor s:client.getSensors()){
            switch (sensorClass){
                case "TemperatureSensor":
                    if (s instanceof TemperatureSensor) {
                        s.startStopSensor();
                        if (s.isStarted()){
                            System.out.println(s.getName() + " turned on!");
                        } else {
                            System.out.println(s.getName() + " turned off!");
                        }
                    }
                    break;
                case "AirPressureSensor":
                    if (s instanceof AirPressureSensor) {
                        s.startStopSensor();
                        if (s.isStarted()){
                            System.out.println(s.getName() + " turned on!");
                        } else {
                            System.out.println(s.getName() + " turned off!");
                        }
                    }
                    break;
                case "WindSpeedSensor":
                    if (s instanceof WindSpeedSensor) {
                        s.startStopSensor();
                        if (s.isStarted()){
                            System.out.println(s.getName() + " turned on!");
                        } else {
                            System.out.println(s.getName() + " turned off!");
                        }
                    }
                    break;
            }
        }
    }

    private void handleTransferQuestion(){
        if (client.isTransferring()){
            System.out.println("\nTransfer going on in the background!");
        } else {
            System.out.println("\nNo transfers!");
        }
    }

    private void requestChart(){
        String msg;
        System.out.println("Choose the sensor for which you wish to get the chart!");
        System.out.println("(1) Temperature sensor");
        System.out.println("(2) Air pressure sensor");
        System.out.println("(3) Wind speed sensor");
        line = cmdScan.nextLine();

        try {
            switch (line) {
                case "1":
                    msg = client.getChartFromServer("temp");
                    System.out.println(msg);
                    break;
                case "2":
                    msg = client.getChartFromServer("air");
                    System.out.println(msg);
                    break;
                case "3":
                    msg = client.getChartFromServer("wind");
                    System.out.println(msg);
                    break;
                default:
                    System.out.println("No such choice!");
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("That went wrong....");
        }
    }
}
