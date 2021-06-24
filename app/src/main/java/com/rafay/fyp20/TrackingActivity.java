package com.rafay.fyp20;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TrackingActivity extends AppCompatActivity {

    private Context mContext;
    private ServiceHandler mServiceHandler;
    public TextView speedText;
    private LocationManager mLocationManager;
    private Geocoder mGeocoder;
    private boolean sensorActive = false;
    private static final long MIN_TIME = 700;
    private static final float MIN_DISTANCE = 0;
    private static final int MAX_RESULTS = 1;

    private List<Point> mPoints;
    private List<String> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        //setup();


    }

    public void setup(){
        mServiceHandler = new ServiceHandler(this);
        speedText = (TextView) findViewById(R.id.speedTextView);
        sensorActive = true;
        mServiceHandler.doBindService();
        GPSHandler(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(this, "onPause Called", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "onResume Called", Toast.LENGTH_SHORT).show();

        if(sensorActive)
            mServiceHandler.doUnbindService();
        sensorActive = false;

        setup();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    private void setSpeedText() {
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
            }
        };
    }


    public void GPSHandler(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
        mGeocoder = new Geocoder(mContext);    // Object to get address using coordinates

        mPoints = new ArrayList<>();

    }

    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //Log.d("debug", "location changed");


            Point mPoint = new Point(location.getLongitude(), location.getLatitude(), System.currentTimeMillis());
            mPoints.add(mPoint);
            //Log.d("TAG", "Speed: "+(location.getSpeed()*(18/5)));



            DecimalFormat df = new DecimalFormat("#.##");
            speedText.setText(String.valueOf(df.format(calcSpeed())));

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }

        @Override
        public void onProviderEnabled(String s) { }

        @Override
        public void onProviderDisabled(String s) { }
    };



    @Override
    protected void onDestroy() {
        close();
        super.onDestroy();
        Toast.makeText(this, "Tracking Stopped", Toast.LENGTH_SHORT).show();
        if(sensorActive)
            mServiceHandler.doUnbindService();
        sensorActive = false;

    }


    public void stopTracking() {
        close();
        if(sensorActive)
            mServiceHandler.doUnbindService();
        sensorActive = false;
    }

    public void stopTracking(View view) {
        close();
        finish();
        if(sensorActive)
            mServiceHandler.doUnbindService();
        sensorActive = false;
    }

    public double calcSpeed() {
        if (mPoints.size() < 2) {
            return 0;
        }
        if (mPoints.size() > 5) {
            mPoints.remove(0);
        }
        long startTime = mPoints.get(0).getLtime();
        long endTime = mPoints.get(mPoints.size() - 1).getLtime();
        double sumDis = 0;
        Point startPoint = mPoints.get(0);
        for (int i = 1; i < mPoints.size(); i++) {
            sumDis += Distance.GetDistance(startPoint, mPoints.get(i));
            startPoint = mPoints.get(i);
        }
        double speed = (sumDis * 1000 / ((endTime - startTime) / 1000.0)) * 3.6;
        return speed;
    }
    public void close(){
        mLocationManager.removeUpdates(mLocationListener);
    }




}