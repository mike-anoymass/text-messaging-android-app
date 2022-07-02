package com.example.myapplication;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MessageNotification extends Activity {
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        receiveMessage();
    }

    private void receiveMessage() {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase("read")) {
                    final String message = intent.getStringExtra("message");
                    final String sender = intent.getStringExtra("sender");
                    makeNotification(message, sender);
                }
            }
        };


        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("read"));

    }

    private void makeNotification(String message, String sender) {
        NotificationManager manager;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notify_01");

        Intent i = new Intent(this, Inbox.class);
        i.putExtra("notiId", 1);

        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        /*NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText("Mike");
        bigTextStyle.setBigContentTitle("TOdays Message");
        bigTextStyle.setSummaryText("Keep it simple");*/

        builder.setContentIntent(pi);
        builder.setSmallIcon(R.drawable.app_icon_32);
        builder.setContentTitle("Message From ! " + sender);
        builder.setContentText(message);
        builder.setPriority(Notification.PRIORITY_MAX);
        //buider.setStyle(bigTextStyle);

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH
            );

            manager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        manager.notify(1, builder.build());
    }
}
