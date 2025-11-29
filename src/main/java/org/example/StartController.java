package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class StartController{

    @FXML
    private Button grammar1;

    @FXML
    private Button grammar2;

    @FXML
    private Button grammar3;

    @FXML
    public void initialize(){
        grammar1.setOnAction(this::handleGrammar1);
        grammar2.setOnAction(this::handleGrammar2);
        grammar3.setOnAction(this::handleGrammar3);
    }

    @FXML
    private void handleGrammar1(ActionEvent event){
        loadGrammarScene(1);
    }

    @FXML
    private void handleGrammar2(ActionEvent event){
        loadGrammarScene(2);
    }

    @FXML
    private void handleGrammar3(ActionEvent event){
        loadGrammarScene(3);
    }

    private void loadGrammarScene(int grammarNumber){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            ShapeGrammarController controller = loader.getController();
            controller.setGrammarNumber(grammarNumber);
            Stage stage = (Stage) grammar1.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("ShapeGrammars");
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();

        }catch (IOException e){
            e.printStackTrace();
            System.err.println("Błąd podczas ładowania sceny głównej!");
        }
    }
}