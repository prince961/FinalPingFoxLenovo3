package com.example.mohit.finalpingfoxlenovo;

import android.app.Application;

import java.util.ArrayList;

public class Controller extends Application {


    private User user = new User();

    private ArrayList<Room> roomList = new ArrayList<>();

    public void setRoomList(ArrayList<Room> roomList) {
        this.roomList = roomList;
    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }

    public ArrayList<Room> getRoomList(){
        return roomList;
    }


}
