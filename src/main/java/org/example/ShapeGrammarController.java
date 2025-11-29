package org.example;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShapeGrammarController {

    public Button menuButton;
    @FXML
    private Canvas canvas;

    @FXML
    private HBox productionsBox;

    @FXML
    private Button resetButton;

    @FXML
    private Pane canvasPane;

    @FXML
    private Label notificationLabel;

    private GraphicsContext gc;
    private final List<Shape> shapes = new ArrayList<>();
    private Integer selectedProd = null;
    private int grammarNumber = 0;
    private final List<Button> markerButtons = new ArrayList<>();
    private final Grammars grammars = new Grammars();
    private GrammarDefinition currentGrammar;

    @FXML
    public void initialize(){
        gc = canvas.getGraphicsContext2D();
        canvas.setLayoutX(0);
        canvas.setLayoutY(0);
    }

    public void setGrammarNumber(int grammarNumber){
        this.grammarNumber = grammarNumber;
        this.currentGrammar = grammars.getGrammar(grammarNumber);
        loadProdForGrammar();
        drawInitSymbol();
    }

    private void loadProdForGrammar() {
        productionsBox.getChildren().clear();

        for(int i = 1; i <= currentGrammar.getRuleCount(); i++){
            Button productionBtn = new Button();
            productionBtn.getStyleClass().add("production-button");
            productionBtn.setUserData(i);
            productionBtn.setOnAction(e -> handleProductionClick(e.getSource()));

            try{
                GrammarRule rule = currentGrammar.getRule(i);
                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/pictures/grammar" + grammarNumber + "_" + i + ".png")));
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(90);
                imageView.setFitWidth(110);
                imageView.setPreserveRatio(true);
                productionBtn.setGraphic(imageView);
            }catch(Exception e){
                System.err.println("can't load image");
            }

            productionsBox.getChildren().add(productionBtn);
        }
    }

    private void drawInitSymbol(){
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        double size;
        if(grammarNumber == 2){
            size = 300;
        }else{
            size = 60;
        }
        Shape initShape = currentGrammar.createInitSymbol(centerX, centerY, size);
        shapes.add(initShape);
        redrawCanvas();
    }

    private void handleProductionClick(Object source){
        Button button = (Button) source;
        selectedProd = Integer.parseInt(button.getUserData().toString());
        productionsBox.getChildren().forEach(node -> {
            if(node instanceof Button){
                node.setStyle("");
            }
        });

        showMarkerButtons();
    }

    private void showMarkerButtons(){
        clearMarkerButtons();

        GrammarRule rule = currentGrammar.getRule(selectedProd);
        if(rule == null) return;

        for(Shape shape : shapes) {
            if(rule.canApply(shape, shapes)){
                rule.createMarkerButtons(shape, shapes, canvasPane, markerButtons,
                        (params) -> {
                            rule.apply(shape, shapes, params);
                            clearMarkerButtons();
                            clearProductionSelection();
                            redrawCanvas();
                        });
            }
        }

        if(markerButtons.isEmpty()){
            showNotification();
        }
    }

    private void showNotification() {
        notificationLabel.setText("Nie można zastosować tej produkcji");
        notificationLabel.setVisible(true);
        notificationLabel.setOpacity(1.0);

        notificationLabel.applyCss();
        notificationLabel.layout();

        double labelWidth = notificationLabel.prefWidth(-1);
        if (labelWidth == 0) {
            labelWidth = "Nie można zastosować tej produkcji".length() * 10 + 60;
        }
        notificationLabel.setLayoutX(canvas.getWidth() / 2 - labelWidth / 2);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), notificationLabel);
        fadeOut.setDelay(Duration.seconds(0.5));
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> notificationLabel.setVisible(false));
        fadeOut.play();
    }

    private void clearMarkerButtons(){
        for (Button btn : markerButtons){
            canvasPane.getChildren().remove(btn);
        }
        markerButtons.clear();
    }

    private void clearProductionSelection() {
        selectedProd = null;
        productionsBox.getChildren().forEach(node -> {
            if (node instanceof Button) {
                node.setStyle("");
            }
        });
    }

    private void redrawCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);
        for(int i = 0; i < canvas.getWidth(); i += 30){
            gc.strokeLine(i, 0, i, canvas.getHeight());
        }
        for(int i = 0; i < canvas.getHeight(); i += 30){
            gc.strokeLine(0, i, canvas.getWidth(), i);
        }

        for(Shape shape : shapes){
            ShapeRenderer.drawShape(gc, shape);
        }
    }

    @FXML
    private void handleReset(){
        shapes.clear();
        clearMarkerButtons();
        clearProductionSelection();
        drawInitSymbol();
    }

    public void handleMenu(ActionEvent actionEvent) throws IOException{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/start.fxml")));
        Stage stage = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

}