package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class MoreActivity extends AppCompatActivity {

    private MaterialCardView dsaCodeCard;
    private MaterialCardView bankersCard;
    private MaterialCardView payoutsCard;
    private MaterialCardView emiCalculatorCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize views
        dsaCodeCard = findViewById(R.id.dsaCodeCard);
        bankersCard = findViewById(R.id.bankersCard);
        payoutsCard = findViewById(R.id.payoutsCard);
        emiCalculatorCard = findViewById(R.id.emiCalculatorCard);

        // Set click listeners
        dsaCodeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreActivity.this, DsaCodeMasterActivity.class);
                startActivity(intent);
            }
        });

        bankersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreActivity.this, BankersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        payoutsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreActivity.this, PayoutActivity.class);
                startActivity(intent);
            }
        });

        emiCalculatorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For now, just show a toast message since EMI Calculator doesn't exist yet
                Toast.makeText(MoreActivity.this, "EMI Calculator coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 
