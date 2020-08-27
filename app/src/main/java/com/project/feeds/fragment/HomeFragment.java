package com.project.feeds.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.feeds.PostAdapter;
import com.project.feeds.R;
import com.project.feeds.activity.LoginActivity;
import com.project.feeds.entity.Post;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG ="HomeFragment";
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv;
    PostAdapter postAdapter;
    List<Post> postList;
    List<String> listFollowing;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    GoogleSignInClient googleSignInClient;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rv = view.findViewById(R.id.rv_list);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rv.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(),postList);
        rv.setAdapter(postAdapter);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(String.valueOf(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getContext(),googleSignInOptions);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser!=null){
            Toast.makeText(getContext(), firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
            followingCheck();
        }else{
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void showPosts(){
        CollectionReference reference = FirebaseFirestore.getInstance().collection("photos");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                postList.clear();
                for (DocumentSnapshot snapshot : value){
                    Post post = snapshot.toObject(Post.class);
                    for (String id : listFollowing){
                        if (post.getPostUploader().equals(id)){
                            postList.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }
        });
    }

    public void followingCheck(){
        listFollowing = new ArrayList<>();
        CollectionReference reference = FirebaseFirestore.getInstance()
                .collection("follow")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("following");

        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                listFollowing.clear();
                for (QueryDocumentSnapshot snapshot : value){
                    listFollowing.add(snapshot.getId());
                }
                showPosts();
            }
        });
    }
}