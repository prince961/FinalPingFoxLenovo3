package com.example.mohit.finalpingfoxlenovo;

import java.util.ArrayList;

public class Room {

    private String name;
    private String type;
    private String ID;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setPingFoxDevices(ArrayList<PingFoxDevice> pingFoxDevices) {
        this.pingFoxDevices = pingFoxDevices;
    }

}

