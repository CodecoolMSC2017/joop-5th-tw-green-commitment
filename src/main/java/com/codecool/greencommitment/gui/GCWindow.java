package com.codecool.greencommitment.gui;

import com.codecool.greencommitment.client.Client;
import com.codecool.greencommitment.gui.scenes.*;
import com.codecool.greencommitment.server.Server;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class GCWindow extends Application {

    Stage primaryStage;

    private Scene scene;
    private StackPane root;

    Server server;
    Client client;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        root = new StackPane();
        this.primaryStage = primaryStage;
        changeScene(GCScene.Home);
        primaryStage.setWidth(640);
        primaryStage.setHeight(360);
        primaryStage.setTitle("GC - Home");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    // Method(s)
    public boolean setServer(int port) {
        server = new Server(port);
        new Thread(()-> server.start()).start();
        return true;
    }

    public boolean setClient(int port, String ip) throws IOException {
        client = new Client(port, ip);
        return true;
    }

    public void changeScene(GCScene choice) {
        switch (choice) {
            case Create_Server:
                scene = new CreateServerScene(root, root.getWidth(), root.getHeight(), this);
                primaryStage.setTitle("GC - Create server");
                primaryStage.setScene(scene);
                break;
            case Server:
                scene = new ServerScene(root, root.getWidth(), root.getHeight(), this);
                primaryStage.setTitle("GC - Server window");
                primaryStage.setScene(scene);
                break;
            case Create_Client:
                scene = new CreateClientScene(root, root.getWidth(), root.getHeight(), this);
                primaryStage.setTitle("GC - Create client");
                primaryStage.setScene(scene);
                break;
            case Client:
                scene = new ClientScene(root, root.getWidth(), root.getHeight(), this);
                primaryStage.setTitle("GC - Client window");
                primaryStage.setScene(scene);
                break;
            case Home: default:
                scene = new HomeScene(root, root.getWidth(), root.getHeight(), this);
                primaryStage.setScene(scene);
                break;
        }
    }

    @Override
    public void stop() {
        server.exit();
    }
}
