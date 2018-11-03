package com.example.dominik.test;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ToggleButton;
//GUI & Code by Dominik Erbacher | 5016085

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final ToggleButton timerButton = findViewById(R.id.timerToggle);
        final ToggleButton moveGesture = findViewById(R.id.movementToggle);
        final ToggleButton amountRobots = findViewById(R.id.amountRobots);
        final ToggleButton robotSkin = findViewById(R.id.robotSkin);
        final ToggleButton targetSkin = findViewById(R.id.targetSkin);
        final ToggleButton boardSkin = findViewById(R.id.boardSkin);

        preferences = getSharedPreferences("preferences",MODE_PRIVATE);
        boolean timer = preferences.getBoolean("timer",true);
        timerButton.setChecked(timer);

        boolean robots = preferences.getBoolean("amountRobots",true);
        amountRobots.setChecked(robots);

        boolean move = preferences.getBoolean("moveGesture",true);
        moveGesture.setChecked(move);

        boolean robot = preferences.getBoolean("robot",false);
        robotSkin.setChecked(robot);

        boolean target = preferences.getBoolean("target",false);
        targetSkin.setChecked(target);

        boolean board = preferences.getBoolean("board",false);
        boardSkin.setChecked(board);


        robotSkin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(buttonClick);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("robot", robotSkin.isChecked());
                editor.commit();
            }
        });

        //BoardSkin noch in Beta
        boardSkin.setClickable(false);

        targetSkin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("target", targetSkin.isChecked());
                editor.commit();
            }
            });
        timerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(buttonClick);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("timer", timerButton.isChecked());
                editor.commit();
            }
        });

        moveGesture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(buttonClick);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("moveGesture", moveGesture.isChecked());
                editor.commit();
            }
        });

       amountRobots.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(buttonClick);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("amountRobots", amountRobots.isChecked());
                editor.commit();
            }
        });
    }
}
