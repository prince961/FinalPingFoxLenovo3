package com.example.mohit.finalpingfoxlenovo;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RoomListAdapter extends RecyclerView.Adapter <RoomListAdapter.ViewHolder> {

    private ArrayList<Room> roomList ;
    private Context context;
    private FragmentManager fragmentManager ;
    SharedPreferences sharedPreferences ;

    public  RoomListAdapter (ArrayList<Room> CroomList, Context context, FragmentManager fragmentManager){
        this.roomList = CroomList;
        this.context = context;
        this.fragmentManager = fragmentManager;
        sharedPreferences = context.getSharedPreferences("UserSP", Context.MODE_PRIVATE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView heading ;
        public LinearLayout baseLayout;
        // each data item is just a string in this case
        public ViewHolder(View v) {
            super(v);
            heading = (TextView) v.findViewById(R.id.RoomNameTV);
            baseLayout = (LinearLayout) v.findViewById(R.id.RVroomListItemBaseLayout);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_list_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {


        holder.heading.setText( roomList.get(position).getName());
        holder.baseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor spEditor = sharedPreferences.edit();
                spEditor.putString("AddDeviceInRoom",roomList.get(position).getName());
                spEditor.apply();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentAddDeviceInRoom()).commit();
                Toast.makeText(context, "you have selcted"+roomList.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }
}
