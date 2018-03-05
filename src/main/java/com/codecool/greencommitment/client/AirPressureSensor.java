package com.codecool.greencommitment.client;

import com.codecool.greencommitment.common.MeasurementParser;
import org.w3c.dom.Document;

import java.text.DecimalFormat;

class AirPressureSensor extends Sensor {

    AirPressureSensor(){
        this.id = 1587;
        this.type = "kPa";
    }

    @Override
    public Document readData() {
        Document doc;
        MeasurementParser parser = new MeasurementParser();
        DecimalFormat df = new DecimalFormat("#.###");

        double airPressure = Double.valueOf(df.format(generateRandomNumber(99, 103)));
        long currentTime = currentTimeMillis();

        doc = parser.createDocument(id, currentTime, airPressure, type);

        return doc;
    }
}
