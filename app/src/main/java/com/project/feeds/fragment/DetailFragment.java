package com.project.feeds.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.project.feeds.R;
import com.project.feeds.activity.CommentActivity;
import com.project.feeds.activity.MainActivity;
import com.project.feeds.adapter.PostAdapter;
import com.project.feeds.entity.Post;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {
    RecyclerView recyclerView;
    PostAdapter adapter;
    List<Post> postList;
    String uploadId;
    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        SharedPreferences preferences = getContext().getSharedPreferences(MainActivity.DATA, Context.MODE_PRIVATE);
        uploadId = preferences.getString(CommentActivity.ID_POST,"none");
        recyclerView = v.findViewById(R.id.rv_post_detail);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        adapter = new PostAdapter(getContext(),postList);
        recyclerView.setAdapter(adapter);

        readData();
        return v;
    }

    public void readData(){
        DocumentReference reference = FirebaseFirestore.getInstance().collection("photos").document(uploadId);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                postList.clear();
                Post post = value.toObject(Post.class);
                postList.add(post);
                adapter.notifyDataSetChanged();
            }
        });
    }
}