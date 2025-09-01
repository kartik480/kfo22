package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MarketingHeadDataLinksActivity extends AppCompatActivity {
    private static final String TAG = "MarketingHeadDataLinks";
    private static final String API_BASE_URL = "https://emp.kfinone.com/mobile/api";

    private TextView titleText, errorText;
    private ProgressBar progressBar;
    private RecyclerView iconsRecyclerView;
    private MarketingHeadDataLinksAdapter adapter;
    private List<MarketingHeadDataIcon> iconList = new ArrayList<>();
    private RequestQueue requestQueue;
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_marketing_head_data_links);
        
        // Get user data from intent
        userName = getIntent().getStringExtra("USERNAME");
        userId = getIntent().getStringExtra("USER_ID");
        firstName = getIntent().getStringExtra("FIRST_NAME");
        lastName = getIntent().getStringExtra("LAST_NAME");

        // Debug logging
        Log.d(TAG, "Received userName: " + userName);
        Log.d(TAG, "Received userId: " + userId);
        Log.d(TAG, "Received firstName: " + firstName);

        if (userName == null || userName.isEmpty()) {
            userName = "Marketing Head";
        }
        
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupVolley();
        loadDataIcons();
    }

    private void initializeViews() {
        titleText = findViewById(R.id.titleText);
        progressBar = findViewById(R.id.progressBar);
        errorText = findViewById(R.id.errorText);
        iconsRecyclerView = findViewById(R.id.iconsRecyclerView);
        
        titleText.setText("My Link Headings");
        
        // Setup back button click listener
        findViewById(R.id.backButton).setOnClickListener(v -> {
            Log.d(TAG, "Back button clicked - navigating to MarketingHeadPanelActivity");
            onBackPressed();
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Data Links");
        }
    }

    private void setupRecyclerView() {
        adapter = new MarketingHeadDataLinksAdapter(this, iconList);
        iconsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        iconsRecyclerView.setAdapter(adapter);
    }

    private void setupVolley() {
        requestQueue = Volley.newRequestQueue(this);
    }

    private void loadDataIcons() {
        showLoading(true);
        hideError();
        
        // Build request body
        JSONObject requestBody = new JSONObject();
        try {
            if (userId != null && !userId.isEmpty()) {
                requestBody.put("user_id", userId);
            } else if (userName != null && !userName.isEmpty()) {
                requestBody.put("username", userName);
            } else {
                showError("No user information available");
                return;
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error creating request body: " + e.getMessage());
            showError("Error creating request: " + e.getMessage());
            return;
        }

        String url = API_BASE_URL + "/get_marketing_head_data_icons.php";
        Log.d(TAG, "Making API call to: " + url);
        Log.d(TAG, "Request body: " + requestBody.toString());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
            Request.Method.POST,
            url,
            requestBody,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "API Response: " + response.toString());
                    parseDataIconsResponse(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Volley Error: " + error.getMessage());
                    String errorMessage = "Network error: " + error.getMessage();
                    if (error.networkResponse != null) {
                        errorMessage += " (Status: " + error.networkResponse.statusCode + ")";
                    }
                    showError(errorMessage);
                }
            }
        );

        requestQueue.add(jsonRequest);
    }

    private void parseDataIconsResponse(JSONObject response) {
        try {
            String status = response.getString("status");
            
            if ("success".equals(status)) {
                JSONArray data = response.getJSONArray("data");
                iconList.clear();
                
                for (int i = 0; i < data.length(); i++) {
                    JSONObject iconObj = data.getJSONObject(i);
                    MarketingHeadDataIcon icon = new MarketingHeadDataIcon(
                        iconObj.optString("id"),
                        iconObj.optString("icon_name"),
                        iconObj.optString("icon_url"),
                        iconObj.optString("icon_image"),
                        iconObj.optString("icon_description")
                    );
                    iconList.add(icon);
                }
                
                adapter.notifyDataSetChanged();
                showLoading(false);
                
                if (iconList.isEmpty()) {
                    showError("No data icons found for this user");
                } else {
                    Toast.makeText(this, "Found " + iconList.size() + " icons", Toast.LENGTH_SHORT).show();
                }
                
            } else {
                String message = response.optString("message", "Unknown error");
                showError("API Error: " + message);
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parse Error: " + e.getMessage());
            showError("Error parsing response: " + e.getMessage());
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        iconsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        showLoading(false);
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
        Log.e(TAG, "Error: " + message);
    }

    private void hideError() {
        errorText.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button pressed - navigating to MarketingHeadPanelActivity");
        // Navigate back to Marketing Head Panel
        Intent backIntent = new Intent(MarketingHeadDataLinksActivity.this, MarketingHeadPanelActivity.class);
        backIntent.putExtra("USERNAME", userName);
        backIntent.putExtra("FIRST_NAME", firstName);
        backIntent.putExtra("LAST_NAME", lastName);
        backIntent.putExtra("USER_ID", userId);
        backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d(TAG, "Starting MarketingHeadPanelActivity with flags: CLEAR_TOP | NEW_TASK");
        startActivity(backIntent);
        finish(); // Close current activity
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            showToast("Settings - Coming Soon!");
            return true;
        } else if (id == R.id.action_help) {
            showToast("Help - Coming Soon!");
            return true;
        } else if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About Data Links");
        builder.setMessage("Data Links Panel v1.0\n\n" +
                "This panel provides access to various data links and resources including:\n" +
                "• External data sources\n" +
                "• Resource links\n" +
                "• Information portals\n\n" +
                "© 2024 KfinOne. All rights reserved.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}
