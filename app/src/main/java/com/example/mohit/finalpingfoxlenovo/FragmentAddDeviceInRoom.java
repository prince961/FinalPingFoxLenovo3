package com.example.mohit.finalpingfoxlenovo;

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
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.util.Random;

public class FragmentAddDeviceInRoom extends Fragment {

    View myView;
    private Boolean bPingFoxConnected = false;
    ProgressDialog progDailog ;
    InetAddress pingFoxIP;
    Button toggleButton;
    ImageView deviceStatusImage;
    String deviceName = "abc";
    String uniqueDeviceName ;
    private ArrayList<String> AlreadyAddedMacAddress;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_control_devices, container, false);
        deviceStatusImage =(ImageView) myView.findViewById(R.id.DeviceStatusImage);
        FragmentAddDeviceInRoom.NetworkSniffTask task = new FragmentAddDeviceInRoom.NetworkSniffTask(getContext());
        task.execute();
        sharedPreferences = getContext().getSharedPreferences("UserSP", Context.MODE_PRIVATE);
        return myView;
    }

    private String getPingFoxDeviceName(String s) {
        String pingFoxDeviceName = null;
        if (s.equals("7 (Sonoff 4CH)")){
            pingFoxDeviceName = "Pingfox quatro";
        }
        return pingFoxDeviceName;
    }


    class NetworkSniffTask extends AsyncTask<Void, Void, Boolean> {

        private static final String TAG = SyncStateContract.Constants.DATA + "nstask";
        private WeakReference<Context> mContextRef;
        private ArrayList<InetAddress> activeIPlist;
        Context noClueContext ;

        public NetworkSniffTask(Context context) {
            mContextRef = new WeakReference<Context>(context);
            noClueContext = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = ProgressDialog.show(getContext(),"Scanning","please wait");
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

                    for (int i = 100; i < 250; i++) {

                        String testIp = prefix + String.valueOf(i);
                        Log.d(TAG, "testip: " + testIp);
                        pingFoxIP = InetAddress.getByName(testIp);
                        boolean reachable = pingFoxIP.isReachable(150);
                        //Log.d(TAG, "testip: " + testIp + "isreachable- "+reachable);
                        String hostName = pingFoxIP.getCanonicalHostName();
                        Log.d(TAG, "testip: " + testIp + "isreachable- " + reachable + ", host-" + hostName);

                        if (reachable){
                            Log.i(TAG, "Host: " + String.valueOf(hostName) + "(" + String.valueOf(testIp) + ") is reachable!");
                            //activeIPlist.add(address);1
                            //Boolean pingfoxIP = null;
                            Boolean pingfoxIP = checkIfpingFox(pingFoxIP, context);
                            Log.i("booleann",pingfoxIP.toString());
                            if (pingfoxIP == true){
                                Log.i("pingfoxIP",testIp);
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
            if (aBoolean){
                //Toast.makeText(getContext(),"Found pingfox device on network",Toast.LENGTH_SHORT).show();
                String pingFoxDeviceName = getPingFoxDeviceName(deviceName);
                //set a nique devicename for a unique topic
                setUniqueDeviceName(pingFoxDeviceName);

                Toast.makeText(noClueContext, pingFoxDeviceName+" detected", Toast.LENGTH_SHORT).show();
                //Configure the module
                FragmentAddDeviceInRoom.sendConfigrationDetailsToModule task = new FragmentAddDeviceInRoom.sendConfigrationDetailsToModule();
                task.execute();


            }else {
                Toast.makeText(getContext(),"No pingfox device found on network",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void setUniqueDeviceName(String pingFoxDeviceName) {
        //need to develope a method which will giv the device a unique name, to avoid conflict of topic name
        uniqueDeviceName = pingFoxDeviceName;

    }

    private Boolean checkIfpingFox(InetAddress address, Context context) {
        Boolean pingfoxIp = false ;



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
                Log.i("jsonString",jsonString);
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
        }
        return pingfoxIp;


    }

    class sendConfigrationDetailsToModule extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = ProgressDialog.show(getContext(),"Configuring","please wait");
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
            int pingFoxPassword = new Random().nextInt(100000)+ 10000;
            Log.i("randomPassword", String.valueOf(pingFoxPassword));

            //String topic = "emailId/Room/device";
            String room = sharedPreferences.getString("AddDeviceInRoom","no room seected");
            String email = sharedPreferences.getString("LoggedInUserEmail","no email recieved");
            String topic = uniqueDeviceName;
            String fullTopic = email+"/"+room+"/%25topic%25/";
            topic = topic.replace(" ","_");
            fullTopic = fullTopic.replace(" ","_");
            Log.i("topic",topic);
            Log.i("fullTopic",fullTopic);

            try {
                URL url = new URL("http:/" + pingFoxIP + "/cm?cmnd=FullTopic%20"+fullTopic);
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
                    String jsonString = line.split("=")[2];
                    Log.i("jsonString",jsonString);
                    JSONObject jsonObject = new JSONObject(jsonString);
                    //Just check to the values received in Logcat
                    Log.i("custom_check", "The values received in the store part are as follows:");
                    Log.i("rever", line);
                    Log.i("response_code", Integer.toString(responseCode));
                    String revertFullTopic = jsonObject.getString("FullTopic");
                    Log.i("revertFullTopic",revertFullTopic);



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
        }

    }
}
