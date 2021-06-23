package com.rafay.fyp20;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TrackingActivity extends AppCompatActivity {

    private ServiceHandler mServiceHandler;
    private GPSHandler mLocationHandler;
    public TextView speedText;
    //public Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        mServiceHandler = new ServiceHandler(this);
        mServiceHandler.doBindService();
        mLocationHandler = new GPSHandler(this);

        speedText = (TextView) findViewById(R.id.speedTextView);
        speedText.setText(String.valueOf(mLocationHandler.calcSpeed()));
    }

    private void setSpeedText() {
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                speedText.setText(String.valueOf(mLocationHandler.calcSpeed()));
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Destroyed", Toast.LENGTH_SHORT).show();
        mServiceHandler.doUnbindService();
        mLocationHandler.close();
        //mLocationHandler.removeCallbacks(mRunnable);
    }

    public void stopTracking(View view) {
        finish();
    }
}