package com.rafay.fyp20;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class PhoneVerificationActivity extends AppCompatActivity {

    private static final String KEY = "com.rafay.fyp20.secret";
    private static final String STATE = "com.rafay.fyp20.state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        SharedPreferences.Editor editor = getSharedPreferences(KEY, MODE_PRIVATE).edit();
        editor.putString(STATE, "verification");
        editor.commit();
    }

    public void goToDashboard(View view) {
        finish();
        startActivity(new Intent(this, DashboardActivity.class));
    }
}