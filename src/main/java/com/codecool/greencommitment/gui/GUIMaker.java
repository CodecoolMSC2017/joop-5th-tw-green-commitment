package com.codecool.greencommitment.gui;

import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Font;

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
        alert.setHeaderText(error);
        //alert.setContentText(message);

        alert.showAndWait();
    }
}
