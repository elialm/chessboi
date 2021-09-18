package org.efac.chess;

public class Point {
    int xComponent;
    int yComponent;

    public int getXComponent() { return xComponent; }
    public int getYComponent() { return yComponent; }

    public Point(int xComponent, int yComponent) {
        this.xComponent = xComponent;
        this.yComponent = yComponent;
    }
}
