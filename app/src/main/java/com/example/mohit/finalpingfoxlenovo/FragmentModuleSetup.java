package com.example.mohit.finalpingfoxlenovo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class FragmentModuleSetup extends Fragment {

    View myView;
    private Boolean bPingFoxConnected = false;
    ProgressDialog progDailog;
    InetAddress pingFoxIP;

    String deviceName = "abc";
    String uniqueDeviceName;
    private ArrayList<String> AlreadyAddedMacAddress;
    SharedPreferences sharedPreferences;
    int numberRelays;
    private ArrayList<Relay> relayArrayList ;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Button submitButton;
    private DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase ;
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    String fullTopic;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_moduule_setup, container, false);
        relayArrayList = new ArrayList<>();
        recyclerView = (RecyclerView) myView.findViewById(R.id.moduleRelayRV);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        FragmentModuleSetup.NetworkSniffTask task = new FragmentModuleSetup.NetworkSniffTask(getContext());
        task.execute();
        sharedPreferences = getContext().getSharedPreferences("UserSP", Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseUser = mAuth.getCurrentUser();
        submitButton = (Button) myView.findViewById(R.id.buttonRegisterPingFoxDecvice);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboardFrom(getContext(),myView);
                RegisterDevice();

            }
        });
        final Controller controller = (Controller) getActivity().getApplicationContext();
        return myView;
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void RegisterDevice() {
        //getDetails
        for (int i = 0; i<relayArrayList.size();i++){
            String deviceName;
            Log.i("size", String.valueOf(recyclerView.getAdapter().getItemCount()));

            View row = recyclerView.getLayoutManager().findViewByPosition(i);

            //Log.i("rowData",row.)
            try{

                EditText deviceNameET = row.findViewById(R.id.DeviceNameET);
                Log.i("StringFromET",deviceNameET.getText().toString());
                deviceName = deviceNameET.getText().toString();
            }catch (NullPointerException e){
                e.printStackTrace();
                deviceName = relayArrayList.get(i).getRelayName();

            }
            // EditText deviceNameET = row.findViewById(R.id.DeviceNameET);
            //Log.i("StringFromET",deviceNameET.getText().toString());
            //String relayDeviceName = relayArrayList.get(i).getRelayName();
            //Relay relay = relayArrayList.get(i);
            //relay.setRelayName(relayDeviceName);
            Log.i("device name",deviceName);

        }

        final Controller controller = (Controller) getActivity().getApplicationContext();
        User contollerUser = controller.getUser();
        ArrayList<Room> roomArrayList = contollerUser.getRoomsArray();
        String roomID = sharedPreferences.getString("AddDeviceInRoomID",null);
        Log.i("roomListlength", contollerUser.getFullName());
        Room selectedRoom = contollerUser.getRoom(roomID);
        final PingFoxDevice pingFoxDevice = new PingFoxDevice(uniqueDeviceName,fullTopic,relayArrayList);
        selectedRoom.getPingFoxDevices().add(pingFoxDevice);
        JsonSenderToMongo jsonSenderToMongo = new JsonSenderToMongo();
        jsonSenderToMongo.updateUserInDB(contollerUser);
        Log.i("controllerTest",controller.getUser().getRoom(roomID).getPingFoxDevices().get(0).getFullTopic());






        final DatabaseReference userDataBaseRef =databaseReference.child("users").child(firebaseUser.getUid()).getRef();
        userDataBaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String fullName = user.getFullName();
                //ArrayList<Room> roomList = controller.getRoomList();
                Log.i("fullName", fullName);

                if (dataSnapshot.hasChild("PingfoxDeviceMap")){
                    //read and write data

                }else {


                    //write data
                    DatabaseReference pingfoxDeviceMapDataRef = databaseReference.child("users").child(firebaseUser.getUid()).child("PingfoxDeviceMap");
                    //pingfoxDeviceMapDataRef.setValue()
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });



    }

    private String getPingFoxDeviceName(String s) {
        String pingFoxDeviceName = null;
        if (s.equals("7 (Sonoff 4CH)")) {
            pingFoxDeviceName = "Pingfox quatro";
            numberRelays = 4;
        }
        return pingFoxDeviceName;
    }

    class NetworkSniffTask extends AsyncTask<Void, Void, Boolean> {

        private static final String TAG = SyncStateContract.Constants.DATA + "nstask";
        private WeakReference<Context> mContextRef;
        private ArrayList<InetAddress> activeIPlist;
        Context noClueContext;

        public NetworkSniffTask(Context context) {
            mContextRef = new WeakReference<Context>(context);
            noClueContext = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = ProgressDialog.show(getContext(), "Scanning", "please wait");
            progDailog.setMessage("Searching for pingfox Device on your network");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.d(TAG, "Let's sniff the network");


            try {
                Context context = mContextRef.get();

                if (context != null) {

                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                    WifiInfo connectionInfo = wm.getConnectionInfo();
                    int ipAddress = connectionInfo.getIpAddress();
                    String ipString = Formatter.formatIpAddress(ipAddress);


                    Log.d(TAG, "activeNetwork: " + String.valueOf(activeNetwork));
                    Log.d(TAG, "ipString: " + String.valueOf(ipString));

                    String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);
                    Log.d(TAG, "prefix: " + prefix);

                    for (int i = 1; i < 250; i++) {

                        String testIp = prefix + String.valueOf(i);
                        Log.d(TAG, "testip: " + testIp);
                        pingFoxIP = InetAddress.getByName(testIp);
                        boolean reachable = pingFoxIP.isReachable(150);
                        //Log.d(TAG, "testip: " + testIp + "isreachable- "+reachable);
                        String hostName = pingFoxIP.getCanonicalHostName();
                        Log.d(TAG, "testip: " + testIp + "isreachable- " + reachable + ", host-" + hostName);

                        if (reachable) {
                            Log.i(TAG, "Host: " + String.valueOf(hostName) + "(" + String.valueOf(testIp) + ") is reachable!");
                            //activeIPlist.add(address);1
                            //Boolean pingfoxIP = null;
                            Boolean pingfoxIP = checkIfpingFox(pingFoxIP, context);
                            Log.i("booleann", pingfoxIP.toString());
                            if (pingfoxIP == true) {
                                Log.i("pingfoxIP", testIp);
                                bPingFoxConnected = true;
                                return bPingFoxConnected;
                            }
                        }

                    }
                }
            } catch (Throwable t) {
                Log.e(TAG, "Well that's not good....", t);
                progDailog.dismiss();
                bPingFoxConnected = false;
                return bPingFoxConnected;


            }

            return bPingFoxConnected;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progDailog.dismiss();
            if (aBoolean) {
                //Toast.makeText(getContext(),"Found pingfox device on network",Toast.LENGTH_SHORT).show();
                String pingFoxDeviceName = getPingFoxDeviceName(deviceName);
                //set a nique devicename for a unique topic
                setUniqueDeviceName(pingFoxDeviceName);

                Toast.makeText(noClueContext, pingFoxDeviceName + " detected", Toast.LENGTH_SHORT).show();
                //Configure the module
                FragmentModuleSetup.sendConfigrationDetailsToModule task = new FragmentModuleSetup.sendConfigrationDetailsToModule();
                task.execute();


            } else {
                Toast.makeText(getContext(), "No pingfox device found on network", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void setUniqueDeviceName(String pingFoxDeviceName) {
        //need to develope a method which will giv the device a unique name, to avoid conflict of topic name
        uniqueDeviceName = pingFoxDeviceName;

    }

    private Boolean checkIfpingFox(InetAddress address, Context context) {
        Boolean pingfoxIp = false;


        try {
            URL url = new URL("http:/" + address + "/cm?cmnd=module");
            Log.i("URL", String.valueOf(url));
            //URL url = new URL("http://192.168.0.105/cm?cmnd=power%20toggle");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(100);
            conn.setRequestMethod("GET");
            int responseCode;

            conn.setDoOutput(true);

            //conn.setRequestProperty("Content-Type", "application/json");
            //int responseCode  = conn.getResponseCode();
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            String body = new String();
            writer.write(body);
            //Sending the data to the server - This much is enough to send data to server
            //But to read the response of the server, you will have to implement the procedure below
            writer.flush();
            Log.i("custom_check", body);
            responseCode = conn.getResponseCode();

            Log.i("response_code", Integer.toString(responseCode));


            if (responseCode >= 200 && responseCode < 400) {
                // Create an InputStream in order to extract the response object
                //InputStream is = conn.getInputStream();
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                pingfoxIp = true;
                //userLocalStore.SetUserLoggedIn(true);
                String line;

                while ((line = reader.readLine()) != null) {
                    //Read till there is something available
                    sb.append(line + "\n");     //Reading and saving line by line - not all at once
                }
                line = sb.toString();           //Saving complete data received in string, you can do it differently
                String jsonString = line.split("=")[1];
                Log.i("jsonString", jsonString);
                JSONObject jsonObject = new JSONObject(jsonString);
                //Just check to the values received in Logcat
                Log.i("custom_check", "The values received in the store part are as follows:");
                Log.i("rever", line);
                Log.i("response_code", Integer.toString(responseCode));
                deviceName = jsonObject.getString("Module");


            } else {
                pingfoxIp = false;
                //InputStream is = conn.getErrorStream();
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    //Read till there is something available
                    sb.append(line + "\n");     //Reading and saving line by line - not all at once
                }
                line = sb.toString();
                //Saving complete data received in string, you can do it differently
                JSONObject jsonObject = new JSONObject(line);
                //jsonErrorArray = jsonObject.getJSONArray("errors");
                //JSONObject jsonErrorObject = jsonErrorArray.getJSONObject(0);
                //errorMessage = jsonObject.getString("message");
                //errorCodeString = jsonObject.getString("code");

                //Just check to the values received in Logcat
                //Toast.makeText(Register.this, "there is some error", Toast.LENGTH_SHORT).show();


                Log.i("custom_check", "The values received in the store part are as follows:");
                Log.i("custom_check", line);
                //Log.i("custom_check", errorMessage);
                Log.i("Response_Code", Integer.toString(responseCode));
                return false;

            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return pingfoxIp;


    }

    class sendConfigrationDetailsToModule extends AsyncTask<Void, Void, Void> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = ProgressDialog.show(getContext(), "Configuring", "please wait");
            progDailog.setMessage("Saving your settings on the device");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //Getting the required MqttDetails
            String host = "m11.cloudmqtt.com";
            int port = 15121;
            String client = "Android"; //DVES_%06X
            String userName = "rdqlgagy";
            String password = "V4-dlT_EKFEe";

            //device login password will be a random number for security reasons
            int pingFoxPassword = new Random().nextInt(100000) + 10000;
            Log.i("randomPassword", String.valueOf(pingFoxPassword));

            //String topic = "emailId/Room/device";
            String room = sharedPreferences.getString("AddDeviceInRoomID", "no room seected");
            String email = sharedPreferences.getString("LoggedInUserEmail", "no email recieved");
            String topic = uniqueDeviceName;
            fullTopic = email + "/" + room + "/" + uniqueDeviceName;
            topic = topic.replace(" ", "_");
            fullTopic = fullTopic.replace(" ", "_");
            Log.i("topic", topic);
            Log.i("fullTopic", fullTopic);

            try {
                URL url = new URL("http:/" + pingFoxIP + "/cm?cmnd=FullTopic%20" + fullTopic);
                Log.i("URL", String.valueOf(url));
                //URL url = new URL("http://192.168.0.105/cm?cmnd=power%20toggle");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(100);
                conn.setRequestMethod("GET");
                int responseCode;

                conn.setDoOutput(true);

                //conn.setRequestProperty("Content-Type", "application/json");
                //int responseCode  = conn.getResponseCode();
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                String body = new String();
                writer.write(body);
                //Sending the data to the server - This much is enough to send data to server
                //But to read the response of the server, you will have to implement the procedure below
                writer.flush();
                Log.i("custom_check", body);
                responseCode = conn.getResponseCode();

                Log.i("response_code", Integer.toString(responseCode));


                // Create an InputStream in order to extract the response object
                //InputStream is = conn.getInputStream();
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    //Read till there is something available
                    sb.append(line + "\n");     //Reading and saving line by line - not all at once
                }
                line = sb.toString();           //Saving complete data received in string, you can do it differently
                String jsonString = line.split("=")[1];
                Log.i("jsonString", jsonString);
                JSONObject jsonObject = new JSONObject(jsonString);
                //Just check to the values received in Logcat
                Log.i("custom_check", "The values received in the store part are as follows:");
                Log.i("rever", line);
                Log.i("response_code", Integer.toString(responseCode));
                String revertFullTopic = jsonObject.getString("FullTopic");
                Log.i("revertFullTopic", revertFullTopic);


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Setting up password for te devicem so tat it cannot be scanned again
            /*
            try {
                URL url = new URL("http:/" + pingFoxIP + "/cm?cmnd=WebPassword%20123456");
                Log.i("URL", String.valueOf(url));
                //URL url = new URL("http://192.168.0.105/cm?cmnd=power%20toggle");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(100);
                conn.setRequestMethod("GET");
                int responseCode;

                conn.setDoOutput(true);

                //conn.setRequestProperty("Content-Type", "application/json");
                //int responseCode  = conn.getResponseCode();
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                String body = new String();
                writer.write(body);
                //Sending the data to the server - This much is enough to send data to server
                //But to read the response of the server, you will have to implement the procedure below
                writer.flush();
                Log.i("custom_check", body);
                responseCode = conn.getResponseCode();

                Log.i("response_code", Integer.toString(responseCode));


                // Create an InputStream in order to extract the response object
                //InputStream is = conn.getInputStream();
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    //Read till there is something available
                    sb.append(line + "\n");     //Reading and saving line by line - not all at once
                }
                line = sb.toString();           //Saving complete data received in string, you can do it differently
                String jsonString = line.split("=")[1];
                Log.i("jsonString", jsonString);
                JSONObject jsonObject = new JSONObject(jsonString);
                //Just check to the values received in Logcat
                Log.i("custom_check", "The values received in the store part are as follows:");
                Log.i("revert", line);
                Log.i("response_code", Integer.toString(responseCode));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            //get power state of the relays
            try {
                URL url = new URL("http:/" + pingFoxIP + "/cm?user=admin&password=123456&cmnd=status%2011");
                Log.i("URL", String.valueOf(url));
                //URL url = new URL("http://192.168.0.105/cm?cmnd=power%20toggle");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(300);
                conn.setRequestMethod("GET");
                int responseCode;

                conn.setDoOutput(true);

                //conn.setRequestProperty("Content-Type", "application/json");
                //int responseCode  = conn.getResponseCode();
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                String body = new String();
                writer.write(body);
                //Sending the data to the server - This much is enough to send data to server
                //But to read the response of the server, you will have to implement the procedure below
                writer.flush();
                Log.i("custom_check", body);
                responseCode = conn.getResponseCode();

                Log.i("response_code", Integer.toString(responseCode));


                // Create an InputStream in order to extract the response object
                //InputStream is = conn.getInputStream();
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    //Read till there is something available
                    sb.append(line + "\n");     //Reading and saving line by line - not all at once
                }
                line = sb.toString();           //Saving complete data received in string, you can do it differently
                String jsonString = line.split("=")[1];
                Log.i("jsonString", jsonString);
                JSONObject jsonObject1 = new JSONObject(jsonString);
                //Just check to the values received in Logcat
                Log.i("custom_check", "The values received in the store part are as follows:");
                Log.i("revert", line);
                Log.i("response_code", Integer.toString(responseCode));
                JSONObject jsonObject2 = jsonObject1.getJSONObject("StatusSTS");

                for (int i = 0; i < numberRelays; i++) {
                    int k = i + 1;
                    String powerState = jsonObject2.getString("POWER" + k);
                    if (powerState.equals("ON")) {
                        relayArrayList.add(new Relay("Device " + k, true, k,fullTopic));
                    } else {
                        relayArrayList.add(new Relay("Device " + k, false, k,fullTopic));
                    }
                    Log.i("PowerState" + k, powerState);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progDailog.dismiss();
            Log.i("ArraySize", String.valueOf(relayArrayList.size()));
            adapter = new AdapterModuleSetup(relayArrayList,getContext(),pingFoxIP);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemViewCacheSize(relayArrayList.size());
            recyclerView.setHasFixedSize(true);
        }
    }
}
