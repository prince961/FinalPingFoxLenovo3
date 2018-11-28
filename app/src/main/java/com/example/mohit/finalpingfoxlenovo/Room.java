package com.example.mohit.finalpingfoxlenovo;

import java.util.ArrayList;

public class Room {


    private String name;
    private String type;
    private ArrayList<PingFoxDevice> pingFoxDevice;

    public Room(){
        //required for firebase
    }

    public Room(String name, String type, ArrayList<PingFoxDevice> pingFoxDeviceList) {
        this.name = name;
        this.type = type;
        this.pingFoxDevice =  pingFoxDeviceList;
    }


    public String getName() {
        return name;
    }
}

