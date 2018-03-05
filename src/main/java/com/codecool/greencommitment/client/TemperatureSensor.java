package com.codecool.greencommitment.client;

import org.w3c.dom.Document;


public class TemperatureSensor extends Sensor {

    public TemperatureSensor(){
        this.id = 453;
        this.type = "celsius";
    }

    @Override
    public Document readData() {
        double temperature = generateRandomNumber(18, 25);
        long currentTime = currentTimeMillis();

        return null;
    }
}
