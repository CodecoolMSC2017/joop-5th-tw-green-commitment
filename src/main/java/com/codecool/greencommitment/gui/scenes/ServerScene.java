package com.codecool.greencommitment.gui.scenes;

import com.codecool.greencommitment.gui.GCWindow;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;

import java.time.LocalDateTime;

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
        consoleTab.setClosable(false);
        tabPane.getTabs().add(consoleTab);

        consoleField = new Text();
        consoleField.maxWidth(1920);
        consoleField.setLineSpacing(5);
        consoleTab.setContent(consoleField);

        // Results tab setup
        resultsTab = new Tab("Results");
        resultsTab.setClosable(false);
        tabPane.getTabs().add(resultsTab);
        resultImg = new ImageView("http://www.glowscript.org/docs/VPythonDocs/images/graph.png");
        resultsTab.setContent(resultImg);

        pane.getChildren().add(tabPane);

        consoleWrite("Server", "Server started.");
    }


    // Method(s)
    private void consoleWrite(String source, String text) {
        consoleField.setText(consoleField.getText() +
                String.format("%s:%s:%s - (%s) >>> %s\n",
                        LocalDateTime.now().getHour(),
                        LocalDateTime.now().getMinute(),
                        LocalDateTime.now().getSecond(),
                        source, text)
        );
    }
}
