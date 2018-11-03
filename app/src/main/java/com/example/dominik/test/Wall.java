package com.example.dominik.test;


import java.io.Serializable;

public class Wall implements Serializable{
    Direction one;
    Direction two;

    Wall() {
        this.one = null;
        this.two = null;
    }

    public void rotateWallCW() {
        if (this.one != null)
            this.one = one.getNext();
        if (this.two != null)
            this.two = two.getNext();
    }

    @Override
    public String toString() {
        return "" + one + two + "";
    }

    Wall(Direction one) {
        this.one = one;
    }

    Wall(Direction one, Direction two) {
        this.one = one;
        this.two = two;
    }

    public boolean isPartOf(Direction direction) {
        if (this.one == direction || this.two == direction)
            return true;
        return false;

    }

    public Direction getOne() {
        return one;
    }

    public void setOne(Direction one) {
        this.one = one;
    }

    public Direction getTwo() {
        return two;
    }

    public void setTwo(Direction two) {
        this.two = two;
    }

}
