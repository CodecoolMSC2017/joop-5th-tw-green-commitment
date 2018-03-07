package com.codecool.greencommitment.server;

import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.HashMap;
import java.util.List;

public class AutoSaver extends ServerInputHandler {

    private int intervals;

    public AutoSaver(HashMap<Integer, HashMap<Integer, List<Element>>> data, String xmlFilePath, int intervals) {
        super(data, xmlFilePath);
        this.intervals = intervals * 1000;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(intervals);
                save();
            }
        } catch (InterruptedException e) {
            System.out.println("Autosaver stopped");
        }
    }

    private void save() {
        try {
            saveXml();
            System.out.println("Data autosaved");
        } catch (TransformerException | ParserConfigurationException e) {
            System.out.println("Autosaving failed");
        }
    }
}