package com.example.mohit.finalpingfoxlenovo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FragmentFirstLogin extends Fragment {
    View myView;
    private String phoneNumber;
    private String Address;
    private String BHKId;
    private EditText ETphoneNumber, ETAddress;
    private CheckBox locationAccess;
    private Spinner ShouseConfig;

    private Boolean mLocationPermissionGranted;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    FirebaseDatabase database;
    Location currentLocation;
    SharedPreferences sharedPreferences;
    TextView hiText ;
    FirebaseUser firebaseUser;
    String mongoUSerJSONbody ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final Controller controller = (Controller) getActivity().getApplicationContext();

        myView = inflater.inflate(R.layout.fragment_first_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        sharedPreferences = getContext().getSharedPreferences("UserSP", Context.MODE_PRIVATE);
        firebaseUser = mAuth.getCurrentUser();


        ETphoneNumber = (EditText) myView.findViewById(R.id.Etphone);
        ETAddress = (EditText) myView.findViewById(R.id.EtAddress);
        ShouseConfig = (Spinner) myView.findViewById(R.id.houseConfigSpinner);
        Button submitData = (Button) myView.findViewById(R.id.BSubmitUserInfo);
        hiText = (TextView) myView.findViewById(R.id.HiText);
        hiText.setText("Hi "+ firebaseUser.getDisplayName()+", welcome to the pinfox ecosystem");


        submitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = ETphoneNumber.getText().toString();
                Address = ETAddress.getText().toString();
                BHKId = ShouseConfig.getSelectedItem().toString();
                Log.i("BHKId",BHKId);
                FragmentManager fragmentManager = getFragmentManager();

                firebaseUser = mAuth.getCurrentUser();
                User localUser = new User(firebaseUser.getDisplayName(),firebaseUser.getEmail());
                localUser.setAddress(Address);
                localUser.setPhone(phoneNumber);
                localUser.setLatitude(Double.parseDouble(sharedPreferences.getString("FirstLatitude","1")));
                localUser.setLongitude(Double.parseDouble(sharedPreferences.getString("FirstLongitude","1")));
                localUser.setBhk(BHKId);
                JSONObject jsonBody = ConvertUserDetailsJson(localUser);
                mongoUSerJSONbody = jsonBody.toString();
                Log.i("JSONbody",jsonBody.toString());
                databaseReference.child("users").child(firebaseUser.getUid()).setValue(localUser);
                Log.i("Submit Data", "swtarting fragment to control device");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("NewUser",false);
                editor.apply();
                controller.setUser(localUser);
                Log.i("ControllerUserName",controller.getUser().getFullName());
                new sendDatatoServer().execute();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentDeviceControl()).commit();

                //Intent intent = new Intent(getContext(), AddDeviceInRoom.class);
                //startActivity(intent);
            }
        });

        return myView;
    }

    private JSONObject ConvertUserDetailsJson(User localUser) {
        JSONObject bodyJson = new JSONObject();



        try {
            bodyJson.put("emailId",firebaseUser.getEmail());
            bodyJson.put("phone",localUser.getPhone());
            bodyJson.put("name",localUser.getFullName());
            bodyJson.put("bhkId",localUser.getBhk());
            JSONArray roomJsonArray = new JSONArray();

            ArrayList<Room> roomsArray = localUser.getRoomsArray();
            int roomArrayCount = localUser.getRoomsArray().size();
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
                    JSONArray devicesJSONarray = new JSONArray();
                    for (int i=0 ;i< devices.size() ; i++){
                        Relay relay = devices.get(i);
                        JSONObject relayJSOnobject = new JSONObject();
                        relayJSOnobject.put("relayName",relay.getRelayName());
                        relayJSOnobject.put("relayOn",relay.getRelayOn());
                        relayJSOnobject.put("commonRelayTopic",pingFoxDevice.getFullTopic());
                        relayJSOnobject.put("channel",relay.getChannel());
                        deviceJsonArray.put(relayJSOnobject);

                    }
                    pingFoxDeviceJSONobject.put("deviceList",deviceJsonArray);
                    deviceJsonArray.put(pingFoxDeviceJSONobject);
                }
                roomJsonObject.put("pingFoxDeviceList",deviceJsonArray);
                roomJsonArray.put(roomJsonObject);
            }


            bodyJson.put("roomArray",roomJsonArray);



        }catch (JSONException e){
            e.printStackTrace();
        }
        //Log.i("JsonBody",bodyJson.toString());
        return bodyJson;

    }

    public class sendDatatoServer extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... voids) {
            try{
                URL url = new URL("http://18.221.190.166:4000/api/ninjas");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("POST");
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

                Log.i("orderMEssage",line);
                int responseCode = conn.getResponseCode();
                Log.i("responseCode", String.valueOf(responseCode));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }



}


