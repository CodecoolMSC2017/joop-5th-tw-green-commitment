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

        // Back Button
        Button backButton = GUIMaker.makeBackButton();
        backButton.setOnMouseClicked(event -> goToPreviousScene());
        pane.getChildren().add(backButton);

        // Port Field
        pane.getChildren().add(GUIMaker.makeText("Port"));
        serverPortField = new TextField();
        serverPortField.setText("7777");
        pane.getChildren().add(serverPortField);

        // Create server Button
        createServerButton = GUIMaker.makeButton("Create server", 200, 50);
        createServerButton.setDefaultButton(true);
        createServerButton.setOnMouseClicked(event ->
                {
                    try {
                        window.setServer(Integer.parseInt(serverPortField.getText()));
                        window.changeScene(GCScene.Server);
                    } catch (Exception e) {
                        GUIMaker.makeAlert("Server create failed", "Couldn't create server.");
                    }
                }
        );

        pane.getChildren().add(createServerButton);
    }

    private void goToPreviousScene() {
        window.changeScene(GCScene.Home);
    }
}
