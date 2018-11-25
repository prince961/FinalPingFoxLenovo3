package com.example.mohit.finalpingfoxlenovo;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdapterModuleSetup extends RecyclerView.Adapter<AdapterModuleSetup.ViewHolder> {

    private ArrayList<Relay> relayList;
    private Context context;

    public AdapterModuleSetup(ArrayList<Relay> relayList, Context context) {

        this.relayList = relayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
       View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_configre_channels,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Relay relay = relayList.get(position);
        viewHolder.deviceName.setHint(relay.getRelayName());
        Log.i("relayName",relay.getRelayName());
        Boolean relayOn = relay.getRelayOn();
        if (relayOn){
            viewHolder.pingText.setTextColor(Color.parseColor("#53FF6D"));
        }

    }

    @Override
    public int getItemCount() {
        return relayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public EditText deviceName;
        public TextView pingText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            deviceName = (EditText) itemView.findViewById(R.id.DeviceNameET);
            pingText = (TextView) itemView.findViewById(R.id.pingButtonTV);
        }
    }


}
