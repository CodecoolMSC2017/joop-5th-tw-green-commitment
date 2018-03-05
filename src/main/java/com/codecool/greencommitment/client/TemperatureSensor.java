package com.codecool.greencommitment.client;

import com.codecool.greencommitment.common.MeasurementParser;
import org.w3c.dom.Document;


public class TemperatureSensor extends Sensor {

    public TemperatureSensor(){
        this.id = 453;
        this.type = "celsius";
    }

    @Override
    public Document readData() {
        Document doc;
        MeasurementParser parser = new MeasurementParser();
        double temperature = generateRandomNumber(18, 25);
        long currentTime = currentTimeMillis();

        doc = parser.createDocument(id, currentTime, temperature, type);

        return doc;
    }
}
