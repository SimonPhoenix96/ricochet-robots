package com.example.dominik.test;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.dominik.test.Color.BLUE;
//Code by Dominik Erbacher | 5016085

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

        Button tutorialActivity = findViewById(R.id.tutorial);
        Button secondActivity = findViewById(R.id.singleplayer);
        Button thirdActivity = findViewById(R.id.multiplayer);
        Button fourthActivity = findViewById(R.id.settings);
        Button aboutUs = findViewById(R.id.aboutus);


        //Multiplayer Debug Stuff
         FirebaseInterface fbInterface = new FirebaseInterface();
    /* // fbInterface.createCurrentlyOnlinePlayersList();
        Player justin = new Player("justin");
        Player mike = new Player("mike");
        Player domi = new Player("domi");
        Player jakob = new Player("jakob");
        Lobby lobby1 = new Lobby(justin, 5);
        Lobby lobby2 = new Lobby(mike, 5);
        Lobby lobby3 = new Lobby(domi, 5);
        Lobby lobby4 = new Lobby(jakob, 5);
        fbInterface.createLobby(lobby1);
        fbInterface.createLobby(lobby2);
        fbInterface.createLobby(lobby3);
        fbInterface.createLobby(lobby4);*/

       Intent startIntent = new Intent(getApplicationContext(), MultiplayerMenuActivity.class);
        startIntent.putExtra("username", "Cuckman");
        //show how to pass information to another activity

        // Add player to currentlyOnlinePlayers list in Firebase
        Player playerGoingOnline = new Player("Cuckman");
        fbInterface.addPlayerToOnlineList(playerGoingOnline);

        startActivity(startIntent);



        //wenn Button geklickt wird (außer bei aboutUs), wird die jeweilige Activity gestartet
        tutorialActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Intent startIntent = new Intent(getApplicationContext(), TutorialActivity.class);
                startActivity(startIntent);
            }
        });

        secondActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Intent startIntent = new Intent(getApplicationContext(), SingleplayerActivity.class);
                startActivity(startIntent);
            }
        });

        thirdActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Intent startIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(startIntent);
            }
        });

        fourthActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Intent startIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(startIntent);
            }
        });

        //Popup/AlertDialog erscheint bei Buttonclick
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)    {
                v.startAnimation(buttonClick);
                AlertDialog.Builder a_builder = new AlertDialog.Builder(MainActivity.this);
                a_builder.setMessage(R.string.aboutustxt).setTitle(R.string.aboutus);
                AlertDialog dialog = a_builder.create();
                dialog.show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        //Nachfrage, ob der User wirklich die App schließen will (erscheint nur, bei Klick auf die Return-Taste, nicht aber beim Homebutton)
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this activity?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
