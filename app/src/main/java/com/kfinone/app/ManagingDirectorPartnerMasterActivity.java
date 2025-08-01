package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.card.MaterialCardView;

public class ManagingDirectorPartnerMasterActivity extends AppCompatActivity {

    private MaterialCardView addPartnerCard;
    private MaterialCardView myPartnerCard;
    private MaterialCardView partnerTeamCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar and make fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        // Additional flags to ensure complete fullscreen
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        
        setContentView(R.layout.activity_managing_director_partner_master);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Partner Master");
        }

        // Initialize views
        addPartnerCard = findViewById(R.id.addPartnerCard);
        myPartnerCard = findViewById(R.id.myPartnerCard);
        partnerTeamCard = findViewById(R.id.partnerTeamCard);

        // Set click listeners
        addPartnerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagingDirectorPartnerMasterActivity.this, ManagingDirectorAddPartnerActivity.class);
                startActivity(intent);
            }
        });

        myPartnerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagingDirectorPartnerMasterActivity.this, MyPartnerActivity.class);
                startActivity(intent);
            }
        });

        partnerTeamCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagingDirectorPartnerMasterActivity.this, ManagingDirectorPartnersTeamActivity.class);
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