package com.example.myapplication;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
    Notification n;
    NotificationCompat nc;
    NotificationManager nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        makeNoti();
    }

    private void makeNoti() {
        Intent i = new Intent(this, MainActivity.class);

        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        //n = new Notification(R.mipmap.ic_launcher, "Hie", System.currentTimeMillis() );

        Notification.Builder b = new Notification.Builder(this);

        b.setContentIntent(pi);
        b.setContentTitle("Message");
        b.setTicker("Hello there");
        b.setSmallIcon(R.mipmap.ic_launcher);
        //b.setOngoing(true);

        n = b.build();

        nm.notify(1, n);
    }


}