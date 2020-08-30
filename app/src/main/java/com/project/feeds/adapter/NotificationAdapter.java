package com.project.feeds.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.project.feeds.R;
import com.project.feeds.entity.Notification;
import com.project.feeds.entity.Post;
import com.project.feeds.entity.User;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    Context context;
    List<Notification> notificationList;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notif,parent,false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Notification notification = notificationList.get(position);
        holder.tvText.setText(notification.getText());
        getUser(holder.ivPPNotif,holder.tvUname,notification.getUserId());

        if (notification.getPost()){
            holder.ivPostNotif.setVisibility(View.VISIBLE);
            getPostedImages(holder.ivPostNotif,notification.getIdUploader());
        }else{
            holder.ivPostNotif.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPPNotif, ivPostNotif;
        TextView tvUname, tvText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPPNotif = itemView.findViewById(R.id.iv_photo_profile_notif);
            ivPostNotif = itemView.findViewById(R.id.iv_post_image_notif);
            tvUname = itemView.findViewById(R.id.tv_uname_notif);
            tvText = itemView.findViewById(R.id.tv_comment_notif);
        }
    }

    public void getUser(final ImageView ivPP, final TextView tvUname, final String uploader){
        DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(uploader);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert  value != null;
                User user = value.toObject(User.class);
                assert user != null;
                Glide.with(context).load(user.getPhotoUrl()).into(ivPP);
                tvUname.setText(user.getUserName());
            }
        });
    }

    public void getPostedImages(final ImageView ivPostedPhoto, String uploader){
        DocumentReference reference = FirebaseFirestore.getInstance().collection("photos").document(uploader);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                Post post = value.toObject(Post.class);
                assert post != null;
                Glide.with(context).load(post.getPostImages()).into(ivPostedPhoto);
            }
        });
    }
}
