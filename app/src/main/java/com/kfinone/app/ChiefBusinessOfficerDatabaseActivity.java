package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class ChiefBusinessOfficerDatabaseActivity extends AppCompatActivity {

    private static final String TAG = "CBODatabase";

    // UI Elements
    private ImageView backButton;
    private TextView titleText;
    private ImageView accountIcon;
    private TextView welcomeText;
    private TextView descriptionText;

    // Data Cards
    private MaterialCardView mySalDataCard;
    private MaterialCardView mySenpDataCard;
    private MaterialCardView mySepDataCard;
    private MaterialCardView myNriDataCard;
    private MaterialCardView myEducationalDataCard;

    // Bottom Navigation
    private LinearLayout dashboardButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;
    private LinearLayout profileButton;

    // User data
    private String userId;
    private String username;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chief_business_officer_database);

        // Get user data from intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        // Debug logging
        Log.d(TAG, "Received user data:");
        Log.d(TAG, "USER_ID: " + userId);
        Log.d(TAG, "USERNAME: " + username);
        Log.d(TAG, "FIRST_NAME: " + firstName);
        Log.d(TAG, "LAST_NAME: " + lastName);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        accountIcon = findViewById(R.id.accountIcon);
        welcomeText = findViewById(R.id.welcomeText);
        descriptionText = findViewById(R.id.descriptionText);

        // Data Cards
        mySalDataCard = findViewById(R.id.mySalDataCard);
        mySenpDataCard = findViewById(R.id.mySenpDataCard);
        mySepDataCard = findViewById(R.id.mySepDataCard);
        myNriDataCard = findViewById(R.id.myNriDataCard);
        myEducationalDataCard = findViewById(R.id.myEducationalDataCard);

        // Bottom Navigation
        dashboardButton = findViewById(R.id.dashboardButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);
        profileButton = findViewById(R.id.profileButton);
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> onBackPressed());

        // Account icon
        accountIcon.setOnClickListener(v -> {
            Log.d(TAG, "Account icon clicked");
            Toast.makeText(this, "Account settings - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        // Data Cards
        mySalDataCard.setOnClickListener(v -> {
            Log.d(TAG, "My SAL Data card clicked");
            Intent intent = new Intent(this, ChiefBusinessOfficerSalDataActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        mySenpDataCard.setOnClickListener(v -> {
            Log.d(TAG, "My SENP Data card clicked");
            Toast.makeText(this, "My SENP Data - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to SENP Data activity when implemented
            // Intent intent = new Intent(this, ChiefBusinessOfficerSenpDataActivity.class);
            // passUserDataToIntent(intent);
            // startActivity(intent);
        });

        mySepDataCard.setOnClickListener(v -> {
            Log.d(TAG, "My SEP Data card clicked");
            Toast.makeText(this, "My SEP Data - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to SEP Data activity when implemented
            // Intent intent = new Intent(this, ChiefBusinessOfficerSepDataActivity.class);
            // passUserDataToIntent(intent);
            // startActivity(intent);
        });

        myNriDataCard.setOnClickListener(v -> {
            Log.d(TAG, "My NRI Data card clicked");
            Toast.makeText(this, "My NRI Data - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to NRI Data activity when implemented
            // Intent intent = new Intent(this, ChiefBusinessOfficerNriDataActivity.class);
            // passUserDataToIntent(intent);
            // startActivity(intent);
        });

        myEducationalDataCard.setOnClickListener(v -> {
            Log.d(TAG, "My Educational Data card clicked");
            Toast.makeText(this, "My Educational Data - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Educational Data activity when implemented
            // Intent intent = new Intent(this, ChiefBusinessOfficerEducationalDataActivity.class);
            // passUserDataToIntent(intent);
            // startActivity(intent);
        });

        // Bottom Navigation
        dashboardButton.setOnClickListener(v -> {
            Log.d(TAG, "Dashboard button clicked");
            Intent intent = new Intent(this, ChiefBusinessOfficerPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        reportsButton.setOnClickListener(v -> {
            Log.d(TAG, "Reports button clicked");
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        settingsButton.setOnClickListener(v -> {
            Log.d(TAG, "Settings button clicked");
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        profileButton.setOnClickListener(v -> {
            Log.d(TAG, "Profile button clicked");
            Toast.makeText(this, "Profile - Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
        if (username != null) intent.putExtra("USERNAME", username);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
