package org.example;

import java.util.ArrayList;
import java.util.List;

public class GeometryUtils {

    public static double[][] getTriangleSideMidpoints(Shape triangle){
        double[][] vertices = triangle.getVertices();
        return new double[][] {
                {(vertices[0][0] + vertices[1][0]) / 2, (vertices[0][1] + vertices[1][1]) / 2},
                {(vertices[0][0] + vertices[2][0]) / 2, (vertices[0][1] + vertices[2][1]) / 2},
                {(vertices[1][0] + vertices[2][0]) / 2, (vertices[1][1] + vertices[2][1]) / 2}
        };
    }
    public static boolean hasSharedEdge(double[][] v1, double[][] v2){
        int x = 0;
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(v1[i][0] == v2[j][0] && v1[i][1] == v2[j][1]){
                    x++;
                    break;
                }
            }
        }
        return x >= 2;
    }

    private static boolean samePoint(double[] a, double[] b){
        return a[0] == b[0] && a[1] == b[1];
    }

    public static int[][] getSharedEdgeIndices(Shape t1, Shape t2){
        double[][] v1 = t1.getVertices();
        double[][] v2 = t2.getVertices();
        int[][] sharedEdges = new int[2][1];
        for(int i = 0; i < 3; i++){
            int i2 = (i + 1) % 3;
            for(int j = 0; j < 3; j++){
                int j2 = (j + 1) % 3;
                boolean sameA = samePoint(v1[i], v2[j]) && samePoint(v1[i2], v2[j2]);
                boolean sameB = samePoint(v1[i], v2[j2]) && samePoint(v1[i2], v2[j]);
                if(sameA || sameB){
                    sharedEdges[0][0] = i;
                    sharedEdges[1][0] = j;
                    return sharedEdges;
                }
            }
        }
        return null;
    }

    public static List<double[]> getUniqueVertices(double[][] vertices1, double[][] vertices2){
        List<double[]> unique = new ArrayList<>();
        for(double[] v : vertices1){
            unique.add(new double[]{v[0], v[1]});
        }
        for(double[] v : vertices2){
            boolean exists = false;
            for(double[] u : unique){
                if(samePoint(v, u)){
                    exists = true;
                    break;
                }
            }
            if(!exists){
                unique.add(new double[]{v[0], v[1]});
            }
        }

        return unique;
    }

    public static double[] calculateCentroid(List<double[]> points){
        double avgX = 0, avgY = 0;
        for(double[] p : points){
            avgX += p[0];
            avgY += p[1];
        }
        return new double[]{avgX / points.size(), avgY / points.size()};
    }

    public static double[][] createAttachedTriangleVertices(Shape triangle, int sideIndex){
        double size = triangle.getSize();
        double height = size * Math.sqrt(3) / 2;
        double[][] vertices = triangle.getVertices();
        double x1, y1, x2, y2;
        switch(sideIndex) {
            case 0 -> { x1 = vertices[0][0]; y1 = vertices[0][1];
                x2 = vertices[1][0]; y2 = vertices[1][1]; }
            case 1 -> { x1 = vertices[0][0]; y1 = vertices[0][1];
                x2 = vertices[2][0]; y2 = vertices[2][1]; }
            case 2 -> { x1 = vertices[1][0]; y1 = vertices[1][1];
                x2 = vertices[2][0]; y2 = vertices[2][1]; }
            default -> { return null; }
        }
        double midX = (x1 + x2) / 2;
        double midY = (y1 + y2) / 2;
        double vecX = y2 - y1;
        double vecY = -(x2 - x1);
        double vecLength = Math.sqrt(vecX * vecX + vecY * vecY);
        vecX /= vecLength;
        vecY /= vecLength;
        double toMidX = midX - triangle.getX();
        double toMidY = midY - triangle.getY();
        double dotProduct = vecX * toMidX + vecY * toMidY;
        if(dotProduct < 0){
            vecX = -vecX;
            vecY = -vecY;
        }
        double x3 = midX + vecX * height;
        double y3 = midY + vecY * height;
        return new double[][]{
                {x3, y3},
                {x1, y1},
                {x2, y2}
        };
    }
}