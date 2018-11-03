package com.example.dominik.test;

import java.util.ArrayList;

//Code by Justin Diaz | 5115108
public class Player {

    public String USERNAME;
    public int NR_MOVES = 0;
    public int NR_CHIPS = 0;

    public boolean HAS_SOLUTION = false;
    public boolean WON = false;

    public ArrayList <String> INVITES = new ArrayList < > ();

    public Player() {} // Default Constructor needed for DataSnapshot (Firebase)

    public Player(String USERNAME) {
        this.INVITES.add(" ");
        this.USERNAME = USERNAME;
    }
}





