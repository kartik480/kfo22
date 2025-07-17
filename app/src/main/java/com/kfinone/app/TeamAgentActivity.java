package com.kfinone.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class TeamAgentActivity extends AppCompatActivity {

    private AutoCompleteTextView selectUserDropdown;
    private MaterialButton showDataButton;
    private MaterialButton resetButton;
    private LinearLayout tableContent;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_agent);

        // Initialize views
        selectUserDropdown = findViewById(R.id.selectUserDropdown);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);
        tableContent = findViewById(R.id.tableContent);
        backButton = findViewById(R.id.backButton);

        // Setup back button
        backButton.setOnClickListener(v -> finish());

        // Setup dropdown with sample data
        String[] users = {"User 1", "User 2", "User 3", "User 4", "User 5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, users);
        selectUserDropdown.setAdapter(adapter);

        // Show Data button click listener
        showDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedUser = selectUserDropdown.getText().toString();
                if (selectedUser.isEmpty()) {
                    Toast.makeText(TeamAgentActivity.this, "Please select a user", Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO: Implement actual data fetching and display
                displaySampleData();
            }
        });

        // Reset button click listener
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUserDropdown.setText("");
                tableContent.removeAllViews();
            }
        });
    }

    private void displaySampleData() {
        // Clear existing data
        tableContent.removeAllViews();

        // Sample data - replace with actual data from your backend
        String[][] sampleData = {
            {"John Doe", "1234567890", "john@example.com", "Admin"},
            {"Jane Smith", "9876543210", "jane@example.com", "Manager"},
            {"Bob Johnson", "5555555555", "bob@example.com", "Supervisor"}
        };

        // Add rows to the table
        for (String[] row : sampleData) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setPadding(8, 8, 8, 8);

            // Add columns
            for (int i = 0; i < row.length; i++) {
                TextView textView = new TextView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT);
                
                // Set weights based on column
                switch (i) {
                    case 0: // Full Name
                        params.weight = 2;
                        break;
                    case 1: // Mobile
                        params.weight = 1.5f;
                        break;
                    case 2: // Email
                        params.weight = 2;
                        break;
                    case 3: // Created By
                        params.weight = 1.5f;
                        break;
                }
                
                textView.setLayoutParams(params);
                textView.setText(row[i]);
                rowLayout.addView(textView);
            }

            // Add Action button
            MaterialButton actionButton = new MaterialButton(this);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT);
            buttonParams.weight = 1;
            actionButton.setLayoutParams(buttonParams);
            actionButton.setText("View");
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Implement view action
                    Toast.makeText(TeamAgentActivity.this, "View clicked for " + row[0], Toast.LENGTH_SHORT).show();
                }
            });
            rowLayout.addView(actionButton);

            tableContent.addView(rowLayout);
        }
    }
} 
