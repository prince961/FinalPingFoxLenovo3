package com.example.mohit.finalpingfoxlenovo;

import java.util.ArrayList;

public class PingFoxDevice {

    private String PingFoxDeviceName;
    private String fullTopic;
    private int DeviceChannels;
    private Boolean InternetConnection;
    private Boolean LocalConnection;
    private ArrayList<Relay> devices;
    private String macAddress;

    public PingFoxDevice(){
        //required for firebse
    }

    public PingFoxDevice(String pingFoxDeviceName, String fullTopic,ArrayList<Relay> devices) {
        this.PingFoxDeviceName = pingFoxDeviceName;
        this.devices = devices;
        this.fullTopic = fullTopic;
        macAddress = "dummy";
    }

    public String getPingFoxDeviceName() {
        return PingFoxDeviceName;
    }

    public String getFullTopic() {
        return fullTopic;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public int getDeviceChannels() {
        return DeviceChannels;
    }

    public Boolean getInternetConnection() {
        return InternetConnection;
    }

    public Boolean getLocalConnection() {
        return LocalConnection;
    }

    public ArrayList<Relay> getDevices() {
        return devices;
    }
}
