package com.project.feeds.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.project.feeds.R;
import com.project.feeds.activity.MainActivity;
import com.project.feeds.entity.User;
import com.project.feeds.fragment.ProfileFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> users;

    private FirebaseUser mUser;

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = users.get(position);
        holder.btnFollow.setVisibility(View.VISIBLE);
        holder.tvUname.setText(user.getUserName());
        Glide.with(context).load(user.getPhotoUrl()).into(holder.circleImageView);

        isFollowing(user.getId(),holder.btnFollow,mUser.getUid());
        if(user.getId().equals(mUser.getUid())){
            holder.btnFollow.setVisibility(View.GONE);
        }
        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btnFollow.getText().toString().equals("Follow")) {

                    Toast.makeText(context, mUser.getEmail(), Toast.LENGTH_SHORT).show();
                    Map<String, Object> dataFollowing = new HashMap<>();
                    dataFollowing.put(user.getId(), true);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("follow").document(mUser.getUid())
                            .collection("following").document(user.getId()).set(dataFollowing);

                    Map<String, Object> dataFollower = new HashMap<>();
                    dataFollower.put(mUser.getUid(), true);
                    db.collection("follow").document(user.getId())
                            .collection("followers").document(mUser.getUid()).set(dataFollowing);

                } else {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("follow").document(mUser.getUid())
                            .collection("following").document(user.getId()).delete();
                    db.collection("follow").document(user.getId())
                            .collection("followers").document(mUser.getUid()).delete();
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = context.getSharedPreferences(MainActivity.DATA, Context.MODE_PRIVATE).edit();
                editor.putString(MainActivity.KEY,user.getId());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.rv_container, new ProfileFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUname;
        CircleImageView circleImageView;
        Button btnFollow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUname = itemView.findViewById(R.id.userName_item);
            circleImageView = itemView.findViewById(R.id.item_circle_image);
            btnFollow = itemView.findViewById(R.id.btn_item_follow);
        }
    }
    private void isFollowing(final String userId, final Button button, final String following){
        FirebaseFirestore instance = FirebaseFirestore.getInstance();
        DocumentReference reference = instance.collection("follow")
                .document(following)
                .collection("following")
                .document(userId);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null && value.exists()){
                    button.setText("Following");
                }else {
                    button.setText("Follow");
                }
            }
        });

    }
}
