package com.codecool.greencommitment.gui;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

import java.io.IOException;

public class CreateClientScene extends Scene {
    GCWindow window;
    FlowPane pane;

    private TextField serverIpField, serverPortField;

    private Button serverConnectButton;

    public CreateClientScene(Parent root, double width, double height, GCWindow window) {
        super(root, width, height);
        this.window = window;

        pane = new FlowPane();
        pane.setStyle("-fx-background-color: #333");
        pane.setOrientation(Orientation.VERTICAL);
        pane.setAlignment(Pos.CENTER);
        pane.setVgap(10);
        setRoot(pane);

        serverIpField = new TextField();
        serverIpField.setText("127.0.0.1");
        pane.getChildren().add(serverIpField);

        serverPortField = new TextField();
        serverPortField.setText("7777");
        pane.getChildren().add(serverPortField);

        serverConnectButton = GUIMaker.makeButton("Connect to server", 200, 50);
        serverConnectButton.setDefaultButton(true);
        serverConnectButton.setOnMouseClicked(event ->
                {
                    try {
                        if (window.setClient(Integer.parseInt(serverPortField.getText()), serverIpField.getText())) {
                            window.changeScene(GCScene.Client);
                        }
                    } catch (IOException e) {

                    }
                }
        );

        pane.getChildren().add(serverConnectButton);
    }
}