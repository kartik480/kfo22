package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;
import com.kfinone.app.ManagingDirectorPayoutPanelActivity;

public class PayoutActivity extends AppCompatActivity {

    private MaterialCardView categoryCard;
    private MaterialCardView payoutCard;
    private MaterialCardView payoutTypeCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Payout");

        // Initialize new cards
        categoryCard = findViewById(R.id.categoryCard);
        payoutCard = findViewById(R.id.payoutCard);
        payoutTypeCard = findViewById(R.id.payoutTypeCard);

        // Set click listeners for new cards
        categoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayoutActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

        payoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayoutActivity.this, AddPayoutActivity.class);
                startActivity(intent);
            }
        });

        payoutTypeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayoutActivity.this, PayoutTypeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 
