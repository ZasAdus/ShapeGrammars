package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class ShapeRenderer{

    public static void drawShape(GraphicsContext gc, Shape shape){
        gc.save();

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.setFill(Color.TRANSPARENT);

        double x = shape.getX();
        double y = shape.getY();
        double size = shape.getSize();
        switch (shape) {
            case EquilateralTriangle e -> drawEquilateralTriangle(gc, e);
            case Circle c -> drawCircle(gc, x, y, size);
            case Rhombus r -> drawRhombus(gc, r.getVertices());
            case Square s -> drawSquare(gc, x, y, size);
            case IsoscelesTriangle i -> drawIsoscelesTriangle(gc, x, y, size);
            case Rectangle r -> drawRectangle(gc, x, y, r.getWidth(), r.getHeight());
            default -> {}
        }
        gc.restore();
    }

    private static void drawEquilateralTriangle(GraphicsContext gc, EquilateralTriangle triangle){
        double[][] vertices = triangle.getVertices();
        double[] xs = new double[3];
        double[] ys = new double[3];
        System.out.println(Arrays.deepToString(vertices));
        for (int i = 0; i < 3; i++) {
            xs[i] = vertices[i][0];
            ys[i] = vertices[i][1];
        }
        gc.strokePolygon(xs, ys, 3);
    }

    private static void drawCircle(GraphicsContext gc, double x, double y, double size){
        gc.strokeOval(x - size/2, y - size/2, size, size);
    }

    private static void drawSquare(GraphicsContext gc, double x, double y, double size){
        double halfSize = size / 2;
        gc.strokeRect(x - halfSize, y - halfSize, size, size);
    }

    private static void drawIsoscelesTriangle(GraphicsContext gc, double x, double y, double size){
        double halfSize = size / 2;
        double[] xPoints = {
                x,
                x - halfSize,
                x + halfSize
        };
        double[] yPoints = {
                y - halfSize,
                y + halfSize,
                y + halfSize
        };
        gc.strokePolygon(xPoints, yPoints, 3);
    }

    private static void drawRectangle(GraphicsContext gc, double x, double y, double width, double height){
        gc.strokeRect(x - width/2, y - height/2, width, height);
    }

    private static void drawRhombus(GraphicsContext gc, double[][] vertices) {
        double Cx = 0, Cy = 0;
        for(double[] v : vertices){
            Cx += v[0];
            Cy += v[1];
        }
        final double cx = Cx / vertices.length;  //środek rombu obliczamy
        final double cy = Cy / vertices.length;

        Arrays.sort(vertices, (v1, v2) -> { //sortowanie by przy użyciu metody do rysowania wielokątu powstał nam romb
            double angle1 = Math.atan2(v1[1] - cy, v1[0] - cx);
            double angle2 = Math.atan2(v2[1] - cy, v2[0] - cx);
            return Double.compare(angle1, angle2);
        });

        double[] xPoints = new double[4];
        double[] yPoints = new double[4];

        for(int i = 0; i < 4; i++){
            xPoints[i] = vertices[i][0];
            yPoints[i] = vertices[i][1];
        }

        gc.strokePolygon(xPoints, yPoints, 4);
    }
}