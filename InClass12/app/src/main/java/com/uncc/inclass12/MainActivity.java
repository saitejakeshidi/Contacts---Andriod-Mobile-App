
package com.uncc.inclass12;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyAdpater.InteractWithMainActivity {

    private ImageView signOutImageView;
    ArrayList<Contact> contactArrayList;
    RecyclerView contactRecycleView;
    private Button createNewContact;
    private TextView emptyView;
    private FirebaseAuth mAuth;

    private MyAdpater myAdapter;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private static final int REQ_CODE = 9000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactRecycleView = findViewById(R.id.contactRecycleView);
        signOutImageView = findViewById(R.id.signOutButton);
        createNewContact = findViewById(R.id.createContactButton);
        emptyView = findViewById(R.id.empty_view);

        contactArrayList = new ArrayList<Contact>();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        db.collection("users").document(currentUser.getUid()).collection("contacts")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Log.d("demo", "onSuccess: " + documentSnapshot.getId());

                            String email = (String) documentSnapshot.get("email");
                            String phoneNo = (String) documentSnapshot.get("phoneNo");
                            String name = (String) documentSnapshot.get("name");
                            String imageUri = (String) documentSnapshot.get("imageUri");

                            Contact contactObj = new Contact(name, email, phoneNo, imageUri, documentSnapshot.getId());

                            contactArrayList.add(contactObj);
                        }
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (contactArrayList.isEmpty()) {
                                    contactRecycleView.setVisibility(View.GONE);
                                    emptyView.setVisibility(View.VISIBLE);
                                }
                                else {
                                    contactRecycleView.setVisibility(View.VISIBLE);
                                    emptyView.setVisibility(View.GONE);
                                }
                                myAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                });

        contactRecycleView.addItemDecoration(new DividerItemDecoration(contactRecycleView.getContext(), DividerItemDecoration.VERTICAL));

        if (!isConnected()) {
            Toast.makeText(MainActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
        }

        setTitle("Contacts");


        myAdapter = new MyAdpater(contactArrayList, MainActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        contactRecycleView.setLayoutManager(layoutManager);
        contactRecycleView.setItemAnimator(new DefaultItemAnimator());
        contactRecycleView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.HORIZONTAL));

        contactRecycleView.setAdapter(myAdapter);

        if (contactArrayList.isEmpty()) {
            contactRecycleView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            contactRecycleView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        createNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMainActivity = new Intent(MainActivity.this, CreateContact.class);
                startActivityForResult(toMainActivity, REQ_CODE);
            }
        });

        signOutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent it = new Intent(MainActivity.this, Login.class);
                startActivityForResult(it,REQ_CODE);
            }
        });

    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    public void deleteContact(int position) {

        Contact contactObj = contactArrayList.get(position);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        db.collection("users").document(currentUser.getUid()).collection("contacts").document(contactObj.getDocumentId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Demo", "Expense successfully deleted!");
                        Toast.makeText(MainActivity.this, "Contact successfully deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Demo", "Error deleting document", e);
                        Toast.makeText(MainActivity.this, "Error in deleting Contact", Toast.LENGTH_SHORT).show();
                    }
                });
        contactArrayList.remove(position);
        myAdapter.notifyDataSetChanged();

    }

    @Override
    public void deleteItem(final int position) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete an item?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {

                            deleteContact(position);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }
}
