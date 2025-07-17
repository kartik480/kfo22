package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;

public class PartnerMasterActivity extends AppCompatActivity {

    private MaterialCardView typeOfPartnerCard;
    private MaterialCardView addPartnerCard;
    private MaterialCardView partnerActiveListCard;
    private MaterialCardView partnerInactiveListCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_master);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Partner Master");
        }

        // Initialize views
        typeOfPartnerCard = findViewById(R.id.typeOfPartnerCard);
        addPartnerCard = findViewById(R.id.addPartnerCard);
        partnerActiveListCard = findViewById(R.id.partnerActiveListCard);
        partnerInactiveListCard = findViewById(R.id.partnerInactiveListCard);

        // Set click listeners
        typeOfPartnerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PartnerMasterActivity.this, PartnerTypeActivity.class);
                startActivity(intent);
            }
        });

        addPartnerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Launch Add Partner activity
                // Intent intent = new Intent(PartnerMasterActivity.this, AddPartnerActivity.class);
                // startActivity(intent);
            }
        });

        partnerActiveListCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PartnerMasterActivity.this, ActivePartnerListActivity.class);
                startActivity(intent);
            }
        });

        partnerInactiveListCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Launch Partner Inactive List activity
                // Intent intent = new Intent(PartnerMasterActivity.this, PartnerInactiveListActivity.class);
                // startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 
