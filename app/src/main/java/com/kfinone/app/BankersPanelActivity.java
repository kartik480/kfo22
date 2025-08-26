package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class BankersPanelActivity extends AppCompatActivity {

    private MaterialCardView listBox;
    private TextView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bankers_panel);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        listBox = findViewById(R.id.listBox);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());



        listBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BankerListActivity.class);
            startActivity(intent);
        });
    }
} 