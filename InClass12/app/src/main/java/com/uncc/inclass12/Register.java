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

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button registerButton, cancelButton;
    private TextView emailTxtView, passwordTxtView, repeatPasswordTxtView, firstNameTxtView, lastNameTxtView;
    private static final int REQ_CODE = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailTxtView = findViewById(R.id.emailTxtView);
        firstNameTxtView = findViewById(R.id.firstNameTxtView);
        lastNameTxtView = findViewById(R.id.lastNameTxtView);
        passwordTxtView = findViewById(R.id.passowrdTxtView);
        repeatPasswordTxtView = findViewById(R.id.repeatPasswordTxtView);
        registerButton = findViewById(R.id.signUpBtn);
        cancelButton = findViewById(R.id.cancelButton);

        mAuth = FirebaseAuth.getInstance();

        setTitle("Sign Up");

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailTxtView.getText().toString();
                String password = passwordTxtView.getText().toString();
                String repeatPassword = repeatPasswordTxtView.getText().toString();

                if (email.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter email",
                            Toast.LENGTH_SHORT).show();
                }  else if (password.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter password",
                            Toast.LENGTH_SHORT).show();
                } else if (!password.equals(repeatPassword)) {
                    Toast.makeText(getApplicationContext(), "Passwords doesn't match",
                            Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Register.this,new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("demo", "createUserWithEmail:success");
                                        Toast.makeText(getApplicationContext(), "User registered successfully",
                                                Toast.LENGTH_SHORT).show();
                                        Intent toMainActivity = new Intent(Register.this, MainActivity.class);
                                        startActivityForResult(toMainActivity, REQ_CODE);

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("demo", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
