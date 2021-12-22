package com.example.message.activities.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.message.R;
import com.example.message.activities.BaseActivity;
import com.example.message.activities.SignInActivity;
import com.example.message.databinding.ActivityUpdateProfileBinding;
import com.example.message.utilities.Constants;
import com.example.message.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class UpdateProfileActivity extends BaseActivity {
    private ActivityUpdateProfileBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.Theme_Message);
        } else {
            setTheme(R.style.Theme_Message);
        }
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserDetails();
    }

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.imageSignOut.setOnClickListener(v -> signOut());
        binding.changeName.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), UpdateUsernameActivity.class);
            startActivity(intent);
        });
        binding.changePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), UpdatePasswordActivity.class);
            startActivity(intent);
        });
        binding.changeAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), UpdateAvatarActivity.class);
            startActivity(intent);
        });
        darkMode();
    }

    void darkMode() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.darkMode.setChecked(true);
        }
        binding.darkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                preferenceManager.putBoolean(Constants.DARK_MODE, true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                preferenceManager.putBoolean(Constants.DARK_MODE, false);
            }
        });
    }

    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signOut() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.sign_out);
        alert.setMessage(R.string.confirm_sign_out);
        alert.setPositiveButton(R.string.sign_out, (dialog, which) -> {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                    preferenceManager.getString(Constants.KEY_USER_ID));
            HashMap<String, Object> updates = new HashMap<>();
            updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
            documentReference.update(updates).addOnSuccessListener(unused -> {
                        preferenceManager.clear();
                        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> showToast(getString(R.string.cannot_sign_out)));
        });
        alert.setNegativeButton(R.string.cancel, null);
        alert.create().show();
    }
}