package com.project.feeds.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.project.feeds.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {
    private static final String TAG = "AddFragment";
    private Uri uri;
    private String MyUri = "";
    private StorageReference mStorageRef;
    private StorageTask storageTask;
    private ImageView ivClose, ivChoose;
    private TextView tvUpload;
    private EditText etDeskripsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        init();
        mStorageRef = FirebaseStorage.getInstance().getReference("photos");
        tvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImages();
                Toast.makeText(AddActivity.this, "Berhasil Upload", Toast.LENGTH_SHORT).show();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        CropImage.activity().setAspectRatio(1,1)
                .start(this);


    }
    private void init(){
        ivChoose = findViewById(R.id.image_view_choose_photo);
        ivClose = findViewById(R.id.image_view_close);
        tvUpload =findViewById(R.id.tv_view_upload_bar);
        etDeskripsi =findViewById(R.id.et_deskripsi_add);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            uri = result.getUri();

            ivChoose.setImageURI(uri);
        } else {
            Toast.makeText(this, "Somethings Wrong... \uD83D\uDE14 ", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddActivity.this, MainActivity.class));
            finish();
        }

    }

    public static String extensionFile(Context context,Uri uri){
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }
        return extension;
    }

    private void uploadImages(){
        if (uri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + extensionFile(getApplicationContext(), uri));
            storageTask = fileReference.putFile(uri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isComplete()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        MyUri = downloadUrl.toString();

                        CollectionReference reference = FirebaseFirestore.getInstance().collection("photos");
                        String idUpload = reference.document().getId();

                        Map<String, Object> dataUpload = new HashMap<>();
                        dataUpload.put("uploadId", idUpload);
                        dataUpload.put("postImages", MyUri);
                        dataUpload.put("postDesc", etDeskripsi.getText().toString().trim());
                        dataUpload.put("postUploader", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.document(idUpload).set(dataUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(AddActivity.this, MainActivity.class));
                                    Toast.makeText(getApplicationContext(), "Success Add To Databases", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(AddActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(AddActivity.this, "You Have Not Choose Image Yet", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Toast.makeText(AddActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}