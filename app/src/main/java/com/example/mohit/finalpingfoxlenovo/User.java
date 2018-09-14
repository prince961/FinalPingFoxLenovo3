package com.example.mohit.finalpingfoxlenovo;

import com.google.firebase.database.IgnoreExtraProperties;

import java.lang.reflect.Array;
import java.util.ArrayList;

@IgnoreExtraProperties
public class User {

    public String fullName;
    public String email;
    public String phone;
    public String address;
    public String bhk;
    public double latitude,longitude;
    public ArrayList<Room> roomsArray = new ArrayList<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public void setBhk(Long Lbhk) {
        Room masterbedroom = new Room("Master Bedroom","Bedroom");
        Room bathroomAttached = new Room("Bathroom","Bathroom");
        Room kitchen = new Room("Kitchen", "Kitchen");
        Room livingRoom  = new Room("LivingRoom", "LivingRoom");
        Room lobby = new Room("Lobby","Lobby");
        Room balcony = new Room("Balcony","Balcony");
        Room Bedroom1 = new Room("Bedroom 1","Bedroom");
        Room Bedroom2 = new Room("Bedroom 2","Bedroom");
        Room Bedroom3 = new Room("Bedroom 3","Bedroom");
        Room bathroomCommon = new Room("Bathroom Common","Bathroom");
        Room bathroom2 = new Room("Bathroom 2","Bathroom");



        if (Lbhk.intValue() == 0){
            this.bhk = "1 BHK";
            this.roomsArray.add(masterbedroom);
            this.roomsArray.add(bathroomAttached);
            this.roomsArray.add(kitchen);
            this.roomsArray.add(livingRoom);
            this.roomsArray.add(bathroomCommon);

        }
        if (Lbhk.intValue() == 1){
            this.bhk = "2 BHK";
            this.roomsArray.add(masterbedroom);
            this.roomsArray.add(bathroomAttached);
            this.roomsArray.add(kitchen);
            this.roomsArray.add(livingRoom);
            this.roomsArray.add(bathroomCommon);
        }
        if (Lbhk.intValue() == 2){
            this.bhk = "3 BHK";
            this.bhk = "2 BHK";
            this.roomsArray.add(masterbedroom);
            this.roomsArray.add(bathroomAttached);
            this.roomsArray.add(kitchen);
            this.roomsArray.add(livingRoom);
            this.roomsArray.add(bathroomCommon);

        }
        if (Lbhk.intValue() == 3){
            this.bhk = "4 BHK";
            this.roomsArray.add(masterbedroom);
            this.roomsArray.add(bathroomAttached);
            this.roomsArray.add(kitchen);
            this.roomsArray.add(livingRoom);
            this.roomsArray.add(bathroomCommon);
        }
        if (Lbhk.intValue() == 4){
            this.bhk = "Custom";
            this.roomsArray.add(masterbedroom);
            this.roomsArray.add(bathroomAttached);
            this.roomsArray.add(kitchen);
            this.roomsArray.add(livingRoom);
            this.roomsArray.add(bathroomCommon);
        }

    }
}
