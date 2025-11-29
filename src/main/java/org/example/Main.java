package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/start.fxml")));
        //primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("ShapeGrammars");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.resizableProperty().setValue(false);
        primaryStage.show();
    }
}