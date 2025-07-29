package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BusinessHeadMyEmpLinksActivity extends AppCompatActivity implements ManageIconAdapter.OnItemClickListener {

    private static final String TAG = "BusinessHeadMyEmpLinks";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    private ImageView backButton;
    private ProgressBar progressBar;
    private LinearLayout contentLayout;
    private LinearLayout errorLayout;
    private TextView errorText;
    private Button retryButton;
    private RecyclerView linksRecyclerView;

    private ManageIconAdapter adapter;
    private List<ManageIcon> manageIcons;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_my_emp_links);

        // Get user ID from intent
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            if (userId == null) {
                userId = intent.getStringExtra("userId");
            }
            
            // Debug logging
            Log.d(TAG, "Received USER_ID: " + intent.getStringExtra("USER_ID"));
            Log.d(TAG, "Received userId: " + intent.getStringExtra("userId"));
            Log.d(TAG, "Final userId: " + userId);
        }

        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        loadManageIcons();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        progressBar = findViewById(R.id.progressBar);
        contentLayout = findViewById(R.id.contentLayout);
        errorLayout = findViewById(R.id.errorLayout);
        errorText = findViewById(R.id.errorText);
        retryButton = findViewById(R.id.retryButton);
        linksRecyclerView = findViewById(R.id.linksRecyclerView);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> onBackPressed());
        retryButton.setOnClickListener(v -> loadManageIcons());
    }

    private void setupRecyclerView() {
        manageIcons = new ArrayList<>();
        adapter = new ManageIconAdapter(this, manageIcons, this);
        linksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        linksRecyclerView.setAdapter(adapter);
    }

    private void loadManageIcons() {
        Log.d(TAG, "Loading manage icons for user ID: " + userId);
        
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "User ID is null or empty");
            showError("User ID not found. Please login again.");
            return;
        }

        showLoading();

        executor.execute(() -> {
            try {
                String url = BASE_URL + "get_business_head_my_emp_links.php?user_id=" + userId;
                URL apiUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    String jsonResponse = response.toString();
                    Log.d(TAG, "API Response: " + jsonResponse);
                    Log.d(TAG, "API URL called: " + url);

                    parseManageIconsResponse(jsonResponse);
                } else {
                    runOnUiThread(() -> showError("Server error: " + responseCode));
                }

            } catch (Exception e) {
                Log.e(TAG, "Error loading manage icons: " + e.getMessage(), e);
                runOnUiThread(() -> showError("Network error: " + e.getMessage()));
            }
        });
    }

    private void parseManageIconsResponse(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            boolean success = jsonObject.getBoolean("success");
            String message = jsonObject.getString("message");
            
            Log.d(TAG, "Parse response - success: " + success + ", message: " + message);

            if (success) {
                JSONArray dataArray = jsonObject.getJSONArray("data");
                Log.d(TAG, "Data array length: " + dataArray.length());
                List<ManageIcon> icons = new ArrayList<>();

                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject iconObject = dataArray.getJSONObject(i);
                    ManageIcon icon = new ManageIcon(
                            iconObject.getInt("id"),
                            iconObject.getString("icon_name"),
                            iconObject.getString("icon_url"),
                            iconObject.getString("icon_image"),
                            iconObject.getString("icon_description"),
                            iconObject.getString("status")
                    );
                    icons.add(icon);
                }

                runOnUiThread(() -> {
                    manageIcons.clear();
                    manageIcons.addAll(icons);
                    adapter.notifyDataSetChanged();
                    showContent();
                });

            } else {
                Log.e(TAG, "API returned success=false: " + message);
                runOnUiThread(() -> showError(message));
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage(), e);
            runOnUiThread(() -> showError("Error parsing response: " + e.getMessage()));
        }
    }

    private void showLoading() {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
        });
    }

    private void showContent() {
        progressBar.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            contentLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorText.setText(message);
        });
    }

    @Override
    public void onItemClick(ManageIcon manageIcon) {
        // Handle icon click - show details or navigate to icon functionality
        Toast.makeText(this, "Clicked: " + manageIcon.getIconName(), Toast.LENGTH_SHORT).show();
        
        // You can add navigation logic here based on icon_url or other properties
        if (manageIcon.getIconUrl() != null && !manageIcon.getIconUrl().isEmpty()) {
            // Navigate to the icon URL or perform specific action
            Log.d(TAG, "Icon URL: " + manageIcon.getIconUrl());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
} 