package com.example.message.activities.profile;

import android.os.Bundle;
import android.widget.Toast;
import com.example.message.R;
import com.example.message.activities.BaseActivity;
import com.example.message.databinding.ActivityUpdateInformBinding;
import com.example.message.utilities.Constants;
import com.example.message.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class UpdateInformActivity extends BaseActivity {
    private ActivityUpdateInformBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateInformBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListener();
    }

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.buttonUpdateInform.setOnClickListener(v -> updateInform());
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    showToast(getString(R.string.update_inform_success));
                    finish();
                })
                .addOnFailureListener(e -> showToast(getString(R.string.update_inform_fail)));
    }
}