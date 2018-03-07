package com.codecool.greencommitment.gui.scenes;

import com.codecool.greencommitment.client.Sensor;
import com.codecool.greencommitment.gui.GCWindow;
import com.codecool.greencommitment.gui.GUIMaker;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.FlowPane;

public class ClientScene extends Scene {
    private GCWindow window;
    private FlowPane pane;

    private TabPane tabPane;
    private Tab optionsTab;

    public ClientScene(Parent root, double width, double height, GCWindow window) {
        super(root, width, height);
        this.window = window;

        pane = new FlowPane();
        setRoot(pane);

        tabPane = new TabPane();
        tabPane.setMaxWidth(1920);
        tabPane.setPrefWidth(1920);
        pane.getChildren().add(tabPane);

        optionsTab = new Tab("Options");
        optionsTab.setClosable(false);
        tabPane.getTabs().add(optionsTab);
        Button dataStreamButton = new Button("Stop");

        window.getClient().setIsTransferring(true);
        window.getClient().dataTransfer.start();

        dataStreamButton.setOnMouseClicked(event -> {
            if (dataStreamButton.getText().equals("Start")) {
                window.getClient().setIsTransferring(true);
                dataStreamButton.setText("Stop");
            } else {
                window.getClient().setIsTransferring(false);
                dataStreamButton.setText("Start");
            }
        });

        optionsTab.setContent(dataStreamButton);

        initSensorTabs();
    }

    private void initSensorTabs() {
        for (Sensor sensor : window.getClient().getSensors()) {
            tabPane.getTabs().add(GUIMaker.makeSensorTab(sensor));
        }
    }
}
