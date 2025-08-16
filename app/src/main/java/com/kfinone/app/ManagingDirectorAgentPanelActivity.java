package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ManagingDirectorAgentPanelActivity extends AppCompatActivity {

    private static final String TAG = "ManagingDirectorAgentPanel";

    // UI Elements
    private TextView welcomeText;

    // User data
    private String userId;
    private String username;
    private String firstName;
    private String lastName;

    // Volley request queue
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managing_director_agent_panel);

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Get user data from intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupClickListeners();
        updateWelcomeMessage();
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
    }

    private void setupClickListeners() {
        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        // My Agent Box
        findViewById(R.id.myAgentBox).setOnClickListener(v -> {
            Intent intent = new Intent(this, ManagingDirectorMyAgentSimpleActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        // Agent Team Box
        findViewById(R.id.agentTeamBox).setOnClickListener(v -> {
            Intent intent = new Intent(this, ManagingDirectorAgentTeamSimpleActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
    }

    private void updateWelcomeMessage() {
        if (firstName != null && lastName != null) {
            welcomeText.setText("Welcome, " + firstName + " " + lastName + "!");
        } else if (username != null) {
            welcomeText.setText("Welcome, " + username + "!");
        } else {
            welcomeText.setText("Welcome, Managing Director!");
        }
    }

    private void passUserDataToIntent(Intent intent) {
        // Get current user data and pass it to the new activity
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
        if (username != null) intent.putExtra("USERNAME", username);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}
