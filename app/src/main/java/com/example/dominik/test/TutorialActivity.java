package com.example.dominik.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
//GUI & Code by Dominik Erbacher | 5016085

public class TutorialActivity extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        ConstraintLayout cl = findViewById(R.id.cllayout);

        cl.setOnTouchListener(new OnSwipeTouchListener(TutorialActivity.this){
            public void onSwipeRight() {
                overridePendingTransition(0, 0);
                finish();
                overridePendingTransition(0, 0);
            }

            public void onSwipeLeft() {
                Intent startIntent = new Intent(getApplicationContext(), TutorialActivity_Controls.class);
                overridePendingTransition(0, 0);
                startActivity(startIntent);
                overridePendingTransition(0, 0);
            }
        });
    }
}
