package com.example.message.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import androidx.appcompat.app.AppCompatActivity;
import com.example.message.R;
import com.example.message.databinding.ActivityAccountInformBinding;
import com.example.message.utilities.Constants;
import com.example.message.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class AccountInformActivity extends AppCompatActivity {
    private ActivityAccountInformBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountInformBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListener();
        getUserInform();
    }

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUserInform() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            binding.email.setText(documentSnapshot.getString(Constants.KEY_EMAIL));
            if (Objects.requireNonNull(documentSnapshot.getString(Constants.KEY_GENDER)).equals(Constants.KEY_GENDER_MALE)) {
                binding.gender.setText(R.string.male);
            } else if (Objects.requireNonNull(documentSnapshot.getString(Constants.KEY_GENDER)).equals(Constants.KEY_GENDER_FEMALE)) {
                binding.gender.setText(R.string.female);
            } else {
                binding.gender.setText(R.string.undetected);
            }
            binding.birthday.setText(documentSnapshot.getString(Constants.KEY_BIRTHDAY));
            binding.city.setText(documentSnapshot.getString(Constants.KEY_CITY));
            binding.country.setText(documentSnapshot.getString(Constants.KEY_COUNTRY));
            binding.job.setText(documentSnapshot.getString(Constants.KEY_JOB));
        });
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }
}