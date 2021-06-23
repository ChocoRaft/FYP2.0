package com.rafay.fyp20;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    private String state;
    private static final String KEY = "com.rafay.fyp20.secret";
    private static final String STATE = "com.rafay.fyp20.state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseApp.initializeApp(this);
        SharedPreferences mSharedPreferences = getSharedPreferences(KEY, MODE_PRIVATE);
        state = mSharedPreferences.getString(STATE, null);

        if (state == "signUp") {
            finish();
            startActivity(new Intent(this, SignUpActivity.class));
        } else if (state == "personalInfo") {
            finish();
            startActivity(new Intent(this, PersonalInfoActivity.class));
        } else if (state == "login") {
            finish();
            startActivity(new Intent(this, LoginScreenActivity.class));
        } else {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(STATE, "signUp");
            editor.commit();
            finish();
            startActivity(new Intent(this, SignUpActivity.class));
        }
    }
}