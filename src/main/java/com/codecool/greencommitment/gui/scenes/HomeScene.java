package com.codecool.greencommitment.gui.scenes;

import com.codecool.greencommitment.gui.GCWindow;
import com.codecool.greencommitment.gui.GUIMaker;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;

public class HomeScene extends Scene {
    private GCWindow window;
    private FlowPane pane;


    // Constructor(s)
    public HomeScene(Parent root, double width, double height, GCWindow window) {
        super(root, width, height);
        this.window = window;

        // Root pane
        pane = new FlowPane();
        pane.setStyle("-fx-background-color: #333");
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(40);
        setRoot(pane);

        // Setup content
        initServerButton();
        pane.getChildren().add(GUIMaker.makeText("OR"));
        initClientButton();
    }


    // Method(s)
    private void initServerButton() {
        Button serverButton = GUIMaker.makeButton("Server");
        serverButton.setOnMouseClicked(event ->
                {
                    window.changeScene(GCScene.Create_Server);
                }
        );

        pane.getChildren().add(serverButton);
    }

    private void initClientButton() {
        Button clientButton = GUIMaker.makeButton("Client");
        clientButton.setDefaultButton(true);
        clientButton.setOnMouseClicked(event ->
                {
                    window.changeScene(GCScene.Create_Client);
                }
        );

        pane.getChildren().add(clientButton);
    }
}
