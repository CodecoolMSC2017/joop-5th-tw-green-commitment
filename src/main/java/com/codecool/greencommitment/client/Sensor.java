package com.codecool.greencommitment.client;

import org.w3c.dom.Document;

public abstract class Sensor implements Runnable {
    public abstract Document readData();

    public abstract void sendData(Document data);
}
