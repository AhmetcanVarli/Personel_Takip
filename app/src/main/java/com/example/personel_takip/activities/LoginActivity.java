package com.example.personel_takip.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.personel_takip.databinding.ActivityLoginBinding;
import com.example.personel_takip.utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseFirestore db ;
    private DocumentReference documentReference;
    private FirebaseAuth mAuth;



    String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        setListeners();
    }

    private void setListeners() {
        binding.buttonLogin.setOnClickListener(view -> {
            signIn();
        });
        binding.textRegister.setOnClickListener(view -> startActivity(new Intent(this,RegisterActivity.class)));
    }

    private void signIn() {
        String email = binding.textInputEmail.getText().toString();
        String password = binding.textInputPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            binding.textInputEmail.setError("Email cannot be empty");
            binding.textInputEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            binding.textInputPassword.setError("Password cannot be empty");
            binding.textInputPassword.requestFocus();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "User Logged in Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "Login ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}