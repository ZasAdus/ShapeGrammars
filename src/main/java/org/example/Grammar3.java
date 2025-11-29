package org.example;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Grammar3 implements GrammarDefinition{

    private final GrammarRule rule1 = new SquareToTwoSquares();
    private final GrammarRule rule2 = new TwoSquaresToRectangle();
    private final GrammarRule rule3 = new SquareWithInscribedCircle();

    @Override
    public int getRuleCount(){
        return 3;
    }

    @Override
    public GrammarRule getRule(int ruleNumber){
        return switch(ruleNumber){
            case 1 -> rule1;
            case 2 -> rule2;
            case 3 -> rule3;
            default -> null;
        };
    }

    @Override
    public Shape createInitSymbol(double centerX, double centerY, double size){
        return new Square(centerX, centerY, size);
    }
}

class SquareToTwoSquares implements GrammarRule{

    private static final String RULE_NAME = "SquareToTwoSquares";

    @Override
    public boolean canApply(Shape shape, List<Shape> allShapes){
        return shape instanceof Square && !shape.isRuleBlocked(RULE_NAME);
    }

    @Override
    public void apply(Shape shape, List<Shape> allShapes, Map<String, Object> params){
        String direction =(String) params.getOrDefault("direction", "right");
        double x = shape.getX();
        double y = shape.getY();
        double size = shape.getSize();
        double newX = x;
        double newY = y;
        switch(direction){
            case "right" -> newX = x + size;
            case "left" -> newX = x - size;
            case "up" -> newY = y - size;
            case "down" -> newY = y + size;
        }
        allShapes.add(new Square(newX, newY, size));
    }

    @Override
    public void createMarkerButtons(Shape shape, List<Shape> allShapes, Pane canvasPane, List<Button> buttonList, java.util.function.Consumer<Map<String, Object>> onApplyWithParams){
        double x = shape.getX();
        double y = shape.getY();
        double size = shape.getSize();
        double halfSize = size / 2;
        String[] directions ={"right", "left", "up", "down"};
        double[][] positions ={
               {x + halfSize, y},
               {x - halfSize, y},
               {x, y - halfSize},
               {x, y + halfSize}
        };
        for(int i = 0; i < 4; i++){
            final String dir = directions[i];
            double newX = x;
            double newY = y;
            switch(dir){
                case "right" -> newX = x + size;
                case "left" -> newX = x - size;
                case "up" -> newY = y - size;
                case "down" -> newY = y + size;
            }
            boolean positionOccupied = false;
            for(Shape other : allShapes){
                if(other instanceof Square && other.getX() == newX && other.getY() == newY){
                    positionOccupied = true;
                    break;
                }
            }
            if(positionOccupied) continue;
            Button btn = MarkerCreator.createMarker(positions[i][0], positions[i][1],() ->{
                Map<String, Object> params = new HashMap<>();
                params.put("direction", dir);
                onApplyWithParams.accept(params);
            });
            buttonList.add(btn);
            canvasPane.getChildren().add(btn);
        }
    }
}

class SquareWithInscribedCircle implements GrammarRule{

    private static final String RULE_NAME = "SquareWithInscribedCircle";

    @Override
    public boolean canApply(Shape shape, List<Shape> allShapes){
        return shape instanceof Square && !shape.isRuleBlocked(RULE_NAME);
    }

    @Override
    public void apply(Shape shape, List<Shape> allShapes, Map<String, Object> params){
        double size = shape.getSize();
        for(Shape other : allShapes){
            if(other instanceof Circle && other.getX() == shape.getX() && other.getY() == shape.getY()){
                return;
            }
        }
        shape.blockRule("SquareWithInscribedCircle");
        shape.blockRule("TwoSquaresToRectangle");
        shape.blockRule("SquareToTwoSquares");
        allShapes.add(new Circle(shape.getX(), shape.getY(), size));
    }

    @Override
    public void createMarkerButtons(Shape shape, List<Shape> allShapes, Pane canvasPane, List<Button> buttonList, java.util.function.Consumer<Map<String, Object>> onApplyWithParams){
        Button btn = MarkerCreator.createMarker(shape.getX(), shape.getY(),
               () -> onApplyWithParams.accept(new HashMap<>()));
        buttonList.add(btn);
        canvasPane.getChildren().add(btn);
    }
}