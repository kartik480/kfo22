package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;

public class BankersActivity extends AppCompatActivity {

    private MaterialCardView addCard;
    private MaterialCardView listCard;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.green));
        
        setContentView(R.layout.activity_bankers);

        // Initialize views
        addCard = findViewById(R.id.addCard);
        listCard = findViewById(R.id.listCard);
        backButton = findViewById(R.id.backButton);

        // Set click listeners
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BankersActivity.this, AddBankerActivity.class);
                startActivity(intent);
            }
        });

        listCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BankersActivity.this, BankerListActivity.class);
                startActivity(intent);
            }
        });
    }
} 
