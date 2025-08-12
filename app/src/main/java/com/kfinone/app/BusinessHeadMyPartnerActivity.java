package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class BusinessHeadMyPartnerActivity extends AppCompatActivity {
    private static final String TAG = "BHMyPartner";
    private static final String API_BASE_URL = "https://emp.kfinone.com/mobile/api/";
    private static final String BH_PARTNER_USERS_API = "business_head_my_partner_users.php";

    private TextView totalPartnerCount, activePartnerCount, welcomeText;
    private ListView partnerListView;
    private EditText searchEditText;
    private Button refreshButton;
    private ProgressBar loadingProgress;
    private View emptyStateLayout;
    
    private String userId;
    private String userName;
    private PartnerAdapter partnerAdapter;
    private List<PartnerUser> partnerList;
    private List<PartnerUser> filteredPartnerList;
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar and make fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_business_head_my_partner);
        
        // Get user data from intent
        userName = getIntent().getStringExtra("USERNAME");
        userId = getIntent().getStringExtra("USER_ID");
        if (userName == null || userName.isEmpty()) {
            userName = "Business Head"; // Default fallback
        }
        
        initializeViews();
        setupToolbar();
        setupClickListeners();
        setupSearchFunctionality();
        initializePartnerList();
        loadPartnerData();
        updateWelcomeMessage(userName);
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        totalPartnerCount = findViewById(R.id.totalPartnerCount);
        activePartnerCount = findViewById(R.id.activePartnerCount);
        partnerListView = findViewById(R.id.partnerListView);
        searchEditText = findViewById(R.id.searchEditText);
        refreshButton = findViewById(R.id.refreshButton);
        loadingProgress = findViewById(R.id.loadingProgress);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Partner Users");
        }
    }

    private void setupClickListeners() {
        refreshButton.setOnClickListener(v -> {
            loadPartnerData();
        });
    }

    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPartners(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void initializePartnerList() {
        partnerList = new ArrayList<>();
        filteredPartnerList = new ArrayList<>();
        partnerAdapter = new PartnerAdapter(this, filteredPartnerList);
        partnerListView.setAdapter(partnerAdapter);
    }

    private void loadPartnerData() {
        showLoading(true);
        
        executor.execute(() -> {
            try {
                String apiUrl = API_BASE_URL + BH_PARTNER_USERS_API;
                if (userId != null) {
                    apiUrl += "?user_id=" + userId;
                } else if (userName != null) {
                    apiUrl += "?username=" + userName;
                }
                
                Log.d(TAG, "Calling API: " + apiUrl);
                
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "API Response Code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    Log.d(TAG, "API Response: " + response.toString());
                    parsePartnerData(response.toString());
                } else {
                    Log.e(TAG, "API Error: " + responseCode);
                    runOnUiThread(() -> {
                        showError("API Error: " + responseCode);
                        showLoading(false);
                    });
                }
                
                connection.disconnect();
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading partner data", e);
                runOnUiThread(() -> {
                    showError("Network error: " + e.getMessage());
                    showLoading(false);
                });
            }
        });
    }

    private void parsePartnerData(String jsonResponse) {
        try {
            JSONObject response = new JSONObject(jsonResponse);
            
            if (response.getString("status").equals("success")) {
                JSONArray data = response.getJSONArray("data");
                JSONObject stats = response.getJSONObject("statistics");
                
                List<PartnerUser> newPartnerList = new ArrayList<>();
                
                for (int i = 0; i < data.length(); i++) {
                    JSONObject partner = data.getJSONObject(i);
                    
                    PartnerUser partnerUser = new PartnerUser();
                    partnerUser.setId(partner.optString("id", ""));
                    partnerUser.setFullName(partner.optString("full_name", ""));
                    partnerUser.setCompanyName(partner.optString("company_name", ""));
                    partnerUser.setPhoneNumber(partner.optString("Phone_number", ""));
                    partnerUser.setAlternativePhoneNumber(partner.optString("alternative_Phone_number", ""));
                    partnerUser.setEmailId(partner.optString("email_id", ""));
                    partnerUser.setPartnerType(partner.optString("partnerType", ""));
                    partnerUser.setState(partner.optString("state", ""));
                    partnerUser.setLocation(partner.optString("location", ""));
                    partnerUser.setAddress(partner.optString("address", ""));
                    partnerUser.setVisitingCard(partner.optString("visiting_card", ""));
                    partnerUser.setCreatedUser(partner.optString("created_user", ""));
                    partnerUser.setCreatedBy(partner.optString("createdBy", ""));
                    partnerUser.setStatus(partner.optString("status", ""));
                    partnerUser.setCreatedAt(partner.optString("created_at", ""));
                    partnerUser.setUpdatedAt(partner.optString("updated_at", ""));
                    partnerUser.setUserId(partner.optString("user_id", ""));
                    partnerUser.setUsername(partner.optString("username", ""));
                    partnerUser.setFirstName(partner.optString("firstName", ""));
                    partnerUser.setLastName(partner.optString("lastName", ""));
                    partnerUser.setCreatedByName(partner.optString("created_by_name", ""));
                    
                    newPartnerList.add(partnerUser);
                }
                
                runOnUiThread(() -> {
                    updatePartnerList(newPartnerList);
                    updateStatistics(stats);
                    showLoading(false);
                });
                
            } else {
                String errorMessage = response.optString("message", "Unknown error");
                Log.e(TAG, "API Error: " + errorMessage);
                runOnUiThread(() -> {
                    showError(errorMessage);
                    showLoading(false);
                });
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON response", e);
            runOnUiThread(() -> {
                showError("Error parsing response: " + e.getMessage());
                showLoading(false);
            });
        }
    }

    private void updatePartnerList(List<PartnerUser> newPartnerList) {
        partnerList.clear();
        partnerList.addAll(newPartnerList);
        filteredPartnerList.clear();
        filteredPartnerList.addAll(newPartnerList);
        partnerAdapter.updateData(filteredPartnerList);
        
        if (newPartnerList.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
        }
    }

    private void updateStatistics(JSONObject stats) {
        try {
            int totalPartners = stats.optInt("total_partners", 0);
            int activePartners = stats.optInt("active_partners", 0);
            
            totalPartnerCount.setText(String.valueOf(totalPartners));
            activePartnerCount.setText(String.valueOf(activePartners));
            
        } catch (Exception e) {
            Log.e(TAG, "Error updating statistics", e);
        }
    }

    private void filterPartners(String query) {
        filteredPartnerList.clear();
        
        if (query == null || query.trim().isEmpty()) {
            filteredPartnerList.addAll(partnerList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (PartnerUser partner : partnerList) {
                if (partner.getDisplayName().toLowerCase().contains(lowerCaseQuery) ||
                    partner.getDisplayCompany().toLowerCase().contains(lowerCaseQuery) ||
                    partner.getDisplayPhone().toLowerCase().contains(lowerCaseQuery) ||
                    partner.getDisplayEmail().toLowerCase().contains(lowerCaseQuery) ||
                    (partner.getPartnerType() != null && partner.getPartnerType().toLowerCase().contains(lowerCaseQuery)) ||
                    (partner.getLocation() != null && partner.getLocation().toLowerCase().contains(lowerCaseQuery))) {
                    filteredPartnerList.add(partner);
                }
            }
        }
        
        partnerAdapter.notifyDataSetChanged();
        
        if (filteredPartnerList.isEmpty()) {
            showEmptyState(true);
        } else {
            showEmptyState(false);
        }
    }

    private void showLoading(boolean show) {
        loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        partnerListView.setVisibility(show ? View.GONE : View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    private void showEmptyState(boolean show) {
        emptyStateLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        partnerListView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void updateWelcomeMessage(String userName) {
        if (welcomeText != null) {
            welcomeText.setText("Welcome, " + userName + "!");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
} 