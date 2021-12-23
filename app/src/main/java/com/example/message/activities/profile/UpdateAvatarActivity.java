package com.example.message.activities.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.example.message.R;
import com.example.message.activities.BaseActivity;
import com.example.message.databinding.ActivityUpdateAvatarBinding;
import com.example.message.utilities.Constants;
import com.example.message.utilities.PreferenceManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

public class UpdateAvatarActivity extends BaseActivity {
    private ActivityUpdateAvatarBinding binding;
    private String encodedImage;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateAvatarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.btnChangeAvatar.setOnClickListener(v -> {
            if (isValidChangeAvatar()) {
                changeAvatar();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void changeAvatar() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_IMAGE, encodedImage);
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
                    showToast(getString(R.string.update_avatar_success));
                    finish();
                })
                .addOnFailureListener(e -> showToast(getString(R.string.update_avatar_fail)));

        CollectionReference collectionReference = database.collection(Constants.KEY_COLLECTION_CONVERSATIONS);
        HashMap<String, Object> updateSenderImage = new HashMap<>();
        updateSenderImage.put(Constants.KEY_SENDER_IMAGE, encodedImage);
        collectionReference.whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String documentID = document.getId();
                        collectionReference.document(documentID)
                                .update(updateSenderImage).addOnSuccessListener(unused -> finish());
                    }
                });

        HashMap<String, Object> updateReceiverImage = new HashMap<>();
        updateReceiverImage.put(Constants.KEY_RECEIVER_IMAGE, encodedImage);
        collectionReference.whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String documentID = document.getId();
                        collectionReference.document(documentID)
                                .update(updateReceiverImage).addOnSuccessListener(unused -> finish());
                    }
                });
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitMap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitMap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private Boolean isValidChangeAvatar() {
        if (encodedImage == null) {
            showToast(getString(R.string.empty_avatar));
            return false;
        }
        return true;
    }
}