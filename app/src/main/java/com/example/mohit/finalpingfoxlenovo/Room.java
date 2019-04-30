package com.example.mohit.finalpingfoxlenovo;

import java.util.ArrayList;

public class Room {


    private String name;
    private String type;
    private int ID;
    private ArrayList<PingFoxDevice> pingFoxDevices;

    public Room(){
        //required for firebase
    }

    public Room(String name, String type, ArrayList<PingFoxDevice> pingFoxDeviceList) {
        this.name = name;
        this.type = type;
        this.pingFoxDevices =  pingFoxDeviceList;
    }


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public ArrayList<PingFoxDevice> getPingFoxDevices() {
        return pingFoxDevices;
    }
}

