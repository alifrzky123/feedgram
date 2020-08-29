package com.project.feeds.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.feeds.AddActivity;
import com.project.feeds.CommentAdapter;
import com.project.feeds.SearchFragment;
import com.project.feeds.fragment.HomeFragment;
import com.project.feeds.fragment.LikeFragment;
import com.project.feeds.R;
import com.project.feeds.fragment.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    public static final String DATA = "PREF_UID";
    public static final String KEY = "profileId";
    BottomNavigationView bottomNavigationView;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        getFragmentPage(new HomeFragment());
        bottomNavbar();
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(String.valueOf(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);

        Bundle i = getIntent().getExtras();
        if (i!=null){
            String uploader = i.getString(CommentAdapter.ID_PUBLISHER);
            SharedPreferences.Editor editor = getSharedPreferences(DATA,MODE_PRIVATE).edit();
            editor.putString(KEY,uploader);
            editor.apply();
            Fragment fragment = null;
            fragment = new ProfileFragment();
            getFragmentPage(fragment);
        }else{
            Fragment fragment = null;
            fragment = new HomeFragment();
            getFragmentPage(fragment);
        }
    }

    private void bottomNavbar(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()){
                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.nav_add :
                        startActivity(new Intent(MainActivity.this, AddActivity.class));
                        break;
                    case R.id.nav_profil :
                        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = getSharedPreferences(DATA, MODE_PRIVATE).edit();
                        editor.putString(KEY, FirebaseAuth.getInstance().getCurrentUser().getUid());
                        editor.apply();
                        fragment = new ProfileFragment();
                        break;
                    case R.id.nav_liked_post :
                        fragment = new LikeFragment();
                        break;
                    case R.id.nav_search :
                        fragment = new SearchFragment();
                        break;
                    default:
                        break;
                }
                return getFragmentPage(fragment);
            }
        });
    }
    private boolean getFragmentPage(Fragment fragment){
        if (fragment!=null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.rv_container,fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser!=null){
            Toast.makeText(this, firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void updateUI(FirebaseUser user){
        if (user==null){
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}