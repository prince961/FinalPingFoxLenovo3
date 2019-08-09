package com.example.mohit.finalpingfoxlenovo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterAllDevices extends RecyclerView.Adapter<AdapterAllDevices.ViewHolder>   {

    private ArrayList<Room> roomList ;
    private Context context;
    private FragmentManager fragmentManager ;
    SharedPreferences sharedPreferences ;

    public  AdapterAllDevices (ArrayList<Room> CroomList, Context context, FragmentManager fragmentManager){
        this.roomList = CroomList;
        this.context = context;
        this.fragmentManager = fragmentManager;
        sharedPreferences = context.getSharedPreferences("UserSP", Context.MODE_PRIVATE);
    }



        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public TextView deviceName ;
            public TextView roomName;
            public Switch deviceSwitch;
            public ImageView timerImage;
            public ImageView connectionImage;


            public LinearLayout baseLayout;

            public ViewHolder(View v) {
                super(v);
                deviceName = (TextView) v.findViewById(R.id.DeviceName);
                roomName = (TextView) v.findViewById(R.id.RoomName);
                deviceSwitch = (Switch) v.findViewById(R.id.DeviceSwitch);
                timerImage = (ImageView) v.findViewById(R.id.timerImg);
                connectionImage = (ImageView) v.findViewById(R.id.ConnectionImg);
                baseLayout = (LinearLayout) v.findViewById(R.id.listItemAllDevicesBaseLayout);

            }
        }

}
