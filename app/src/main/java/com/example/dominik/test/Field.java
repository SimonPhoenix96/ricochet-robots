package com.example.dominik.test;


import java.io.Serializable;

public class Field implements Serializable {
    Wall wall;
    Color goal;
    boolean robot; // presence of a Robot

    Field() {
        this.wall = new Wall();
        this.goal = null;
        this.robot = false;
    }

    @Override
    public String toString() {
        return "Field [wall=" + wall + ", goal=" + goal + ", robot=" + robot + "]";
    }

    Field(Wall wall, Color goal) {
        this.wall = wall;
        this.goal = goal;
    }

    Field(Direction one, Color goal) {
        this.wall = new Wall(one);
        this.goal = goal;
    }

    Field(Direction one, Direction two, Color goal) {
        this.wall = new Wall(one, two);
        this.goal = goal;
    }

    public Wall getWall() {
        return wall;
    }

    public void setWall(Wall wall) {
        this.wall = wall;
    }

    public void setWall(Direction one) {
        this.wall = new Wall(one);
    }

    public void setWall(Direction one, Direction two) {
        this.wall = new Wall(one, two);
    }

    public boolean hasRobot() {
        return robot;
    }

    public void setRobot(boolean robot) {
        this.robot = robot;
    }

    public Color getGoal() {
        return goal;
    }

    public void setGoal(Color goal) {
        this.goal = goal;
    }

    public boolean hasGoal() {
        if (this.goal != null)
            return true;
        return false;
    }

}
