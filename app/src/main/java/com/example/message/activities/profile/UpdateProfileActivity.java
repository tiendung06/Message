package com.example.message.activities.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
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
    private String encodedImage;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        alert.setTitle("Đăng xuất");
        alert.setMessage("Bạn có muốn đăng xuất không?");
        alert.setPositiveButton("Đăng xuất", (dialog, which) -> {
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
                    .addOnFailureListener(e -> showToast("Đăng xuất thất bại"));
        });
        alert.setNegativeButton("Hủy", null);
        alert.create().show();
    }
}