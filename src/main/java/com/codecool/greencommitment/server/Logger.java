package com.codecool.greencommitment.server;

import com.codecool.greencommitment.gui.scenes.ServerScene;

public class Logger {

    private ServerScene serverScene;

    public Logger(ServerScene serverScene) {
        this.serverScene = serverScene;
    }

    public void log(String source, String message) {
        System.out.println(message);
        if (serverScene != null) {
            serverScene.consoleWrite(source, message);
        }
    }
}
