package com.example.myapplication;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class Inbox extends Activity {

    TabHost tabHost;
    AutoCompleteTextView phoneNumber;
    EditText message;
    String[] phone = {"0996570252", "0884799203", "0997235773", "0885560082", "0994038394"};
    ListView messageList;
    ArrayList<Integer> id = new ArrayList<>();
    ArrayList<String> phoneNum = new ArrayList<>();
    ArrayList<String> textMessage = new ArrayList<>();

    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    short SMS_PORT = 10013;

    public void onCreate(Bundle savedIBundle) {
        super.onCreate(savedIBundle);
        setContentView(R.layout.tabs);

        initializeVars();
        setUpTabs();
        autoCompletePhoneNumber();

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(1);
    }

    private void autoCompletePhoneNumber() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, phone);
        phoneNumber.setThreshold(0);
        phoneNumber.setAdapter(adapter);
    }

    private void initializeVars() {
        tabHost = findViewById(R.id.tab_host);
        phoneNumber = findViewById(R.id.phoneNumber);
        message = findViewById(R.id.message);
        messageList = findViewById(R.id.list);

        sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);


    }

    private void setUpTabs() {
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("Messages");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("Compose");
        tabHost.addTab(tabSpec);

        //messageList = findViewById(android.R.id.list);

    }

    public void sendMessage(View view) {
        if (checkInput(phoneNumber.getText().toString(), message.getText().toString())) {
            sendSms(phoneNumber.getText().toString(), message.getText().toString());
        }

    }

    private boolean checkInput(String num, String text) {
        if (num.isEmpty() || text.isEmpty()) {
            displayText("Error: Empty fields not Allowed!!");
            return false;
        }
        return true;
    }

    private void sendSms(String phone, String sms) {
        SmsManager smsManager = SmsManager.getDefault();
        //byte[] bytesMessage = sms.getBytes();
        smsManager.sendDataMessage(phone, null, SMS_PORT, sms.getBytes(), sentPI, deliveredPI);
        //smsManager.sendTextMessage(phone,null , sms, sentPI, deliveredPI);
    }

    private void clearFields() {
        phoneNumber.setText("");
        message.setText("");
    }


    @Override
    protected void onResume() {
        super.onResume();
        displayData();
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        displayText("SMS sent");
                        saveMessage(phoneNumber.getText().toString(), message.getText().toString());
                        clearFields();
                        break;

                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        displayText("Generic Failure");
                        break;

                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        displayText("No Service");
                        break;

                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        displayText("Null PDU");
                        break;

                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        displayText("Radio OFF");
                        break;
                }
            }

        };

        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        displayText("SMS Delivered");

                        break;

                    case Activity.RESULT_CANCELED:
                        displayText("SMS not Delivered");
                        break;

                }
            }
        };

        registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));
        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
    }

    private void displayData() {
        DBAdapter db = new DBAdapter(Inbox.this);
        db.open();

        Cursor c = db.getAllMessages();

        if (c.moveToFirst()) {
            do {
                id.add(c.getInt(c.getColumnIndex(DBAdapter.M_ID)));
                phoneNum.add(c.getString(c.getColumnIndex(DBAdapter.M_CONTACT)));
                textMessage.add(c.getString(c.getColumnIndex(DBAdapter.M_NAME)));
            } while (c.moveToNext());
        }

        DisplayAdapter displayAdapter = new DisplayAdapter(this, id, phoneNum, textMessage);
        messageList.setAdapter(displayAdapter);
        db.close();
    }

    private void saveMessage(String num, String text) {
        DBAdapter db = new DBAdapter(Inbox.this);
        db.open();
        long id = db.insertMessage("Me to " + num, text);
        db.close();


        if (id == -1) {
            displayText("Error saving message");
        } else {
            displayText("Message Saved");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(smsDeliveredReceiver);
        unregisterReceiver(smsSentReceiver);
        finish();
    }

    private void displayText(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
