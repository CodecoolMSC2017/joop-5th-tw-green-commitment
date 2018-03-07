package com.codecool.greencommitment.gui.scenes;

import com.codecool.greencommitment.gui.GCWindow;
import com.codecool.greencommitment.gui.GUIMaker;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;

public class ClientScene extends Scene {
    private GCWindow window;
    private FlowPane pane;

    private Button startDataStreamButton, stopDataStreamButton;

    public ClientScene(Parent root, double width, double height, GCWindow window) {
        super(root, width, height);
        this.window = window;

        pane = new FlowPane();
        setRoot(pane);

        // Setup StartDataStreamButton
        startDataStreamButton = GUIMaker.makeButton("Start DataStream", 200, 50);
        pane.getChildren().add(startDataStreamButton);

        // Setup StopDataStreamButton
        stopDataStreamButton = GUIMaker.makeButton("Stop DataStream", 200, 50);
        pane.getChildren().add(stopDataStreamButton);
    }
}
