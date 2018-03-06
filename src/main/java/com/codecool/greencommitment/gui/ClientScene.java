package com.codecool.greencommitment.gui;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class ClientScene extends Scene {
    GCWindow window;
    public ClientScene(Parent root, double width, double height, GCWindow window) {
        super(root, width, height);
        this.window = window;
    }
}
