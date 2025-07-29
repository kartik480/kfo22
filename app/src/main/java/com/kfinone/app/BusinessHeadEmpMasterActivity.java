package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class BusinessHeadEmpMasterActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;

    // Business Head Emp Master boxes
    private CardView addEmpBox;
    private CardView activeEmpListBox;
    private CardView inactiveEmpListBox;

    // User data
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_emp_master);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupClickListeners();
        loadBusinessHeadEmpMasterData();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);

        // Business Head Emp Master boxes
        addEmpBox = findViewById(R.id.addEmpBox);
        activeEmpListBox = findViewById(R.id.activeEmpListBox);
        inactiveEmpListBox = findViewById(R.id.inactiveEmpListBox);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());

        // Business Head Emp Master boxes click listeners
        addEmpBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessHeadAddEmpActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        activeEmpListBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessHeadActiveEmpListActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        inactiveEmpListBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessHeadInactiveEmpListActivity.class);
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

    private void loadBusinessHeadEmpMasterData() {
        // TODO: Implement API call to load Business Head employee master data
        // For now, set sample data
        Toast.makeText(this, "Data loaded successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
} 