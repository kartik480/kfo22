package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import com.google.android.material.card.MaterialCardView;
import androidx.appcompat.app.AppCompatActivity;

public class DirectorInsurancePanelActivity extends AppCompatActivity {
    private MaterialCardView directorAddInsuranceBox;
    private MaterialCardView directorMyInsuranceBox;
    private MaterialCardView directorTeamInsuranceBox;
    private ImageButton directorBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_insurance_panel);

        directorAddInsuranceBox = findViewById(R.id.directorAddInsuranceBox);
        directorMyInsuranceBox = findViewById(R.id.directorMyInsuranceBox);
        directorTeamInsuranceBox = findViewById(R.id.directorTeamInsuranceBox);
        directorBackButton = findViewById(R.id.directorBackButton);

        directorAddInsuranceBox.setOnClickListener(v -> {
            startActivity(new Intent(this, DirectorAddInsuranceActivity.class));
        });
        directorMyInsuranceBox.setOnClickListener(v -> {
            // TODO: Launch DirectorMyInsuranceActivity
        });
        directorTeamInsuranceBox.setOnClickListener(v -> {
            // TODO: Launch DirectorTeamInsuranceActivity
        });
        directorBackButton.setOnClickListener(v -> onBackPressed());
    }
} 