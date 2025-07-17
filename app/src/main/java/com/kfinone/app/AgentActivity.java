package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class AgentActivity extends AppCompatActivity {

    private MaterialCardView addAgentCard;
    private MaterialCardView myAgentCard;
    private MaterialCardView teamAgentCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent);

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize views
        addAgentCard = findViewById(R.id.addAgentCard);
        myAgentCard = findViewById(R.id.myAgentCard);
        teamAgentCard = findViewById(R.id.teamAgentCard);

        // Set click listeners
        addAgentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgentActivity.this, AddAgentActivity.class);
                startActivity(intent);
            }
        });

        myAgentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgentActivity.this, MyAgentActivity.class);
                startActivity(intent);
            }
        });

        teamAgentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AgentActivity.this, EmpTeamActivity.class);
                startActivity(intent);
            }
        });
    }
} 
