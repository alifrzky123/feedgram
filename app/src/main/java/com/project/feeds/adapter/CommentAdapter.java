package com.project.feeds.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.project.feeds.R;
import com.project.feeds.activity.MainActivity;
import com.project.feeds.entity.Comment;
import com.project.feeds.entity.User;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context context;
    List<Comment> commentList;
    FirebaseUser mUser;
    public static final String ID_PUBLISHER ="uploadId";


    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment,parent,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = commentList.get(position);

        holder.tvComment.setText(comment.getText());
        holder.tvUnameComment.setText(comment.getMrComment());
        getUserInfo(holder.ivPhotoProfile,holder.tvUnameComment,comment.getMrComment());

        holder.tvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra(ID_PUBLISHER,comment.getMrComment());
                context.startActivity(i);
            }
        });

        holder.ivPhotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra(ID_PUBLISHER,comment.getMrComment());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhotoProfile;
        TextView tvUnameComment, tvComment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            init(itemView);
        }
        public void init(View view){
            ivPhotoProfile = view.findViewById(R.id.iv_photo_profile_comment);
            tvUnameComment = view.findViewById(R.id.tv_uname_comment);
            tvComment = view.findViewById(R.id.tv_item_comment);
        }
    }
    public void getUserInfo(final ImageView ivPhotoProfile, final TextView tvUname, final String uploader){
        FirebaseFirestore.getInstance().collection("users").document(uploader)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        User user = value.toObject(User.class);
                        Glide.with(context).load(user.getPhotoUrl()).into(ivPhotoProfile);
                        tvUname.setText(user.getUserName());
                    }
                });
    }
}
