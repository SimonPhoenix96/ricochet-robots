package com.example.dominik.test;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//Code by Dominik Erbacher (GUI) | 5016085 && Justin Diaz | 5115108  
public class MultiplayerMenuActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    EditText gameID;
    String username;
    String localgame = "yes";

    boolean switchingActivity = false;
    private ArrayList < String > data = new ArrayList < String > ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_menu);


        final AlphaAnimation click = new AlphaAnimation(1F, 0.8F);
        Button createGame = findViewById(R.id.tutorial);
        Button joinGame = findViewById(R.id.singleplayer);
        Button invites = findViewById(R.id.multiplayer);
        Button showProfile = findViewById(R.id.showProfile);
        Button automatch = findViewById(R.id.automatchButton);
        Button localMultiplayer = findViewById(R.id.localMultiplayer);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");

            TextView usernameText = findViewById(R.id.usernameText1);
            usernameText.setText("Welcome " + username + "!");
            //The key argument here must match that used in the other activity
        }

        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(click);

                final AlertDialog.Builder a_builder = new AlertDialog.Builder(MultiplayerMenuActivity.this);
                final LayoutInflater inflater = MultiplayerMenuActivity.this.getLayoutInflater();

                View promptView = inflater.inflate(R.layout.dialog_maxplayeramount, null);
                a_builder.setView(promptView);

                final EditText edittext = promptView.findViewById(R.id.playerCount);

                a_builder.setPositiveButton("Set & Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String maxplayers = edittext.getText().toString();
                        String localgame = "no";
                        Log.d("maxplayers", maxplayers);
                        if (Integer.parseInt(maxplayers) <= 4 && Integer.parseInt(maxplayers) >= 2) {
                            Intent startIntent = new Intent(getApplicationContext(), CreateGameActivity.class);
                            startIntent.putExtra("username", username);
                            startIntent.putExtra("hostusername", username);
                            startIntent.putExtra("maxplayers", maxplayers);
                            startIntent.putExtra("localgame", localgame);
                            switchingActivity = true;
                            startActivity(startIntent);
                        } else if (!(Integer.parseInt(maxplayers) <= 4 && Integer.parseInt(maxplayers) >= 2)) Toast.makeText(MultiplayerMenuActivity.this, "Sorry, only 2-4 players allowed!", Toast.LENGTH_SHORT).show();

                    }
                });
                a_builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = a_builder.create();
                dialog.show();
            }

        });

        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(click);

                final AlertDialog.Builder a_builder = new AlertDialog.Builder(MultiplayerMenuActivity.this);
                final LayoutInflater inflater = MultiplayerMenuActivity.this.getLayoutInflater();

                View promptView = inflater.inflate(R.layout.dialog_joingame, null);
                a_builder.setView(promptView);

                final EditText edittext = promptView.findViewById(R.id.gameID);

                a_builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        myRef = database.getInstance().getReference("lobbies/");
                        final String gameid = edittext.getText().toString();
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (!dataSnapshot.hasChild(gameid)) Toast.makeText(MultiplayerMenuActivity.this, "Sorry " + gameid + "'s lobby doesn't exist!", Toast.LENGTH_SHORT).show();

                                for (DataSnapshot lobbySnapshot: dataSnapshot.getChildren()) {
                                    Lobby returnedlobby = lobbySnapshot.getValue(Lobby.class);
                                    FirebaseInterface fbInterface = new FirebaseInterface();
                                    if (returnedlobby.getLOBBY_OWNER().equals(gameid)) {
                                        Player player = new Player(username);
                                        if (returnedlobby.isLOBBY_FULL()) {
                                            Toast.makeText(MultiplayerMenuActivity.this, "Sorry, that lobby is full!", Toast.LENGTH_SHORT).show();
                                        } else
                                        if (returnedlobby.isInLobby(username)) {
                                            Toast.makeText(MultiplayerMenuActivity.this, "Sorry, you're already in that lobby!", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                        fbInterface.addPlayerToLobby(returnedlobby, player);

                                        Toast.makeText(MultiplayerMenuActivity.this, username + " joined " + gameid + "'s lobby!", Toast.LENGTH_SHORT).show();
                                       String localsgame = "no";
                                        // Switch to lobby view
                                        Intent startIntent = new Intent(getApplicationContext(), CreateGameActivity.class);
                                        startIntent.putExtra("username", username);
                                        startIntent.putExtra("hostusername", returnedlobby.getLOBBY_OWNER());
                                        startIntent.putExtra("maxplayers", String.valueOf(returnedlobby.getMAX_NR_PLAYERS()));
                                        startIntent.putExtra("localgame", localsgame);
                                        startIntent.putExtra("gamecreated", returnedlobby.getGameData());
                                        startIntent.putExtra("boardcreated", returnedlobby.getBoardData());
                                        switchingActivity = true;
                                        startActivity(startIntent);
                                        break;
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // ...
                            }
                        });

                    }
                });
                a_builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = a_builder.create();
                dialog.show();

            }
        });

        invites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(click);
                Intent startIntent = new Intent(getApplicationContext(), InviteListActivity.class);
                switchingActivity = true;
                String localgame = "no";
                startIntent.putExtra("username", username);
                startIntent.putExtra("localgame", localgame);
                startActivity(startIntent);

            }
        });



        showProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(click);
                // Popup!
                // Anzahl Wins, Anzahl Spiele, username, ... Jakob & Justin
                AlertDialog.Builder a_builder = new AlertDialog.Builder(MultiplayerMenuActivity.this);
                a_builder.setMessage("Name:\t\t\t\t" + username + "\n" + "Games: \t\t99\n" + "Wins:\t\t\t\t\t" + "0" + "\n" + "Rank:\t\t\t\t\t" + "GE").setTitle(R.string.profil);
                AlertDialog dialog = a_builder.create();
                dialog.show();
            }

        });

        automatch.setOnClickListener(new View.OnClickListener() {
            FirebaseInterface fbInterface = new FirebaseInterface(MultiplayerMenuActivity.this);
            Player player2add = new Player(username);
            @Override
            public void onClick(View v) {
                v.startAnimation(click);
                myRef = database.getInstance().getReference("lobbies/");
                boolean lobbyFound;
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot lobbySnapshot: dataSnapshot.getChildren()) {
                            Lobby returnedlobby = lobbySnapshot.getValue(Lobby.class);
                            String localgame = "no";
                            if (!returnedlobby.isLOBBY_FULL()) {
                                fbInterface.addPlayerToLobby(returnedlobby, player2add);
                                Toast.makeText(MultiplayerMenuActivity.this, player2add.USERNAME + " added to " + returnedlobby.getLOBBY_OWNER() + "' lobby!", Toast.LENGTH_SHORT).show();
                                // Switch to lobby view
                                Intent startIntent = new Intent(getApplicationContext(), CreateGameActivity.class);
                                startIntent.putExtra("username", username);
                                startIntent.putExtra("hostusername", returnedlobby.getLOBBY_OWNER());
                                startIntent.putExtra("maxplayers", String.valueOf(returnedlobby.getMAX_NR_PLAYERS()));
                                startIntent.putExtra("localgame", localgame);
                                startIntent.putExtra("gamecreated", returnedlobby.getGameData());
                                startIntent.putExtra("boardcreated", returnedlobby.getBoardData());
                                switchingActivity = true;
                                startActivity(startIntent);
                                break;
                            }
                        }
                        Toast.makeText(MultiplayerMenuActivity.this, "No empty lobby found!", Toast.LENGTH_SHORT).show();



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // ...
                    }
                });
            }
        });



        localMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(click);
                final AlertDialog.Builder a_builder = new AlertDialog.Builder(MultiplayerMenuActivity.this);
                final LayoutInflater inflater = MultiplayerMenuActivity.this.getLayoutInflater();

                View promptView = inflater.inflate(R.layout.dialog_maxplayeramount, null);
                a_builder.setView(promptView);

                final EditText edittext = promptView.findViewById(R.id.playerCount);

                a_builder.setPositiveButton("Set & Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String maxplayers = edittext.getText().toString();
                        Log.d("maxplayers", maxplayers);
                        if (Integer.parseInt(maxplayers) <= 4 && Integer.parseInt(maxplayers) >= 2) {
                            //Intent startIntent = new Intent(getApplicationContext(), CreateGameActivity.class);
                            Intent startIntent = new Intent(getApplicationContext(), DeviceListActivity.class);
                            startIntent.putExtra("username", username);
                            startIntent.putExtra("hostusername", username);
                            startIntent.putExtra("maxplayers", maxplayers);
                            startIntent.putExtra("localgame", localgame);
                            switchingActivity = true;
                            startActivity(startIntent);
                        } else if (!(Integer.parseInt(maxplayers) <= 4 && Integer.parseInt(maxplayers) >= 2)) Toast.makeText(MultiplayerMenuActivity.this, "Sorry, only 2-4 players allowed!", Toast.LENGTH_SHORT).show();

                    }
                });
                a_builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = a_builder.create();
                dialog.show();
            }

        });
    }

    // Remove player from currentlyOnlinePlayers list when exiting Multiplayer menu
    @Override
    public void onBackPressed() {
        final FirebaseInterface fbInterface = new FirebaseInterface();
        fbInterface.removePlayerFromOnlineList(username);
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (switchingActivity == false) {
            final FirebaseInterface fbInterface = new FirebaseInterface();
            fbInterface.removePlayerFromOnlineList(username);
        }
    }

}