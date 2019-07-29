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

        private String id;


    public PingFoxDevice(){
        //required for firebse
    }

    public PingFoxDevice(String pingFoxDeviceName, String fullTopic,ArrayList<Relay> devices) {
        this.PingFoxDeviceName = pingFoxDeviceName;
        this.devices = devices;
        this.fullTopic = fullTopic;
        macAddress = "dummy";
        InternetConnection = true;
        LocalConnection = true;
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

    public void setPingFoxDeviceName(String pingFoxDeviceName) {
        PingFoxDeviceName = pingFoxDeviceName;
    }

    public void setFullTopic(String fullTopic) {
        this.fullTopic = fullTopic;
    }

    public void setDeviceChannels(int deviceChannels) {
        DeviceChannels = deviceChannels;
    }

    public void setInternetConnection(Boolean internetConnection) {
        InternetConnection = internetConnection;
    }

    public void setLocalConnection(Boolean localConnection) {
        LocalConnection = localConnection;
    }

    public void setDevices(ArrayList<Relay> devices) {
        this.devices = devices;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setId(String id) {
        this.id = id;
    }


}
