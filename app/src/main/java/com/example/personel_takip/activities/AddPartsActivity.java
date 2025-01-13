package com.example.personel_takip.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.personel_takip.databinding.ActivityAddPartsBinding;
import com.example.personel_takip.databinding.ActivityAddStaffBinding;
import com.example.personel_takip.model.Parts;
import com.example.personel_takip.utilities.CaptureAct;
import com.example.personel_takip.utilities.Constants;
import com.example.personel_takip.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;

public class AddPartsActivity extends AppCompatActivity {

    private ActivityAddPartsBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPartsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.imageQR.setOnClickListener(v -> scanCode());
        binding.buttonSave.setOnClickListener(v -> {
            if (partsDetails()){
                partsDBSave();
            }
        });
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Barkod Okuyucu");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(),result -> {
       if (result.getContents() != null){
           AlertDialog.Builder builder = new AlertDialog.Builder(AddPartsActivity.this);
           builder.setTitle("result");
           builder.setMessage(result.getContents());
           builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   binding.textQR.setText(result.getContents().toString());
                   dialogInterface.dismiss();
               }
           }).show();
       }
    });

    private void partsDBSave(){
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String,Object> parts = new HashMap();

        parts.put(Constants.KEY_BARCODE_NO,binding.textQR.getText().toString());
        parts.put(Constants.KEY_PARTS_NAME, binding.textPartsName.getText().toString());
        parts.put(Constants.KEY_PARTS_PRICE, Double.parseDouble(binding.textPartsPrice.getText().toString()));
        parts.put(Constants.KEY_PARTS_AMOUNT, Integer.parseInt(binding.textPartsAmount.getText().toString()));

        db.collection(Constants.KEY_PARTS_COLLECTIONS_NAME)
                .add(parts)
                .addOnSuccessListener(documentReference -> {
                   loading(false);
                   preferenceManager.putString(Constants.KEY_PARTS_ID,documentReference.getId());
                   preferenceManager.putString(Constants.KEY_BARCODE_NO,binding.textQR.getText().toString());
                   preferenceManager.putString(Constants.KEY_PARTS_NAME,binding.textPartsName.getText().toString());
                   preferenceManager.putString(Constants.KEY_PARTS_PRICE,binding.textPartsPrice.getText().toString());
                   preferenceManager.putString(Constants.KEY_PARTS_AMOUNT,binding.textPartsAmount.getText().toString());
                   clearActivity();
                   showToastMessage("Başarılı Kayıt");
                })
                .addOnFailureListener(exception ->{
                    loading(false);
                    showToastMessage("Başarısız Kayıt");
                });
    }

    private void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean partsDetails(){
        if (binding.textQR.getText().toString().trim().isEmpty()){
            showToastMessage("qr gir");
            return false;
        }else if(binding.textPartsName.getText().toString().trim().isEmpty()){
            showToastMessage("name gir");
            return false;
        }else if (binding.textPartsPrice.getText().toString().trim().isEmpty()){
            showToastMessage("price gir");
            return false;
        }else if (binding.textPartsAmount.getText().toString().trim().isEmpty()){
            showToastMessage("amount gir");
            return false;
        }else {
            return true;
        }
    }

    private void clearActivity(){
        binding.textQR.setText("");
        binding.textPartsName.setText("");
        binding.textPartsPrice.setText("");
        binding.textPartsAmount.setText("");
    }

    private void loading(boolean isLoading){
        if (isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonSave.setVisibility(View.INVISIBLE);
        }else{
            binding.progressBar.setVisibility(View.GONE);
            binding.buttonSave.setVisibility(View.VISIBLE);
        }
    }
}