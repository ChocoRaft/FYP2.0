package com.rafay.fyp20;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class SensorService extends Service implements SensorEventListener {

    // TAG to identify notification
    private static final int NOTIFICATION = 0;


    private static final long MIN_TIME = 700;
    private static final float MIN_DISTANCE = 0;
    private static final int MAX_RESULTS = 1;

    // IBinder object to allow Activity to connect
    private final IBinder mBinder = new LocalBinder();

    // Sensor Objects
    private Sensor accelerometer;
    private Location mLocation;
    private SensorManager mSensorManager;
    private boolean highSpeed;
    private double accelerationX, accelerationY, accelerationZ;
    private float speed;
    private LocationManager mLocationManager;
    private int threshold = 50;


    // Notification Manager
    private NotificationManager mNotificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public SensorService getService() {
            return SensorService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }



        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        showNotif2();
        //showNotification();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSensorManager.unregisterListener(this);                            // Unregister sensor when not in use
        mLocationManager.removeUpdates(mLocationListener);
        mNotificationManager.cancel(NOTIFICATION);
        stopSelf();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        accelerationX = (Math.round(sensorEvent.values[0]*1000)/1000.0);
        accelerationY = (Math.round(sensorEvent.values[1]*1000)/1000.0);
        accelerationZ = (Math.round(sensorEvent.values[2]*1000)/1000.0);

        /*** Detect Accident ***/
        if ((accelerationX > threshold || accelerationY > threshold || accelerationZ > threshold) && highSpeed ){
            Intent mIntent = new Intent();
            mIntent.setClass(this, SendSMSActivity.class);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mSensorManager.unregisterListener(this);                            // Unregister sensor when not in use
            mNotificationManager.cancel(NOTIFICATION);
            stopSelf();
            startActivity(mIntent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if((location.getSpeed()*(18/5))>30){
                highSpeed = true;
            }
            else{
                highSpeed = false;
            }

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }

        @Override
        public void onProviderEnabled(String s) { }

        @Override
        public void onProviderDisabled(String s) { }
    };


    private void showNotification() {
        Log.d("SERVICE DEBUG", "Notification Shown");
        CharSequence text = "Crash detection is Running!";

        // PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        // PendingIntent deleteIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        Notification mNotification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.alarm)
                .setTicker(text)
                .setContentTitle("SafeApp")
                .setContentText(text)
                .setAutoCancel(false)
                .build();

        mNotificationManager.notify(0, mNotification);
    }


    private void showNotif2(){
        String CHANNEL_ID = "MESSAGE";
        String CHANNEL_NAME = "MESSAGE";
        CharSequence text = "Crash detection is Running!";

        NotificationManagerCompat manager;
        manager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.alarm)
                .setTicker(text)
                .setContentTitle("SafeApp")
                .setContentText(text)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSilent(true)
                .build();
        manager.notify(0, notification);
    }
}