package com.example.dominik.test;

import java.io.Serializable;

public enum Direction implements Serializable{
	N, W, S, E, ;
	
	public Direction getOpposite() {
		Direction direction = this;
		switch(direction) {
		case N: direction = S;
			break;
		case E: direction = W;
			break;
		case S: direction = N;
			break;
		case W: direction = E;
			break;
		default:
			break;
		}
		return direction;
		
	}
	
	public Direction getNext() {
		Direction direction = this;
		switch(direction) {
		case N: direction = E;
			break;
		case E: direction = S;
			break;
		case S: direction = W;
			break;
		case W: direction = N;
			break;
		default:
			break;
		}
		return direction;
	}
}


