package com.example.dominik.test;

import java.io.Serializable;

public class Position implements Serializable {
	int x;
	int y;

	Position(int x, int y){
		this.x = x;
		this.y = y;
	}

	public Position nextPosition(Direction direction) {
		Position nextPosition = null;
		switch(direction) {
			case N:	nextPosition =  new Position(this.x, this.y + 1);
				break;
			case E:	nextPosition = new Position(this.x + 1, this.y);
				break;
			case S:	nextPosition = new Position(this.x, this.y - 1);
				break;
			case W: nextPosition = new Position(this.x - 1, this.y);
				break;
			default:
				break;
		}
		return nextPosition;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
