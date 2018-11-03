package com.example.dominik.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
//GUI by Dominik Erbacher | 5016085
//Code by Jakob Steinbach

public class     RegisterActivity extends AppCompatActivity {

    FirebaseFirestore db;
    EditText username;
    EditText password;
    EditText password2;
    Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

        db= FirebaseFirestore.getInstance();
        username = findViewById(R.id.username);
        password = findViewById(R.id.passwordRegister);
        password2 = findViewById(R.id.passwordRegister2);
        add = findViewById(R.id.createAccountButton);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                    checkForPassword();
            }
        });
    }

    public void addNewUser(){

        String passwordHash = new String(Hex.encodeHex(DigestUtils.sha256(password.getText().toString())));

        HashMap<String,Object> newUser = new HashMap<>();
        newUser.put("username", username.getText().toString().toLowerCase());
        newUser.put("password", passwordHash);

        db.collection("UserData").document(username.getText().toString().toLowerCase())
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegisterActivity.this, "Added new user", Toast.LENGTH_SHORT).show();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR", e.getMessage());
                    }
                });


    }

    public void checkForPassword(){
        if(password.getText().toString().length()<5){
            Toast.makeText(RegisterActivity.this, "Das Passwort muss aus mindestens 5 Zeichen bestehen.", Toast.LENGTH_SHORT).show();
            password.setText("");
            password2.setText("");
        }
        else{
            if(!password.getText().toString().equals(password2.getText().toString())){
                Toast.makeText(RegisterActivity.this, "Die Passwörter müssen übereinstimmen.", Toast.LENGTH_SHORT).show();
                password.setText("");
                password2.setText("");
            }
            else{
                checkForUsername();
            }
        }

    }

    public void checkForUsername() {

        DocumentReference docRef = db.collection("UserData").document(username.getText().toString().toLowerCase());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(RegisterActivity.this, "Der Benutzername ist schon vergeben.", Toast.LENGTH_SHORT).show();
                    } else {
                        addNewUser();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
