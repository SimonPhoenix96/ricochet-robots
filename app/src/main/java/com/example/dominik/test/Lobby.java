package com.example.dominik.test;

import android.content.Context;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.Gson;

import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Code by Justin Diaz | 5115108 
@IgnoreExtraProperties
public class Lobby {

    private List<Player> players = new ArrayList<>();
    private Player emptySlot = new Player(" ");

    private int MAX_NR_PLAYERS;
    private int CURRENT_NR_PLAYERS;
    private String LOBBY_OWNER;

    private boolean LOBBY_FULL = false;
    private boolean LOBBY_START = false;

    private String gameData;
    private String boardData;

    private String lowestBidder;
    private int lowestBidderMoves;

    private boolean firstBidExists = false;
    private boolean bidPhaseOver = false;
    private boolean timerSet = false;

    private String bidMap;
    private String bidMapReversed;

    private String toastMessages;
    private boolean newMessage;

    private int turn;
    private boolean nextTurn;


    public Lobby() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Lobby(Player LOBBY_OWNER, int MAX_NR_PLAYERS) {

        this.LOBBY_OWNER = LOBBY_OWNER.USERNAME;
  /* if(MAX_NR_PLAYERS > 4) this.MAX_NR_PLAYERS = 4;
   if(MAX_NR_PLAYERS <= 0) this.MAX_NR_PLAYERS = 2;*/
        this.MAX_NR_PLAYERS = MAX_NR_PLAYERS;
        players.add(LOBBY_OWNER);
        for (int i = 1; i < MAX_NR_PLAYERS; i++) {
            players.add(emptySlot);
        }
        updateCURRENT_NR_PLAYERS();
        updateRoomStatus();

        final Board[] boards = Board.createBoards();
        //
        Game game = new Game(boards, 1, false);
        Board board = game.getBoard();
        HashMap < String, String > bids = new HashMap < > ();
        HashMap < String, String > bidsreversed = new HashMap < > ();

        Gson gson = new Gson();



        gameData = gson.toJson(game);
        boardData = gson.toJson(board);
        bidMap = gson.toJson(bids);
        bidMapReversed = gson.toJson(bidsreversed);
        try {
            JSONObject request1 = new JSONObject(gameData);
            JSONObject request2 = new JSONObject(boardData);
            JSONObject request3 = new JSONObject(bidMap);
            JSONObject request4 = new JSONObject(bidMapReversed);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public boolean isInLobby(String player) {
        for (int i = 0; i < this.MAX_NR_PLAYERS; i++) {
            if (players.get(i).USERNAME.equalsIgnoreCase(player)) {
                return true;
            } //Dont add player if he already exists in lobby
        }
        return false;
    }

    public void addPlayer(Player player) {

        for (int i = 0; i < this.MAX_NR_PLAYERS; i++) {
            if (isInLobby(player.USERNAME)) {
                break;
            } //Dont add player if he already exists in lobby
            if (players.get(i).USERNAME.equalsIgnoreCase(" ")) {
                players.set(i, player);
                updateCURRENT_NR_PLAYERS();
                updateRoomStatus();
                break;
            }

        }
    }


    public void removePlayer(String player) {

        for (int i = 0; i < this.MAX_NR_PLAYERS; i++) {
            if (players.get(i).USERNAME.equals(player)) {
                players.set(i, emptySlot);
                updateCURRENT_NR_PLAYERS();
                updateRoomStatus();
                break;
            }

        }


    }

    public void updateCURRENT_NR_PLAYERS() {
        int count = 0;
        for (int i = 0; i < MAX_NR_PLAYERS; i++) {
            if (players.get(i).USERNAME.equals(" ")) {
                continue;
            } else
                count++;
        }
        this.CURRENT_NR_PLAYERS = count;
    }

    public void updateRoomStatus() {

        if (this.CURRENT_NR_PLAYERS == this.MAX_NR_PLAYERS) {
            this.LOBBY_FULL = true;
        } else {
            this.LOBBY_FULL = false;
        } // update ROOM_FULL status

    }


    //Getter & Setter
    public boolean isLOBBY_FULL() {
        return LOBBY_FULL;
    }

    public void setLOBBY_FULL(boolean LOBBY_FULL) {
        this.LOBBY_FULL = LOBBY_FULL;
    }

    public int getCURRENT_NR_PLAYERS() {

        return CURRENT_NR_PLAYERS;
    }

    public void setCURRENT_NR_PLAYERS(int CURRENT_NR_PLAYERS) {
        this.CURRENT_NR_PLAYERS = CURRENT_NR_PLAYERS;
    }


    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Player getEmptySlot() {
        return emptySlot;
    }

    public void setEmptySlot(Player emptySlot) {
        this.emptySlot = emptySlot;
    }

    public int getMAX_NR_PLAYERS() {
        return MAX_NR_PLAYERS;
    }

    public void setMAX_NR_PLAYERS(int MAX_NR_PLAYERS) {
        this.MAX_NR_PLAYERS = MAX_NR_PLAYERS;
    }


    public String getLOBBY_OWNER() {
        return LOBBY_OWNER;
    }

    public void setLOBBY_OWNER(String LOBBY_OWNER) {
        this.LOBBY_OWNER = LOBBY_OWNER;
    }


    @Override
    public String toString() {
        return "Lobby{" +
                ", emptySlot=" + emptySlot +
                ", MAX_NR_PLAYERS=" + MAX_NR_PLAYERS +
                ", CURRENT_NR_PLAYERS=" + CURRENT_NR_PLAYERS +
                ", LOBBY_OWNER='" + LOBBY_OWNER + '\'' +
                ", LOBBY_FULL=" + LOBBY_FULL +
                '}';
    }


    public String getGameData() {
        return gameData;
    }

    public void setGameData(String gameData) {
        this.gameData = gameData;
    }

    public String getBoardData() {
        return boardData;
    }

    public void setBoardData(String boardData) {
        this.boardData = boardData;
    }

    public String getLowestBidder() {
        return lowestBidder;
    }

    public void setLowestBidder(String lowestBidder) {
        this.lowestBidder = lowestBidder;
    }

    public int getLowestBidderMoves() {
        return lowestBidderMoves;
    }

    public void setLowestBidderMoves(int lowestBidderMoves) {
        this.lowestBidderMoves = lowestBidderMoves;
    }

    public String getBidMap() {
        return bidMap;
    }

    public void setBidMap(String bidMap) {
        this.bidMap = bidMap;
    }

    public boolean isLOBBY_START() {
        return LOBBY_START;
    }

    public void setLOBBY_START(boolean LOBBY_START) {
        this.LOBBY_START = LOBBY_START;
    }

    public boolean isFirstBidExists() {
        return firstBidExists;
    }

    public void setFirstBidExists(boolean firstBidExists) {
        this.firstBidExists = firstBidExists;
    }

    public boolean isBidPhaseOver() {
        return bidPhaseOver;
    }

    public void setBidPhaseOver(boolean bidPhaseOver) {
        this.bidPhaseOver = bidPhaseOver;
    }

    public boolean isTimerSet() {
        return timerSet;
    }

    public void setTimerSet(boolean timerSet) {
        this.timerSet = timerSet;
    }

    public String getBidMapReversed() {
        return bidMapReversed;
    }

    public void setBidMapReversed(String bidMapReversed) {
        this.bidMapReversed = bidMapReversed;
    }

    public String getToastMessages() {
        return toastMessages;
    }

    public void setToastMessages(String toastMessages) {
        this.toastMessages = toastMessages;
    }

    public boolean isNewMessage() {
        return newMessage;
    }

    public void setNewMessage(boolean newMessage) {
        this.newMessage = newMessage;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public boolean isNextTurn() {
        return nextTurn;
    }

    public void setNextTurn(boolean nextTurn) {
        this.nextTurn = nextTurn;
    }
}
