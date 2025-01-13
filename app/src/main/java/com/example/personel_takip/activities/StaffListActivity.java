package com.example.personel_takip.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.PeriodicSync;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.personel_takip.R;
import com.example.personel_takip.adapters.PartsAdapter;
import com.example.personel_takip.adapters.PersonAdapter;
import com.example.personel_takip.databinding.ActivityStaffListBinding;
import com.example.personel_takip.databinding.PersonDetilsDialogBinding;
import com.example.personel_takip.listeners.PartsListener;
import com.example.personel_takip.listeners.PersonListener;
import com.example.personel_takip.model.Parts;
import com.example.personel_takip.model.Person;
import com.example.personel_takip.utilities.Constants;
import com.example.personel_takip.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StaffListActivity extends AppCompatActivity implements PersonListener {

    private ActivityStaffListBinding binding;
    PersonDetilsDialogBinding bindingDialog;

    private PreferenceManager preferenceManager;
    private FirebaseFirestore db;

    List<Person> personList = new ArrayList<>();
    PersonAdapter personAdapter = new PersonAdapter(personList,this);



    String TAG = "TAG";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStaffListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindingDialog = PersonDetilsDialogBinding.inflate(getLayoutInflater());

        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        getPerson();
        setListeners();
    }



    private void setListeners(){
        binding.imageBack.setOnClickListener(view -> onBackPressed());
        binding.imageRefresh.setOnClickListener(v->{
            Intent intent = new Intent(this,StaffListActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void getPerson() {
        loading(true);
        db.collection(Constants.KEY_PERSON_COLLECTIONS_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if (task.isSuccessful() && task.getResult() != null){
                        for (QueryDocumentSnapshot document : task.getResult()){
                            Person person = new Person();

                            person.id = document.getString(Constants.KEY_PERSON_ID);
                            person.name = document.getString(Constants.KEY_PERSON_NAME);
                            person.surname = document.getString(Constants.KEY_PERSON_SURNAME);
                            person.birtDate = document.getString(Constants.KEY_PERSON_BIRT_DATE);
                            person.department = document.getString(Constants.KEY_PERSON_DEPARTMENT);
                            person.phone = document.getString(Constants.KEY_PERSON_PHONE);
                            person.address = document.getString(Constants.KEY_PERSON_ADDRESS);
                            person.image = document.getString(Constants.KEY_PERSON_IMAGE);

                            personList.add(person);

                        }
                        if (personList.size() > 0){
                            Log.d(TAG, "getPerson: " + personList);
                            binding.recyclerView.setHasFixedSize(true);
                            binding.recyclerView.setAdapter(personAdapter);
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        }else{
                            showErrorMessage();
                        }
                    }
                });

    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void personUpdate(Person person){
        db.collection(Constants.KEY_PERSON_COLLECTIONS_NAME).document(Constants.KEY_DOCUMENT_ID)
                .update(Constants.KEY_PERSON_PHONE , bindingDialog.textPersonPhone.getText().toString(),
                        Constants.KEY_PERSON_ADDRESS, bindingDialog.textPersonAddress.getText().toString());
    }

    private void personDelete(Person person){
        db.collection(Constants.KEY_PERSON_COLLECTIONS_NAME).document(Constants.KEY_DOCUMENT_ID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void dialogBuilder(Person person){
        AlertDialog.Builder builder = new AlertDialog.Builder(StaffListActivity.this);
        builder.setTitle("Person");

        View dialogLayout = getLayoutInflater().inflate(R.layout.person_detils_dialog,bindingDialog.getRoot());
        builder.setView(dialogLayout);

        bindingDialog.textPersonID.setText(person.id);
        bindingDialog.textPersonNameSurname.setText(person.name + " " + person.surname);
        bindingDialog.textPersonBirthDate.setText(person.birtDate);
        bindingDialog.textPersonDepartment.setText(person.department);
        bindingDialog.textPersonPhone.setText(person.phone);
        bindingDialog.textPersonAddress.setText(person.address);

        bindingDialog.btnUpdate.setOnClickListener(v-> personUpdate(person));
        bindingDialog.btnDelete.setOnClickListener(v-> personDelete(person));

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onPersonClicked(Person person) {
        dialogBuilder(person);


    }
}