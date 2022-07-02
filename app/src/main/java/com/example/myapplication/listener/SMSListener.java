package com.example.myapplication.listener;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.myapplication.DBAdapter;
import com.example.myapplication.Inbox;
import com.example.myapplication.R;


public class SMSListener extends BroadcastReceiver {
    String contactName = "";
    private String SMS_RECEIVED = "android.intent.action.DATA_SMS_RECEIVED";
    private Intent smsIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        String number = "";
        String str = "";
        String str1 = "";
        //final Bundle bundle = intent.getExtras();
        if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;


            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdusObj.length];

                for (int i = 0; i < pdusObj.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                }

                for (SmsMessage currentMessage : messages) {
                    if (!currentMessage.isStatusReportMessage()) {
                        str = currentMessage.getDisplayMessageBody();
                        number = currentMessage.getOriginatingAddress();
                        byte[] messageByteArray = currentMessage.getPdu();

                        int x = 1 + messageByteArray[0] + 19 + 7;

                        str1 = new String(messageByteArray, x, messageByteArray.length - x);
                    }
                }

                if (number.contains("6570252") || number.contains("4799203")) contactName = "Mike";
                else if (number.contains("7235773") || number.contains("5560082"))
                    contactName = "Chimz";
                else contactName = number;

                Toast.makeText(context, "New Message from " + contactName, Toast.LENGTH_LONG).show();
                /*smsIntent = new Intent("read");
                smsIntent.putExtra("message", str1);
                smsIntent.putExtra("sender", number);
                LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);*/


                //notify when message is received
                NotificationManager manager;
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notify_01");

                Intent i = new Intent(context, Inbox.class);
                i.putExtra("notiId", 1);

                PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);

                builder.setContentIntent(pi);
                builder.setSmallIcon(R.drawable.app_icon_32);
                builder.setContentTitle("Message From " + contactName);
                builder.setContentText(str1);
                builder.setPriority(Notification.PRIORITY_MAX);

                manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String channelId = "message ID";
                    NotificationChannel channel = new NotificationChannel(
                            channelId,
                            "Channel human readable title",
                            NotificationManager.IMPORTANCE_HIGH
                    );

                    manager.createNotificationChannel(channel);
                    builder.setChannelId(channelId);
                }
                manager.notify(1, builder.build());


                //save to database
                DBAdapter db = new DBAdapter(context);
                db.open();
                long id = db.insertMessage(number, str1);
                db.close();

            }
        }
    }

}
