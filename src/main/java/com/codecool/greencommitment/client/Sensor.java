package com.codecool.greencommitment.client;

import org.w3c.dom.Document;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Sensor{

    protected String name;
    protected String type;
    protected int id;
    private boolean isStarted;

    // Method(s)
    public abstract Document readData();

    public String getName() {
        return name;
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

    double generateRandomNumber(double minNumber, double maxNumber){
        return ThreadLocalRandom.current().nextDouble(minNumber, maxNumber);
    }

    long currentTimeMillis(){
        return System.currentTimeMillis();
    }
}
