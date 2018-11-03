package com.example.dominik.test;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
//Code by Dominik Erbacher | 5016085

public class ListAdapter extends BaseAdapter{
    private final Activity context;
    private final String[] playersInLobby;
    public ListAdapter(Activity context,
                      String[] playersInLobby){
        this.context = context;
        this.playersInLobby = playersInLobby;
    }

    @Override
    public int getCount() {
        return playersInLobby.length;
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
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.listitem, null);
        TextView textView = rowView.findViewById(R.id.text);
        textView.setText(playersInLobby[position]);
        return rowView;
    }
}