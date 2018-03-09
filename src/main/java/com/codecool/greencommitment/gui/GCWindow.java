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

    private Stage primaryStage;

    private Scene scene;
    private StackPane root;

    private Server server;
    private Client client;

    public void startWindow() {
        launch();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        root = new StackPane();
        this.primaryStage = primaryStage;
        changeScene(GCScene.Home);
        primaryStage.setWidth(640);
        primaryStage.setHeight(500);
        primaryStage.setTitle("GC - Home");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    // Getter(s)
    public Server getServer() {
        return server;
    }

    public Client getClient() {
        return client;
    }


    // Setter(s)
    public void setServer(int port) {
        server = new Server(port);
    }

    public void setClient() throws IOException {
        client = new Client();
        client.start();
    }


    // Method(s)
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
            case Home:
            default:
                scene = new HomeScene(root, root.getWidth(), root.getHeight(), this);
                primaryStage.setScene(scene);
                break;
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            server.exit();
        }
        if (client != null) {
            try {
                client.logOut();
            } catch (IOException e) {
                GUIMaker.makeAlert("Client error", "Couldn't close socket properly.");
            }
        }
        System.exit(0);
    }

    public void logOut() {
        if (server != null) {
            server.exit();
            server = null;
        }
        if (client != null) {
            try {
                client.logOut();
            } catch (IOException e) {
                GUIMaker.makeAlert("Client error", "Couldn't close socket properly.");
            }
            client = null;
        }
        changeScene(GCScene.Home);
    }
}
