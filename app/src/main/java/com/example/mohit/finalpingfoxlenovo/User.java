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
    private ArrayList<PingFoxDevice> pingFoxDeviceArrayList= new ArrayList<>();

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

    public void setBhk(String Lbhk) {
        Room masterbedroom = new Room("Master Bedroom","Bedroom",pingFoxDeviceArrayList);
        Room bathroomAttached = new Room("Bathroom","Bathroom",pingFoxDeviceArrayList);
        Room kitchen = new Room("Kitchen", "Kitchen",pingFoxDeviceArrayList);
        Room livingRoom  = new Room("LivingRoom", "LivingRoom",pingFoxDeviceArrayList);
        Room lobby = new Room("Lobby","Lobby",pingFoxDeviceArrayList);
        Room balcony = new Room("Balcony","Balcony",pingFoxDeviceArrayList);
        Room Bedroom1 = new Room("Bedroom 1","Bedroom",pingFoxDeviceArrayList);
        Room Bedroom2 = new Room("Bedroom 2","Bedroom",pingFoxDeviceArrayList);
        Room Bedroom3 = new Room("Bedroom 3","Bedroom",pingFoxDeviceArrayList);
        Room bathroomCommon = new Room("Bathroom Common","Bathroom",pingFoxDeviceArrayList);
        Room bathroom2 = new Room("Bathroom 2","Bathroom",pingFoxDeviceArrayList);



        if (Lbhk.equals("1 Bhk")){
            this.bhk = "1 BHK";
            this.roomsArray.add(masterbedroom);
            this.roomsArray.add(bathroomAttached);
            this.roomsArray.add(kitchen);
            this.roomsArray.add(livingRoom);
            this.roomsArray.add(bathroomCommon);

        }
        if (Lbhk.equals("2 Bhk")){
            this.bhk = "2 BHK";
            this.roomsArray.add(masterbedroom);
            this.roomsArray.add(bathroomAttached);
            this.roomsArray.add(kitchen);
            this.roomsArray.add(livingRoom);
            this.roomsArray.add(bathroomCommon);
        }
        if (Lbhk.equals("3 Bhk")){
            this.bhk = "3 BHK";
            this.roomsArray.add(masterbedroom);
            this.roomsArray.add(bathroomAttached);
            this.roomsArray.add(kitchen);
            this.roomsArray.add(livingRoom);
            this.roomsArray.add(bathroomCommon);

        }
        if (Lbhk.equals("4 Bhk")){
            this.bhk = "4 BHK";
            this.roomsArray.add(masterbedroom);
            this.roomsArray.add(bathroomAttached);
            this.roomsArray.add(kitchen);
            this.roomsArray.add(livingRoom);
            this.roomsArray.add(bathroomCommon);
        }
        if (Lbhk.equals("Custom")){
            this.bhk = "Custom";
            this.roomsArray.add(masterbedroom);
            this.roomsArray.add(bathroomAttached);
            this.roomsArray.add(kitchen);
            this.roomsArray.add(livingRoom);
            this.roomsArray.add(bathroomCommon);
        }



    }

    public String getPhone() {
        return phone;
    }

    public String getFullName() {
        return fullName;
    }

    public ArrayList<Room> getRoomsArray() {
        return roomsArray;
    }
}
