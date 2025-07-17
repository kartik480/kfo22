package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.kfinone.app.databinding.ActivityBankMasterBinding;
import com.google.android.material.card.MaterialCardView;

public class BankMasterActivity extends AppCompatActivity {
    private ActivityBankMasterBinding binding;
    private MaterialCardView banksCard;
    private MaterialCardView vendorBanksCard;
    private MaterialCardView accountTypeCard;
    private MaterialCardView bankersDesignationCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBankMasterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up back button
        binding.backButton.setOnClickListener(v -> finish());

        // Initialize views
        banksCard = binding.banksCard;
        vendorBanksCard = binding.vendorBanksCard;
        accountTypeCard = binding.accountTypeCard;
        bankersDesignationCard = binding.bankersDesignationCard;

        // Set click listeners
        banksCard.setOnClickListener(v -> {
            Intent intent = new Intent(BankMasterActivity.this, BanksActivity.class);
            startActivity(intent);
        });

        vendorBanksCard.setOnClickListener(v -> {
            Intent intent = new Intent(BankMasterActivity.this, VendorBanksActivity.class);
            startActivity(intent);
        });

        accountTypeCard.setOnClickListener(v -> {
            Intent intent = new Intent(BankMasterActivity.this, AccountTypeActivity.class);
            startActivity(intent);
        });

        bankersDesignationCard.setOnClickListener(v -> {
            Intent intent = new Intent(BankMasterActivity.this, BankersDesignationActivity.class);
            startActivity(intent);
        });
    }
} 
