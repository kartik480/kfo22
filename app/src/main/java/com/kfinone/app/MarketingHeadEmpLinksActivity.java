package com.kfinone.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MarketingHeadEmpLinksActivity extends AppCompatActivity {
    private static final String TAG = "MarketingHeadEmpLinks";
    
    private String username;
    private String firstName;
    private String lastName;
    private String userId;
    
    private TextView welcomeText;
    private LinearLayout empLinksContainer;
    private RequestQueue requestQueue;
    private ExecutorService executor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_head_emp_links);
        
        // Get user data from intent
        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("USERNAME");
            firstName = intent.getStringExtra("FIRST_NAME");
            lastName = intent.getStringExtra("LAST_NAME");
            userId = intent.getStringExtra("USER_ID");
        }
        
        // Initialize Volley
        requestQueue = Volley.newRequestQueue(this);
        executor = Executors.newSingleThreadExecutor();
        
        // Initialize views
        initializeViews();
        setupClickListeners();
        updateWelcomeText();
        
        // Setup back button
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
        
        // Load emp links data
        loadEmpLinksData();
    }
    
    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        empLinksContainer = findViewById(R.id.empLinksContainer);
    }
    
    private void setupClickListeners() {
        // Add any additional click listeners here
    }
    
    private void updateWelcomeText() {
        if (firstName != null && lastName != null) {
            welcomeText.setText("Welcome, " + firstName + " " + lastName + " - Employee Links");
        } else if (username != null) {
            welcomeText.setText("Welcome, " + username + " - Employee Links");
        } else {
            welcomeText.setText("Welcome, Marketing Head - Employee Links");
        }
    }
    
    private void loadEmpLinksData() {
        // Show loading state
        empLinksContainer.removeAllViews();
        addLoadingView();
        
        // First, fetch the manage_icons from tbl_user
        fetchUserManageIcons();
    }
    
    private void fetchUserManageIcons() {
        String url = "https://emp.kfinone.com/mobile/api/get_user_manage_icons.php";
        
        JSONObject postData = new JSONObject();
        try {
            postData.put("username", username);
            postData.put("user_id", userId);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating post data", e);
            showError("Error preparing request data");
            return;
        }
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "User manage icons response: " + response.toString());
                        try {
                            if (response.has("success") && response.getBoolean("success")) {
                                if (response.has("manage_icons")) {
                                    String manageIconsStr = response.getString("manage_icons");
                                    if (manageIconsStr != null && !manageIconsStr.isEmpty()) {
                                        // Parse the manage_icons string (comma-separated IDs)
                                        String[] iconIds = manageIconsStr.split(",");
                                        fetchManageIconDetails(iconIds);
                                    } else {
                                        showNoIconsMessage();
                                    }
                                } else {
                                    showNoIconsMessage();
                                }
                            } else {
                                String message = response.optString("message", "Failed to fetch manage icons");
                                showError(message);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing response", e);
                            showError("Error parsing response data");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching user manage icons", error);
                        showError("Network error: " + error.getMessage());
                    }
                });
        
        requestQueue.add(request);
    }
    
    private void fetchManageIconDetails(String[] iconIds) {
        if (iconIds == null || iconIds.length == 0) {
            showNoIconsMessage();
            return;
        }
        
        // Clear loading view
        empLinksContainer.removeAllViews();
        
        // Create comma-separated string of IDs for the API
        StringBuilder idsBuilder = new StringBuilder();
        for (int i = 0; i < iconIds.length; i++) {
            if (i > 0) idsBuilder.append(",");
            idsBuilder.append(iconIds[i].trim());
        }
        
        String url = "https://emp.kfinone.com/mobile/api/get_manage_icon_details.php";
        
        JSONObject postData = new JSONObject();
        try {
            postData.put("icon_ids", idsBuilder.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error creating post data", e);
            showError("Error preparing request data");
            return;
        }
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Manage icon details response: " + response.toString());
                        try {
                            if (response.has("success") && response.getBoolean("success")) {
                                if (response.has("icons")) {
                                    JSONArray iconsArray = response.getJSONArray("icons");
                                    displayEmpLinks(iconsArray);
                                } else {
                                    showNoIconsMessage();
                                }
                            } else {
                                String message = response.optString("message", "Failed to fetch icon details");
                                showError(message);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing response", e);
                            showError("Error parsing response data");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching manage icon details", error);
                        showError("Network error: " + error.getMessage());
                    }
                });
        
        requestQueue.add(request);
    }
    
    private void displayEmpLinks(JSONArray iconsArray) {
        try {
            if (iconsArray.length() == 0) {
                showNoIconsMessage();
                return;
            }
            
            // Add section header
            addSectionHeader("Employee Management Links");
            
            // Display each icon as a clickable card
            for (int i = 0; i < iconsArray.length(); i++) {
                JSONObject icon = iconsArray.getJSONObject(i);
                
                String iconId = icon.optString("id", "");
                String iconName = icon.optString("icon_name", "Unknown");
                String iconUrl = icon.optString("icon_url", "");
                String iconImage = icon.optString("icon_image", "");
                String iconDescription = icon.optString("icon_description", "");
                String status = icon.optString("status", "active");
                
                // Only show active icons
                if ("active".equalsIgnoreCase(status)) {
                    addEmpLinkCard(iconName, iconDescription, iconUrl, iconImage);
                }
            }
            
            // Add refresh functionality
            addRefreshButton();
            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing icons array", e);
            showError("Error parsing icon data");
        }
    }
    
    private void addSectionHeader(String title) {
        TextView headerText = new TextView(this);
        headerText.setText(title);
        headerText.setTextSize(20);
        headerText.setTextColor(getResources().getColor(android.R.color.black));
        headerText.setTypeface(null, android.graphics.Typeface.BOLD);
        headerText.setPadding(0, 20, 0, 20);
        
        empLinksContainer.addView(headerText);
    }
    
    private void addEmpLinkCard(String title, String description, String url, String imageUrl) {
        CardView cardView = new CardView(this);
        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(16, 8, 16, 8);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(12);
        cardView.setCardElevation(4);
        cardView.setClickable(true);
        cardView.setFocusable(true);
        cardView.setForeground(getResources().getDrawable(android.R.drawable.list_selector_background));
        
        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        cardContent.setOrientation(LinearLayout.HORIZONTAL);
        cardContent.setPadding(20, 20, 20, 20);
        cardContent.setGravity(android.view.Gravity.CENTER_VERTICAL);
        
        // Icon placeholder (you can load actual image here)
        TextView iconPlaceholder = new TextView(this);
        iconPlaceholder.setText("🔗");
        iconPlaceholder.setTextSize(24);
        iconPlaceholder.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        iconPlaceholder.setPadding(0, 0, 20, 0);
        
        // Text content
        LinearLayout textContent = new LinearLayout(this);
        textContent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        textContent.setOrientation(LinearLayout.VERTICAL);
        
        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(16);
        titleText.setTextColor(getResources().getColor(android.R.color.black));
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        titleText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        
        TextView descText = new TextView(this);
        descText.setText(description);
        descText.setTextSize(14);
        descText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        descText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        descText.setPadding(0, 4, 0, 0);
        
        textContent.addView(titleText);
        textContent.addView(descText);
        
        cardContent.addView(iconPlaceholder);
        cardContent.addView(textContent);
        cardView.addView(cardContent);
        
        // Add click listener to open URL
        cardView.setOnClickListener(v -> {
            if (url != null && !url.isEmpty()) {
                openUrl(url);
            } else {
                showToast("No URL available for this link");
            }
        });
        
        empLinksContainer.addView(cardView);
    }
    
    private void addRefreshButton() {
        CardView refreshCard = new CardView(this);
        CardView.LayoutParams cardParams = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(16, 20, 16, 8);
        refreshCard.setLayoutParams(cardParams);
        refreshCard.setRadius(12);
        refreshCard.setCardElevation(4);
        refreshCard.setCardBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        refreshCard.setClickable(true);
        refreshCard.setFocusable(true);
        refreshCard.setForeground(getResources().getDrawable(android.R.drawable.list_selector_background));
        
        LinearLayout refreshContent = new LinearLayout(this);
        refreshContent.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        refreshContent.setOrientation(LinearLayout.HORIZONTAL);
        refreshContent.setPadding(20, 20, 20, 20);
        refreshContent.setGravity(android.view.Gravity.CENTER);
        
        TextView refreshText = new TextView(this);
        refreshText.setText("🔄 Refresh Employee Links");
        refreshText.setTextSize(16);
        refreshText.setTextColor(getResources().getColor(android.R.color.white));
        refreshText.setTypeface(null, android.graphics.Typeface.BOLD);
        
        refreshContent.addView(refreshText);
        refreshCard.addView(refreshContent);
        
        refreshCard.setOnClickListener(v -> {
            showToast("Refreshing employee links...");
            loadEmpLinksData();
        });
        
        empLinksContainer.addView(refreshCard);
    }
    
    private void addLoadingView() {
        TextView loadingText = new TextView(this);
        loadingText.setText("Loading employee links...");
        loadingText.setTextSize(16);
        loadingText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        loadingText.setGravity(android.view.Gravity.CENTER);
        loadingText.setPadding(0, 40, 0, 40);
        
        empLinksContainer.addView(loadingText);
    }
    
    private void showNoIconsMessage() {
        empLinksContainer.removeAllViews();
        
        TextView noIconsText = new TextView(this);
        noIconsText.setText("No employee management links found.\n\nThis could mean:\n• No icons are assigned to your account\n• All assigned icons are inactive\n• There was an error loading the data");
        noIconsText.setTextSize(16);
        noIconsText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        noIconsText.setGravity(android.view.Gravity.CENTER);
        noIconsText.setPadding(0, 40, 0, 40);
        
        empLinksContainer.addView(noIconsText);
        
        // Add refresh button
        addRefreshButton();
    }
    
    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening URL: " + url, e);
            showToast("Error opening link: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        empLinksContainer.removeAllViews();
        
        TextView errorText = new TextView(this);
        errorText.setText("Error: " + message + "\n\nPlease try again or contact support.");
        errorText.setTextSize(16);
        errorText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        errorText.setGravity(android.view.Gravity.CENTER);
        errorText.setPadding(0, 40, 0, 40);
        
        empLinksContainer.addView(errorText);
        
        // Add refresh button
        addRefreshButton();
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Navigate back to login
                    Intent intent = new Intent(MarketingHeadEmpLinksActivity.this, EnhancedLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
        if (executor != null) {
            executor.shutdown();
        }
    }
}
