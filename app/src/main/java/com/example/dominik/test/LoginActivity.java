package com.example.dominik.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
//GUI Dominik Erbacher | 5016085
//Code Jakob Steinbach
public class LoginActivity extends AppCompatActivity {
    FirebaseFirestore db;
    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db= FirebaseFirestore.getInstance();
        username = findViewById(R.id.username);
        password = findViewById(R.id.passwordRegister2);
        Button login = findViewById(R.id.createAccountButton);
        TextView register = findViewById(R.id.regnow);
        TextView guest = findViewById(R.id.guestcontinue);

        final AlphaAnimation click = new AlphaAnimation(1F, 0.8F);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(click);
                if(username.length() >0 ){
                    checkLogin();
                }
                else{
                    Toast.makeText(LoginActivity.this, "Bitte geben Sie einen Benutzernamen ein!", Toast.LENGTH_SHORT).show();
                    password.setText("");
                    username.setText("");
                }
                }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(click);
                Intent startIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(startIntent);
            }

        });
        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(click);
                Intent startIntent = new Intent(getApplicationContext(), MultiplayerMenuActivity.class);
                //Jakob & Justin: guest+random Zahl generieren und in temporär in DB speichern!
                startIntent.putExtra("username", "guestXXXX");

                // Add player to currentlyOnlinePlayers list in Firebase TEMPORARY!!!!!! FOR DEBUG PURPOSE
                Player playerGoingOnline = new Player("guestXXXX");
                FirebaseInterface fbInterface = new FirebaseInterface();
                fbInterface.addPlayerToOnlineList(playerGoingOnline);

                startActivity(startIntent);
            }

        });

    }
    public void checkLogin() {

        DocumentReference docRef = db.collection("UserData").document(username.getText().toString().toLowerCase());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String hashPassword = new String(Hex.encodeHex(DigestUtils.sha256(password.getText().toString())));
                        if(document.getString("password").equals(hashPassword)){
                            Intent startIntent = new Intent(getApplicationContext(), MultiplayerMenuActivity.class);
                            startIntent.putExtra("username", document.getString("username"));
                            //show how to pass information to another activity

                            // Add player to currentlyOnlinePlayers list in Firebase
                            Player playerGoingOnline = new Player(username.getText().toString());
                            FirebaseInterface fbInterface = new FirebaseInterface();
                            fbInterface.addPlayerToOnlineList(playerGoingOnline);

                            startActivity(startIntent);
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Benutzername oder Passwort falsch!", Toast.LENGTH_SHORT).show();
                            password.setText("");
                            username.setText("");
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "Benutzername oder Passwort falsch!", Toast.LENGTH_SHORT).show();
                        password.setText("");
                        username.setText("");
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
