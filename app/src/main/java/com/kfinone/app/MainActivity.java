package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.kfinone.app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize DSA-Code Master card
        binding.dsaCodeMasterCard.setOnClickListener(v -> {
            // Launch DSA-Code Master activity
            Intent intent = new Intent(MainActivity.this, DsaCodeMasterActivity.class);
            startActivity(intent);
        });
    }
} 
