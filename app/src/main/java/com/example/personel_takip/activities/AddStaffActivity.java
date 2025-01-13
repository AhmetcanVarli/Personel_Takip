package com.example.personel_takip.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.service.media.MediaBrowserService;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.personel_takip.R;
import com.example.personel_takip.databinding.ActivityAddStaffBinding;
import com.example.personel_takip.utilities.Constants;
import com.example.personel_takip.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.santalu.maskara.Mask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import io.grpc.BinaryLog;

public class AddStaffActivity extends AppCompatActivity {

    private ActivityAddStaffBinding binding;
    private DatePickerDialog datePickerDialog;
    private PreferenceManager preferenceManager;


    private static final int PERMISSION_CODE = 1234;
    private static final int CAPTURE_CODE = 1001;

    private ArrayAdapter<CharSequence> adapterDepartment;

    Uri image_uri;

    String encodedImage;
    String department;
    int position;
    Boolean gender;

    String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStaffBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());


        setListeners();
        checkControl();
    }

    private void setListeners() {
        spinnerSelected();
        binding.imageBack.setOnClickListener(view -> onBackPressed());
        binding.buttonDatePicker.setOnClickListener(view -> datePicker());
        binding.textBirtDate.setOnClickListener(view -> materialDataPicker());
        binding.layoutImage.setOnClickListener(view -> camera());
        binding.buttonSave.setOnClickListener(view -> {

            if (personDetails()){
                userDBSave();
                checkControl();
                spinnerSelected();
            }
        });



    }

    private void spinnerSelected(){
        adapterDepartment =ArrayAdapter.createFromResource(this,R.array.Roller, R.layout.spinner_selected_item);
        adapterDepartment.setDropDownViewResource(R.layout.dropdown_item);
        binding.spinnerDepartment.setAdapter(adapterDepartment);

        binding.spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                department = adapterView.getItemAtPosition(i).toString();

                position = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                position = 0;
            }
        });
    }

    private void openDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        LinearLayout camera = dialog.findViewById(R.id.layoutCamera);
        LinearLayout gallery = dialog.findViewById(R.id.layoutGallery);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Image");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
                image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
                startActivityForResult(intent,CAPTURE_CODE);
                dialog.dismiss();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,CAPTURE_CODE);
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void datePicker() {
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.date)).build();
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                     SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
                     binding.buttonDatePicker.setText(sdf.format(datePicker.getSelection()));
            }
        });
        datePicker.show(getSupportFragmentManager(),"TAG");

    }

    private void materialDataPicker(){

        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.date)).build();

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                SimpleDateFormat sdf = new  SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                binding.textInputBirthDate.getEditText().setText(sdf.format(datePicker.getSelection()));
            }
        });

        datePicker.show(getSupportFragmentManager(),"TAG");

    }

    private void camera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {

                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            } else {
                //openCamera();
                openDialog();
            }
        } else {
           // openCamera();
            openDialog();
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(camIntent, CAPTURE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            image_uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(image_uri);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image_uri);
                 bitmap = BitmapFactory.decodeStream(inputStream);
                binding.textAddImage.setVisibility(View.GONE);
                binding.imageProfile.setImageBitmap(bitmap);
                encodedImage = encodeImage(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (resultCode == RESULT_OK) {

            try {
                InputStream inputStream =getContentResolver().openInputStream(image_uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                binding.textAddImage.setVisibility(View.GONE);
                binding.imageProfile.setImageURI(image_uri);
                encodedImage = encodeImage(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void checkControl(){
         ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_enabled}
                },
                new int[]{
                        Color.rgb(64,64,64),
                        Color.rgb(230,82,29)
                }
        );
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (binding.radioMan.getId() == i){
                    binding.radioMan.setButtonTintList(colorStateList);
                    gender = true;
                }else if(binding.radioWoman.getId() == i){
                    binding.radioWoman.setButtonTintList(colorStateList);
                    gender = false;
                }
            }
        });
    }

    private boolean personDetails() {
        String id = binding.idTextNumber.getText().toString();
        String date = binding.buttonDatePicker.getText().toString();
        String name = binding.userTextName.getText().toString();
        String surname = binding.userTextSurname.getText().toString();
        String birtDate = binding.textBirtDate.getText().toString();
        String phone = binding.userTextPhoneNumber.getText().toString();
        String address = binding.userTextAddress.getText().toString();
        if (encodedImage == null){
            showToastMessage("Fotoğraf Seçiniz");
            return false;
        }else if (date.equals("-- -- --")){
            showToastMessage("Tarih secin");
            return false;
        }else if (TextUtils.isEmpty(id)){
            binding.idTextNumber.setError("Email cannot be empty");
            binding.idTextNumber.requestFocus();
            return false;
        }else if (TextUtils.isEmpty(name)){
            binding.userTextName.setError("Email cannot be empty");
            binding.userTextName.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(surname)){
            binding.userTextSurname.setError("Email cannot be empty");
            binding.userTextSurname.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(birtDate)){
            binding.textBirtDate.setError("Email cannot be empty");
            binding.textBirtDate.requestFocus();
            return false;
        }else if(gender == null){
            showToastMessage("Cinsiyet Secin");
            return false;
        }else if (department.equals("Seçiniz")){
            showToastMessage("Department Seçiniz");
            return false;
        }else if (TextUtils.isEmpty(phone)){
            binding.userTextPhoneNumber.setError("Email cannot be empty");
            binding.userTextPhoneNumber.requestFocus();
            return false;
        }else if (TextUtils.isEmpty(address)){
            binding.userTextPhoneNumber.setError("address cannot be empty");
            binding.userTextPhoneNumber.requestFocus();
            return false;
        }else{
            return true;
        }
    }

    private void clearActivity(){
        binding.userTextName.setText("");
        binding.userTextSurname.setText("");
        binding.idTextNumber.setText("");
        binding.textBirtDate.setText("");
        binding.radioGroup.clearCheck();
        binding.userTextPhoneNumber.setText("");
        binding.userTextAddress.setText("");
        binding.buttonDatePicker.setText("-- -- --");
        binding.imageProfile.setImageDrawable(getDrawable(R.drawable.background_image));
        binding.textAddImage.setVisibility(View.VISIBLE);
    }

    private void userDBSave(){
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String,Object> person = new HashMap<>();

        person.put(Constants.KEY_PERSON_ID,binding.idTextNumber.getText().toString());
        person.put(Constants.KEY_PERSON_NAME,binding.userTextName.getText().toString());
        person.put(Constants.KEY_PERSON_SURNAME,binding.userTextSurname.getText().toString());
        person.put(Constants.KEY_PERSON_BIRT_DATE,binding.textBirtDate.getText().toString());
        person.put(Constants.KEY_PERSON_GENDER,gender);
        person.put(Constants.KEY_PERSON_DEPARTMENT,department);
        person.put(Constants.KEY_PERSON_PHONE,binding.userTextPhoneNumber.getText().toString());
        person.put(Constants.KEY_PERSON_ADDRESS,binding.userTextAddress.getText().toString());
        person.put(Constants.KEY_PERSON_IMAGE,encodedImage);
        db.collection(Constants.KEY_PERSON_COLLECTIONS_NAME)
                .add(person)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putString(Constants.KEY_DOCUMENT_ID,documentReference.getId());
                    preferenceManager.putString(Constants.KEY_PERSON_NAME,binding.userTextName.getText().toString());
                    preferenceManager.putString(Constants.KEY_PERSON_SURNAME,binding.userTextSurname.getText().toString());
                    preferenceManager.putString(Constants.KEY_PERSON_IMAGE,encodedImage);
                    preferenceManager.putString(Constants.KEY_PERSON_DEPARTMENT,department);
                    showToastMessage("Kayıt başarılı");
                    Log.d(TAG, "userDBSave: " + Constants.KEY_DOCUMENT_ID+ "Doc ref" + documentReference.getId());

                    clearActivity();
                }).addOnFailureListener(exception ->{
                    loading(false);
                    showToastMessage(exception.getMessage());
                });


    }

    private String encodeImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth/bitmap.getWidth();
        Bitmap previewBitmap = bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    private void loading(boolean isLoading){
        if (isLoading){
            binding.buttonSave.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.buttonSave.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

}