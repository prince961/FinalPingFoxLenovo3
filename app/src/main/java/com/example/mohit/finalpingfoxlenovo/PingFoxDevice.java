package com.example.mohit.finalpingfoxlenovo;

import java.util.ArrayList;

public class PingFoxDevice {

    private String PingFoxDeviceName;
    private String MacAddress;
    private int DeviceChannels;
    private Boolean InternetConnection;
    private Boolean LocalConnection;
    private ArrayList<Relay> Devices;

    public PingFoxDevice(String pingFoxDeviceName, String macAddress, int deviceChannels, Boolean internetConnection, Boolean localConnection) {
        PingFoxDeviceName = pingFoxDeviceName;
        MacAddress = macAddress;
        DeviceChannels = deviceChannels;
        InternetConnection = internetConnection;
        LocalConnection = localConnection;
    }
}
