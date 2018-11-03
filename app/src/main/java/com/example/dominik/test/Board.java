package com.example.dominik.test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Scanner;

public class Board implements Serializable{
	int length;
	Field[][] board;

	Board(int length) {
		this.length = length;
		this.board = new Field[length][length];
		for (int k = 0; k < length; k++) {
			for (int i = 0; i < length; i++) {
				this.board[i][k] = new Field();
			}
		}
	}

	public Board rotateCW() {
		Board board = this;
		Board newBoard = new Board(board.getLength());
		for (int i = 0; i < board.getLength(); i++) {
			for (int k = 0; k < board.getLength(); k++) {
				newBoard.getBoard()[k][board.length - 1 - i] = board.getBoard()[i][k];
				newBoard.getWall(k, board.length - 1 - i).rotateWallCW();
			}
		}
		return newBoard;
	}

	public Board rotateCW(int number) {
		Board newBoard = this;
		for (int n = 0; n < number; n++) {
			Board temp = newBoard.rotateCW();
			newBoard = temp;
		}
		return newBoard;
	}

	public Boolean hasRobot(int x, int y) {
		return this.getField(x, y).hasRobot();
	}


	public Color getGoal(int x, int y) {
		return this.getField(x, y).getGoal();
	}

	public Boolean hasWall(int x, int y) {
		return this.getField(x, y).getWall().getOne() != null;

	}

	public Wall getWall(int x, int y) {
		return this.getField(x, y).getWall();
	}

	public Direction getWallOne(int x, int y) {
		return this.getField(x, y).getWall().getOne();

	}

	public Direction getWalltwo(int x, int y) {
		return this.getField(x, y).getWall().getTwo();

	}

	public void printBoard() {
		Scanner scanner = new Scanner(System.in);
		for (int y = length - 1; y >= 0; y--) {
			System.out.print("\n");
			for (int x = 0; x < length; x++) {
				if (this.hasWall(x, y) == false)
					System.out.print(" XX");
				else if (this.getWalltwo(x, y) == null)
					System.out.print(" " + this.getWallOne(x, y) + "X");
				else
					System.out.print(" " + this.getWall(x, y));
			}
		}
		System.out.println();
		scanner.close();
	}

	@Override
	public String toString() {
		return "Board [length=" + length + ", board=" + Arrays.toString(board) + "]";
	}

	public Field getField(Position position) {
		return this.board[position.getX()][position.getY()];
	}

	public Field getField(int x, int y) {
		return this.board[x][y];
	}

	public boolean hasGoal(int x, int y) {
		return this.getField(x, y).hasGoal();
	}

	public int getLength() {
		return length;
	}

	public Field[][] getBoard() {
		return board;
	}

	public void setBoard(Field[][] board) {
		this.board = board;
		this.length = board.length;
	}

	public static Board[] createBoards(){

        Board oneA = new Board(8);
        oneA.getField(0, 0).setWall(Direction.W);
        oneA.getField(7, 0).setWall(Direction.N, Direction.W);
        oneA.getField(0, 1).setWall(Direction.N, Direction.W);
        oneA.getField(3, 1).setWall(Direction.S, Direction.W);
        oneA.getField(0, 2).setWall(Direction.W);
        oneA.getField(0, 3).setWall(Direction.W);
        oneA.getField(0, 4).setWall(Direction.W);
        oneA.getField(6, 4).setWall(Direction.S, Direction.E);
        oneA.getField(0, 5).setWall(Direction.W);
        oneA.getField(1, 5).setWall(Direction.N, Direction.E);
        oneA.getField(0, 6).setWall(Direction.W);
        oneA.getField(4, 6).setWall(Direction.N, Direction.W);
        oneA.getField(0, 7).setWall(Direction.N, Direction.W);
        oneA.getField(1, 7).setWall(Direction.N, Direction.E);
        oneA.getField(2, 7).setWall(Direction.N);
        oneA.getField(3, 7).setWall(Direction.N);
        oneA.getField(4, 7).setWall(Direction.N);
        oneA.getField(5, 7).setWall(Direction.N);
        oneA.getField(6, 7).setWall(Direction.N);
        oneA.getField(7, 7).setWall(Direction.N);
        oneA.getField(3, 1).setGoal(Color.BLUE);
        oneA.getField(6, 4).setGoal(Color.YELLOW);
        oneA.getField(1, 5).setGoal(Color.GREEN);
        oneA.getField(4, 6).setGoal(Color.RED);

        //2A
        Board twoA = new Board(8);
        twoA.getField(0, 0).setWall(Direction.W);
        twoA.getField(0, 1).setWall(Direction.W);
        twoA.getField(0, 2).setWall(Direction.W);
        twoA.getField(0, 3).setWall(Direction.N, Direction.W);
        twoA.getField(0, 4).setWall(Direction.W);
        twoA.getField(0, 5).setWall(Direction.W);
        twoA.getField(0, 6).setWall(Direction.W);
        twoA.getField(0, 7).setWall(Direction.N, Direction.W);
        twoA.getField(1, 7).setWall(Direction.N);
        twoA.getField(2, 7).setWall(Direction.N);
        twoA.getField(3, 7).setWall(Direction.N, Direction.W);
        twoA.getField(4, 7).setWall(Direction.N);
        twoA.getField(5, 7).setWall(Direction.N);
        twoA.getField(6, 7).setWall(Direction.N);
        twoA.getField(7, 7).setWall(Direction.N);
        twoA.getField(7, 0).setWall(Direction.N, Direction.W);
        twoA.getField(2, 1).setWall(Direction.N, Direction.E);
        twoA.getField(6, 3).setWall(Direction.N, Direction.W);
        twoA.getField(1, 5).setWall(Direction.S, Direction.W);
        twoA.getField(5, 6).setWall(Direction.S, Direction.E);
        twoA.getField(2, 1).setGoal(Color.BLUE);
        twoA.getField(6, 3).setGoal(Color.YELLOW);
        twoA.getField(1, 5).setGoal(Color.RED);
        twoA.getField(5, 6).setGoal(Color.GREEN);

        //3A
        Board threeA = new Board(8);
        threeA.getField(0, 0).setWall(Direction.W);
        threeA.getField(0, 1).setWall(Direction.W);
        threeA.getField(0, 2).setWall(Direction.N, Direction.W);
        threeA.getField(0, 3).setWall(Direction.W);
        threeA.getField(0, 4).setWall(Direction.W);
        threeA.getField(0, 5).setWall(Direction.W);
        threeA.getField(0, 6).setWall(Direction.W);
        threeA.getField(0, 7).setWall(Direction.N, Direction.W);
        threeA.getField(1, 7).setWall(Direction.N);
        threeA.getField(2, 7).setWall(Direction.N);
        threeA.getField(3, 7).setWall(Direction.N, Direction.E);
        threeA.getField(4, 7).setWall(Direction.N);
        threeA.getField(5, 7).setWall(Direction.N);
        threeA.getField(6, 7).setWall(Direction.N);
        threeA.getField(7, 7).setWall(Direction.N);
        threeA.getField(7, 0).setWall(Direction.N, Direction.W);
        threeA.getField(1, 1).setWall(Direction.N, Direction.W);
        threeA.getField(2, 3).setWall(Direction.N, Direction.E);
        threeA.getField(7, 2).setWall(Direction.S, Direction.W);
        threeA.getField(5, 5).setWall(Direction.S, Direction.E);
        threeA.getField(1, 1).setGoal(Color.YELLOW);
        threeA.getField(2, 3).setGoal(Color.GREEN);
        threeA.getField(7, 2).setGoal(Color.RED);
        threeA.getField(5, 5).setGoal(Color.BLUE);

        //4A
        Board fourA = new Board(8);
        fourA.getField(0, 0).setWall(Direction.N, Direction.W);
        fourA.getField(0, 1).setWall(Direction.W);
        fourA.getField(0, 2).setWall(Direction.W);
        fourA.getField(0, 3).setWall(Direction.W);
        fourA.getField(0, 4).setWall(Direction.W);
        fourA.getField(0, 5).setWall(Direction.W);
        fourA.getField(0, 6).setWall(Direction.W);
        fourA.getField(0, 7).setWall(Direction.N, Direction.W);
        fourA.getField(1, 7).setWall(Direction.N);
        fourA.getField(2, 7).setWall(Direction.N);
        fourA.getField(3, 7).setWall(Direction.N, Direction.E);
        fourA.getField(4, 7).setWall(Direction.N);
        fourA.getField(5, 7).setWall(Direction.N);
        fourA.getField(6, 7).setWall(Direction.N);
        fourA.getField(7, 7).setWall(Direction.N);
        fourA.getField(7, 0).setWall(Direction.N, Direction.W);
        fourA.getField(2, 2).setWall(Direction.S, Direction.E);
        fourA.getField(7, 2).setWall(Direction.S, Direction.E);
        fourA.getField(5, 3).setWall(Direction.N, Direction.W);
        fourA.getField(1, 4).setWall(Direction.N, Direction.E);
        fourA.getField(6, 6).setWall(Direction.S, Direction.W);
        fourA.getField(2, 2).setGoal(Color.RED);
        fourA.getField(7, 2).setGoal(Color.VORTEX);
        fourA.getField(5, 3).setGoal(Color.GREEN);
        fourA.getField(1, 4).setGoal(Color.YELLOW);
        fourA.getField(6, 6).setGoal(Color.BLUE);

        //1B
        Board oneB = new Board(8);
        oneB.getField(0, 0).setWall(Direction.W);
        oneB.getField(0, 1).setWall(Direction.W, Direction.N);
        oneB.getField(0, 2).setWall(Direction.W);
        oneB.getField(0, 3).setWall(Direction.W);
        oneB.getField(0, 4).setWall(Direction.W);
        oneB.getField(0, 5).setWall(Direction.W);
        oneB.getField(0, 6).setWall(Direction.W);
        oneB.getField(0, 7).setWall(Direction.N, Direction.W);
        oneB.getField(1, 7).setWall(Direction.N);
        oneB.getField(2, 7).setWall(Direction.N);
        oneB.getField(3, 7).setWall(Direction.N);
        oneB.getField(4, 7).setWall(Direction.N, Direction.E);
        oneB.getField(5, 7).setWall(Direction.N);
        oneB.getField(6, 7).setWall(Direction.N);
        oneB.getField(7, 7).setWall(Direction.N);
        oneB.getField(7, 0).setWall(Direction.N, Direction.W);
        oneB.getField(3, 1).setWall(Direction.S, Direction.W);
        oneB.getField(3, 1).setGoal(Color.RED);
        oneB.getField(6, 2).setWall(Direction.N, Direction.E);
        oneB.getField(6, 2).setGoal(Color.BLUE);
        oneB.getField(1, 5).setWall(Direction.N, Direction.W);
        oneB.getField(1, 5).setGoal(Color.GREEN);
        oneB.getField(6, 6).setWall(Direction.S, Direction.E);
        oneB.getField(6, 6).setGoal(Color.YELLOW);

        //2B
        Board twoB = new Board(8);
        twoB.getField(0, 0).setWall(Direction.W);
        twoB.getField(0, 1).setWall(Direction.W);
        twoB.getField(0, 2).setWall(Direction.N, Direction.W);
        twoB.getField(0, 3).setWall(Direction.W);
        twoB.getField(0, 4).setWall(Direction.W);
        twoB.getField(0, 5).setWall(Direction.W);
        twoB.getField(0, 6).setWall(Direction.W);
        twoB.getField(0, 7).setWall(Direction.N, Direction.W);
        twoB.getField(1, 7).setWall(Direction.N);
        twoB.getField(2, 7).setWall(Direction.N);
        twoB.getField(3, 7).setWall(Direction.N);
        twoB.getField(4, 7).setWall(Direction.N, Direction.E);
        twoB.getField(5, 7).setWall(Direction.N);
        twoB.getField(6, 7).setWall(Direction.N);
        twoB.getField(7, 7).setWall(Direction.N);
        twoB.getField(7, 0).setWall(Direction.N, Direction.W);
        twoB.getField(1, 1).setWall(Direction.S, Direction.E);
        twoB.getField(1, 1).setGoal(Color.GREEN);
        twoB.getField(4, 2).setWall(Direction.N, Direction.E);
        twoB.getField(4, 2).setGoal(Color.RED);
        twoB.getField(6, 4).setWall(Direction.S, Direction.W);
        twoB.getField(6, 4).setGoal(Color.BLUE);
        twoB.getField(2, 6).setWall(Direction.N, Direction.W);
        twoB.getField(2, 6).setGoal(Color.YELLOW);

        //3B
        Board threeB = new Board(8);
        threeB.getField(0, 0).setWall(Direction.W);
        threeB.getField(0, 1).setWall(Direction.N, Direction.W);
        threeB.getField(0, 2).setWall(Direction.W);
        threeB.getField(0, 3).setWall(Direction.W);
        threeB.getField(0, 4).setWall(Direction.W);
        threeB.getField(0, 5).setWall(Direction.W);
        threeB.getField(0, 6).setWall(Direction.W);
        threeB.getField(0, 7).setWall(Direction.N, Direction.W);
        threeB.getField(1, 7).setWall(Direction.N);
        threeB.getField(2, 7).setWall(Direction.N);
        threeB.getField(3, 7).setWall(Direction.N, Direction.E);
        threeB.getField(4, 7).setWall(Direction.N);
        threeB.getField(5, 7).setWall(Direction.N);
        threeB.getField(6, 7).setWall(Direction.N);
        threeB.getField(7, 7).setWall(Direction.N);
        threeB.getField(7, 0).setWall(Direction.N, Direction.W);
        threeB.getField(7, 2).setWall(Direction.N, Direction.W);
        threeB.getField(7, 2).setGoal(Color.YELLOW);
        threeB.getField(2, 3).setWall(Direction.S, Direction.E);
        threeB.getField(2, 3).setGoal(Color.BLUE);
        threeB.getField(6, 5).setWall(Direction.N, Direction.E);
        threeB.getField(6, 5).setGoal(Color.GREEN);
        threeB.getField(1, 6).setWall(Direction.S, Direction.W);
        threeB.getField(1, 6).setGoal(Color.RED);

        //4B
        Board fourB = new Board(8);
        fourB.getField(0, 0).setWall(Direction.W);
        fourB.getField(0, 1).setWall(Direction.W);
        fourB.getField(0, 2).setWall(Direction.N, Direction.W);
        fourB.getField(0, 3).setWall(Direction.W);
        fourB.getField(0, 4).setWall(Direction.W);
        fourB.getField(0, 5).setWall(Direction.W);
        fourB.getField(0, 6).setWall(Direction.W);
        fourB.getField(0, 7).setWall(Direction.N, Direction.W);
        fourB.getField(1, 7).setWall(Direction.N);
        fourB.getField(2, 7).setWall(Direction.N);
        fourB.getField(3, 7).setWall(Direction.N);
        fourB.getField(4, 7).setWall(Direction.N, Direction.E);
        fourB.getField(5, 7).setWall(Direction.N);
        fourB.getField(6, 7).setWall(Direction.N);
        fourB.getField(7, 7).setWall(Direction.N);
        fourB.getField(7, 0).setWall(Direction.N, Direction.W);
        fourB.getField(3, 0).setWall(Direction.S, Direction.E);
        fourB.getField(3, 0).setGoal(Color.VORTEX);
        fourB.getField(5, 1).setWall(Direction.N, Direction.E);
        fourB.getField(5, 1).setGoal(Color.BLUE);
        fourB.getField(6, 3).setWall(Direction.N, Direction.W);
        fourB.getField(6, 3).setGoal(Color.YELLOW);
        fourB.getField(1, 4).setWall(Direction.S, Direction.W);
        fourB.getField(1, 4).setGoal(Color.GREEN);
        fourB.getField(2, 6).setWall(Direction.S, Direction.E);
        fourB.getField(2, 6).setGoal(Color.RED);

        Board[] boards = {oneA, twoA, threeA, fourA, oneB, twoB, threeB, fourB};
        return boards;
    }

}
