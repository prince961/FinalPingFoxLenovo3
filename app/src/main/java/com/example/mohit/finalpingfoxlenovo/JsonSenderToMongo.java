package com.example.mohit.finalpingfoxlenovo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class JsonSenderToMongo extends AppCompatActivity {


    private String mongoUSerJSONbody;
    User user;

    public void updateUserInDB(User user){
        ConvertUserDetailsJson(user);
        new sendDatatoServer().execute();

    }

    private JSONObject ConvertUserDetailsJson(User user) {
        JSONObject bodyJson = new JSONObject();
        this.user = user;



        try {
            bodyJson.put("emailId",user.getEmail());
            bodyJson.put("phone",user.getPhone());
            bodyJson.put("name",user.getFullName());
            bodyJson.put("bhkId",user.getBhk());
            bodyJson.put("address",user.getAddress());
            bodyJson.put("latitude",user.getLatitude());
            bodyJson.put("longitude",user.getLongitude());
            JSONArray roomJsonArray = new JSONArray();

            ArrayList<Room> roomsArray = user.getRoomsArray();
            int roomArrayCount = user.getRoomsArray().size();
            for (int l=0 ;l< roomArrayCount ; l++){
                JSONObject roomJsonObject = new JSONObject();
                Room room = roomsArray.get(l);
                roomJsonObject.put("roomName",room.getName());
                roomJsonObject.put("roomType",room.getType());
                roomJsonObject.put("roomId",1);
                ArrayList<PingFoxDevice> pingFoxDeviceArrayList = room.getPingFoxDevices();
                JSONArray deviceJsonArray = new JSONArray();
                for (int k=0 ;k< pingFoxDeviceArrayList.size() ; k++){
                    PingFoxDevice pingFoxDevice = pingFoxDeviceArrayList.get(k);
                    JSONObject pingFoxDeviceJSONobject = new JSONObject();
                    pingFoxDeviceJSONobject.put("pingfoxDeviceName",pingFoxDevice.getPingFoxDeviceName());
                    pingFoxDeviceJSONobject.put("fullTopic",pingFoxDevice.getFullTopic());
                    pingFoxDeviceJSONobject.put("deviceChannels",pingFoxDevice.getDeviceChannels());
                    pingFoxDeviceJSONobject.put("internetConnection",pingFoxDevice.getInternetConnection());
                    pingFoxDeviceJSONobject.put("localConnection",pingFoxDevice.getLocalConnection());
                    pingFoxDeviceJSONobject.put("macAddress",pingFoxDevice.getMacAddress());
                    ArrayList<Relay> devices = pingFoxDevice.getDevices();
                    JSONArray relayJsonArray = new JSONArray();
                    for (int i=0 ;i< devices.size() ; i++){
                        Relay relay = devices.get(i);
                        JSONObject relayJSOnobject = new JSONObject();
                        relayJSOnobject.put("relayName",relay.getRelayName());
                        relayJSOnobject.put("relayOn",relay.getRelayOn());
                        relayJSOnobject.put("commonRelayTopic",pingFoxDevice.getFullTopic());
                        relayJSOnobject.put("channel",relay.getChannel());
                        Log.i("channelTest",relayJSOnobject.toString());
                        relayJsonArray.put(relayJSOnobject);

                    }
                    //Log.i("RoomJson",roomJsonObject.toString());
                    pingFoxDeviceJSONobject.put("deviceList",relayJsonArray);
                    deviceJsonArray.put(pingFoxDeviceJSONobject);
                }
                roomJsonObject.put("pingFoxDeviceList",deviceJsonArray);
                Log.i("RoomJson",roomJsonObject.toString());
                roomJsonArray.put(roomJsonObject);
            }


            bodyJson.put("roomArray",roomJsonArray);



        }catch (JSONException e){
            e.printStackTrace();
        }
        mongoUSerJSONbody = bodyJson.toString();
        //Log.i("JsonBody",bodyJson.toString());
        return bodyJson;

    }

    public class sendDatatoServer extends AsyncTask<Void, Void, User> {


        @Override
        protected User doInBackground(Void... voids) {
            try{
                URL url = new URL("http://dev.pingfox.in:4000/api/ninjas/"+user.getEmail());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("PUT");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                String body = mongoUSerJSONbody;
                Log.i("BODY",body);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(body);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    //Read till there is something available
                    sb.append(line + "\n");     //Reading and saving line by line - not all at once
                }
                line = sb.toString();
                Log.i("CreatedUserJsonResponse",line);
                int responseCode = conn.getResponseCode();
                Log.i("responseCode", String.valueOf(responseCode));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null ;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);


        }


    }



}
