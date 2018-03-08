package com.codecool.greencommitment.gui.scenes;

import com.codecool.greencommitment.gui.GCWindow;
import com.codecool.greencommitment.gui.GUIMaker;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

import java.io.IOException;

public class CreateClientScene extends Scene {
    private GCWindow window;
    private FlowPane pane;

    private Button serverConnectButton;

    private Integer dot = 0;
    private String[] loading = new String[]{"/", "_", "\\", "|"};

    public CreateClientScene(Parent root, double width, double height, GCWindow window) {
        super(root, width, height);
        this.window = window;

        pane = new FlowPane();
        pane.setStyle("-fx-background-color: #333");
        pane.setOrientation(Orientation.VERTICAL);
        pane.setAlignment(Pos.CENTER);
        pane.setVgap(10);
        setRoot(pane);

        // Back button
        Button backButton = GUIMaker.makeBackButton();
        backButton.setOnMouseClicked(event -> goToPreviousScene());
        pane.getChildren().add(backButton);

        // Connect Button
        serverConnectButton = GUIMaker.makeButton("Connect", 200, 50);
        serverConnectButton.setDefaultButton(true);
        serverConnectButton.setOnMouseClicked(event ->
                {
                    try {
                        serverConnectButton.setText("Connecting...");
                        window.setClient();
                        window.changeScene(GCScene.Client);
                    } catch (IOException e) {
                        GUIMaker.makeAlert("Connect Error", "Could not connect to the server.");
                    }
                }
        );

        pane.getChildren().add(serverConnectButton);
    }


    // Method(s)
    private void goToPreviousScene() {
        window.changeScene(GCScene.Home);
    }
}
