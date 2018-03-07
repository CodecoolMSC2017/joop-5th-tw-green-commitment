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
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

import java.time.LocalDateTime;

public class ServerScene extends Scene {
    private GCWindow window;
    private FlowPane pane;


    private TabPane tabPane;

    // Clients tab
    private Tab clientsTab;
    private TextArea clientList;

    // Console tab
    private Tab consoleTab;
    private TextArea consoleField;

    // Results tab
    private Tab resultsTab;
    private ImageView resultImg;

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


        // Console tab setup
        consoleTab = new Tab("Console");
        consoleTab.setClosable(false);
        tabPane.getTabs().add(consoleTab);

        consoleField = new TextArea();
        consoleField.setEditable(false);
        consoleTab.setContent(consoleField);


        // Clients tab setup
        clientsTab = new Tab("Clients");
        clientsTab.setClosable(false);
        tabPane.getTabs().add(clientsTab);


        // Results tab setup
        resultsTab = new Tab("Results");
        resultsTab.setClosable(false);
        tabPane.getTabs().add(resultsTab);

        resultImg = new ImageView("http://www.glowscript.org/docs/VPythonDocs/images/graph.png");
        resultsTab.setContent(resultImg);


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
        consoleField.appendText(String.format("%s:%s:%s - (%s) >>> %s\n",
                LocalDateTime.now().getHour(),
                LocalDateTime.now().getMinute(),
                LocalDateTime.now().getSecond(),
                source, text));
    }

    public void goToPreviousScene() {
        window.changeScene(GCScene.Create_Server);
    }
}
