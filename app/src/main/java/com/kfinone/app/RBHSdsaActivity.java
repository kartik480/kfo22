package com.kfinone.app;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RBHSdsaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rbh_emp_links);
        ImageButton backButton = findViewById(R.id.backButton);
        TextView titleText = findViewById(R.id.titleText);
        titleText.setText("RBH SDSA");
        backButton.setOnClickListener(v -> finish());
    }
} 