package com.project.feeds;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.project.feeds.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    private UserAdapter userAdapter;
    private List<User> userList;
    private EditText etSearch;
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.rv_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        etSearch = view.findViewById(R.id.et_search);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this.getContext(),userList);
        recyclerView.setAdapter(userAdapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchAcc(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        readUser();

        return view;
    }

    public void searchAcc(String key){
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("users");
        collectionReference.whereEqualTo("userName", key).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        userList.clear();
                        for (QueryDocumentSnapshot snapshots : Objects.requireNonNull(task.getResult())){
                            Log.d(TAG,"onEvent : "+snapshots);
                            User user = snapshots.toObject(User.class);
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void readUser(){
        FirebaseFirestore.getInstance().collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (etSearch.getText().toString().equals("")){
                            assert value !=null;
                            for (DocumentSnapshot snapshot : value){
                                User user = snapshot.toObject(User.class);
                                userList.add(user);
                                Toast.makeText(getActivity(), "Data Berhasil Masuk", Toast.LENGTH_SHORT).show();
                            }
                            userAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}