package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class MyEmpLinksActivity extends AppCompatActivity {

    private static final String TAG = "MyEmpLinksActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // Content elements
    private RecyclerView recyclerView;
    private MyEmpLinksAdapter adapter;
    private List<IconPermissionItem> iconList;
    private LinearLayout emptyStateText;

    // K RAJESH KUMAR Section elements
    private LinearLayout rajeshKumarSection;
    private LinearLayout permissionsSummaryBox;
    private TextView managePermissionsCount;
    private TextView dataPermissionsCount;
    private TextView workPermissionsCount;
    private TextView totalPermissionsCount;

    // User data
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_emp_links);

        // Get user data from intent
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            if (userId == null) {
                userId = intent.getStringExtra("userId"); // Try alternative key
            }
            userName = intent.getStringExtra("USERNAME");
            if (userName == null) {
                userName = intent.getStringExtra("userName"); // Try alternative key
            }
        }

        // If user data is still missing, try to restore from SharedPreferences
        if (userId == null || userId.isEmpty()) {
            restoreUserDataFromPreferences();
        }

        // Save user data to preferences for backup
        if (userId != null && !userId.isEmpty()) {
            saveUserDataToPreferences();
        }

        // Debug logging
        Log.d(TAG, "MyEmpLinksActivity onCreate - User ID: " + userId + ", User Name: " + userName);

        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        loadUserPermissions();
    }

    private void saveUserDataToPreferences() {
        android.content.SharedPreferences prefs = getSharedPreferences("MyEmpLinksPrefs", MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = prefs.edit();
        editor.putString("USER_ID", userId);
        editor.putString("USERNAME", userName);
        editor.apply();
        Log.d(TAG, "User data saved to preferences - User ID: " + userId);
    }

    private void restoreUserDataFromPreferences() {
        android.content.SharedPreferences prefs = getSharedPreferences("MyEmpLinksPrefs", MODE_PRIVATE);
        userId = prefs.getString("USER_ID", null);
        userName = prefs.getString("USERNAME", null);
        Log.d(TAG, "User data restored from preferences - User ID: " + userId);
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

        // Content
        recyclerView = findViewById(R.id.recyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);

        // K RAJESH KUMAR Section
        rajeshKumarSection = findViewById(R.id.rajeshKumarSection);
        permissionsSummaryBox = findViewById(R.id.permissionsSummaryBox);
        managePermissionsCount = findViewById(R.id.managePermissionsCount);
        dataPermissionsCount = findViewById(R.id.dataPermissionsCount);
        workPermissionsCount = findViewById(R.id.workPermissionsCount);
        totalPermissionsCount = findViewById(R.id.totalPermissionsCount);

        // Show K RAJESH KUMAR section only for user ID 8
        if ("8".equals(userId)) {
            rajeshKumarSection.setVisibility(View.VISIBLE);
        } else {
            rajeshKumarSection.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewPermission());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SpecialPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        empLinksButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmpLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerView() {
        iconList = new ArrayList<>();
        adapter = new MyEmpLinksAdapter(iconList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadUserPermissions() {
        Log.d(TAG, "loadUserPermissions called - User ID: " + userId);
        
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "User ID is null or empty - cannot load permissions");
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = BASE_URL + "get_user_manage_icons.php";
        Log.d(TAG, "Making API request to: " + url);
        
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("userId", userId);
            Log.d(TAG, "Request body: " + requestBody.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error creating request body", e);
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "API Response received: " + response.toString());
                        try {
                            String status = response.getString("status");
                            if ("success".equals(status)) {
                                parseManageIconsData(response.getJSONArray("data"));
                            } else {
                                String message = response.getString("message");
                                Log.e(TAG, "API Error: " + message);
                                Toast.makeText(MyEmpLinksActivity.this, "API Error: " + message, Toast.LENGTH_LONG).show();
                                showEmptyStateWithMessage("API Error: " + message);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing response", e);
                            Toast.makeText(MyEmpLinksActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                            showEmptyStateWithMessage("Error parsing server response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Network error", error);
                        String errorMessage = "Network error";
                        if (error.networkResponse != null) {
                            errorMessage += " (HTTP " + error.networkResponse.statusCode + ")";
                        }
                        Toast.makeText(MyEmpLinksActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        showEmptyStateWithMessage(errorMessage + "\n\nPlease check your internet connection and try again.");
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void parseManageIconsData(JSONArray data) {
        Log.d(TAG, "parseManageIconsData called with data: " + data.toString());
        iconList.clear();
        int manageCount = 0;
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject icon = data.getJSONObject(i);
                IconPermissionItem item = new IconPermissionItem(
                    null, // id not used
                    icon.optString("icon_name", ""),
                    icon.optString("icon_image", ""),
                    icon.optString("icon_description", ""),
                    "Yes", // always granted if present in manage_icons
                    "Manage"
                );
                iconList.add(item);
                manageCount++;
                Log.d(TAG, "Added manage icon: " + icon.optString("icon_name", ""));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing manage icons data", e);
        }
        adapter.notifyDataSetChanged();
        updatePermissionsSummary(manageCount, 0, 0);
        updateEmptyState();
    }

    private void updatePermissionsSummary(int manageCount, int dataCount, int workCount) {
        managePermissionsCount.setText("Manage: " + manageCount);
        dataPermissionsCount.setText("Data: " + dataCount);
        workPermissionsCount.setText("Work: " + workCount);
        
        int total = manageCount + dataCount + workCount;
        totalPermissionsCount.setText("Total Permissions: " + total);
        
        // Update colors based on counts
        if (manageCount > 0) {
            managePermissionsCount.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            managePermissionsCount.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
        
        if (dataCount > 0) {
            dataPermissionsCount.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        } else {
            dataPermissionsCount.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
        
        if (workCount > 0) {
            workPermissionsCount.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            workPermissionsCount.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void updateEmptyState() {
        if (iconList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
        }
    }

    private void showEmptyStateWithMessage(String message) {
        iconList.clear();
        adapter.notifyDataSetChanged();
        
        recyclerView.setVisibility(View.GONE);
        emptyStateText.setVisibility(View.VISIBLE);
        
        // Update empty state text with custom message
        TextView emptyStateTitle = findViewById(R.id.emptyStateTitle);
        TextView emptyStateDescription = findViewById(R.id.emptyStateDescription);
        
        if (emptyStateTitle != null) {
            emptyStateTitle.setText("Connection Error");
        }
        if (emptyStateDescription != null) {
            emptyStateDescription.setText(message);
        }
    }

    private void goBack() {
        Intent intent = new Intent(this, EmpLinksActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing permissions...", Toast.LENGTH_SHORT).show();
        loadUserPermissions();
    }

    private void addNewPermission() {
        Toast.makeText(this, "Add New Permission - Coming Soon", Toast.LENGTH_SHORT).show();
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        intent.putExtra("SOURCE_PANEL", "SPECIAL_PANEL");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    // Data class for icon permission items
    public static class IconPermissionItem {
        public String id;
        public String iconName;
        public String iconImage;
        public String iconDescription;
        public String hasPermission;
        public String type;

        public IconPermissionItem(String id, String iconName, String iconImage, 
                                String iconDescription, String hasPermission, String type) {
            this.id = id;
            this.iconName = iconName;
            this.iconImage = iconImage;
            this.iconDescription = iconDescription;
            this.hasPermission = hasPermission;
            this.type = type;
        }
    }
} 