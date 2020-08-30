package com.project.feeds.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.feeds.activity.CommentActivity;
import com.project.feeds.R;
import com.project.feeds.activity.MainActivity;
import com.project.feeds.entity.Post;
import com.project.feeds.fragment.LikeFragment;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    Context context;
    List<Post> posts;

    public PhotoAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_feed,parent,false);
        return new PhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Post post = posts.get(position);
        Glide.with(context).load(post.getPostImages()).into(holder.ivPhotoList);
        holder.ivPhotoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences(MainActivity.DATA,Context.MODE_PRIVATE).edit();
                editor.putString(CommentActivity.ID_POST,post.getUploadId());
                editor.apply();
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.rv_container, new LikeFragment()).commit();

            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhotoList;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhotoList = itemView.findViewById(R.id.iv_photo_list);
        }
    }
}
