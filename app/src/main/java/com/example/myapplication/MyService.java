package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class SMSListener extends BroadcastReceiver {
        Context c;
        String str;
        String number;
        NotificationManager manager;

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            c = context;


            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                for (int i = 0; i < msgs.length; i++) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    if (i == 0) {
                        number = msgs[i].getOriginatingAddress();

                    }

                    str = msgs[i].getMessageBody().toString();
                }

                Toast.makeText(context, number + " : " + str, Toast.LENGTH_LONG).show();

                makeNotification();

                this.abortBroadcast();
            }
        }

        public void makeNotification() {
            NotificationManager manager;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(c, "notify_01");

            Intent i = new Intent(c, Inbox.class);
            i.putExtra("notiId", 1);

            PendingIntent pi = PendingIntent.getActivity(c, 0, i, 0);

        /*NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText("Mike");
        bigTextStyle.setBigContentTitle("TOdays Message");
        bigTextStyle.setSummaryText("Keep it simple");*/

            builder.setContentIntent(pi);
            builder.setSmallIcon(R.drawable.app_icon_32);
            builder.setContentTitle("Message From ! " + number);
            builder.setContentText(str);
            builder.setPriority(Notification.PRIORITY_MAX);
            //buider.setStyle(bigTextStyle);

            manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);

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
}
