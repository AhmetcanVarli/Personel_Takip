package com.example.personel_takip.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.personel_takip.adapters.PartsAdapter;
import com.example.personel_takip.databinding.ActivityPartsListBinding;
import com.example.personel_takip.listeners.PartsListener;
import com.example.personel_takip.model.Parts;
import com.example.personel_takip.utilities.Constants;
import com.example.personel_takip.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PartsListActivity extends AppCompatActivity implements PartsListener {

    private ActivityPartsListBinding binding;
    private PreferenceManager preferenceManager;

    private FirebaseFirestore db;
    private DocumentReference docRef;

    String TAG = "TAG";

    List<Parts> partsList = new ArrayList<>();
    PartsAdapter partsAdapter = new PartsAdapter(partsList,this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPartsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        getParts();

        setListeners();

    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        binding.imageRefresh.setOnClickListener(v->{
            Intent intent = new Intent(this,PartsListActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void getParts(){
        loading(true);
        db.collection(Constants.KEY_PARTS_COLLECTIONS_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                  if (task.isSuccessful()&&task.getResult() != null){
                      for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                          Log.d(TAG, "getParts:  " + documentSnapshot.getData());

                          Parts parts = new Parts();

                          parts.partsName = documentSnapshot.getString(Constants.KEY_PARTS_NAME);
                          parts.barcodeNo = documentSnapshot.getString(Constants.KEY_BARCODE_NO);
                          partsList.add(parts);


                      }
                      if(partsList.size() > 0){
                          Log.d(TAG, "getParts: " + partsList);
                          binding.recyclerView.setHasFixedSize(true);
                          binding.recyclerView.setAdapter(partsAdapter);
                          binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
                          binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
                          //binding.recyclerView.setVisibility(View.VISIBLE);
                      }else {
                          showErrorMessage();
                      }
                  }
                });

        /*docRef = db.collection(Constants.KEY_PARTS_COLLECTIONS_NAME).document();
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading(false);
                        Log.d(TAG, "onSuccess: HATA" );
                    }
                });*/
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No parts available "));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPartsClicked(Parts parts) {

    }
}