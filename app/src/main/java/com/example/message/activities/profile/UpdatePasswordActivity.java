package com.example.message.activities.profile;

import android.os.Bundle;
import android.widget.Toast;
import com.example.message.activities.BaseActivity;
import com.example.message.databinding.ActivityUpdatePasswordBinding;
import com.example.message.utilities.Constants;
import com.example.message.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class UpdatePasswordActivity extends BaseActivity {
    private ActivityUpdatePasswordBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdatePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.buttonChangePassword.setOnClickListener(v -> {
            if (isValidPasswordDetails()) {
                changePassword();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void changePassword() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_PASSWORD, Objects.requireNonNull(
                binding.inputChangePassword.getText()).toString().trim());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.putString(Constants.KEY_PASSWORD,
                            binding.inputChangePassword.getText().toString().trim());
                    showToast("Đổi mật khẩu thành công");
                    onBackPressed();
                })
                .addOnFailureListener(e -> showToast("Không thể đổi mật khẩu"));
    }

    private Boolean isValidPasswordDetails() {
        if (Objects.requireNonNull(binding.inputCurrentPassword.getText()).toString().trim().isEmpty()) {
            binding.inputCurrentPassword.setError("Nhập mật khẩu hiện tại");
            binding.inputCurrentPassword.requestFocus();
            return false;
        } else if (!Objects.requireNonNull(binding.inputCurrentPassword.getText()).toString().trim().equals(
                preferenceManager.getString(Constants.KEY_PASSWORD))) {
            binding.inputCurrentPassword.setError("Mật khẩu cũ không đúng");
            binding.inputCurrentPassword.requestFocus();
            return false;
        } else if (Objects.requireNonNull(binding.inputChangePassword.getText()).toString().trim().isEmpty()) {
            binding.inputChangePassword.setError("Nhập ật khẩu mới");
            binding.inputChangePassword.requestFocus();
            return false;
        } else if (!binding.inputChangePassword.getText().toString().equals(
                Objects.requireNonNull(binding.inputConfirmChangePassword.getText()).toString())) {
            binding.inputConfirmChangePassword.setError("Password & Confirm password must be same");
            binding.inputConfirmChangePassword.requestFocus();
            return false;
        }
        return true;
    }
}