package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class BusinessHeadAgentActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;

    // Agent boxes
    private CardView addAgentBox;
    private CardView myAgentBox;

    // User data
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_agent);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupClickListeners();
        loadAgentData();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);

        // Agent boxes
        addAgentBox = findViewById(R.id.addAgentBox);
        myAgentBox = findViewById(R.id.myAgentBox);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());

        // Agent boxes click listeners
        addAgentBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessHeadAddAgentActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        myAgentBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessHeadMyAgentActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
    }

    private void goBack() {
        Intent intent = new Intent(this, BusinessHeadPanelActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void passUserDataToIntent(Intent intent) {
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }

    private void loadAgentData() {
        // TODO: Implement API call to load agent data
        // For now, set sample data
        Toast.makeText(this, "Agent data loaded successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
} 