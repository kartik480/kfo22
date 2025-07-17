package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

public class AccountActivity extends AppCompatActivity {

    private ImageButton backButton;
    private MaterialButton editProfileButton;
    private MaterialButton logoutButton;
    private View changePasswordLayout;
    private View notificationSettingsLayout;
    private View privacySettingsLayout;
    private View helpCenterLayout;
    private View contactSupportLayout;
    private TextView userNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Initialize views
        backButton = findViewById(R.id.backButton);
        editProfileButton = findViewById(R.id.editProfileButton);
        logoutButton = findViewById(R.id.logoutButton);
        changePasswordLayout = findViewById(R.id.changePasswordLayout);
        notificationSettingsLayout = findViewById(R.id.notificationSettingsLayout);
        privacySettingsLayout = findViewById(R.id.privacySettingsLayout);
        helpCenterLayout = findViewById(R.id.helpCenterLayout);
        contactSupportLayout = findViewById(R.id.contactSupportLayout);
        userNameText = findViewById(R.id.userNameText);

        // Get full name from Intent and display it
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("FULL_NAME")) {
                String fullName = intent.getStringExtra("FULL_NAME");
                if (fullName != null && !fullName.isEmpty()) {
                    userNameText.setText(fullName);
                }
            }
        }

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set up click listeners
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement edit profile functionality
                Toast.makeText(AccountActivity.this, "Edit Profile clicked", Toast.LENGTH_SHORT).show();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement logout functionality
                Toast.makeText(AccountActivity.this, "Logout clicked", Toast.LENGTH_SHORT).show();
            }
        });

        changePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement change password functionality
                Toast.makeText(AccountActivity.this, "Change Password clicked", Toast.LENGTH_SHORT).show();
            }
        });

        notificationSettingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement notification settings functionality
                Toast.makeText(AccountActivity.this, "Notification Settings clicked", Toast.LENGTH_SHORT).show();
            }
        });

        privacySettingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement privacy settings functionality
                Toast.makeText(AccountActivity.this, "Privacy Settings clicked", Toast.LENGTH_SHORT).show();
            }
        });

        helpCenterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement help center functionality
                Toast.makeText(AccountActivity.this, "Help Center clicked", Toast.LENGTH_SHORT).show();
            }
        });

        contactSupportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement contact support functionality
                Toast.makeText(AccountActivity.this, "Contact Support clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 
