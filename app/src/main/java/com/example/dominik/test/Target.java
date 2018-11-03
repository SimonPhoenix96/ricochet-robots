package com.example.dominik.test;


import java.io.Serializable;

public class Target implements Serializable{
    Color targetColor;
    Position targetPostion;

    Target() {
        this.targetColor = null;
        this.targetPostion = null;
    }

    Target(Color targetColor, Position targetPosition) {
        this.targetColor = targetColor;
        this.targetPostion = targetPosition;
    }

    Target(Color targetColor, int x, int y) {
        this.targetColor = targetColor;
        this.targetPostion = new Position(x, y);
    }

    public Color getTargetColor() {
        return targetColor;
    }

    public void setTargetColor(Color targetColor) {
        this.targetColor = targetColor;
    }

    @Override
    public String toString() {
        return "Target [targetColor=" + targetColor + ", targetPostion=" + targetPostion + "]";
    }

    public Position getTargetPostion() {
        return targetPostion;
    }

    public void setTargetPostion(Position targetPostion) {
        this.targetPostion = targetPostion;
    }

}