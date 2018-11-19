package com.example.nico.myfirstapplication;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Console;
import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    public ArrayList<Tuple> list;
    private Context context;


    public MyAdapter(Context context, ArrayList<Tuple> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Tuple getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View row;
        final ListViewHolder listViewHolder;
        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.activity_custom_listview,parent,false);
            listViewHolder = new ListViewHolder();
            listViewHolder.wifiInfo = row.findViewById(R.id.wifiInfo);
            listViewHolder.location = row.findViewById(R.id.location);
            listViewHolder.distance = row.findViewById(R.id.distance);
            final EditText text = row.findViewById(R.id.location);
            final TextView infoWifi = row.findViewById(R.id.wifiInfo);
            final TextView distance = row.findViewById(R.id.distance);
            text.setOnEditorActionListener(new EditText.OnEditorActionListener(){
                            @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                            actionId == EditorInfo.IME_ACTION_DONE ||
                            event != null &&
                                    event.getAction() == KeyEvent.ACTION_DOWN &&
                                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        if (event == null || !event.isShiftPressed()) {
                            // the user is done typing.
                            
                            Log.d("INFO", String.valueOf(infoWifi.getText())+" "+String.valueOf(text.getText())+" "+String.valueOf(distance.getText()));
                            return true; // consume.
                        }
                    }
                    return false; // pass on to other listeners.
                }
            });
            row.setTag(listViewHolder);
        }
        else
        {
            row=convertView;
            listViewHolder= (ListViewHolder) row.getTag();
        }
        final Tuple tuple = getItem(position);

        listViewHolder.wifiInfo.setText(tuple.wifiInfo);
        listViewHolder.location.setText(tuple.location);
        listViewHolder.distance.setText(tuple.distance);

        return row;
    }
}
