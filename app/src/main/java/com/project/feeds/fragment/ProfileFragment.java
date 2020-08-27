package com.project.feeds.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.feeds.R;
import com.project.feeds.activity.LoginActivity;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private ImageView ivPhotoProfile, ivLogOut;
    private TextView tvUname, tvBio, tvPost, tvFollowing, tvFollowers;
    private Button btnEditProfile;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    GoogleSignInClient googleSignInClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ivPhotoProfile = v.findViewById(R.id.iv_pro_pict);
        ivLogOut = v.findViewById(R.id.iv_log_out);
        tvUname = v.findViewById(R.id.tv_pro_uname);
        tvBio = v.findViewById(R.id.tv_bio_pro);
        tvPost = v.findViewById(R.id.tv_total_posts);
        tvFollowers = v.findViewById(R.id.tv_total_followers);
        tvFollowing = v.findViewById(R.id.tv_total_followings);
        btnEditProfile = v.findViewById(R.id.btn_update_profile);

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
        return v;
    }
    public void logOut() {
        mAuth.signOut();
        googleSignInClient.signOut().addOnCompleteListener((Activity) this.getContext(),
                new OnCompleteListener<Void>() {
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
}