package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.kfinone.app.databinding.ActivityDsaCodeMasterBinding;

public class DsaCodeMasterActivity extends AppCompatActivity {
    private ActivityDsaCodeMasterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDsaCodeMasterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize back button
        binding.backButton.setOnClickListener(v -> finish());

        // Add DSA card click listener
        binding.addDsaCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddDsaActivity.class);
            startActivity(intent);
        });

        // DSA List card click listener
        binding.dsaListCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, DsaListActivity.class);
            startActivity(intent);
        });
    }
} 
