package com.codecool.greencommitment.client;

import org.w3c.dom.Document;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Sensor{

    protected String name;
    protected String type;
    protected int id;
    protected boolean isStarted;
    protected int interval;

    // Method(s)
    public abstract Document readData();

    String startStopSensor() {
        if (isStarted) {
            isStarted = false;
            return name + " stopped!";
        } else {
            isStarted = true;
            return name + " started!";
        }
    }

    String setInterval(int interval){
        this.interval = interval;
        return "New interval" + interval + "s set!";
    }

    double generateRandomNumber(double minNumber, double maxNumber){
        return ThreadLocalRandom.current().nextDouble(minNumber, maxNumber);
    }

    long currentTimeMillis(){
        return System.currentTimeMillis();
    }
}
