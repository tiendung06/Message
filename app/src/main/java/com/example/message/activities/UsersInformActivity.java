package com.example.message.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import com.example.message.databinding.ActivityUsersInformBinding;
import com.example.message.utilities.Constants;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsersInformActivity extends BaseActivity {
    private ActivityUsersInformBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersInformBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
        getUserInform();
    }

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUserInform() {
        String receiverId = getIntent().getStringExtra(Constants.KEY_RECEIVER_ID);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(receiverId);
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            binding.imageProfile.setImageBitmap(getUserImage(documentSnapshot.getString(Constants.KEY_IMAGE)));
            binding.textName.setText(documentSnapshot.getString(Constants.KEY_NAME));
            binding.email.setText(documentSnapshot.getString(Constants.KEY_EMAIL));
            binding.city.setText(documentSnapshot.getString(Constants.KEY_CITY));
            binding.country.setText(documentSnapshot.getString(Constants.KEY_COUNTRY));
            binding.job.setText(documentSnapshot.getString(Constants.KEY_JOB));
        });
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}