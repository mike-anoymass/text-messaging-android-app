package com.example.myapplication;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class Menu extends ListActivity {
    String[] classes = new String[]{"Inbox", "Calls", "Chills"};


    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        String[] permissions = {"android.permission.SEND_SMS", "android.permission.READ_SMS", "android.permission.RECEIVE_SMS",
                "android.permission.WRITE_SMS"};
        ActivityCompat.requestPermissions(Menu.this, permissions, 2);

        ListAdapter adapter = new ArrayAdapter<String>(Menu.this, android.R.layout.simple_selectable_list_item, classes);

        setListAdapter(adapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String selectedClass = classes[position];

        try {
            Class getSelectedClass = Class.forName("com.example.myapplication." + selectedClass);
            Intent i = new Intent(this, getSelectedClass);
            startActivity(i);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
