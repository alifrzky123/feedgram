package com.project.feeds;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.feeds.entity.Comment;
import com.project.feeds.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {
    public static final String ID_POST = "postid";
    public static final String ID_PUBLISHER = "CommentActivity";
    RecyclerView recyclerView;
    ImageView ivPhotoProCom;
    EditText etComment;
    TextView tvSendComment;
    List<Comment> commentList;
    CommentAdapter commentAdapter;

    FirebaseUser mUser;

    String postId,publisherId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        init();
        Intent intent = getIntent();
        postId = intent.getStringExtra(ID_POST);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this,commentList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.rv_comment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(commentAdapter);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent i = getIntent();
        postId = i.getStringExtra(ID_POST);
        publisherId = i.getStringExtra(CommentAdapter.ID_PUBLISHER);

        tvSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etComment.getText().toString().matches("")){
                    etComment.setError("Comment Cannot Be Empty");
                }else {
                    AddComment();
                }
            }
        });

        getPhotoProfile();
        readComment();
    }
    public void init(){
        ivPhotoProCom = findViewById(R.id.iv_pp_comment);
        etComment = findViewById(R.id.et_add_comment);
        tvSendComment = findViewById(R.id.tv_add_comment);
    }
    public void AddComment(){
        String text = etComment.getText().toString();
        String mrComment = mUser.getUid();
        Map<String,Object> dataComment = new HashMap<>();
        dataComment.put("text",etComment.getText().toString());
        dataComment.put("mrComment",mUser.getUid());
        Comment comment = new Comment(text,mrComment);
        FirebaseFirestore.getInstance()
                .collection("comments")
                .document(postId)
                .collection(FirebaseFirestore.getInstance().collection("comments").getId())
                .add(comment)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(CommentActivity.this, "Comment Berhasil Di Kirim", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void getPhotoProfile(){
        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection("users")
                .document(mUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User user = task.getResult().toObject(User.class);
                Glide.with(getApplicationContext()).load(user.getPhotoUrl()).into(ivPhotoProCom);
            }
        });
    }
    public void readComment(){
        FirebaseFirestore.getInstance().collection("comments").document(postId).collection(postId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        assert value != null;
                        for (DocumentChange change : value.getDocumentChanges()){
                            Comment comment = change.getDocument().toObject(Comment.class);
                            commentList.add(comment);
                        }
                        commentAdapter.notifyDataSetChanged();
                    }
                });
    }
}