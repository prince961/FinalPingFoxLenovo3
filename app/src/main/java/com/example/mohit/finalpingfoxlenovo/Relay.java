package com.example.mohit.finalpingfoxlenovo;

public class Relay {
    private String relayName;
    private Boolean relayOn;
    int channel;

    public Relay(String relayName, Boolean relayOn, int channel) {
        relayName = relayName;
        relayOn = relayOn;
        channel = channel;
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
