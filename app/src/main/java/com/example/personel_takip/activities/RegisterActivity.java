package com.example.personel_takip.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.example.personel_takip.databinding.ActivityRegisterBinding;
import com.example.personel_takip.model.Admin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db;

    String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        setListener();
    }

    private void setListener(){
        binding.buttonRegister.setOnClickListener(view -> createUser());

    }

    private void createUser(){
        String nameSurname = binding.inputTextNameSurname.getText().toString();
        String email = binding.inputTextEmail.getText().toString();
        String password = binding.inputTextPassword.getText().toString();
        String confirmPassword = binding.inputTextPasswordConfirm.getText().toString();

        if (TextUtils.isEmpty(nameSurname)){
            binding.inputTextNameSurname.setError("Email cannot be empty");
            binding.inputTextNameSurname.requestFocus();
        }else if (TextUtils.isEmpty(email)){
            binding.inputTextEmail.setError("Email cannot be empty");
            binding.inputTextEmail.requestFocus();
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.inputTextEmail.setError("Valied Email");
            binding.inputTextEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            binding.inputTextPassword.setError("Password cannot be empty");
            binding.inputTextPassword.requestFocus();
        }else if (TextUtils.isEmpty(confirmPassword)){
            binding.inputTextPasswordConfirm.setError("Password cannot be empty");
            binding.inputTextPasswordConfirm.requestFocus();
        }else if(!(password.equals(confirmPassword))){
            binding.inputTextPassword.setError("password");
            binding.inputTextPasswordConfirm.setError("password");
        } else{
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        Admin admin = new Admin(nameSurname,email,password);

                        db.collection("Admin").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .set(admin)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: " + unused);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: FAILLLL");
                                    }
                                });

                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));

                        Toast.makeText(RegisterActivity.this, "User Registered is Successfully", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(RegisterActivity.this, "Registration ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

}