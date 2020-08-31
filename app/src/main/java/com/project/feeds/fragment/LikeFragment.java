package com.project.feeds.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.feeds.adapter.NotificationAdapter;
import com.project.feeds.R;
import com.project.feeds.entity.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LikeFragment extends Fragment {
    RecyclerView recyclerView;
    NotificationAdapter notificationAdapter;
    List<Notification> notificationList;

    public LikeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_like, container, false);
        recyclerView = v.findViewById(R.id.rv_notif);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(),notificationList);
        recyclerView.setAdapter(notificationAdapter);

        readNotif();
        return v;
    }

    public void readNotif(){
        FirebaseFirestore.getInstance().collection("notification")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert  value != null;
                        notificationList.clear();
                        for (QueryDocumentSnapshot snapshot : value){
                            Notification notification = snapshot.toObject(Notification.class);
                            notificationList.add(notification);
                        }
                        Collections.reverse(notificationList);
                        notificationAdapter.notifyDataSetChanged();
                    }
                });
    }
}