package com.example.personel_takip.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.personel_takip.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DocumentReference docRef;
    String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("EEEE LLLL");
        binding.textDate.setText(dateFormat.format(date));


        Log.d(TAG, "onCreate: " + date.toString());
        Log.d(TAG, "onCreate: " + dateFormat.format(date));

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            getAdminDetails();
        }



        setListeners();
    }

    private void setListeners() {

        binding.imageSignOut.setOnClickListener(view ->{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this,LoginActivity.class));
        });
        binding.cardViewAddStaff.setOnClickListener(view ->
                startActivity(new Intent(this, AddStaffActivity.class)));
        binding.cardViewStaffList.setOnClickListener(view ->
                startActivity(new Intent(this, StaffListActivity.class)));
        binding.cardViewAddParts.setOnClickListener(view ->
            startActivity(new Intent(this,AddPartsActivity.class)));
        binding.cardViewPartsList.setOnClickListener(view -> startActivity(new Intent(this,PartsListActivity.class)));


    }

    private void getAdminDetails(){
        docRef = db.collection("Admin").document(mAuth.getUid());
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            binding.textAdminName.setText(documentSnapshot.getData().get("name").toString());
                        }else{
                            Log.d(TAG, "onSuccess: fail  yok");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.textAdminName.setText("Hata");
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        }
    }
}