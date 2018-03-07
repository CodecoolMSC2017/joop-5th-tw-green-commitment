package com.codecool.greencommitment.client;

import com.codecool.greencommitment.common.MeasurementParser;
import org.w3c.dom.Document;

import java.text.DecimalFormat;

public class WindSpeedSensor extends Sensor {

    WindSpeedSensor(){
        this.name = "Windspeed sensor";
        this.id = 1785;
        this.type = "kph";
    }

    @Override
    public Document readData() {
        Document doc;
        MeasurementParser parser = new MeasurementParser();
        DecimalFormat df = new DecimalFormat("#.#");

        double windSpeed = Double.valueOf(df.format(generateRandomNumber(0, 100)));
        long currentTime = currentTimeMillis();

        doc = parser.createDocument(id, currentTime, windSpeed, type);

        return doc;
    }
}
