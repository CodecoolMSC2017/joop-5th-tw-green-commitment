package com.codecool.greencommitment.gui;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class CreateServerScene extends Scene {
    private GCWindow window;
    private FlowPane pane;

    private TextField serverPortField;

    private Button createServerButton;

    public CreateServerScene(Parent root, double width, double height, GCWindow window) {
        super(root, width, height);
        this.window = window;

        pane = new FlowPane();
        pane.setStyle("-fx-background-color: #333");
        pane.setOrientation(Orientation.VERTICAL);
        pane.setAlignment(Pos.CENTER);
        pane.setVgap(10);
        setRoot(pane);

        serverPortField = new TextField();
        serverPortField.setText("7777");
        pane.getChildren().add(serverPortField);

        createServerButton = GUIMaker.makeButton("Create server", 200, 50);
        createServerButton.setDefaultButton(true);
        createServerButton.setOnMouseClicked(event ->
                {
                    try {
                        if (window.setServer(Integer.parseInt(serverPortField.getText()))) {
                            window.changeScene(GCScene.Server);
                        }
                    } catch (Exception e) {
                        GUIMaker.makeAlert("Server create failed", "Couldn't create server.");
                    }
                }
        );

        pane.getChildren().add(createServerButton);
    }
}
