package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegionalBusinessHeadActiveEmpListActivity extends AppCompatActivity {
    private TextView titleText, welcomeText;
    private View backButton, refreshButton, addButton;
    private String userName, userId;
    private BottomNavigationView rbhBottomNav;
    private RecyclerView userRecyclerView;
    private RegionalBusinessHeadUserAdapter userAdapter;
    private List<RegionalBusinessHeadUser> userList;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar and make fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_regional_business_head_active_emp_list);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        if (userName == null || userName.isEmpty()) {
            userName = "RBH"; // Default fallback
        }
        
        initializeViews();
        setupClickListeners();
        updateWelcomeMessage();
        setupRecyclerView();
        fetchActiveUsers();
    }

    private void initializeViews() {
        titleText = findViewById(R.id.titleText);
        welcomeText = findViewById(R.id.welcomeText);
        
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);
        
        userRecyclerView = findViewById(R.id.userRecyclerView);
        rbhBottomNav = findViewById(R.id.rbhBottomNav);
        
        executorService = Executors.newSingleThreadExecutor();
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Refresh button
        refreshButton.setOnClickListener(v -> {
            Toast.makeText(this, "Refreshing active employee list...", Toast.LENGTH_SHORT).show();
            fetchActiveUsers();
        });

        // Add button
        addButton.setOnClickListener(v -> {
            Toast.makeText(this, "Add New Active Employee - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to add active employee activity
        });

        // Bottom Navigation
        rbhBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_team) {
                Intent intent = new Intent(this, RBHTeamActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_portfolio) {
                Intent intent = new Intent(this, RBHPortfolioActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_reports) {
                Intent intent = new Intent(this, RBHReportsActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }

    private void updateWelcomeMessage() {
        welcomeText.setText("Active User Management");
    }

    private void setupRecyclerView() {
        userList = new ArrayList<>();
        userAdapter = new RegionalBusinessHeadUserAdapter(userList, this);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.setAdapter(userAdapter);
    }

    private void fetchActiveUsers() {
        executorService.execute(() -> {
            try {
                // Create API request to fetch users reporting to the logged-in user
                String apiUrl = "https://emp.kfinone.com/mobile/api/get_rbh_active_users.php?reportingTo=" + userId + "&status=active";
                
                android.util.Log.d("RBHActiveEmpList", "Fetching users from: " + apiUrl);
                
                String response = makeGetRequest(apiUrl);
                
                runOnUiThread(() -> {
                    if (response != null && !response.isEmpty()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getBoolean("success")) {
                                JSONArray usersArray = jsonResponse.getJSONArray("users");
                                userList.clear();
                                
                                for (int i = 0; i < usersArray.length(); i++) {
                                    JSONObject userObj = usersArray.getJSONObject(i);
                                    RegionalBusinessHeadUser user = new RegionalBusinessHeadUser(
                                        userObj.getString("first_name"),
                                        userObj.getString("last_name"),
                                        userObj.getString("username"),
                                        userObj.getString("Phone_number"),
                                        userObj.getString("email_id"),
                                        userObj.getString("status")
                                    );
                                    userList.add(user);
                                }
                                
                                userAdapter.notifyDataSetChanged();
                                
                                if (userList.isEmpty()) {
                                    Toast.makeText(this, "No active users found reporting to you", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Found " + userList.size() + " active users", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                String errorMessage = jsonResponse.getString("message");
                                Toast.makeText(this, "API Error: " + errorMessage, Toast.LENGTH_LONG).show();
                                android.util.Log.e("RBHActiveEmpList", "API Error: " + errorMessage);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            android.util.Log.e("RBHActiveEmpList", "JSON Parse Error: " + e.getMessage(), e);
                        }
                    } else {
                        Toast.makeText(this, "No response from server. Please check your connection.", Toast.LENGTH_LONG).show();
                        android.util.Log.e("RBHActiveEmpList", "No response from server");
                    }
                });
                
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error fetching users: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    android.util.Log.e("RBHActiveEmpList", "Fetch Error: " + e.getMessage(), e);
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
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
                android.util.Log.e("RBHActiveEmpList", errorMessage);
                return null;
            }
        } catch (Exception e) {
            android.util.Log.e("RBHActiveEmpList", "Network error: " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
} 