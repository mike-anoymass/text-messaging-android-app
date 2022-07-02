package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Integer> id;
    private ArrayList<String> phoneNumber;
    private ArrayList<String> message;

    public DisplayAdapter(Context c, ArrayList<Integer> id, ArrayList<String> num, ArrayList<String> text) {
        this.context = c;
        this.id = id;
        this.phoneNumber = num;
        this.message = text;
    }

    @Override
    public int getCount() {
        return id.size();
    }


    @Override
    public Object getItem(int position) {
        return null;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        LayoutInflater layoutInflater;
        if (convertView == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.listcell, null);
            holder = new Holder();

            holder.phoneNumber = (TextView) convertView.findViewById(R.id.phone);
            holder.message = (TextView) convertView.findViewById(R.id.messages);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.phoneNumber.setText(phoneNumber.get(position));
        holder.message.setText(message.get(position));

        return convertView;
    }

    public class Holder {
        TextView phoneNumber;
        TextView message;
    }
}
