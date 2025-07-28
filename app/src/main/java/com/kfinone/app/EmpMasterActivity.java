package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class EmpMasterActivity extends AppCompatActivity {

    // User data
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_master);

        // Get user data from intent
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            if (userId == null) {
                userId = intent.getStringExtra("userId"); // Try alternative key
            }
            userName = intent.getStringExtra("USERNAME");
            if (userName == null) {
                userName = intent.getStringExtra("userName"); // Try alternative key
            }
        }

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize card views
        MaterialCardView addEmpCard = findViewById(R.id.addEmpCard);
        MaterialCardView activeEmpListCard = findViewById(R.id.activeEmpListCard);
        MaterialCardView inactiveEmpListCard = findViewById(R.id.inactiveEmpListCard);
        MaterialCardView empDepartmentCard = findViewById(R.id.empDepartmentCard);
        MaterialCardView empDesignationCard = findViewById(R.id.empDesignationCard);

        // Set click listeners
        addEmpCard.setOnClickListener(v -> {
            try {
                // Start AddEmpDetailsActivity using direct class reference
                Intent newIntent = new Intent(EmpMasterActivity.this, AddEmpDetailsActivity.class);
                passUserDataToIntent(newIntent);
                startActivity(newIntent);
            } catch (Exception e) {
                Toast.makeText(EmpMasterActivity.this, "Error starting AddEmpDetailsActivity: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });

        activeEmpListCard.setOnClickListener(v -> {
            Intent newIntent = new Intent(EmpMasterActivity.this, ActiveEmpListActivity.class);
            passUserDataToIntent(newIntent);
            startActivity(newIntent);
        });

        inactiveEmpListCard.setOnClickListener(v -> {
            Intent newIntent = new Intent(EmpMasterActivity.this, InactiveEmpListActivity.class);
            passUserDataToIntent(newIntent);
            startActivity(newIntent);
        });

        empDepartmentCard.setOnClickListener(v -> {
            Intent newIntent = new Intent(EmpMasterActivity.this, EmpDepartmentActivity.class);
            passUserDataToIntent(newIntent);
            startActivity(newIntent);
        });

        empDesignationCard.setOnClickListener(v -> {
            Intent newIntent = new Intent(EmpMasterActivity.this, EmpDesignationActivity.class);
            passUserDataToIntent(newIntent);
            startActivity(newIntent);
        });
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) {
            intent.putExtra("USER_ID", userId);
            intent.putExtra("userId", userId); // Also pass with lowercase key for permission activities
        }
        if (userName != null) {
            intent.putExtra("USERNAME", userName);
            intent.putExtra("userName", userName); // Also pass with lowercase key for permission activities
        }
    }
} 
