package com.codecool.greencommitment.gui;

import com.codecool.greencommitment.client.Sensor;
import com.codecool.greencommitment.server.Server;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
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

    public static Button makeButton(String text) {
        Button button = new Button(text);
        button.setCursor(Cursor.CLOSED_HAND);
        button.setFont(Font.font("Monaco", 20));

        return button;
    }

    public static Button makeBackButton() {
        Button backButton = new Button("« Back");
        backButton.setCursor(Cursor.CLOSED_HAND);
        backButton.setFont(Font.font("Monaco", 15));
        return backButton;
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

        Button dataStreamButton = new Button("On");
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
        lineChartImg.setFitWidth(520);
        lineChartImg.setFitHeight(320);

        new Thread(() -> {
            while (true) {
                try {
                    String imgPath = System.getProperty("user.home") + "/" + "" + sensor.getId() + "LineChart.jpeg";
                    lineChartImg.setImage(new Image(new File(imgPath).toURI().toURL().toString()));
                    Thread.sleep(5000);
                } catch (IOException | InterruptedException ignored) { }
            }
        }).start();
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
                window.logOut();
            });
            dataPane.getChildren().add(endServerButton);
        } else {
            dataTab.setText("Client");
            dataPane.getChildren().add(new Text("Client ID: " + window.getClient().getClientId()));
            dataPane.getChildren().add(new Text("Server IP: " + window.getClient().getHost()));
            dataPane.getChildren().add(new Text("Server Port: " + window.getClient().getPort()));
            Button endClientButton = new Button("Log out");
            endClientButton.setOnMouseClicked(event -> {
                window.logOut();
            });
            dataPane.getChildren().add(endClientButton);
        }

        return dataTab;
    }

    public static Tab makeClientsTab(Server server) {
        Tab clientsTab = new Tab("Clients");
        clientsTab.setClosable(false);

        FlowPane clientsPane = new FlowPane(Orientation.VERTICAL);
        clientsPane.setPadding(new Insets(5, 5, 5, 5));
        clientsTab.setContent(clientsPane);

        TextArea clients = new TextArea();
        clients.setEditable(false);
        clients.setPrefHeight(400);

        new Thread(() -> {
            while (true) {
                try {
                    clients.setText("");
                    for (String client : server.getLoggedInClients()) {
                        clients.appendText("- Client " + String.valueOf(client) + "\n");
                    }
                    Thread.sleep(1000);
                } catch (Exception ignored) {}
            }
        }).start();

        clientsPane.getChildren().add(clients);

        return clientsTab;
    }
}
