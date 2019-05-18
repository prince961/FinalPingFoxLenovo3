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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    MqttAndroidClient client ;
    String SdeviceStatus;
    Boolean BdeviceOn ;
    ImageView deviceStatusImage ;
    Button toggleButton;
    SharedPreferences sharedPreferences ;
    FragmentManager fragmentManager = getSupportFragmentManager();
    GoogleSignInClient mGoogleSignInClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getApplicationContext().getSharedPreferences("UserSP", Context.MODE_PRIVATE);
        controller = (Controller) getApplicationContext();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "new devices can be added by pressing this button", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentRoomList()).commit();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Boolean UserLoggedIn = sharedPreferences.getBoolean("UserLoggedIn",false);
        Log.i("UsernameMainActivity",sharedPreferences.getString("UserName","xyz"));

        if (UserLoggedIn){
            //Boolean newUser = true;

            Boolean newUser = sharedPreferences.getBoolean("NewUser",false);
            if (newUser) {
                //Check permission
                checkLocationPermission();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentFirstLogin()).commit();
                Log.i("oncreated", "starting first login fragment");
            }else {
                //connectWithCloudMQTT();`
                //oldUser
                initialiseController(sharedPreferences.getString("LoggedInUserEmailId","a"));
                Log.i("oncreated", "starting fragment to control device");
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentHomeScreen()).commit();
            }
        }else {
            Log.i("LoggedInUser", String.valueOf(UserLoggedIn));
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
    }

    private void initialiseController(String email) {
        getUserDataFromMogDB task = new getUserDataFromMogDB(email,controller);
        task.execute();

    }

    public class getUserDataFromMogDB extends AsyncTask<Void, Void, Void> {

        String userEmail;
        Controller controller;
        public getUserDataFromMogDB(String email,Controller controller) {
            this.userEmail = email;
            this.controller = controller;
            Log.i("UserEmail",userEmail);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                //URL url = new URL("http://dev.pingfox.in:4000/api/ninjas/"+userEmail);
                URL url = new URL("http://18.221.190.166:4000/api/ninjas/prince.prince961@gmail.com");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                //conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                //conn.setRequestProperty("aftership-api-key", ApiKey);
                conn.connect();


                //OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                //writer.write(body);
                //writer.flush();
                int responseCode = conn.getResponseCode();
                Log.i("responseCode", String.valueOf(responseCode));
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    //Read till there is something available
                    sb.append(line + "\n");     //Reading and saving line by line - not all at once
                }
                line = sb.toString();
                Log.i("UserJSONresponse",line);

                JsonParser userFromJson = new JsonParser();
                User retrivedUser = userFromJson.getUserFromJSON(line);
                controller.setUser(retrivedUser);

                String retrivedUserTest = retrivedUser.getRoomsArray().get(0).getName();


                Log.i("userTestBoolean",retrivedUserTest);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }



    private void checkLocationPermission() {
       fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //getLocationPermission();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Location", "permission not granted");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.i("Explanation Location", "permission not granted");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                Log.i("LocationNoExplanation", "permission not granted");

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Log.i("Location", "permission already granted");
            Task location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Log.d("location task", "successful");
                        Location currentLocation = (Location) task.getResult();
                        Log.d("latitude", String.valueOf(currentLocation.getLatitude()));
                        Log.d("Longitude", String.valueOf(currentLocation.getLongitude()));
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("FirstLatitude",String.valueOf(currentLocation.getLatitude()));
                        editor.putString("FirstLongitude",String.valueOf(currentLocation.getLongitude()));
                        editor.apply();
                    } else {
                        Log.d("location task", "failed");
                    }
                }
            });
        }
    }

    //Permission Request Handler
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        Log.i("permissions", "granted1");
        switch (requestCode) {

            case 1: {
                // If request is cancelled, the result arrays are empty.
                Log.i("permissions", "granted");

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("permissions", "granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //this method is useless, this is just here to satisfy the error by the getLastLocation method
                        return;
                    }
                    Task location = fusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Log.d("location task", "successful");
                                Location currentLocation = (Location) task.getResult();
                                Log.d("latitude", String.valueOf(currentLocation.getLatitude()));
                                Log.d("Longitude", String.valueOf(currentLocation.getLongitude()));
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("FirstLatitude",String.valueOf(currentLocation.getLatitude()));
                                editor.putString("FirstLongitude",String.valueOf(currentLocation.getLongitude()));
                                editor.apply();

                            } else {
                                Log.d("location task", "failed");
                            }
                        }
                    });
                } else {
                    Log.d("location task", "failed");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    private void connectWithCloudMQTT() {
        String clientId = MqttClient.generateClientId();

        client = new MqttAndroidClient(this,"tcp://m11.cloudmqtt.com:15121",
                clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("rdqlgagy");
        options.setPassword("V4-dlT_EKFEe".toCharArray());

        try {

            IMqttToken token = client.connect(options);
            Log.i("MainActivity","trying to establish connection");
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("MainActivity", "MMQTT Connection successful");
                    try {
                        subscribe(client,"stat/sonoff1/POWER",1);
                    } catch (MqttException e) {
                        Log.d("MainActivity", "MMQTT exception");
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems

                    Log.d("MainActivity", "Connection to Cloud mqtt failed");

                }
            });
        } catch (MqttException e) {
            Log.d("MainActivity", "not tried");

            e.printStackTrace();
        }
    }
    public void subscribe(@NonNull MqttAndroidClient client,
                          @NonNull final String topic, int qos) throws MqttException {
        IMqttToken token = client.subscribe(topic, qos);
        token.setActionCallback(new IMqttActionListener() {

            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                Log.d("TAG", "Subscribe Successfully " + topic);
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                Log.e("TAG", "Subscribe Failed " + topic);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.w("ButtonPressed", "Logout button pressed");
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
            FirebaseAuth.getInstance().signOut();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentFirstLogin()).commit();
            // Handle the camera action
        }else if (id == R.id.nav_offline) {
            //trial
            fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentyAllDevices()).commit();

        } else if (id == R.id.nav_online_mode) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentDeviceControl()).commit();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
