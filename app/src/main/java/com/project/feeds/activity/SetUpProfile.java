package com.project.feeds.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.project.feeds.R;
import com.project.feeds.entity.User;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class SetUpProfile extends AppCompatActivity {
    EditText etUname,etFullname,etBio;
    ImageView ivExitPro, ivDoneEditPro,ivPhotoPro;

    FirebaseUser mUser;
    Uri uri;
    StorageTask storageTask;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_profile);

        etUname = findViewById(R.id.et_uname_edit_pro);
        etFullname = findViewById(R.id.et_fullname_edit_pro);
        etBio = findViewById(R.id.et_biodata_edit_pro);
        ivDoneEditPro = findViewById(R.id.iv_save_edit_profile);
        ivExitPro = findViewById(R.id.iv_close_edit_profile);
        ivPhotoPro = findViewById(R.id.iv_photo_profile_edit);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(mUser.getUid());
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                User user = value.toObject(User.class);
                etUname.setText(user.getUserName());
                etFullname.setText(user.getFullName());
                etBio.setText(user.getBio());
                Glide.with(getApplicationContext()).load(user.getPhotoUrl()).into(ivPhotoPro);
            }
        });

        ivExitPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivPhotoPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(SetUpProfile.this);
            }
        });

        ivDoneEditPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String _uname = etUname.getText().toString().trim();
                String _bio = etBio.getText().toString();
                String _fullname = etFullname.getText().toString();
                changeProfile(_uname,_bio,_fullname);
            }
        });
    }

    public void changeProfile(String uName, String bioData, String fullName){
        Map<String,Object> data = new HashMap<>();
        data.put("fullName",fullName);
        data.put("bio",bioData);
        data.put("userName",uName);
        DocumentReference reference = FirebaseFirestore.getInstance()
                .collection("users")
                .document(mUser.getUid());
        reference.update(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(SetUpProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadImages() {
        final ProgressDialog loadingUpload = new ProgressDialog(SetUpProfile.this);
        loadingUpload.setTitle("harap tunggu, gambar sedang diupload");
        loadingUpload.show();

        if (uri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + AddActivity.extensionFile(getApplicationContext(), uri));

            storageTask = fileReference.putFile(uri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String uri = downloadUri.toString();

                        Map<String, Object> dataUrlImage = new HashMap<>();
                        dataUrlImage.put("imageUrl", uri);
                        FirebaseFirestore.getInstance().collection("users")
                                .document(mUser.getUid()).update(dataUrlImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingUpload.dismiss();
                                Toast.makeText(SetUpProfile.this, "Upload Images", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(SetUpProfile.this, "Fail To Load Images", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetUpProfile.this, "on failure" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "You Didn't Choose Any Images", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            uri = result.getUri();
            uploadImages();
        }else{
            Toast.makeText(this, "There's Something Wrong", Toast.LENGTH_SHORT).show();
        }
    }
}