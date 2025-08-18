package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class BusinessHeadBankersListActivity extends AppCompatActivity {
    
    private TextView titleText, welcomeText;
    private View listBox;
    private String userName, firstName, lastName, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_bankers_list);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        userId = intent.getStringExtra("USER_ID");
        
        initializeViews();
        setupToolbar();
        updateWelcomeMessage();
    }

    private void initializeViews() {
        titleText = findViewById(R.id.titleText);
        welcomeText = findViewById(R.id.welcomeText);
        listBox = findViewById(R.id.listBox);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Business Head Bankers List");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            }
        }
        
        // List Box click listener
        listBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BankerListActivity.class);
            intent.putExtra("USERNAME", userName);
            intent.putExtra("FIRST_NAME", firstName);
            intent.putExtra("LAST_NAME", lastName);
            intent.putExtra("USER_ID", userId);
            intent.putExtra("SOURCE_PANEL", "BUSINESS_HEAD_BANKERS_LIST");
            startActivity(intent);
        });
    }

    private void updateWelcomeMessage() {
        if (firstName != null && lastName != null && !firstName.isEmpty() && !lastName.isEmpty()) {
            welcomeText.setText("Welcome, " + firstName + " " + lastName);
        } else if (firstName != null && !firstName.isEmpty()) {
            welcomeText.setText("Welcome, " + firstName);
        } else if (userName != null && !userName.isEmpty()) {
            welcomeText.setText("Welcome, " + userName);
        } else {
            welcomeText.setText("Welcome, Business Head");
        }
    }

    private void goBack() {
        // Navigate back to Business Head Panel
        Intent intent = new Intent(this, BusinessHeadPanelActivity.class);
        intent.putExtra("USERNAME", userName);
        intent.putExtra("FIRST_NAME", firstName);
        intent.putExtra("LAST_NAME", lastName);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        goBack();
        return true;
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
