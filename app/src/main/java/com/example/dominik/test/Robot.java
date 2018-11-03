package com.example.dominik.test;


import java.io.Serializable;

public class Robot implements Serializable{
    Color color;
    Position position;

    public Robot (Color color){
        this.color = color;
    }
    public Robot(Color color, Position position) {
        this.color = color;
        this.position = position;
    }

    public Robot(Color color, int x, int y) {
        this.color = color;
        this.position = new Position(x, y);
    }

    @Override
    public String toString() {
        return "Robot [color=" + color + ", position=" + position + "]";
    }

    public void moveRobot(Board board, Direction direction) {
        boolean obstacle = false;
        while (obstacle == false) {
            if (this.canMove(board, direction)) {
                board.getField(this.position).setRobot(false);
                this.position = this.position.nextPosition(direction);
                board.getField(this.position).setRobot(true);
            } else
                obstacle = true;
        }
    }


    private boolean canMove(Board board, Direction direction) {
        if (board.getField(this.position).getWall().isPartOf(direction)
                || board.getField(this.position.nextPosition(direction)).hasRobot()
                || board.getField(this.position.nextPosition(direction)).getWall().isPartOf(getOpposite(direction)))
            return false;
        return true;
    }

    public static Direction getOpposite(Direction direction) {
        switch (direction) {
            case N:
                direction = Direction.S;
                break;
            case E:
                direction = Direction.W;
                break;
            case S:
                direction = Direction.N;
                break;
            case W:
                direction = Direction.E;
                break;
            default:
                break;
        }
        return direction;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

}
