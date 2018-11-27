package com.example.mohit.finalpingfoxlenovo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdapterModuleSetup extends RecyclerView.Adapter<AdapterModuleSetup.ViewHolder> {

    private ArrayList<Relay> relayList;
    private Context context;
    private InetAddress pingfoxIP;

    public AdapterModuleSetup(ArrayList<Relay> relayList, Context context, InetAddress inetAddress) {

        this.relayList = relayList;
        this.context = context;
        this.pingfoxIP = inetAddress;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
       View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_configre_channels,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        Relay relay = relayList.get(position);
        viewHolder.deviceName.setHint(relay.getRelayName());
        Log.i("relayName",relay.getRelayName());
        Boolean relayOn = relay.getRelayOn();
        if (relayOn){
            viewHolder.pingText.setTextColor(Color.parseColor("#53FF6D"));
        }
        else {
            viewHolder.pingText.setTextColor(Color.parseColor("#272727"));
        }

        viewHolder.pingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toggle the relay and change text color, if possible animate pressing animation
                //1. Toggle the relay
                AdapterModuleSetup.TogglePower task = new AdapterModuleSetup.TogglePower(position,viewHolder);
                task.execute();

            }
        });

    }
    private class TogglePower extends AsyncTask<Void, Void, Void> {

        int position;
        ViewHolder viewHolder;

        public TogglePower(int position, ViewHolder viewHolder){
            this.position = position;
            this.viewHolder = viewHolder;
        }


        @Override
        protected Void doInBackground(Void... voids) {

            int k = position+1;
            try {
                URL url = new URL("http:/" + pingfoxIP + "/cm?user=admin&password=123456&cmnd=power"+k+"%20toggle");
                Log.i("URL", String.valueOf(url));


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
                JSONObject jsonObject = new JSONObject(jsonString);
                String powerStatus = String.valueOf(jsonObject.get("POWER"+k));
                powerStatus.replaceAll("\\s+","");
                Log.i("powerStatus", powerStatus);
                if (powerStatus.equals("ON")){
                    Relay relay = relayList.get(position);
                    Log.i("RelayOn", "True");
                    relay.setRelayOn(true);
                    //relay.setRelayName("bulb");
                }
                if (powerStatus.equals("OFF")){
                    Log.i("RelayOn", "False");
                    Relay relay = relayList.get(position);
                    relay.setRelayOn(false);
                    //relay.setRelayName("bulb");
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
            notifyDataSetChanged();

        }
    }

    @Override
    public int getItemCount() {
        return relayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public EditText deviceName;
        public TextView pingText;
        public CardView pingButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            deviceName = (EditText) itemView.findViewById(R.id.DeviceNameET);
            pingText = (TextView) itemView.findViewById(R.id.pingButtonTV);
            pingButton = (CardView) itemView.findViewById(R.id.pingButtonCV);
        }
    }


}
