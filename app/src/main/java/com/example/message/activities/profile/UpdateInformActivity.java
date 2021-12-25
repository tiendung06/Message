package com.example.message.activities.profile;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Toast;
import com.example.message.R;
import com.example.message.activities.BaseActivity;
import com.example.message.databinding.ActivityUpdateInformBinding;
import com.example.message.utilities.Constants;
import com.example.message.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class UpdateInformActivity extends BaseActivity {
    private ActivityUpdateInformBinding binding;
    private PreferenceManager preferenceManager;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateInformBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListener();
        loadCurrentInform();
    }

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.buttonUpdateInform.setOnClickListener(v -> updateInform());
        binding.birthday.setOnClickListener(v -> setDate());
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loadCurrentInform() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (Objects.requireNonNull(documentSnapshot.getString(Constants.KEY_GENDER)).equals(Constants.KEY_GENDER_MALE)) {
                binding.male.setChecked(true);
            } else {
                binding.female.setChecked(true);
            }
            binding.birthday.setText(documentSnapshot.getString(Constants.KEY_BIRTHDAY));
            binding.inputCity.setText(documentSnapshot.getString(Constants.KEY_CITY));
            binding.inputCountry.setText(documentSnapshot.getString(Constants.KEY_COUNTRY));
            binding.inputJob.setText(documentSnapshot.getString(Constants.KEY_JOB));
        });
    }

    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(
                UpdateInformActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                onDateSetListener, year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        onDateSetListener = (view, year1, month1, dayOfMonth) -> {
            month1 = month1 + 1;
            String date = dayOfMonth + "/" + month1 + "/" + year1;
            binding.birthday.setText(date);
        };
    }

    private void updateInform() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        if (Objects.requireNonNull(binding.inputCity.getText()).toString().trim().isEmpty()) {
            updates.put(Constants.KEY_CITY, getString(R.string.undetected));
        } else {
            updates.put(Constants.KEY_CITY, binding.inputCity.getText().toString().trim());
        }
        if (Objects.requireNonNull(binding.inputCountry.getText()).toString().trim().isEmpty()) {
            updates.put(Constants.KEY_COUNTRY, getString(R.string.undetected));
        } else {
            updates.put(Constants.KEY_COUNTRY, binding.inputCountry.getText().toString().trim());
        }
        if (Objects.requireNonNull(binding.inputJob.getText()).toString().trim().isEmpty()) {
            updates.put(Constants.KEY_JOB, getString(R.string.undetected));
        } else {
            updates.put(Constants.KEY_JOB, binding.inputJob.getText().toString().trim());
        }
        if (binding.male.isChecked()) {
            updates.put(Constants.KEY_GENDER, Constants.KEY_GENDER_MALE);
        } else {
            updates.put(Constants.KEY_GENDER, Constants.KEY_GENDER_FEMALE);
        }
        if (binding.birthday.getText().toString().equals(getString(R.string.select))) {
            updates.put(Constants.KEY_BIRTHDAY, getString(R.string.undetected));
        } else {
            updates.put(Constants.KEY_BIRTHDAY, binding.birthday.getText().toString());
        }
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    showToast(getString(R.string.update_inform_success));
                    finish();
                })
                .addOnFailureListener(e -> showToast(getString(R.string.update_inform_fail)));
    }
}