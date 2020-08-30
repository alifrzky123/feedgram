package com.project.feeds.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.feeds.adapter.PhotoAdapter;
import com.project.feeds.R;
import com.project.feeds.activity.SetUpProfile;
import com.project.feeds.activity.LoginActivity;
import com.project.feeds.activity.MainActivity;
import com.project.feeds.entity.Notification;
import com.project.feeds.entity.Post;
import com.project.feeds.entity.User;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProfileFragment extends Fragment {
    private ImageView ivPhotoProfile, ivLogOut;
    private TextView tvUname, tvBio, tvPost, tvFollowing, tvFollowers;
    private Button btnEditProfile;
    RecyclerView recyclerView;
    PhotoAdapter photoAdapter;
    List<Post> postList;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    GoogleSignInClient googleSignInClient;
    String profileId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences preferences = Objects.requireNonNull(getContext()).getSharedPreferences(MainActivity.DATA, Context.MODE_PRIVATE);
        profileId = preferences.getString(MainActivity.KEY,"none");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (profileId.equals(firebaseUser.getUid())){
            Toast.makeText(getContext(), profileId, Toast.LENGTH_SHORT).show();
           // btnEditProfile.setText("Set Your Profile");
        }else{
            checkFollow();
        }


        ivPhotoProfile = v.findViewById(R.id.iv_pro_pict);
        ivLogOut = v.findViewById(R.id.iv_log_out);
        tvUname = v.findViewById(R.id.tv_pro_uname);
        tvBio = v.findViewById(R.id.tv_bio_pro);
        tvPost = v.findViewById(R.id.tv_total_posts);
        tvFollowers = v.findViewById(R.id.tv_total_followers);
        tvFollowing = v.findViewById(R.id.tv_total_followings);
        btnEditProfile = v.findViewById(R.id.btn_update_profile);
        recyclerView = v.findViewById(R.id.rv_list_container);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(),postList);
        recyclerView.setAdapter(photoAdapter);


        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this.getContext(),googleSignInOptions);

        ivLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder alert = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                alert.setTitle("Log Out");
                alert.setCancelable(false);
                alert.setMessage("Are you sure to log out?");
                alert.setPositiveButton("logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logOut();
                        Toast.makeText(getContext(), "logout", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.create().show();
            }
        });
        getDataFollowers(profileId);
        getTotalPosts();
        getUserInfo(profileId);
        getPhotos();

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textButton = btnEditProfile.getText().toString();
                if (textButton.equals("Set Your Profile")) {
                    startActivity(new Intent(getContext(), SetUpProfile.class));
                } else if (textButton.equals("ikuti")) {
                    Map<String, Object> dataFollowing = new HashMap<>();
                    dataFollowing.put(profileId, true);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("follow").document(firebaseUser.getUid())
                            .collection("following").document(profileId).set(dataFollowing)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: " + task.getResult());
                                    } else {
                                        Log.d(TAG, "onComplete: " + Objects.requireNonNull(task.getException()).getMessage());
                                    }
                                }
                            });
                    Map<String, Object> dataFollower = new HashMap<>();
                    dataFollower.put(firebaseUser.getUid(), true);
                    db.collection("follow").document(profileId)
                            .collection("followers").document(firebaseUser.getUid()).set(dataFollowing)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: " + task.getResult());
                                    } else {
                                        Log.d(TAG, "onComplete: " + Objects.requireNonNull(task.getException()).getMessage());
                                    }
                                }
                            });
                    addNotif();
                } else if (textButton.equals("mengikuti")) {
                    Map<String, Object> dataFollowing = new HashMap<>();
                    dataFollowing.put(profileId, true);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("follow").document(firebaseUser.getUid())
                            .collection("following").document(profileId).delete();
                    Map<String, Object> dataFollower = new HashMap<>();
                    dataFollower.put(firebaseUser.getUid(), true);
                    db.collection("follow").document(profileId)
                            .collection("followers").document(firebaseUser.getUid()).delete();
                }
            }

        });
        return v;
    }


    public void logOut() {
        mAuth.signOut();
        googleSignInClient.signOut().addOnCompleteListener((Activity) this.getContext(),
                new OnCompleteListener< Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        if (user==null){
            Intent intent = new Intent(this.getContext(), LoginActivity.class);
            startActivity(intent);

        }
    }
    private void getUserInfo(String userId) {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("users")
                .document(userId);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (getContext() == null) {
                    return;
                }
                assert value != null;
                User userData = value.toObject(User.class);
                assert userData != null;
                if (userData!= null){

                    Glide.with(getContext()).load(userData.getPhotoUrl()).into(ivPhotoProfile);
                    tvUname.setText(userData.getUserName());
                    tvBio.setText(userData.getBio());

                }else{
                    Toast.makeText(getContext(), "Gagal Memuat Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkFollow() {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("follow")
                .document(firebaseUser.getUid()).collection("following");

        collectionReference.document(profileId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                if (value.exists()) {
                    btnEditProfile.setText("mengikuti");
                } else {
                    btnEditProfile.setText("ikuti");

                }
            }
        });
    }

    private void getDataFollowers(String userId) {
        CollectionReference collection1 = FirebaseFirestore.getInstance().collection("follow")
                .document(userId).collection("followers");

        collection1.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<String> jumlah = new ArrayList<>();
                assert value != null;
                for (DocumentSnapshot snapshot : value) {
                    jumlah.add(value.getDocumentChanges().toString());
                    Log.d(TAG, "onEvent: getDataFollowers" + snapshot);
                }

                tvFollowers.setText(jumlah.size()+ " Followers");
            }
        });

        CollectionReference collection2 = FirebaseFirestore.getInstance().collection("follow")
                .document(profileId).collection("following");

        collection2.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<String> jumlah = new ArrayList<>();
                assert value != null;
                for (DocumentSnapshot snapshot : value) {
                    jumlah.add(value.getDocumentChanges().toString());
                    Log.d(TAG, "onEvent: getDataFollowers" + snapshot);
                }

                tvFollowing.setText(jumlah.size() + " Following");
            }
        });
    }

    private void getTotalPosts() {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("photos");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int i = 0;
                assert value != null;
                for (DocumentSnapshot snapshot : value) {
                    Post post = snapshot.toObject(Post.class);
                    assert post != null;
                    if (post.getPostUploader().equals(profileId)) {
                        i++;
                    }
                }
                tvPost.setText(i + " Posts");
            }
        });
    }
    public void getPhotos(){
        FirebaseFirestore.getInstance().collection("photos")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentSnapshot snapshot : value){
                            Post post = snapshot.toObject(Post.class);
                            if (post.getPostUploader().equals(profileId)){
                                postList.add(post);
                            }
                        }
                        Collections.reverse(postList);
                        photoAdapter.notifyDataSetChanged();
                    }
                });
    }
    public void addNotif(){
        DocumentReference reference = FirebaseFirestore.getInstance().collection("notification").document(profileId);
        Notification notification = new Notification(firebaseUser.getUid(),"Following",true,"");
    }
}