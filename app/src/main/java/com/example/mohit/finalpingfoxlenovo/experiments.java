package com.example.mohit.finalpingfoxlenovo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class experiments {


    ArrayList<Relay> relayArrayList = new ArrayList<>();
    PingFoxDevice pingFoxDevice = new PingFoxDevice("uniqueDeviceName","fullTopic",relayArrayList);

    private ArrayList<Room> roomList = new ArrayList<>();
    private ArrayList<PingFoxDevice> pingFoxDeviceArrayList = new ArrayList<>();


    View myView;


    @Nullable

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Relay relay = new Relay("Dummy",false,1,"commonTopic");
        relayArrayList.add(relay);
        PingFoxDevice pingFoxDevice = new PingFoxDevice("deviceName","fullTopic",relayArrayList);
        pingFoxDeviceArrayList.add(pingFoxDevice);
        Room room = new Room("Bedroom","Bedroom",pingFoxDeviceArrayList);
        roomList.add(room);

        return null;
    }

    private boolean getRelayStatus(){
        int position = 1;

        Room room = roomList.get(position);


        return false;
    }

}
