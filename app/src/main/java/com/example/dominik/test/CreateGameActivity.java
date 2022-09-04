package com.example.dominik.test;

import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

//GUI-Code by Dominik Erbacher | 5016085 && Logic by Justin Diaz | 5115108  
public class CreateGameActivity extends AppCompatActivity {

    String username;
    String maxplayers;
    String hostusername;
    String localgame;
    String boardcreated;
    String gamecreated;

    boolean leavelobby = false;


    // This is for when user kills app or presses home button
    boolean switchingActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            maxplayers = extras.getString("maxplayers");
            hostusername = extras.getString("hostusername");
            localgame = extras.getString("localgame");
            boardcreated = extras.getString("boardcreated");
            gamecreated = extras.getString("gamecreated");


        }

        //Check if localgame, if yes engage local mode
        if (localgame.equalsIgnoreCase("yes")) {

            final int maxplayersInt = Integer.parseInt(maxplayers);
            Player host = new Player(username);
            final Lobby lobby = new Lobby(host, maxplayersInt);

            // String array that holds values of players in lobby
            String[] playersInLobby = new String[maxplayersInt];


            while(!leavelobby) {

    //replace id placeholder with username
    TextView hostusernameText = findViewById(R.id.gameID);
    hostusernameText.setText("Host: " + lobby.getLOBBY_OWNER());


    for (int i = 0; i < maxplayersInt; i++)
            playersInLobby[i] = lobby.getPlayers().get(i).USERNAME;




        ListView playerList = findViewById(R.id.playerList);
        Button leaveLobby = findViewById(R.id.leaveLobby);
        Button addPlayer = findViewById(R.id.addPlayer);
        Button ready = findViewById(R.id.ready);
        final Button kickPlayer = findViewById(R.id.kickPlayer);
        playerList.setAdapter(new ListAdapter(CreateGameActivity.this, playersInLobby));


        leaveLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent startIntent = new Intent(getApplicationContext(), MultiplayerMenuActivity.class);
                startIntent.putExtra("username", username);
                switchingActivity = true;
                startActivity(startIntent);*/
                finish();
            }
        });

        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
               if(hostusername.equalsIgnoreCase(username)) {
                   Intent startIntent = new Intent(getApplicationContext(), MultiplayerActivity.class);
                   switchingActivity = true;
                   startIntent.putExtra("username", username);
                   startIntent.putExtra("hostusername", hostusername);
                   startIntent.putExtra("maxplayers", maxplayers);
                   startActivity(startIntent);
               }else Toast.makeText(CreateGameActivity.this, "Sorry, only the host can start the game!", Toast.LENGTH_SHORT).show();
                       }

        });

        addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);

                final AlertDialog.Builder a_builder = new AlertDialog.Builder(CreateGameActivity.this);
                final LayoutInflater inflater = CreateGameActivity.this.getLayoutInflater();

                View promptView = inflater.inflate(R.layout.dialog_addplayers, null);
                a_builder.setView(promptView);

                final EditText edittext = promptView.findViewById(R.id.username);

                a_builder.setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String addedplayer = edittext.getText().toString();
                        if (!addedplayer.equalsIgnoreCase(username)) {
                            Player toadd = new Player(addedplayer);
                            lobby.addPlayer(toadd);
                        } else
                            Toast.makeText(CreateGameActivity.this, "You can't invite yourself!", Toast.LENGTH_SHORT).show();

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

        kickPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);

                final AlertDialog.Builder a_builder = new AlertDialog.Builder(CreateGameActivity.this);
                LayoutInflater inflater = CreateGameActivity.this.getLayoutInflater();

                View promptView = inflater.inflate(R.layout.dialog_kickplayer, null);
                a_builder.setView(promptView);
                final EditText edittext = promptView.findViewById(R.id.username);
                a_builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (username == hostusername) {
                            if (edittext.getText().toString().equals(username)) {
                                Toast.makeText(CreateGameActivity.this, "Use the leave button or press the back button if you're trying to leave ", Toast.LENGTH_SHORT).show();
                            } else if (!lobby.isInLobby(edittext.getText().toString())) {
                                Toast.makeText(CreateGameActivity.this, "Sorry, that player doesn't exist in your lobby!", Toast.LENGTH_SHORT).show();
                            } else if ((!edittext.getText().toString().equals(lobby.getLOBBY_OWNER())) && (!edittext.getText().toString().equals(username))) {
                                lobby.removePlayer(edittext.getText().toString());
                                Toast.makeText(CreateGameActivity.this, edittext.getText().toString() + " kicked from lobby!", Toast.LENGTH_SHORT).show();
                            }
                        } else if (username != hostusername)
                            Toast.makeText(CreateGameActivity.this, "Only the host '" + hostusername + "' can kick players from the lobby!", Toast.LENGTH_SHORT).show();


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
}

         else if (localgame.equalsIgnoreCase("no")) {

            final int maxplayersInt = Integer.parseInt(maxplayers);
            // Get a reference to hosts' lobby
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final FirebaseInterface fbInterface = new FirebaseInterface();

            // Only the host needs to create new lobby, not a joining player
            if (username.equalsIgnoreCase(hostusername)) {
                final Player host = new Player(hostusername);
                final Lobby hostslobby = new Lobby(host, maxplayersInt);
                fbInterface.createLobby(hostslobby);
            }

            DatabaseReference myRef = database.getReference("lobbies/" + hostusername);

            // Attach a listener to read the data at our lobby' reference
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final Lobby lobby = dataSnapshot.getValue(Lobby.class);

                    //replace id placeholder with username
                    TextView hostusernameText = findViewById(R.id.gameID);
                    hostusernameText.setText("Host: " + lobby.getLOBBY_OWNER());


                    if (!(lobby == null)) {
                        // String array that holds values of players in lobby
                        String[] playersInLobby = new String[maxplayersInt];


                        for (int i = 0; i < maxplayersInt; i++) {
                            playersInLobby[i] = lobby.getPlayers().get(i).USERNAME;

                        }

                        if(lobby.isLOBBY_START()){
                            Intent startIntent = new Intent(getApplicationContext(), MultiplayerActivity.class);
                            switchingActivity = true;
                            startIntent.putExtra("username", username);
                            startIntent.putExtra("hostusername", hostusername);
                            startIntent.putExtra("gamecreated", lobby.getGameData());
                            startIntent.putExtra("boardcreated", lobby.getBoardData());
                            startActivity(startIntent);
                        }


                        ListView playerList = findViewById(R.id.playerList);
                        Button leaveLobby = findViewById(R.id.leaveLobby);
                        Button addPlayer = findViewById(R.id.addPlayer);
                        Button ready = findViewById(R.id.ready);
                        final Button kickPlayer = findViewById(R.id.kickPlayer);
                        playerList.setAdapter(new ListAdapter(CreateGameActivity.this, playersInLobby));


                        leaveLobby.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.startAnimation(buttonClick);
                                finish();
                                if (username.equalsIgnoreCase(hostusername))
                                    fbInterface.removeLobby(lobby);
                                else if (!username.equalsIgnoreCase(hostusername))
                                    fbInterface.removePlayerFromLobby(lobby, username);
                               /* Intent startIntent = new Intent(getApplicationContext(), MultiplayerMenuActivity.class);
                                startIntent.putExtra("username", username);
                                switchingActivity = true;
                                startActivity(startIntent);*/
                                switchingActivity = true;
                               finish();
                            }
                        });

                        ready.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.startAnimation(buttonClick);
                                if(hostusername.equalsIgnoreCase(username)) {
                                    lobby.setLOBBY_START(true);
                                    fbInterface.createLobby(lobby);
                                }  else  Toast.makeText(CreateGameActivity.this, "Sorry, only the host can start the game!", Toast.LENGTH_SHORT).show();
                            }

                        });

                        addPlayer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.startAnimation(buttonClick);

                                final AlertDialog.Builder a_builder = new AlertDialog.Builder(CreateGameActivity.this);
                                final LayoutInflater inflater = CreateGameActivity.this.getLayoutInflater();

                                View promptView = inflater.inflate(R.layout.dialog_addplayers, null);
                                a_builder.setView(promptView);

                                final EditText edittext = promptView.findViewById(R.id.username);

                                a_builder.setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String addedplayer = edittext.getText().toString();
                                        if (!addedplayer.equalsIgnoreCase(username)) {
                                            fbInterface.sendOnlinePlayerInvite(addedplayer, username);
                                        } else
                                            Toast.makeText(CreateGameActivity.this, "You can't invite yourself!", Toast.LENGTH_SHORT).show();

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

                        kickPlayer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.startAnimation(buttonClick);

                                final AlertDialog.Builder a_builder = new AlertDialog.Builder(CreateGameActivity.this);
                                LayoutInflater inflater = CreateGameActivity.this.getLayoutInflater();

                                View promptView = inflater.inflate(R.layout.dialog_kickplayer, null);
                                a_builder.setView(promptView);
                                final EditText edittext = promptView.findViewById(R.id.username);
                                a_builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (username == hostusername) {
                                            if (edittext.getText().toString().equals(username)) {
                                                Toast.makeText(CreateGameActivity.this, "Use the leave button or press the back button if you're trying to leave ", Toast.LENGTH_SHORT).show();
                                            } else if (!lobby.isInLobby(edittext.getText().toString())) {
                                                Toast.makeText(CreateGameActivity.this, "Sorry, that player doesn't exist in your lobby!", Toast.LENGTH_SHORT).show();
                                            } else if ((!edittext.getText().toString().equals(lobby.getLOBBY_OWNER())) && (!edittext.getText().toString().equals(username))) {
                                                fbInterface.removePlayerFromLobby(lobby, edittext.getText().toString());
                                                Toast.makeText(CreateGameActivity.this, edittext.getText().toString() + " kicked from lobby!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else if (username != hostusername)
                                            Toast.makeText(CreateGameActivity.this, "Only the host '" + hostusername + "' can kick players from the lobby!", Toast.LENGTH_SHORT).show();


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
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                    Toast.makeText(CreateGameActivity.this, "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onBackPressed() {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("lobbies/" + hostusername);
        final FirebaseInterface fbInterface = new FirebaseInterface();
        // Attach a listener to read the data at our lobby' reference
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Lobby lobby = dataSnapshot.getValue(Lobby.class);
                if (lobby != null) {
                    if (username.equalsIgnoreCase(hostusername)) fbInterface.removeLobby(lobby);
                    else if (!username.equalsIgnoreCase(hostusername))
                        fbInterface.removePlayerFromLobby(lobby, username);
                   /* Intent startIntent = new Intent(getApplicationContext(), MultiplayerMenuActivity.class);
                    startIntent.putExtra("username", username);
                    startActivity(startIntent);*/
                   finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                Toast.makeText(CreateGameActivity.this, "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (switchingActivity == false) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference("lobbies/" + hostusername);
            final FirebaseInterface fbInterface = new FirebaseInterface();
            // Attach a listener to read the data at our lobby' reference
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Lobby lobby = dataSnapshot.getValue(Lobby.class);
                    if (lobby != null) {
                        if (username.equalsIgnoreCase(hostusername))
                            fbInterface.removeLobby(lobby);
                        else if (!username.equalsIgnoreCase(hostusername))
                            fbInterface.removePlayerFromLobby(lobby, username);
                       /* Intent startIntent = new Intent(getApplicationContext(), MultiplayerMenuActivity.class);
                        startIntent.putExtra("username", username);
                        startActivity(startIntent);*/
                       finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                    Toast.makeText(CreateGameActivity.this, "The read failed: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
                }

            });
        }
    }
}
