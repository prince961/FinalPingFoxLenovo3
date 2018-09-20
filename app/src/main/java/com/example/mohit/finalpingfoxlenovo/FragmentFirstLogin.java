package com.example.mohit.finalpingfoxlenovo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FragmentFirstLogin extends Fragment {
    View myView;
    private String phoneNumber;
    private String Address;
    private Long BHK;
    private EditText ETphoneNumber, ETAddress;
    private CheckBox locationAccess;
    private Spinner ShouseConfig;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Boolean mLocationPermissionGranted;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    FirebaseDatabase database;
    Location currentLocation;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_first_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        ETphoneNumber = (EditText) myView.findViewById(R.id.Etphone);
        ETAddress = (EditText) myView.findViewById(R.id.EtAddress);
        locationAccess = (CheckBox) myView.findViewById(R.id.locationRadioButton);
        ShouseConfig = (Spinner) myView.findViewById(R.id.houseConfigSpinner);
        Button submitData = (Button) myView.findViewById(R.id.BSubmitUserInfo);

        locationAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("CheckBox ", "pressed1");
                FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
                //getLocationPermission();
                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.i("Location", "permission not granted");
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Log.i("Explanation Location", "permission not granted");
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
                                currentLocation = (Location) task.getResult();
                                Log.d("latitude", String.valueOf(currentLocation.getLatitude()));
                                Log.d("Longitude", String.valueOf(currentLocation.getLongitude()));

                            } else {
                                Log.d("location task", "failed");
                            }
                        }
                    });
                }

            }
        });

        submitData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = ETphoneNumber.getText().toString();
                Address = ETAddress.getText().toString();
                BHK = ShouseConfig.getSelectedItemId();
                FragmentManager fragmentManager = getFragmentManager();

                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                User localUser = new User(firebaseUser.getDisplayName(),firebaseUser.getEmail());
                localUser.setAddress(Address);
                localUser.setPhone(phoneNumber);
                localUser.setLatitude(currentLocation.getLatitude());
                localUser.setLongitude(currentLocation.getLongitude());
                localUser.setBhk(BHK);
                databaseReference.child("users").child(firebaseUser.getUid()).setValue(localUser);
                Log.i("Submit Data", "swtarting fragment to control device");
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentDeviceControl()).commit();
                //Intent intent = new Intent(getContext(), AddDeviceInRoom.class);
                //startActivity(intent);
            }
        });

        return myView;
    }

    //Permission Request Handler
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //this method is useless, this is just here to satisfy the error by the getLastLocation method
                        return;
                    }
                    Task location = fusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Log.d("location task", "successful");
                                currentLocation = (Location) task.getResult();
                                Log.d("latitude", String.valueOf(currentLocation.getLatitude()));
                                Log.d("Longitude", String.valueOf(currentLocation.getLongitude()));

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



    public void SubmitUserInfo(View view) {
        Log.i("LineStatus","SubmitUSerinfo Method in Fragment first login is hit");
        Boolean userInfoCorrectformat = checkUserInfo();
        if(userInfoCorrectformat){
            SubmitUserInfo();
        }
        else{
            Toast.makeText(getContext(),"Please fill in correct Details",Toast.LENGTH_LONG).show();
        }
    }

    private void SubmitUserInfo() {
        phoneNumber = ETphoneNumber.getText().toString();
        Address = ETAddress.getText().toString();
        BHK = ShouseConfig.getSelectedItemId();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        User localUser = new User(firebaseUser.getDisplayName(),firebaseUser.getEmail());
        localUser.setAddress(Address);
        localUser.setPhone(phoneNumber);
        localUser.setLatitude(currentLocation.getLatitude());
        localUser.setLongitude(currentLocation.getLongitude());
        localUser.setBhk(BHK);
        databaseReference.child("users").child(firebaseUser.getUid()).setValue(localUser);
        Intent intent = new Intent(getContext(), Main2Activity.class);
        startActivity(intent);


    }

    private Boolean checkUserInfo() {
        return true;
    }




}


