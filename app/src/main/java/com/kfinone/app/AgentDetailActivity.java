package com.kfinone.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AgentDetailActivity extends AppCompatActivity {

    private ImageView backButton;
    private TextView agentNameText, companyNameText, phoneText, altPhoneText, emailText;
    private TextView partnerTypeText, stateText, locationText, addressText, statusText;
    private TextView createdByText, createdAtText, updatedAtText;
    private CardView visitingCardCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_detail);
        
        initializeViews();
        setupClickListeners();
        loadAgentData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        agentNameText = findViewById(R.id.agentNameText);
        companyNameText = findViewById(R.id.companyNameText);
        phoneText = findViewById(R.id.phoneText);
        altPhoneText = findViewById(R.id.altPhoneText);
        emailText = findViewById(R.id.emailText);
        partnerTypeText = findViewById(R.id.partnerTypeText);
        stateText = findViewById(R.id.stateText);
        locationText = findViewById(R.id.locationText);
        addressText = findViewById(R.id.addressText);
        statusText = findViewById(R.id.statusText);
        createdByText = findViewById(R.id.createdByText);
        createdAtText = findViewById(R.id.createdAtText);
        updatedAtText = findViewById(R.id.updatedAtText);
        visitingCardCard = findViewById(R.id.visitingCardCard);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
    }

    private void loadAgentData() {
        // Get agent data from intent
        String agentName = getIntent().getStringExtra("AGENT_NAME");
        String companyName = getIntent().getStringExtra("COMPANY_NAME");
        String phone = getIntent().getStringExtra("PHONE");
        String altPhone = getIntent().getStringExtra("ALT_PHONE");
        String email = getIntent().getStringExtra("EMAIL");
        String partnerType = getIntent().getStringExtra("PARTNER_TYPE");
        String state = getIntent().getStringExtra("STATE");
        String location = getIntent().getStringExtra("LOCATION");
        String address = getIntent().getStringExtra("ADDRESS");
        String status = getIntent().getStringExtra("STATUS");
        String createdBy = getIntent().getStringExtra("CREATED_BY");
        String createdAt = getIntent().getStringExtra("CREATED_AT");
        String updatedAt = getIntent().getStringExtra("UPDATED_AT");
        String visitingCard = getIntent().getStringExtra("VISITING_CARD");

        // Set the data to views
        if (agentName != null) agentNameText.setText(agentName);
        if (companyName != null) companyNameText.setText(companyName);
        if (phone != null) phoneText.setText(phone);
        if (altPhone != null && !altPhone.isEmpty()) {
            altPhoneText.setText(altPhone);
            altPhoneText.setVisibility(View.VISIBLE);
        } else {
            altPhoneText.setVisibility(View.GONE);
        }
        if (email != null && !email.isEmpty()) {
            emailText.setText(email);
            emailText.setVisibility(View.VISIBLE);
        } else {
            emailText.setVisibility(View.GONE);
        }
        if (partnerType != null) partnerTypeText.setText(partnerType);
        if (state != null) stateText.setText(state);
        if (location != null) locationText.setText(location);
        if (address != null && !address.isEmpty()) {
            addressText.setText(address);
            addressText.setVisibility(View.VISIBLE);
        } else {
            addressText.setVisibility(View.GONE);
        }
        if (status != null) statusText.setText(status);
        if (createdBy != null) createdByText.setText(createdBy);
        if (createdAt != null) createdAtText.setText(createdAt);
        if (updatedAt != null) updatedAtText.setText(updatedAt);

        // Handle visiting card
        if (visitingCard != null && !visitingCard.isEmpty()) {
            visitingCardCard.setVisibility(View.VISIBLE);
            // You can add image loading logic here if needed
        } else {
            visitingCardCard.setVisibility(View.GONE);
        }
    }
}
