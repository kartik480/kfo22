package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class BusinessHeadPartnerActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;

    // Partner boxes
    private CardView addPartnerBox;
    private CardView myPartnerBox;

    // User data
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_partner);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupClickListeners();
        loadPartnerData();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);

        // Partner boxes
        addPartnerBox = findViewById(R.id.addPartnerBox);
        myPartnerBox = findViewById(R.id.myPartnerBox);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());

        // Partner boxes click listeners
        addPartnerBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessHeadAddPartnerActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        myPartnerBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessHeadMyPartnerActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
    }

    private void goBack() {
        // Simply finish this activity to return to the previous one
        // This preserves the user data in the BusinessHeadPanelActivity
        finish();
    }

    private void passUserDataToIntent(Intent intent) {
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }

    private void loadPartnerData() {
        // TODO: Implement API call to load partner data
        // For now, set sample data
        Toast.makeText(this, "Partner data loaded successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
} 