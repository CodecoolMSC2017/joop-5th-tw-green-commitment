package com.codecool.greencommitment.gui;

import com.codecool.greencommitment.client.Sensor;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GUIMaker {
    public static Button makeButton(String text, double width, double height) {
        Button button = new Button(text);
        button.setPrefWidth(width);
        button.setPrefHeight(height);
        button.setCursor(Cursor.CLOSED_HAND);
        button.setFont(Font.font(20));

        return button;
    }

    public static void makeAlert(String error, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(error);
        alert.setHeaderText(message);

        alert.showAndWait();
    }

    public static Tab makeSensorTab(Sensor sensor) {
        Tab sensorTab = new Tab(sensor.getName());
        sensorTab.setClosable(false);

        Pane sensorPane = new FlowPane();
        sensorPane.setPadding(new Insets(5, 5, 5, 5));
        sensorTab.setContent(sensorPane);

        // ( DataStream : Off )
        Pane dataStreamButtonContainer = new FlowPane();
        dataStreamButtonContainer.getChildren().add(new Text("DataStream: "));

        Button dataStreamButton = new Button("Off");
        dataStreamButton.setOnMouseClicked(event -> {
            sensor.startStopSensor();
            if(dataStreamButton.getText().equals("Off")) {
                dataStreamButton.setText("On");
            } else {
                dataStreamButton.setText("Off");
            }
        });
        dataStreamButtonContainer.getChildren().add(dataStreamButton);

        // (Graph : Refresh)
        sensorPane.getChildren().add(dataStreamButtonContainer);

        return sensorTab;
    }
}
