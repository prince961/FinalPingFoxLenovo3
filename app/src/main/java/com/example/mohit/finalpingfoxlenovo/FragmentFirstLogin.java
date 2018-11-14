package com.example.mohit.finalpingfoxlenovo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class FragmentFirstLogin extends Fragment {
    View myView;
    private String phoneNumber;
    private String Address;
    private Long BHK;
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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
                BHK = ShouseConfig.getSelectedItemId();
                FragmentManager fragmentManager = getFragmentManager();

                firebaseUser = mAuth.getCurrentUser();
                User localUser = new User(firebaseUser.getDisplayName(),firebaseUser.getEmail());
                localUser.setAddress(Address);
                localUser.setPhone(phoneNumber);
                localUser.setLatitude(Double.parseDouble(sharedPreferences.getString("FirstLatitude","1")));
                localUser.setLongitude(Double.parseDouble(sharedPreferences.getString("FirstLongitude","1")));
                localUser.setBhk(BHK);
                databaseReference.child("users").child(firebaseUser.getUid()).setValue(localUser);
                Log.i("Submit Data", "swtarting fragment to control device");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("NewUser",false);
                editor.apply();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentDeviceControl()).commit();
                //Intent intent = new Intent(getContext(), AddDeviceInRoom.class);
                //startActivity(intent);
            }
        });

        return myView;
    }









}


