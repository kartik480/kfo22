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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RBHMySdsaActivity extends AppCompatActivity {

    // UI elements
    private View backButton;
    private View refreshButton;
    private View addButton;
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
        setContentView(R.layout.activity_rbh_my_sdsa);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        // Debug logging
        android.util.Log.d("RBHMySdsa", "Received userName: " + userName);
        android.util.Log.d("RBHMySdsa", "Received userId: " + userId);

        // If userId is null, try to get it from the logged-in user
        if (userId == null || userId.isEmpty()) {
            android.util.Log.w("RBHMySdsa", "userId is null or empty, attempting to get from SharedPreferences");
            try {
                android.content.SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                String storedUserId = prefs.getString("USER_ID", null);
                if (storedUserId != null && !storedUserId.isEmpty()) {
                    userId = storedUserId;
                    android.util.Log.d("RBHMySdsa", "Using USER_ID from SharedPreferences: " + userId);
                }
            } catch (Exception e) {
                android.util.Log.e("RBHMySdsa", "Error reading SharedPreferences: " + e.getMessage());
            }
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
            Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        empLinksButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RBHEmpLinksActivity.class);
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
        Intent intent = new Intent(this, RBHSdsaActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing RBH SDSA data...", Toast.LENGTH_SHORT).show();
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
                // Create API request to fetch SDSA users reporting to the logged-in RBH user
                // Using original API with temporary RBH verification disabled
                String apiUrl = "https://emp.kfinone.com/mobile/api/get_rbh_my_sdsa_users.php?reportingTo=" + userId + "&status=active";
                
                android.util.Log.d("RBHMySdsa", "=== RBH My SDSA Debug Start ===");
                android.util.Log.d("RBHMySdsa", "Fetching SDSA users from: " + apiUrl);
                android.util.Log.d("RBHMySdsa", "Using userId: " + userId);
                android.util.Log.d("RBHMySdsa", "User data - userName: " + userName + ", userId: " + userId);
                
                String response = makeGetRequest(apiUrl);
                
                android.util.Log.d("RBHMySdsa", "Raw API Response: " + response);
                
                runOnUiThread(() -> {
                    showLoading(false);
                    if (response != null && !response.isEmpty()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            android.util.Log.d("RBHMySdsa", "JSON Response parsed successfully");
                            android.util.Log.d("RBHMySdsa", "Success: " + jsonResponse.optBoolean("success"));
                            android.util.Log.d("RBHMySdsa", "Message: " + jsonResponse.optString("message"));
                            
                            if (jsonResponse.getBoolean("success")) {
                                JSONArray usersArray = jsonResponse.getJSONArray("users");
                                android.util.Log.d("RBHMySdsa", "Users array length: " + usersArray.length());
                                
                                sdsaUserList.clear();
                                
                                for (int i = 0; i < usersArray.length(); i++) {
                                    JSONObject userObj = usersArray.getJSONObject(i);
                                    android.util.Log.d("RBHMySdsa", "Processing user " + (i + 1) + ": " + userObj.optString("first_name") + " " + userObj.optString("last_name"));
                                    
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
                                        userObj.optString("displayName")
                                    );
                                    sdsaUserList.add(user);
                                }
                                
                                android.util.Log.d("RBHMySdsa", "Total users added to list: " + sdsaUserList.size());
                                sdsaUserAdapter.notifyDataSetChanged();
                                
                                // Display statistics
                                if (jsonResponse.has("statistics")) {
                                    JSONObject stats = jsonResponse.getJSONObject("statistics");
                                    android.util.Log.d("RBHMySdsa", "Statistics: " + stats.toString());
                                    
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
                                    android.util.Log.w("RBHMySdsa", "No SDSA users found in the response");
                                    Toast.makeText(this, "No SDSA users found reporting to you", Toast.LENGTH_SHORT).show();
                                } else {
                                    android.util.Log.d("RBHMySdsa", "Successfully loaded " + sdsaUserList.size() + " SDSA users");
                                    Toast.makeText(this, "Found " + sdsaUserList.size() + " SDSA users", Toast.LENGTH_SHORT).show();
                                }
                                
                                android.util.Log.d("RBHMySdsa", "=== RBH My SDSA Debug End (Success) ===");
                            } else {
                                String errorMessage = jsonResponse.getString("message");
                                android.util.Log.e("RBHMySdsa", "API returned success=false: " + errorMessage);
                                showError("API Error: " + errorMessage);
                            }
                        } catch (JSONException e) {
                            android.util.Log.e("RBHMySdsa", "JSON Parse Error: " + e.getMessage(), e);
                            android.util.Log.e("RBHMySdsa", "Raw response that failed to parse: " + response);
                            showError("Error parsing response: " + e.getMessage());
                        }
                    } else {
                        android.util.Log.e("RBHMySdsa", "No response from server or empty response");
                        android.util.Log.e("RBHMySdsa", "Response was: " + (response == null ? "null" : "empty"));
                        showError("No response from server. Please check your connection.");
                    }
                });
                
            } catch (Exception e) {
                android.util.Log.e("RBHMySdsa", "Fetch Error: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    showLoading(false);
                    showError("Error fetching SDSA users: " + e.getMessage());
                });
            }
        });
    }

    private void showLoading(boolean show) {
        if (show) {
            loadingProgress.setVisibility(View.VISIBLE);
            errorText.setVisibility(View.GONE);
        } else {
            loadingProgress.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
        statsText.setVisibility(View.GONE);
    }

    private String makeGetRequest(String urlString) throws IOException {
        android.util.Log.d("RBHMySdsa", "Making GET request to: " + urlString);
        
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        
        android.util.Log.d("RBHMySdsa", "Connection established, getting response...");
        
        try {
            int responseCode = connection.getResponseCode();
            android.util.Log.d("RBHMySdsa", "Response code: " + responseCode);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                android.util.Log.d("RBHMySdsa", "HTTP OK - Reading response...");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                String responseText = response.toString();
                android.util.Log.d("RBHMySdsa", "Response length: " + responseText.length() + " characters");
                android.util.Log.d("RBHMySdsa", "Response preview: " + (responseText.length() > 200 ? responseText.substring(0, 200) + "..." : responseText));
                
                return responseText;
            } else {
                android.util.Log.e("RBHMySdsa", "HTTP Error " + responseCode + " - Reading error stream...");
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line);
                }
                errorReader.close();
                
                String errorText = errorResponse.toString();
                String errorMessage = "HTTP Error " + responseCode + ": " + errorText;
                android.util.Log.e("RBHMySdsa", errorMessage);
                android.util.Log.e("RBHMySdsa", "Error response length: " + errorText.length() + " characters");
                android.util.Log.e("RBHMySdsa", "Error response preview: " + (errorText.length() > 200 ? errorText.substring(0, 200) + "..." : errorText));
                
                return null;
            }
        } catch (IOException e) {
            android.util.Log.e("RBHMySdsa", "IOException during request: " + e.getMessage(), e);
            throw e;
        } finally {
            android.util.Log.d("RBHMySdsa", "Disconnecting connection...");
            connection.disconnect();
        }
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        intent.putExtra("SOURCE_PANEL", "RBH_PANEL");
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