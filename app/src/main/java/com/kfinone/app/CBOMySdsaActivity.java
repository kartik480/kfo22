package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CBOMySdsaActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // User data
    private String userName;
    private String userId;

    // UI elements for SDSA users
    private RecyclerView sdsaUsersRecyclerView;
    private TextView statsText;
    private ProgressBar loadingProgress;
    private TextView errorText;
    private SdsaUserAdapter sdsaUserAdapter;
    private List<SdsaUser> sdsaUserList;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_my_sdsa);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        // Debug logging
        android.util.Log.d("CBOMySdsa", "Received userName: " + userName);
        android.util.Log.d("CBOMySdsa", "Received userId: " + userId);

        // If userId is null, try to get it from SharedPreferences
        if (userId == null || userId.isEmpty()) {
            android.util.Log.w("CBOMySdsa", "userId is null or empty, attempting to get from SharedPreferences");
            android.content.SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            userId = prefs.getString("USER_ID", "");
            userName = prefs.getString("USERNAME", "");
            android.util.Log.d("CBOMySdsa", "Retrieved from SharedPreferences - userId: " + userId);
            android.util.Log.d("CBOMySdsa", "Retrieved from SharedPreferences - userName: " + userName);
        }

        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        fetchSdsaUsers();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);

        // Bottom navigation
        dashboardButton = findViewById(R.id.dashboardButton);
        empLinksButton = findViewById(R.id.empLinksButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);

        // SDSA users UI
        sdsaUsersRecyclerView = findViewById(R.id.sdsaUsersRecyclerView);
        statsText = findViewById(R.id.statsText);
        loadingProgress = findViewById(R.id.loadingProgress);
        errorText = findViewById(R.id.errorText);

        executorService = Executors.newSingleThreadExecutor();
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewSdsa());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChiefBusinessOfficerPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        empLinksButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOEmpLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Reports activity
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Settings activity
        });
    }

    private void setupRecyclerView() {
        sdsaUserList = new ArrayList<>();
        sdsaUserAdapter = new SdsaUserAdapter(sdsaUserList, this);
        sdsaUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sdsaUsersRecyclerView.setAdapter(sdsaUserAdapter);
    }

    private void goBack() {
        Intent intent = new Intent(this, CBOSdsaActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing my SDSA data...", Toast.LENGTH_SHORT).show();
        fetchSdsaUsers();
    }

    private void addNewSdsa() {
        Toast.makeText(this, "Add New SDSA - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add SDSA activity
    }

    private void fetchSdsaUsers() {
        showLoading(true);
        executorService.execute(() -> {
            try {
                // Create API request to fetch SDSA users reporting to the logged-in CBO user
                // Using original API with temporary CBO verification disabled
                String apiUrl = "https://emp.kfinone.com/mobile/api/get_cbo_my_sdsa_users.php?reportingTo=" + userId + "&status=active";
                
                android.util.Log.d("CBOMySdsa", "Fetching SDSA users from: " + apiUrl);
                android.util.Log.d("CBOMySdsa", "Using userId: " + userId);
                
                String response = makeGetRequest(apiUrl);
                
                runOnUiThread(() -> {
                    showLoading(false);
                    if (response != null && !response.isEmpty()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("success")) {
                                JSONArray usersArray = jsonResponse.getJSONArray("users");
                                sdsaUserList.clear();
                                
                                for (int i = 0; i < usersArray.length(); i++) {
                                    JSONObject userObj = usersArray.getJSONObject(i);
                                    SdsaUser user = new SdsaUser(
                                        userObj.optString("id"),
                                        userObj.optString("username"),
                                        userObj.optString("alias_name"),
                                        userObj.optString("first_name"),
                                        userObj.optString("last_name"),
                                        userObj.optString("Phone_number"),
                                        userObj.optString("email_id"),
                                        userObj.optString("alternative_mobile_number"),
                                        userObj.optString("company_name"),
                                        userObj.optString("branch_state_name_id"),
                                        userObj.optString("branch_location_id"),
                                        userObj.optString("bank_id"),
                                        userObj.optString("account_type_id"),
                                        userObj.optString("office_address"),
                                        userObj.optString("residential_address"),
                                        userObj.optString("aadhaar_number"),
                                        userObj.optString("pan_number"),
                                        userObj.optString("account_number"),
                                        userObj.optString("ifsc_code"),
                                        userObj.optString("rank"),
                                        userObj.optString("status"),
                                        userObj.optString("reportingTo"),
                                        userObj.optString("employee_no"),
                                        userObj.optString("department"),
                                        userObj.optString("designation"),
                                        userObj.optString("branchstate"),
                                        userObj.optString("branchloaction"),
                                        userObj.optString("bank_name"),
                                        userObj.optString("account_type"),
                                        userObj.optString("user_id"),
                                        userObj.optString("createdBy"),
                                        userObj.optString("created_at"),
                                        userObj.optString("updated_at"),
                                        userObj.optString("fullName"),
                                        userObj.optString("displayName"),
                                        userObj.optString("password"),
                                        userObj.optString("pan_img"),
                                        userObj.optString("aadhaar_img"),
                                        userObj.optString("photo_img"),
                                        userObj.optString("bankproof_img")
                                    );
                                    sdsaUserList.add(user);
                                }
                                
                                sdsaUserAdapter.notifyDataSetChanged();
                                
                                // Display statistics
                                if (jsonResponse.has("statistics")) {
                                    JSONObject stats = jsonResponse.getJSONObject("statistics");
                                    String statsMessage = String.format(
                                        "📊 Total SDSA Users: %d | Designations: %d | Departments: %d | Banks: %d",
                                        stats.optInt("total_users", 0),
                                        stats.optInt("unique_designations", 0),
                                        stats.optInt("unique_departments", 0),
                                        stats.optInt("unique_banks", 0)
                                    );
                                    statsText.setText(statsMessage);
                                    statsText.setVisibility(View.VISIBLE);
                                }
                                
                                if (sdsaUserList.isEmpty()) {
                                    Toast.makeText(this, "No SDSA users found reporting to you", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Found " + sdsaUserList.size() + " SDSA users", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String errorMessage = jsonResponse.getString("message");
                                showError("API Error: " + errorMessage);
                                android.util.Log.e("CBOMySdsa", "API Error: " + errorMessage);
                            }
                        } catch (JSONException e) {
                            showError("Error parsing response: " + e.getMessage());
                            android.util.Log.e("CBOMySdsa", "JSON Parse Error: " + e.getMessage(), e);
                        }
                    } else {
                        showError("No response from server. Please check your connection.");
                        android.util.Log.e("CBOMySdsa", "No response from server");
                    }
                });
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError("Error fetching SDSA users: " + e.getMessage());
                    android.util.Log.e("CBOMySdsa", "Fetch Error: " + e.getMessage(), e);
                });
            }
        });
    }

    private void showLoading(boolean show) {
        runOnUiThread(() -> {
            if (show) {
                loadingProgress.setVisibility(View.VISIBLE);
                errorText.setVisibility(View.GONE);
                statsText.setVisibility(View.GONE);
            } else {
                loadingProgress.setVisibility(View.GONE);
            }
        });
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            errorText.setText(message);
            errorText.setVisibility(View.VISIBLE);
            statsText.setVisibility(View.GONE);
        });
    }

    private String makeGetRequest(String url) {
        try {
            java.net.URL apiUrl = new java.net.URL(url);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            int responseCode = connection.getResponseCode();
            if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(connection.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                // Handle different HTTP response codes
                java.io.BufferedReader errorReader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(connection.getErrorStream())
                );
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line);
                }
                errorReader.close();
                
                String errorMessage = "HTTP Error " + responseCode + ": " + errorResponse.toString();
                android.util.Log.e("CBOMySdsa", errorMessage);
                return null;
            }
        } catch (Exception e) {
            android.util.Log.e("CBOMySdsa", "Network error: " + e.getMessage(), e);
            return null;
        }
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
} 