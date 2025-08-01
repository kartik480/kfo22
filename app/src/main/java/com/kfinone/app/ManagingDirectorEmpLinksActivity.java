package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

public class ManagingDirectorEmpLinksActivity extends AppCompatActivity {

    private CardView myEmpLinksButton;
    private CardView teamEmpLinksButton;
    
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managing_director_emp_links);

        // Get user data from intent
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            userName = intent.getStringExtra("USERNAME");
            firstName = intent.getStringExtra("FIRST_NAME");
            lastName = intent.getStringExtra("LAST_NAME");
        }

        initializeViews();
        setupClickListeners();
        setupToolbar();
    }

    private void initializeViews() {
        myEmpLinksButton = findViewById(R.id.myEmpLinksButton);
        teamEmpLinksButton = findViewById(R.id.teamEmpLinksButton);
    }

    private void setupClickListeners() {
        myEmpLinksButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManagingDirectorMyEmpLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        teamEmpLinksButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManagingDirectorTeamEmpLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Managing Director Employee Links");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        // Navigate back to Managing Director panel (SpecialPanelActivity)
        Intent intent = new Intent(this, SpecialPanelActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }
} 