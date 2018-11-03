package com.example.dominik.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Game implements Serializable {
    Board board;
    int[] players; //to check who won just divide amount of targets and nr. players + 1, the player that has this amount of targets (or chips) won
    Robot[] robots;
    ArrayList<Target> targets;
    Target currentTarget;
    private Color[] colors = { Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW, Color.SILVER };

    Game(Board[] boards, int numberOfPlayers, boolean silverRobot) {
        ArrayList<Board> boardsList = new ArrayList<Board>();
        for (int i = 0; i < boards.length; i++) {
            boardsList.add(boards[i]);
        }
        Collections.shuffle(boardsList);
        this.board = mergeBoards(boardsList.get(0), boardsList.get(1), boardsList.get(2), boardsList.get(3));
        this.players = new int[numberOfPlayers];
        ArrayList<Position> possibleRobotPositions = new ArrayList<Position>();
        for (int y = 0; y < board.getLength(); y++) {
            for (int x = 0; x < board.getLength(); x++) {
                if (board.hasGoal(x, y) || (x == 7 && y == 7) || (x == 7 && y == 8) || (x == 8 && y == 7)
                        || (x == 8 && y == 8))
                    continue;
                possibleRobotPositions.add(new Position(x, y));
            }
        }
        Collections.shuffle(possibleRobotPositions);
        if (silverRobot)
            this.robots = new Robot[5];
        else
            this.robots = new Robot[4];
        for (int i = 0; i < robots.length; i++) {
            robots[i] = new Robot(colors[i], possibleRobotPositions.get(i));
            board.getField(possibleRobotPositions.get(i)).setRobot(true);
        }

        this.targets = new ArrayList<Target>();
        for (int y = 0; y < board.getLength(); y++) {
            for (int x = 0; x < board.getLength(); x++) {
                if (board.hasGoal(x, y))
                    targets.add(new Target(board.getGoal(x, y), x, y));
            }
        }
        Collections.shuffle(targets);
        currentTarget = targets.get(0);
        targets.remove(0);
    }

    public static Board mergeBoards(Board first, Board second, Board third, Board fourth) {
        int length = first.getLength();
        int mx = 0;
        int my = 0;
        Board secondCW = second.rotateCW(1);
        Board thirdCW = third.rotateCW(2);
        Board fourthCW = fourth.rotateCW(3);
        Board newBoard = new Board(length * 2);
        Board temp = new Board(8);
        for (int y = 0; y < newBoard.getLength(); y++) {
            for (int x = 0; x < newBoard.getLength(); x++) {
                if (x < length && y >= length) {
                    temp = first;
                    mx = 0;
                    my = 8;
                }
                if (x >= length && y >= length) {
                    temp = secondCW;
                    mx = 8;
                    my = 8;
                }
                if (x >= length && y < length) {
                    temp = thirdCW;
                    mx = 8;
                    my = 0;
                }
                if (x < length && y < length) {
                    temp = fourthCW;
                    mx = 0;
                    my = 0;
                }
                newBoard.getBoard()[x][y] = temp.getBoard()[x - mx][y - my];
            }
        }
        return newBoard;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Robot[] getRobots() {
        return robots;
    }

    public void setRobots(Robot[] robots) {
        this.robots = robots;
    }

    public ArrayList<Target> getTargets() {
        return targets;
    }

    public void setTargets(ArrayList<Target> targets) {
        this.targets = targets;
    }

    public Target getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(Target currentTarget) {
        this.currentTarget = currentTarget;
    }

    public int[] getPlayers() {
        return players;
    }

    public void setPlayers(int[] players) {
        this.players = players;
    }

}
