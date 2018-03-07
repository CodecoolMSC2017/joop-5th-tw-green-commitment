package com.codecool.greencommitment.server;

import com.codecool.greencommitment.gui.scenes.ServerScene;

import java.time.LocalDateTime;

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
        System.out.println(String.format("%s:%s:%s - (%s) >>> %s",
                LocalDateTime.now().getHour(),
                LocalDateTime.now().getMinute(),
                LocalDateTime.now().getSecond(),
                source, message));
    }
}
