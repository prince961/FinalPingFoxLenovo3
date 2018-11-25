package com.example.mohit.finalpingfoxlenovo;

public class Relay {
    private String relayName;
    private Boolean relayOn;
    int channel;

    public Relay(String relayName1, Boolean relayOn1, int channel1) {
        relayName = relayName1;
        relayOn = relayOn1;
        channel = channel1;
    }

    public String getRelayName() {
        return relayName;
    }

    public Boolean getRelayOn() {
        return relayOn;
    }

    public int getChannel() {
        return channel;
    }
}
