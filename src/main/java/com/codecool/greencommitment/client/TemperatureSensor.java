package com.codecool.greencommitment.client;

import com.codecool.greencommitment.common.MeasurementParser;
import org.w3c.dom.Document;

import java.text.DecimalFormat;


class TemperatureSensor extends Sensor {

    TemperatureSensor(){
        this.name = "Temperature sensor";
        this.id = 453;
        this.type = "celsius";
    }

    @Override
    public Document readData() {
        Document doc;
        MeasurementParser parser = new MeasurementParser();
        DecimalFormat df = new DecimalFormat("#.#");

        double temperature = Double.valueOf(df.format(generateRandomNumber(18, 25)));
        long currentTime = currentTimeMillis();

        doc = parser.createDocument(id, currentTime, temperature, type);

        return doc;
    }
}
