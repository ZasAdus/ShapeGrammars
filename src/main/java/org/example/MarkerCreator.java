package org.example;

import javafx.scene.control.Button;

public class MarkerCreator {
    public static Button createMarker(double canvasX, double canvasY, Runnable onApply){
        Button btn = new Button();
        int size = 15;
        btn.getStyleClass().add("marker");
        btn.setMinSize(size, size);
        btn.setPrefSize(size, size);
        btn.setMaxSize(size, size);
        btn.setLayoutX(canvasX - size / 2.0);
        btn.setLayoutY(canvasY - size / 2.0);
        btn.setOnAction(e -> onApply.run());
        return btn;
    }
}