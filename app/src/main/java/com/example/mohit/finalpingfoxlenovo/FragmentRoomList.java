package com.example.mohit.finalpingfoxlenovo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FragmentRoomList extends Fragment {

    private RecyclerView mRecyclerView;
    private RoomListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Room> roomArrayList;

    View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_add_device_in_room,container,false);
        FragmentManager fragmentManager = getFragmentManager();
        final Controller controller = (Controller) getActivity().getApplicationContext();



        mRecyclerView = (RecyclerView) mView.findViewById(R.id.RVRoomList);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);



        roomArrayList = controller.getUser().getRoomsArray();
        Log.i("InitiatedController", String.valueOf(controller.getRoomList().size()));



        mAdapter = new RoomListAdapter(roomArrayList,getContext(),fragmentManager);
        mRecyclerView.setAdapter(mAdapter);

        return mView;
    }
}
