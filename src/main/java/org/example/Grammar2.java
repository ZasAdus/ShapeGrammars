package org.example;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Grammar2 implements GrammarDefinition{

    private final GrammarRule rule1 = new SquareToFourSquares();
    private final GrammarRule rule2 = new SquareWithIsoscelesTriangle();
    private final GrammarRule rule3 = new TwoSquaresToRectangle();

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

class SquareToFourSquares implements GrammarRule{

    private static final String RULE_NAME = "SquareToFourSquares";

    @Override
    public boolean canApply(Shape shape, List<Shape> allShapes){
        return shape instanceof Square && !shape.isRuleBlocked(RULE_NAME);
    }

    @Override
    public void apply(Shape shape, List<Shape> allShapes, Map<String, Object> params){
        double x = shape.getX();
        double y = shape.getY();
        double size = shape.getSize();
        double newSize = size / 2;
        double offset = size / 4;
        double[][] newPositions ={
               {x - offset, y - offset},
               {x + offset, y - offset},
               {x - offset, y + offset},
               {x + offset, y + offset}
        };
        for(double[] pos : newPositions){
            for(Shape other : allShapes){
                if(other != shape && other.getX() == pos[0] && other.getY() == pos[1]){
                    return;
                }
            }
        }
        allShapes.remove(shape);
        allShapes.add(new Square(x - offset, y - offset, newSize));
        allShapes.add(new Square(x + offset, y - offset, newSize));
        allShapes.add(new Square(x - offset, y + offset, newSize));
        allShapes.add(new Square(x + offset, y + offset, newSize));
    }

    @Override
    public void createMarkerButtons(Shape shape, List<Shape> allShapes, Pane canvasPane, List<Button> buttonList, java.util.function.Consumer<Map<String, Object>> onApplyWithParams){
        Button btn = MarkerCreator.createMarker(shape.getX(), shape.getY(),
               ()-> onApplyWithParams.accept(new HashMap<>()));
        buttonList.add(btn);
        canvasPane.getChildren().add(btn);
    }

}

class SquareWithIsoscelesTriangle implements GrammarRule{

    private static final String RULE_NAME = "SquareWithIsoscelesTriangle";

    @Override
    public boolean canApply(Shape shape, List<Shape> allShapes){
        return shape instanceof Square && !shape.isRuleBlocked(RULE_NAME);
    }

    @Override
    public void apply(Shape shape, List<Shape> allShapes, Map<String, Object> params){
        double x = shape.getX();
        double y = shape.getY();
        double size = shape.getSize();
        shape.blockRule("SquareWithIsoscelesTriangle");
        shape.blockRule("SquareToFourSquares");
        shape.blockRule("TwoSquaresToRectangle");
        allShapes.add(new IsoscelesTriangle(x, y, size));
    }

    @Override
    public void createMarkerButtons(Shape shape, List<Shape> allShapes, Pane canvasPane, List<Button> buttonList, java.util.function.Consumer<Map<String, Object>> onApplyWithParams){
        Button btn = MarkerCreator.createMarker(shape.getX(), shape.getY(),
               ()-> onApplyWithParams.accept(new HashMap<>()));
        buttonList.add(btn);
        canvasPane.getChildren().add(btn);
    }

}

class TwoSquaresToRectangle implements GrammarRule{

    private static final String RULE_NAME = "TwoSquaresToRectangle";

    @Override
    public boolean canApply(Shape shape, List<Shape> allShapes){
        if(!(shape instanceof Square)|| shape.isRuleBlocked(RULE_NAME))return false;
        return findAdjacentSquare(shape, allShapes)!= null;
    }

    @Override
    public void apply(Shape shape, List<Shape> allShapes, Map<String, Object> params){
        Shape otherShape =(Shape)params.get("otherSquare");
        if(otherShape == null)return;
        double cx =(shape.getX()+ otherShape.getX())/ 2;
        double cy =(shape.getY()+ otherShape.getY())/ 2;
        double size = shape.getSize();
        double dx = Math.abs(shape.getX()- otherShape.getX());
        double dy = Math.abs(shape.getY()- otherShape.getY());
        double width, height;
        if(dx > dy){
            width = size * 2;
            height = size;
        }else{
            width = size;
            height = size * 2;
        }
        allShapes.remove(shape);
        allShapes.remove(otherShape);
        allShapes.add(new Rectangle(cx, cy, width, height));
    }

    @Override
    public void createMarkerButtons(Shape shape, List<Shape> allShapes, Pane canvasPane, List<Button> buttonList, java.util.function.Consumer<Map<String, Object>> onApplyWithParams){
        List<Shape> adjacent = findAllAdjacentSquares(shape, allShapes);
        for(Shape other : adjacent){
            if(other.isRuleBlocked(RULE_NAME))continue;
            double midX =(shape.getX()+ other.getX())/ 2;
            double midY =(shape.getY()+ other.getY())/ 2;
            Button btn = MarkerCreator.createMarker(midX, midY,()->{
                Map<String, Object> params = new HashMap<>();
                params.put("otherSquare", other);
                onApplyWithParams.accept(params);
            });

            buttonList.add(btn);
            canvasPane.getChildren().add(btn);
        }
    }

    private Shape findAdjacentSquare(Shape square, List<Shape> allShapes){
        double size = square.getSize();
        for(Shape other : allShapes){
            if(other != square && other instanceof Square && !other.isRuleBlocked(RULE_NAME)){
                double dx = Math.abs(square.getX()- other.getX());
                double dy = Math.abs(square.getY()- other.getY());
                boolean leftRight = dx == size && dy == 0;
                boolean upDown = dy == size && dx == 0;
                if(leftRight || upDown)return other;
            }
        }
        return null;
    }

    private List<Shape> findAllAdjacentSquares(Shape square, List<Shape> allShapes){
        List<Shape> result = new java.util.ArrayList<>();
        double size = square.getSize();
        for(Shape other : allShapes){
            if(other != square && other instanceof Square){
                double dx = Math.abs(square.getX()- other.getX());
                double dy = Math.abs(square.getY()- other.getY());
                boolean leftRight = dx == size && dy == 0;
                boolean upDown = dy == size && dx == 0;
                if(upDown || leftRight)result.add(other);
            }
        }
        return result;
    }
}