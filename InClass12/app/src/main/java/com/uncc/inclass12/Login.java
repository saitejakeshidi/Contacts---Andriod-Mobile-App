package com.uncc.inclass12;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.security.auth.login.LoginException;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button loginButton, signUpButton;
    private TextView emailTxtView, passwordTxtView;
    private static final int REQ_CODE = 9000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailTxtView = findViewById(R.id.loginTxtView);
        passwordTxtView = findViewById(R.id.passwordTextView);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        mAuth = FirebaseAuth.getInstance();

        setTitle("Login");

        loginButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {

                                               String email = emailTxtView.getText().toString();
                                               String password = passwordTxtView.getText().toString();

                                               mAuth.signInWithEmailAndPassword(email, password)
                                                       .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<AuthResult> task) {
                                                               if (task.isSuccessful()) {
                                                                   // Sign in success, update UI with the signed-in user's information
                                                                   Log.d("demo", "signInWithEmail:success");
                                                                   Intent toMainActivity = new Intent(Login.this, MainActivity.class);
                                                                   startActivityForResult(toMainActivity, REQ_CODE);
                                                               } else {
                                                                   // If sign in fails, display a message to the user.
                                                                   Log.w("demo", "signInWithEmail:failure", task.getException());
                                                                   Toast.makeText(Login.this, "Authentication failed.",
                                                                           Toast.LENGTH_SHORT).show();
                                                               }

                                                           }
                                                       });
                                           }
                                       });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMainActivity = new Intent(Login.this, Register.class);
                startActivityForResult(toMainActivity, REQ_CODE);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }


}
