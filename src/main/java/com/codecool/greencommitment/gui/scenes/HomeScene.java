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

    private Button serverButton;
    private Button clientButton;

    public HomeScene(Parent root, double width, double height, GCWindow window) {
        super(root, width, height);
        this.window = window;

        pane = new FlowPane();
        pane.setStyle("-fx-background-color: #333");
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(40);
        setRoot(pane);

        // Server Button
        serverButton = GUIMaker.makeButton("Server", 200, 100);
        serverButton.setFont(Font.font(20));
        serverButton.setOnMouseClicked(event ->
                {
                    window.changeScene(GCScene.Create_Server);
                }
        );

        // Client Button
        clientButton = GUIMaker.makeButton("Client", 200, 100);
        clientButton.setDefaultButton(true);
        clientButton.setOnMouseClicked(event ->
                {
                    window.changeScene(GCScene.Create_Client);
                }
        );

        pane.getChildren().add(serverButton);
        pane.getChildren().add(clientButton);

    }


}
