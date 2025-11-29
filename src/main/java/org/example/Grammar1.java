package org.example;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Grammar1 implements GrammarDefinition{

    private final GrammarRule rule1 = new TwoTrianglesToRhombus();
    private final GrammarRule rule2 = new TriangleWithInscribedCircle();
    private final GrammarRule rule3 = new AttachTriangleToSide();

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
        return new EquilateralTriangle(centerX, centerY, size);
    }
}

class TwoTrianglesToRhombus implements GrammarRule{

    private static final String RULE_NAME = "TwoTrianglesToRhombus";

    @Override
    public boolean canApply(Shape shape, List<Shape> allShapes){
        if(!(shape instanceof EquilateralTriangle) || shape.isRuleBlocked(RULE_NAME)) return false;
        return findAdjacentTriangle(shape, allShapes) != null;
    }

    @Override
    public void apply(Shape shape, List<Shape> allShapes, Map<String, Object> params){
        Shape other = findAdjacentTriangle(shape, allShapes);
        if(other == null) return;
        double[][] v1 = shape.getVertices();
        double[][] v2 = other.getVertices();
        List<double[]> unique = GeometryUtils.getUniqueVertices(v1, v2);

        if(unique.size() == 4){
            double[] center = GeometryUtils.calculateCentroid(unique);
            double[][] verticesArray = new double[4][2];
            for(int i = 0; i < 4; i++){
                verticesArray[i][0] = unique.get(i)[0];
                verticesArray[i][1] = unique.get(i)[1];
            }
            allShapes.remove(shape);
            allShapes.remove(other);
            allShapes.add(new Rhombus(center[0], center[1], shape.getSize(), verticesArray));
        }
    }

    @Override
    public void createMarkerButtons(Shape shape, List<Shape> allShapes, Pane canvasPane, List<Button> buttonList, java.util.function.Consumer<Map<String, Object>> onApplyWithParams){
        Shape other = findAdjacentTriangle(shape, allShapes);
        if(other == null) return;
        if(other.isRuleBlocked(RULE_NAME)) return;
        int[][] edges = GeometryUtils.getSharedEdgeIndices(shape, other);
        if(edges == null) return;
        double[][] v1 = shape.getVertices();
        int edgeIndex = edges[0][0];
        double midX =(v1[edgeIndex][0] + v1[(edgeIndex + 1) % 3][0]) / 2;
        double midY =(v1[edgeIndex][1] + v1[(edgeIndex + 1) % 3][1]) / 2;
        Button selectBtn = MarkerCreator.createMarker(midX, midY,
              () -> onApplyWithParams.accept(new HashMap<>()));
        buttonList.add(selectBtn);
        canvasPane.getChildren().add(selectBtn);
    }

    private Shape findAdjacentTriangle(Shape triangle, List<Shape> allShapes){
        double[][] v1 = triangle.getVertices();
        for(Shape other : allShapes){
            if(other != triangle && other instanceof EquilateralTriangle && Math.abs(other.getSize() - triangle.getSize()) < 1){
                double[][] v2 = other.getVertices();
                if(GeometryUtils.hasSharedEdge(v1, v2)){
                    return other;
                }
            }
        }
        return null;
    }
}

class TriangleWithInscribedCircle implements GrammarRule{

    private static final String RULE_NAME = "TriangleWithInscribedCircle";

    @Override
    public boolean canApply(Shape shape, List<Shape> allShapes){
        return shape instanceof EquilateralTriangle && !shape.isRuleBlocked(RULE_NAME);
    }

    @Override
    public void apply(Shape shape, List<Shape> allShapes, Map<String, Object> params){
        double inRadius = shape.getSize() /(2 * Math.sqrt(3));
        shape.blockRule("TwoTrianglesToRhombus");
        shape.blockRule("TriangleWithInscribedCircle");
        shape.blockRule("AttachTriangleToSide");
        allShapes.add(new Circle(shape.getX(), shape.getY(), inRadius * 2));
    }

    @Override
    public void createMarkerButtons(Shape shape, List<Shape> allShapes, Pane canvasPane, List<Button> buttonList, java.util.function.Consumer<Map<String, Object>> onApplyWithParams){
        Button selectBtn = MarkerCreator.createMarker(shape.getX(), shape.getY(),
              () -> onApplyWithParams.accept(new HashMap<>()));
        buttonList.add(selectBtn);
        canvasPane.getChildren().add(selectBtn);
    }

}

class AttachTriangleToSide implements GrammarRule{

    private static final String RULE_NAME = "AttachTriangleToSide";

    @Override
    public boolean canApply(Shape shape, List<Shape> allShapes){
        return shape instanceof EquilateralTriangle && !shape.isRuleBlocked(RULE_NAME);
    }

    @Override
    public void apply(Shape shape, List<Shape> allShapes, Map<String, Object> params){
        int sideIndex =(int) params.getOrDefault("sideIndex", 0);
        double[][] newVertices = GeometryUtils.createAttachedTriangleVertices(shape, sideIndex);
        if(newVertices == null) return;
        double newX =(newVertices[0][0] + newVertices[1][0] + newVertices[2][0]) / 3;
        double newY =(newVertices[0][1] + newVertices[1][1] + newVertices[2][1]) / 3;

        for(Shape other : allShapes){
            if(other instanceof EquilateralTriangle && other.getX() == newX  && other.getY() == newY){
                return;
            }
        }

        allShapes.add(new EquilateralTriangle(newX, newY, shape.getSize(),  newVertices));
    }

    @Override
    public void createMarkerButtons(Shape shape, List<Shape> allShapes, Pane canvasPane, List<Button> buttonList, java.util.function.Consumer<Map<String, Object>> onApplyWithParams){
        double[][] sides = GeometryUtils.getTriangleSideMidpoints(shape);
        for(int i = 0; i < 3; i++){
            final int sideIndex = i;
            double[][] newVertices = GeometryUtils.createAttachedTriangleVertices(shape, sideIndex);
            if(newVertices == null) continue;
            double newX =(newVertices[0][0] + newVertices[1][0] + newVertices[2][0]) / 3;
            double newY =(newVertices[0][1] + newVertices[1][1] + newVertices[2][1]) / 3;
            boolean positionOccupied = false;
            for(Shape other : allShapes){
                if(other instanceof EquilateralTriangle && other.getX() == newX  && other.getY() == newY){
                    positionOccupied = true;
                    break;
                }
            }
            if(positionOccupied) continue;
            Button selectBtn = MarkerCreator.createMarker(sides[i][0], sides[i][1],() ->{
                Map<String, Object> params = new HashMap<>();
                params.put("sideIndex", sideIndex);
                onApplyWithParams.accept(params);
            });

            buttonList.add(selectBtn);
            canvasPane.getChildren().add(selectBtn);
        }
    }
}