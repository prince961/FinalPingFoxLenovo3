package com.example.mohit.finalpingfoxlenovo;

import java.util.ArrayList;

public class PingFoxDevice {

    private String PingFoxDeviceName;
    private String fullTopic;
    private int DeviceChannels;
    private Boolean InternetConnection;
    private Boolean LocalConnection;
    private ArrayList<Relay> devices;

    public PingFoxDevice(){
        //required for firebse
    }

    public PingFoxDevice(String pingFoxDeviceName, String fullTopic,ArrayList<Relay> devices) {
        this.PingFoxDeviceName = pingFoxDeviceName;
        this.devices = devices;
        this.fullTopic = fullTopic;
    }
}
