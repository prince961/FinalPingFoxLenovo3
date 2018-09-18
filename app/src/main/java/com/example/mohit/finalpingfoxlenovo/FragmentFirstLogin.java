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
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FragmentFirstLogin extends Fragment {
    View myView;
    private String phoneNumber;
    private String Address ;
    private Long BHK ;
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

        myView = inflater.inflate(R.layout.fragment_first_login,container,false);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        ETphoneNumber = (EditText) myView.findViewById(R.id.Etphone);
        ETAddress = (EditText) myView.findViewById(R.id.EtAddress);
        locationAccess = (CheckBox) myView.findViewById(R.id.locationRadioButton);
        ShouseConfig = (Spinner) myView.findViewById(R.id.houseConfigSpinner);
        locationAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("radio button ", "pressed");
                //getLocationPermission();
                if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    Log.i("Location","permission not granted");
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Log.i("Explanation Location","permission not granted");
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                        Log.i("LocationNoExplanation","permission not granted");

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }




                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());

            }
        });

        return myView;
    }

    public void locationAccess(View view) {
        Log.d("radio button ", "pressed");
        //getLocationPermission();
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Log.i("Location","permission not granted");
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.i("Explanation Location","permission not granted");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                Log.i("LocationNoExplanation","permission not granted");

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }




        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());
     /*   if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
           return;
        }*/
    }







    public void SubmitUserInfo(View view) {
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


