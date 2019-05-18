package com.example.mohit.finalpingfoxlenovo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

class JsonParser {
    public User getUserFromJSON(String line) {

        User user = new User();


        try{
            JSONObject firstJsonObj = new JSONObject(line);
            String emailId = firstJsonObj.getString("emailId");
            String phone = firstJsonObj.getString("phone");
            String name = firstJsonObj.getString("name");
            String bhkId = firstJsonObj.getString("bhkId");
            String address = firstJsonObj.getString("address");
            String latitude = firstJsonObj.getString("latitude");
            String longitude = firstJsonObj.getString("longitude");
            user.setFullName(name);
            user.setEmail(emailId);
            user.setAddress(address);
            user.setBhk(bhkId);
            user.setPhone(phone);
            user.setLatitude(Double.parseDouble(latitude));
            user.setLongitude(Double.parseDouble(longitude));
            JSONArray roomArrayJsonArray = firstJsonObj.getJSONArray("roomArray");
            ArrayList<Room> roomArrayList = new ArrayList<>();
            for (int k=0 ;k< roomArrayJsonArray.length() ; k++){
                Room room = new Room();
                JSONObject roomJsonObject = roomArrayJsonArray.getJSONObject(k);
                String roomName = roomJsonObject.getString("roomName");
                String roomType = roomJsonObject.getString("roomType");
                String roomId = roomJsonObject.getString("roomId");
                String dbRoomId = roomJsonObject.getString("_id");
                room.setID(dbRoomId);
                room.setName(roomName);
                room.setType(roomType);
                JSONArray pingFoxDeviceJsonArray = roomJsonObject.getJSONArray("pingFoxDeviceList");
                ArrayList<PingFoxDevice> pingFoxDeviceArray = new ArrayList<>();
                for (int l=0;l<pingFoxDeviceJsonArray.length();l++){
                    PingFoxDevice pingFoxDevice = new PingFoxDevice();
                    JSONObject pingFoxDeviceJsonObject = pingFoxDeviceJsonArray.getJSONObject(l);
                    String pingfoxDeviceName = pingFoxDeviceJsonObject.getString("pingfoxDeviceName");
                    String fullTopic = pingFoxDeviceJsonObject.getString("fullTopic");
                    int deviceChannels = pingFoxDeviceJsonObject.getInt("deviceChannels");
                    Boolean internetConnection = pingFoxDeviceJsonObject.getBoolean("internetConnection");
                    Boolean localConnection = pingFoxDeviceJsonObject.getBoolean("localConnection");
                    String macAddress = pingFoxDeviceJsonObject.getString("macAddress");
                    String _id = pingFoxDeviceJsonObject.getString("_id");
                    pingFoxDevice.setDeviceChannels(deviceChannels);
                    pingFoxDevice.setFullTopic(fullTopic);
                    pingFoxDevice.setId(_id);
                    pingFoxDevice.setInternetConnection(internetConnection);
                    pingFoxDevice.setLocalConnection(localConnection);
                    pingFoxDevice.setMacAddress(macAddress);
                    pingFoxDevice.setPingFoxDeviceName(pingfoxDeviceName);
                    JSONArray relayJsonArray = pingFoxDeviceJsonObject.getJSONArray("deviceList");
                    ArrayList<Relay> relayArrayList = new ArrayList<>();
                    for (int m=0; m<relayJsonArray.length();m++){
                        Relay relay = new Relay();
                        JSONObject relayJsonObject = relayJsonArray.getJSONObject(m);
                        String id = relayJsonObject.getString("_id");
                        String relayName = relayJsonObject.getString("relayName");
                        Boolean relayOn = relayJsonObject.getBoolean("relayOn");
                        String commonRelayTopic = relayJsonObject.getString("commonRelayTopic");
                        int channel = relayJsonObject.getInt("channel");
                        relay.setRelayName(relayName);
                        relay.setChannel(channel);
                        relay.setRelayOn(relayOn);
                        relay.setCommonRelayTopic(commonRelayTopic);
                        relay.setId(id);
                        relayArrayList.add(relay);

                    }
                    pingFoxDevice.setDevices(relayArrayList);
                    pingFoxDeviceArray.add(pingFoxDevice);

                }
                room.setPingFoxDevices(pingFoxDeviceArray);
                roomArrayList.add(room);



            }
            user.setRoomsArray(roomArrayList);




        }catch (Exception e) {
            e.printStackTrace();}


        return user;

    }

    public Boolean checkExistingUserJson(String line) {
        Boolean existingUser = false;

        try{
            JSONObject firstJsonObj = new JSONObject(line);
            existingUser = firstJsonObj.getBoolean("userAlreadyInDB");

        }catch (Exception e) {
            e.printStackTrace();}
        return existingUser;
    }
}
