package com.example.mohit.finalpingfoxlenovo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mohit.finalpingfoxlenovo.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttClient;

public class FragmentDeviceControl extends Fragment {
    View myView;
    String clientId = MqttClient.generateClientId();
    MqttAndroidClient client ;
    String SdeviceStatus;
    Boolean BdeviceOn ;
    ImageView deviceStatusImage ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("control_devices","Oncreateview metho0d is hit");
        inflater.inflate(R.layout.fragment_control_devices, container, false);
        return myView;
    }
}
