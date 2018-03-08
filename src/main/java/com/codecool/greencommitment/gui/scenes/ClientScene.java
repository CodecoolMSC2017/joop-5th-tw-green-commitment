package com.codecool.greencommitment.gui.scenes;

import com.codecool.greencommitment.client.Sensor;
import com.codecool.greencommitment.gui.GCWindow;
import com.codecool.greencommitment.gui.GUIMaker;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

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

        tabPane.getTabs().add(GUIMaker.makeDataTab(window));

        // Options tab
        optionsTab = new Tab("Options");
        optionsTab.setClosable(false);
        tabPane.getTabs().add(optionsTab);


        // Stop dataStream Button
        FlowPane dataStreamContainer = new FlowPane();

        Button dataStreamButton = new Button("on");

        window.getClient().setIsTransferring(true);
        window.getClient().dataTransfer.start();

        dataStreamButton.setOnMouseClicked(event -> {
            if (dataStreamButton.getText().equals("off")) {
                window.getClient().setIsTransferring(true);
                dataStreamButton.setText("on");
            } else {
                window.getClient().setIsTransferring(false);
                dataStreamButton.setText("off");
            }
        });
        dataStreamContainer.getChildren().add(new Text("DataStream:"));
        dataStreamContainer.getChildren().add(dataStreamButton);

        optionsTab.setContent(dataStreamContainer);

        initSensorTabs();
    }

    private void initSensorTabs() {
        for (Sensor sensor : window.getClient().getSensors()) {
            tabPane.getTabs().add(GUIMaker.makeSensorTab(sensor, window));
        }
    }
}
