package com.codecool.greencommitment.server;

import com.codecool.greencommitment.gui.scenes.ServerScene;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {

    private ServerScene serverScene;

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
        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();

        System.out.println(String.format("%s - (%s) >>> %s",
                date.format(calendar.getTime()),
                source, message));
    }
}
