package com.codecool.greencommitment.gui;

import com.codecool.greencommitment.client.Sensor;
import com.codecool.greencommitment.common.ChartGenerator;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GUIMaker {
    public static Button makeButton(String text, double width, double height) {
        Button button = new Button(text);
        button.setPrefWidth(width);
        button.setPrefHeight(height);
        button.setCursor(Cursor.CLOSED_HAND);
        button.setFont(Font.font("Monaco", 20));

        return button;
    }

    public static Text makeText(String string) {
        Text text = new Text(string);
        text.setFont(Font.font("Monaco", 20));
        text.setFill(Color.WHITE);
        return text;
    }

    public static void makeAlert(String error, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(error);
        alert.setHeaderText(message);

        alert.showAndWait();
    }

    public static Tab makeSensorTab(Sensor sensor, GCWindow window) {
        Tab sensorTab = new Tab(sensor.getName());
        sensorTab.setClosable(false);

        FlowPane sensorPane = new FlowPane(Orientation.VERTICAL);
        sensorPane.setPadding(new Insets(5, 5, 5, 5));
        sensorTab.setContent(sensorPane);

        // ( DataStream : Off )
        Pane dataStreamContainer = new FlowPane(Orientation.HORIZONTAL);
        dataStreamContainer.getChildren().add(new Text("DataStream: "));

        Button dataStreamButton = new Button("Off");
        dataStreamButton.setOnMouseClicked(event -> {
            sensor.startStopSensor();
            if (dataStreamButton.getText().equals("Off")) {
                dataStreamButton.setText("On");
            } else {
                dataStreamButton.setText("Off");
            }
        });
        dataStreamContainer.getChildren().add(dataStreamButton);

        sensorPane.getChildren().add(dataStreamContainer);


        // <ImageView>
        ImageView lineChartImg = new ImageView("http://www.glowscript.org/docs/VPythonDocs/images/graph.png");
        lineChartImg.setPreserveRatio(true);
        lineChartImg.prefWidth(1000);

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnMouseClicked(event -> {
            try {
                String image = window.getClient().getChartFromServer(sensor.getName());
                lineChartImg.setImage(new Image(new File(image).toURI().toURL().toString()));
            } catch (Exception e) {
                makeAlert("Image Error", "Image not found.");
            }
        });

        sensorPane.getChildren().add(refreshButton);
        sensorPane.getChildren().add(lineChartImg);

        return sensorTab;
    }

    public static Tab makeDataTab(GCWindow window) {
        Tab dataTab = new Tab();
        dataTab.setClosable(false);
        FlowPane dataPane = new FlowPane(Orientation.VERTICAL);
        dataPane.setPadding(new Insets(5, 5, 5, 5));
        dataPane.setVgap(5);
        dataTab.setContent(dataPane);

        String localHost = "";
        try {
            localHost = Inet4Address.getLocalHost().toString().split("/")[1];
        } catch (UnknownHostException e) {
        }

        if (window.getServer() != null) {
            dataTab.setText("Server");
            dataPane.getChildren().add(new Text("Server IP: " + localHost));
            dataPane.getChildren().add(new Text("Server Port: " + window.getServer().getPortNumber()));

            Button saveServerButton = new Button("Save data");
            saveServerButton.setOnMouseClicked(event -> {
                window.getServer().save();
            });
            dataPane.getChildren().add(saveServerButton);

            Button endServerButton = new Button("End server");
            endServerButton.setOnMouseClicked(event -> {
                window.getServer().exit();
            });
            dataPane.getChildren().add(endServerButton);
        } else {
            dataTab.setText("Client");
            dataPane.getChildren().add(new Text("Server IP: " + window.getClient().getHost()));
            dataPane.getChildren().add(new Text("Server Port: " + window.getClient().getPort()));
            dataPane.getChildren().add(new Text("IP: " + localHost));
            Button endClientButton = makeButton("Log out", 100, 50);
            endClientButton.setOnMouseClicked(event -> {
                // Client logout
            });
            dataPane.getChildren().add(endClientButton);
        }

        return dataTab;
    }
}
