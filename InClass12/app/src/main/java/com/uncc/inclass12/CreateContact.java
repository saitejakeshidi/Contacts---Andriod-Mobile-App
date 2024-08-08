

package com.uncc.inclass12;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateContact extends AppCompatActivity {

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final int REQ_CODE = 9000;

    private static final int REQUEST_IMAGE_CAPTURE = 1;


    private ImageView cameraImageView;
    private EditText nameTxtView, emailTxtView, phoneNoTextView;
    private Button submitBtn;

    Bitmap bitmap;
    Boolean isTakenPhoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_contact);
        db = FirebaseFirestore.getInstance();
        cameraImageView = findViewById(R.id.cameraImageView);
        nameTxtView = findViewById(R.id.nametextView);
        emailTxtView = findViewById(R.id.emailTxtView);
        phoneNoTextView = findViewById(R.id.phoneNoTextView);
        submitBtn = findViewById(R.id.submitButton);


        storage = FirebaseStorage.getInstance();

        storageRef = storage.getReference();

        mAuth = FirebaseAuth.getInstance();

        setTitle("Create New Contact");


        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name =  nameTxtView.getText().toString();
                String phoneNo = phoneNoTextView.getText().toString();
                String email = emailTxtView.getText().toString();

                if (name.equals("")) {
                    Toast.makeText(CreateContact.this, "Please enter name!", Toast.LENGTH_SHORT).show();

                } else if (email.equals("")) {
                    Toast.makeText(CreateContact.this, "Please enter phone number!", Toast.LENGTH_SHORT).show();

                } else if (phoneNo.equals("")) {
                    Toast.makeText(CreateContact.this, "Please enter email!", Toast.LENGTH_SHORT).show();

                } else if(isTakenPhoto){
                    String uniqueString = UUID.randomUUID().toString();
                    final StorageReference imageStorageRef = storageRef.child("images/" + uniqueString);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    UploadTask uploadTask = imageStorageRef.putBytes(data);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return imageStorageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                Log.d("demo", "Image URI: "+downloadUri);
                               uploadContactInfoToFirebase(downloadUri);
                            } else {

                            }
                        }
                    });

                } else {
                    uploadContactInfoToFirebase(null);
                }
            }
        });
    }

    private void uploadContactInfoToFirebase(Uri imageUri) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Map<String, Object> contact = new HashMap<>();
        contact.put("name", nameTxtView.getText().toString());
        contact.put("phoneNo", phoneNoTextView.getText().toString());
        contact.put("email", emailTxtView.getText().toString());
        if (imageUri != null) {
            contact.put("imageUri", imageUri.toString());
        }

        if(currentUser!=null) {
            Log.d("demo", "Current User: " + currentUser.getEmail());
        }


        db.collection("users").document(currentUser.getUid()).collection("contacts").document()
                .set(contact)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CreateContact.this, "Added contact successfully", Toast.LENGTH_SHORT).show();
                        Intent toMainActivity = new Intent(CreateContact.this, MainActivity.class);
                        startActivityForResult(toMainActivity, REQ_CODE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Demo", "Error adding document", e);
                        Toast.makeText(CreateContact.this, "Failed to add contact", Toast.LENGTH_SHORT).show();                    }
                });

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            bitmap = imageBitmap;
            cameraImageView.setImageBitmap(imageBitmap);
            isTakenPhoto = true;
        }
    }
}
