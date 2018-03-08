package com.codecool.greencommitment.server;

import com.codecool.greencommitment.gui.scenes.ServerScene;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {

    private ServerScene serverScene;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private Calendar calendar = Calendar.getInstance();

    public Logger(ServerScene serverScene) {
        this.serverScene = serverScene;
    }

    public void log(String source, String message) {
        logToTerminal(source, message);
        if (serverScene != null) {
            serverScene.consoleWrite(source, message);
        }
    }

    private void logToTerminal(String source, String message) {
        System.out.println(String.format("%s - (%s) >>> %s",
                dateFormat.format(calendar.getTime()),
                source,
                message));
    }
}
