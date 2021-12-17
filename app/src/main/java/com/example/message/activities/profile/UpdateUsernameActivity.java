package com.example.message.activities.profile;

import android.os.Bundle;
import android.widget.Toast;
import com.example.message.activities.BaseActivity;
import com.example.message.databinding.ActivityUpdateUsernameBinding;
import com.example.message.utilities.Constants;
import com.example.message.utilities.PreferenceManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Objects;

public class UpdateUsernameActivity extends BaseActivity {
    private ActivityUpdateUsernameBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateUsernameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.buttonChangeUsername.setOnClickListener(v -> {
            if (isValidUsernameDetails()) {
                changeUsername();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void changeUsername() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_NAME, Objects.requireNonNull(
                binding.inputChangeUsername.getText()).toString().trim());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.putString(Constants.KEY_NAME,
                            binding.inputChangeUsername.getText().toString().trim());
                    showToast("Đổi tên người dùng thành công");

                })
                .addOnFailureListener(e -> showToast("Không thể đổi tên người dùng"));

        CollectionReference collectionReference = database.collection(Constants.KEY_COLLECTION_CONVERSATIONS);
        HashMap<String, Object> updateSenderName = new HashMap<>();
        updateSenderName.put(Constants.KEY_SENDER_NAME, Objects.requireNonNull(
                binding.inputChangeUsername.getText()).toString().trim());
        collectionReference.whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String documentID = document.getId();
                        collectionReference
                                .document(documentID)
                                .update(updateSenderName)
                                .addOnSuccessListener(unused -> {
                                    finish();
                                });
                    }
                });

        HashMap<String, Object> updateReceiverName = new HashMap<>();
        updateReceiverName.put(Constants.KEY_RECEIVER_NAME, Objects.requireNonNull(
                binding.inputChangeUsername.getText()).toString().trim());
        collectionReference.whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String documentID = document.getId();
                        collectionReference
                                .document(documentID)
                                .update(updateReceiverName)
                                .addOnSuccessListener(unused -> {
                                    finish();
                                });
                    }
                });
    }

    private Boolean isValidUsernameDetails() {
        if (Objects.requireNonNull(binding.inputChangeUsername.getText()).toString().trim().isEmpty()) {
            binding.inputChangeUsername.setError("Tên người dùng không dược để trống");
            binding.inputChangeUsername.requestFocus();
            return false;
        }
        return true;
    }
}