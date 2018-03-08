package com.codecool.greencommitment.gui.scenes;

import com.codecool.greencommitment.gui.GCWindow;
import com.codecool.greencommitment.gui.GUIMaker;
import com.codecool.greencommitment.server.Logger;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ServerScene extends Scene {
    private GCWindow window;
    private FlowPane pane;


    private TabPane tabPane;

    // Console tab
    private Tab consoleTab;
    private TextArea consoleField;

    public ServerScene(Parent root, double width, double height, GCWindow window) {
        super(root, width, height);
        this.window = window;

        pane = new FlowPane();
        pane.setOrientation(Orientation.VERTICAL);
        pane.setAlignment(Pos.TOP_LEFT);
        setRoot(pane);

        tabPane = new TabPane();
        tabPane.setMaxWidth(1920);
        tabPane.setPrefWidth(1920);


        tabPane.getTabs().add(GUIMaker.makeDataTab(window));


        // Console tab setup
        consoleTab = new Tab("Console");
        consoleTab.setClosable(false);
        tabPane.getTabs().add(consoleTab);

        consoleField = new TextArea();
        consoleField.setEditable(false);
        consoleField.setPrefHeight(470);
        consoleTab.setContent(consoleField);


        // Clients tab setup
        tabPane.getTabs().add(GUIMaker.makeClientsTab(window.getServer()));

        pane.getChildren().add(tabPane);

        try {
            window.getServer().setLogger(new Logger(this));
            new Thread(() -> window.getServer().start()).start();
        } catch (Exception e) {
            GUIMaker.makeAlert("Server error", "Could not create server.");
            goToPreviousScene();
        }

        consoleWrite("Server", "Server started.");
    }


    // Method(s)
    public void consoleWrite(String source, String text) {
        if(consoleField.getLength() > 10000) {
            consoleField.clear();
            consoleField.appendText("(Server) >>> Cleared console...\n");
        }
        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        consoleField.appendText(String.format("%s - (%s) >>> %s\n",
                date.format(calendar.getTime()),
                source, text));
    }

    public void goToPreviousScene() {
        window.changeScene(GCScene.Create_Server);
    }
}
