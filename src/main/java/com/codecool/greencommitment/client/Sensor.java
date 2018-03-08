package com.codecool.greencommitment.client;

import org.w3c.dom.Document;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Sensor{

    protected String name;
    protected String type;
    protected int id;
    private boolean isStarted;

    // Method(s)

    //Getters and Setters
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public String startStopSensor() {
        if (isStarted) {
            isStarted = false;
            return name + " stopped!";
        } else {
            isStarted = true;
            return name + " started!";
        }
    }

    //Rest of stuff
    public abstract Document readData();

    double generateRandomNumber(double minNumber, double maxNumber){
        return ThreadLocalRandom.current().nextDouble(minNumber, maxNumber);
    }

    long currentTimeMillis(){
        return System.currentTimeMillis();
    }
}
