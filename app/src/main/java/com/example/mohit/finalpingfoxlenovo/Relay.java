package com.example.mohit.finalpingfoxlenovo;

public class Relay {
    private String relayName;
    private Boolean relayOn;
    int channel;

    public Relay(String relayName, Boolean relayOn, int channel) {
        this.relayName = relayName;
        this.relayOn = relayOn;
        this.channel = channel;
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

    public void setRelayName(String relayName) {
        this.relayName = relayName;
    }

    public void setRelayOn(Boolean relayOn) {
        this.relayOn = relayOn;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }
}
