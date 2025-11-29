package org.example;

import java.util.HashSet;
import java.util.Set;

public abstract class Shape{
    protected final double x;
    protected final double y;
    protected final double size;
    protected final Set<String> blockedRules = new HashSet<>();

    public Shape(double x, double y, double size){
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }

    public void blockRule(String ruleName){
        blockedRules.add(ruleName);
    }

    public boolean isRuleBlocked(String ruleName){
        return blockedRules.contains(ruleName);
    }

    public double getSize(){
        return size;
    }

    public double[][] getVertices(){
        return null;
    }
}

class Circle extends Shape{
    public Circle(double x, double y, double size){
        super(x, y, size);
    }
}

class Rhombus extends Shape{
    private final double[][] customVertices;

    public Rhombus(double x, double y, double size, double[][] vertices){
        super(x, y, size);
        this.customVertices = vertices;
    }

    @Override
    public double[][] getVertices(){
        return customVertices;
    }
}

class Square extends Shape{
    public Square(double x, double y, double size){
        super(x, y, size);
    }
}

class IsoscelesTriangle extends Shape{
    public IsoscelesTriangle(double x, double y, double size) {
        super(x, y, size);
    }
}

class Rectangle extends Shape{
    private final double width;
    private final double height;

    public Rectangle(double x, double y, double width, double height){
        super(x, y, width);
        this.width = width;
        this.height = height;
    }

    public double getWidth(){
        return width;
    }

    public double getHeight(){
        return height;
    }
}

class EquilateralTriangle extends Shape{
    private final double[][] vertices;

    public EquilateralTriangle(double x, double y, double size){
        super(x, y, size);
        this.vertices = calculateVertices(x, y, size);
    }

    public EquilateralTriangle(double x, double y, double size, double[][] vertices){
        super(x, y, size);
        this.vertices = vertices;
    }

    @Override
    public double[][] getVertices(){
        return vertices;
    }

    private static double[][] calculateVertices(double x, double y, double size){
        double height = size * Math.sqrt(3) / 2;
        return new double[][]{
                {x, y - height * 2/3},
                {x - size / 2, y + height * 1/3},
                {x + size / 2, y + height * 1/3}
        };
    }
}