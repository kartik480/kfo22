package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class BusinessHeadMyPartnerActivity extends AppCompatActivity {

    private View backButton;
    private LinearLayout partnerListContainer;
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_my_partner);
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        initializeViews();
        setupClickListeners();
        loadActivePartnerList();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        partnerListContainer = findViewById(R.id.partnerListContainer);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());
    }

    private void loadActivePartnerList() {
        addPartnerItem("John Doe", "1234567890", "john@example.com", "******", "Admin User");
        addPartnerItem("Jane Smith", "9876543210", "jane@example.com", "******", "Business Head");
        addPartnerItem("Mike Johnson", "5555555555", "mike@example.com", "******", "Manager");
    }

    private void addPartnerItem(String name, String phone, String email, String password, String createdBy) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 16);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(12);
        cardView.setCardElevation(4);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.background_light));

        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(20, 20, 20, 20);

        TextView nameText = new TextView(this);
        nameText.setText("Name: " + name);
        nameText.setTextSize(16);
        nameText.setTextColor(getResources().getColor(R.color.black));
        nameText.setPadding(0, 0, 0, 8);
        contentLayout.addView(nameText);

        TextView phoneText = new TextView(this);
        phoneText.setText("Phone: " + phone);
        phoneText.setTextSize(16);
        phoneText.setTextColor(getResources().getColor(R.color.black));
        phoneText.setPadding(0, 0, 0, 8);
        contentLayout.addView(phoneText);

        TextView emailText = new TextView(this);
        emailText.setText("Email: " + email);
        emailText.setTextSize(16);
        emailText.setTextColor(getResources().getColor(R.color.black));
        emailText.setPadding(0, 0, 0, 8);
        contentLayout.addView(emailText);

        TextView passwordText = new TextView(this);
        passwordText.setText("Password: " + password);
        passwordText.setTextSize(16);
        passwordText.setTextColor(getResources().getColor(R.color.black));
        passwordText.setPadding(0, 0, 0, 8);
        contentLayout.addView(passwordText);

        TextView createdByText = new TextView(this);
        createdByText.setText("Created by: " + createdBy);
        createdByText.setTextSize(16);
        createdByText.setTextColor(getResources().getColor(R.color.black));
        createdByText.setPadding(0, 0, 0, 12);
        contentLayout.addView(createdByText);

        Button actionButton = new Button(this);
        actionButton.setText("View Details");
        actionButton.setTextSize(14);
        actionButton.setTextColor(getResources().getColor(R.color.white));
        actionButton.setBackgroundColor(getResources().getColor(R.color.orange));
        actionButton.setPadding(16, 8, 16, 8);
        
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.gravity = android.view.Gravity.END;
        actionButton.setLayoutParams(buttonParams);
        
        actionButton.setOnClickListener(v -> {
            Toast.makeText(this, "Viewing details for " + name, Toast.LENGTH_SHORT).show();
        });
        
        contentLayout.addView(actionButton);

        cardView.addView(contentLayout);
        partnerListContainer.addView(cardView);
    }

    private void goBack() {
        Intent intent = new Intent(this, BusinessHeadPartnerActivity.class);
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

    @Override
    public void onBackPressed() {
        goBack();
    }
} 