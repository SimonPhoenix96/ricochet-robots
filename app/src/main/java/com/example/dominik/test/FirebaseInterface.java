package com.example.dominik.test;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//Code by Justin Diaz | 5115108 
public class FirebaseInterface {

    FirebaseDatabase database;
    DatabaseReference myRef;
    Context context;

    public FirebaseInterface() {}
    public FirebaseInterface(Context context) {
        this.context = context;
    }


    // Lobby Methods
    public void createLobby(Lobby lobby) {

        myRef = database.getInstance().getReference("lobbies/");
        myRef.child(lobby.getLOBBY_OWNER()).setValue(lobby);

    }
    public void removeLobby(Lobby lobby) {

        myRef = database.getInstance().getReference();
        myRef.child("lobbies").child(lobby.getLOBBY_OWNER()).removeValue();

    }
    public void addPlayerToLobby(Lobby lobby, Player player) {
        lobby.addPlayer(player);
        myRef = database.getInstance().getReference();
        myRef.child("lobbies").child(lobby.getLOBBY_OWNER()).setValue(lobby);

    }

    public void removePlayerFromLobby(Lobby lobby, String player) {
        lobby.removePlayer(player);
        myRef = database.getInstance().getReference();
        myRef.child("lobbies").child(lobby.getLOBBY_OWNER()).setValue(lobby);

    }

    // PlayerList Methods

    // ONE TIME USE ONLY: This exists only to create a ArrayList containing the currently online players as a node in the firebase database
    public void createCurrentlyOnlinePlayersList() {
        Player firstPlayer = new Player("firstPlayer");
        ArrayList < Player > currentlyOnlinePlayers = new ArrayList < > ();
        currentlyOnlinePlayers.add(firstPlayer);
        myRef = database.getInstance().getReference();
        myRef.child("currentlyOnlinePlayers").setValue(currentlyOnlinePlayers);


    }

    public void addPlayerToOnlineList(final Player player) {
        myRef = database.getInstance().getReference("currentlyOnlinePlayers");
        final GenericTypeIndicator < List < Player >> t = new GenericTypeIndicator < List < Player >> () {};


        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isAlreadyOnline = false;
                List < Player > currentlyOnlinePlayers = dataSnapshot.getValue(t);
                for (int i = 0; i < currentlyOnlinePlayers.size(); i++) {
                    if (currentlyOnlinePlayers.get(i).USERNAME.equalsIgnoreCase(player.USERNAME)) {
                        isAlreadyOnline = true;
                        break;
                    }
                }

                if (!isAlreadyOnline) currentlyOnlinePlayers.add(player);
                myRef.setValue(currentlyOnlinePlayers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

    }

    public void removePlayerFromOnlineList(final String player) {
        myRef = database.getInstance().getReference("currentlyOnlinePlayers");
        final GenericTypeIndicator < List < Player >> t = new GenericTypeIndicator < List < Player >> () {};
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List < Player > currentlyOnlinePlayers = dataSnapshot.getValue(t);

                for (int i = 0; i < currentlyOnlinePlayers.size(); i++) {
                    if (!currentlyOnlinePlayers.get(i).USERNAME.equalsIgnoreCase(player)) continue;
                    currentlyOnlinePlayers.remove(i);
                }
                myRef.setValue(currentlyOnlinePlayers);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

    }

    public void sendOnlinePlayerInvite(final String player, final String invitee) {
        final GenericTypeIndicator < List < Player >> t = new GenericTypeIndicator < List < Player >> () {};

        myRef = database.getInstance().getReference("currentlyOnlinePlayers");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                List < Player > currentlyOnlinePlayersSnap = dataSnapshot.getValue(t);
                ArrayList < String > INVITES = new ArrayList < > ();
                for (int i = 0; i < currentlyOnlinePlayersSnap.size(); i++) {
                    if (currentlyOnlinePlayersSnap.get(i).USERNAME.equalsIgnoreCase(player)) {
                        currentlyOnlinePlayersSnap.get(i).INVITES.add(invitee);
                        INVITES.addAll(currentlyOnlinePlayersSnap.get(i).INVITES);
                        myRef.child(Integer.toString(i)).child("/INVITES/").setValue(INVITES);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });


    }





}
