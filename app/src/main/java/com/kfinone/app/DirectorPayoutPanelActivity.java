package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class DirectorPayoutPanelActivity extends AppCompatActivity {
    private MaterialCardView payoutTeamBox;
    private ImageButton backButton;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_payout_panel);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        String userId = intent.getStringExtra("USER_ID");
        
        if (userName == null || userName.isEmpty()) {
            userName = "Director"; // Default fallback
        }
        
        // Debug logging
        Log.d("DirectorPayoutPanel", "Received userName: " + userName);
        Log.d("DirectorPayoutPanel", "Received userId: " + userId);
        
        if (userId == null || userId.isEmpty()) {
            Log.e("DirectorPayoutPanel", "CRITICAL ERROR: No valid userId received!");
        } else {
            Log.d("DirectorPayoutPanel", "✓ Valid userId received: " + userId);
        }
        
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        payoutTeamBox = findViewById(R.id.payoutTeamBox);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());
        
        payoutTeamBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, DirectorPayoutTeamActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
            intent.putExtra("USER_DESIGNATION", "Director");
            startActivity(intent);
        });
    }
    
    private void goBack() {
        // Navigate back to Director home page (using SpecialPanelActivity as fallback)
        Intent intent = new Intent(this, SpecialPanelActivity.class);
        intent.putExtra("USERNAME", userName);
        intent.putExtra("USER_ID", getIntent().getStringExtra("USER_ID"));
        intent.putExtra("SOURCE_PANEL", "DIRECTOR_PANEL");
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onBackPressed() {
        goBack();
    }
}
