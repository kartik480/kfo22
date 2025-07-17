package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class AgentMasterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_master);

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize card views
        MaterialCardView callingStatusCard = findViewById(R.id.callingStatusCard);
        MaterialCardView callingSubStatusCard = findViewById(R.id.callingSubStatusCard);
        MaterialCardView agentCard = findViewById(R.id.agentCard);

        // Set click listeners
        callingStatusCard.setOnClickListener(v -> {
            Intent intent = new Intent(AgentMasterActivity.this, CallingStatusActivity.class);
            startActivity(intent);
        });

        callingSubStatusCard.setOnClickListener(v -> {
            Intent intent = new Intent(AgentMasterActivity.this, CallingSubStatusActivity.class);
            startActivity(intent);
        });

        agentCard.setOnClickListener(v -> {
            Intent intent = new Intent(AgentMasterActivity.this, AgentActivity.class);
            startActivity(intent);
        });
    }
} 
