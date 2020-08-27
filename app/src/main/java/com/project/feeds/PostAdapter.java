package com.project.feeds;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.feeds.entity.Comment;
import com.project.feeds.entity.Post;
import com.project.feeds.entity.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private Context context;
    private List<Post> postList;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_rv,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ViewHolder holder, final int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = postList.get(position);
        Glide.with(context).load(post.getPostImages()).into(holder.ivPost);

        if (post.getPostDesc().equals("")){
            holder.tvDesk.setVisibility(View.GONE);
        }else {
            holder.tvDesk.setVisibility(View.VISIBLE);
            holder.tvDesk.setText(post.getPostDesc());
        }
        UploadInfo(holder.ivProfile,holder.tvUname,holder.tvUploader,post.getUploadId());
        LikedPost(post.getUploadId(),holder.ivLike);
        Likers(holder.tvTotalLike,post.getUploadId());
        getComments(post.getUploadId(),holder.tvComment);

        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.ivLike.getTag().equals("sukai")) {
                    Map<String, Object> data = new HashMap<>();
                    data.put(firebaseUser.getUid(), true);
                    FirebaseFirestore.getInstance().collection("suka")
                            .document(post.getUploadId()).set(data);

                    //addNotifikasi(postingan.getPengapload(), postingan.getIdupload());
                } else {
                    FirebaseFirestore.getInstance().collection("suka")
                            .document(post.getUploadId()).delete();
                }
            }
        });
//        holder.ivComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, CommentActivity.class);
//                intent.putExtra(CommentActivity.ID_POST, post.getUploadId());
//                intent.putExtra(CommentActivity.ID_PUBLISHER,post.getPostUploader());
//                context.startActivity(intent);
//            }
//        });

//        holder.tvComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context,CommentActivity.class);
//                intent.putExtra(CommentActivity.ID_POST,post.getUploadId());
//                intent.putExtra(CommentAdapter.ID_PUBLISHER,post.getPostUploader());
//                context.startActivity(intent);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPost,ivProfile,ivLike,ivComment,ivSaveImages;
        TextView tvUname, tvTotalLike, tvUploader, tvDesk,tvComment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPost = itemView.findViewById(R.id.iv_post_images);
            ivProfile = itemView.findViewById(R.id.iv_photo_profile_post);
            ivLike = itemView.findViewById(R.id.iv_like);
            ivComment = itemView.findViewById(R.id.iv_comment);
            ivSaveImages = itemView.findViewById(R.id.iv_save_images);
            tvUname = itemView.findViewById(R.id.tv_uname_post_uname);
            tvTotalLike = itemView.findViewById(R.id.tv_like_total);
            tvUploader = itemView.findViewById(R.id.tv_uploader);
            tvDesk = itemView.findViewById(R.id.tv_deskripsi_post);
            tvComment = itemView.findViewById(R.id.tv_comment);
        }
    }
    public void UploadInfo(final ImageView ivPhotoProfile, final TextView tvUserName, final TextView tvUploader, final String userId){
        DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(userId);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                //assert value != null;
                User user = value.toObject(User.class);
                //assert user != null;
                Glide.with(context).load(user.getPhotoUrl()).into(ivPhotoProfile);
                tvUserName.setText(user.getUserName());
                tvUploader.setText(user.getFullName());
            }
        });
    }

    public void LikedPost(final String PostId, final ImageView ivPostImages){
        DocumentReference reference = FirebaseFirestore.getInstance().collection("like").document(PostId);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value!=null && value.exists()){
                    ivPostImages.setImageResource(R.drawable.ic_liked_images);
                    ivPostImages.setTag("Liked");
                }else{
                    ivPostImages.setImageResource(R.drawable.hati);
                    ivPostImages.setTag("Like");
                }
            }
        });
    }

    public void Likers(final TextView tvLiked, final String postId){
        DocumentReference reference = FirebaseFirestore.getInstance().collection("like").document(postId);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                int total = 0;
                try{
                    total = value.getData().size();
                }catch (NullPointerException n){
                    n.printStackTrace();
                }
                if (value.exists()){
                    String textLike = total + "Liked";
                    tvLiked.setText(textLike);
                }
            }
        });
    }
    public void getComments(final String uploadId, final TextView tvComment) {
        final CollectionReference reference = FirebaseFirestore.getInstance()
                .collection("comments")
                .document(uploadId)
                .collection(uploadId);
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<String> id = new ArrayList<>();
                assert value != null;
                for (DocumentSnapshot snapshot : value) {
                    id.add(snapshot.getId());
                }
                String textComment = id.size() + "Commented";
                tvComment.setText(textComment);
            }
        });
    }
}
