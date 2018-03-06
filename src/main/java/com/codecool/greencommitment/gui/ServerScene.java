package com.codecool.greencommitment.gui;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

public class ServerScene extends Scene {
    private GCWindow window;
    private FlowPane pane;



    TabPane tabPane;

    // Console tab
    Tab consoleTab;
    Text consoleField;

    // Results tab
    Tab resultsTab;
    ImageView resultImg;

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
        tabPane.getTabs().add(consoleTab);

        consoleField = new Text("Hallo");
        consoleField.maxWidth(1920);
        consoleField.setText("Server created...\n");
        consoleField.setText(consoleField.getText() + "text\n");
        consoleTab.setContent(consoleField);

        // Results tab setup
        resultsTab = new Tab("Results");
        tabPane.getTabs().add(resultsTab);
        resultImg = new ImageView("http://www.glowscript.org/docs/VPythonDocs/images/graph.png");
        resultsTab.setContent(resultImg);


        pane.getChildren().add(tabPane);
    }
}
