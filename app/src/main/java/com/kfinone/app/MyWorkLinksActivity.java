package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyWorkLinksActivity extends AppCompatActivity {

    private static final String TAG = "MyWorkLinksActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout workLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // Content elements
    private RecyclerView recyclerView;
    private MyWorkLinksAdapter adapter;
    private List<WorkIconItem> iconList;
    private LinearLayout emptyStateText;

    // User data
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_work_links);

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
        Log.d(TAG, "MyWorkLinksActivity onCreate - User ID: " + userId + ", User Name: " + userName);

        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        loadWorkIconPermissions();
    }

    private void saveUserDataToPreferences() {
        android.content.SharedPreferences prefs = getSharedPreferences("MyWorkLinksPrefs", MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = prefs.edit();
        editor.putString("USER_ID", userId);
        editor.putString("USERNAME", userName);
        editor.apply();
        Log.d(TAG, "User data saved to preferences - User ID: " + userId);
    }

    private void restoreUserDataFromPreferences() {
        android.content.SharedPreferences prefs = getSharedPreferences("MyWorkLinksPrefs", MODE_PRIVATE);
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
        workLinksButton = findViewById(R.id.workLinksButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);

        // Content
        recyclerView = findViewById(R.id.recyclerView);
        emptyStateText = findViewById(R.id.emptyStateText);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewWorkLink());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SpecialPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        workLinksButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkLinksActivity.class);
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
        adapter = new MyWorkLinksAdapter(iconList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void goBack() {
        // Check if we came from Director panel
        String sourcePanel = getIntent().getStringExtra("SOURCE_PANEL");
        if ("DIRECTOR_PANEL".equals(sourcePanel)) {
            // Navigate back to Director Work Links Activity
            Intent intent = new Intent(this, DirectorWorkLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else {
            // Default behavior - go back to WorkLinksActivity
            Intent intent = new Intent(this, WorkLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        }
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing work icon permissions...", Toast.LENGTH_SHORT).show();
        loadWorkIconPermissions();
    }

    private void addNewWorkLink() {
        Toast.makeText(this, "Add New Work Link - Coming Soon", Toast.LENGTH_SHORT).show();
    }

    private void loadWorkIconPermissions() {
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "User ID is null or empty, cannot load permissions");
            Toast.makeText(this, "User ID not found. Please login again.", Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "get_user_work_icons.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                // Send the user ID
                String jsonInput = "{\"userId\":\"" + userId + "\"}";
                try (java.io.OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Server response code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
                    String responseString = response.toString();
                    Log.d(TAG, "Server response: " + responseString);
                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        runOnUiThread(() -> parseWorkIcons(data));
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Error: " + json.optString("message"), Toast.LENGTH_LONG).show();
                            updateEmptyState();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                        updateEmptyState();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception in loadWorkIconPermissions: " + e.getMessage(), e);
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    updateEmptyState();
                });
            }
        }).start();
    }
    private void parseWorkIcons(JSONArray data) {
        Log.d(TAG, "parseWorkIcons called with data: " + data.toString());
        iconList.clear();
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject icon = data.getJSONObject(i);
                WorkIconItem item = new WorkIconItem(
                    null,
                    icon.optString("icon_name", ""),
                    icon.optString("icon_image", ""),
                    icon.optString("icon_description", ""),
                    "Yes"
                );
                iconList.add(item);
                Log.d(TAG, "Added work icon: " + icon.optString("icon_name", ""));
            }
            Log.d(TAG, "Total work icon items added to list: " + iconList.size());
            adapter.notifyDataSetChanged();
            updateEmptyState();
        } catch (Exception e) {
            Log.e(TAG, "Error parsing work icons", e);
            Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
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

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userId != null) intent.putExtra("userId", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (userName != null) intent.putExtra("userName", userName);
    }

    // Work Icon Item class
    private static class WorkIconItem {
        String id;
        String iconName;
        String iconImage;
        String iconDescription;
        String hasPermission;

        WorkIconItem(String id, String iconName, String iconImage, String iconDescription, String hasPermission) {
            this.id = id;
            this.iconName = iconName;
            this.iconImage = iconImage;
            this.iconDescription = iconDescription;
            this.hasPermission = hasPermission;
        }
    }

    // Adapter for RecyclerView
    private class MyWorkLinksAdapter extends RecyclerView.Adapter<MyWorkLinksAdapter.ViewHolder> {
        private List<WorkIconItem> icons;

        MyWorkLinksAdapter(List<WorkIconItem> icons) {
            this.icons = icons;
            Log.d(TAG, "Adapter created with " + icons.size() + " work icons");
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_work_link, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            WorkIconItem icon = icons.get(position);
            holder.iconNameText.setText(icon.iconName);
            holder.iconDescriptionText.setText(icon.iconDescription);

            // Set icon image if available
            if (icon.iconImage != null && !icon.iconImage.isEmpty()) {
                // For now, we'll use a placeholder. You can implement image loading here
                holder.iconImageView.setImageResource(R.drawable.ic_work);
            }

            // Set permission status
            if ("Yes".equals(icon.hasPermission)) {
                holder.permissionStatusText.setText("Permission Granted");
                holder.permissionStatusText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                holder.permissionStatusText.setText("No Permission");
                holder.permissionStatusText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        }

        @Override
        public int getItemCount() {
            return icons.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView iconImageView;
            TextView iconNameText;
            TextView iconDescriptionText;
            TextView permissionStatusText;

            ViewHolder(View view) {
                super(view);
                iconImageView = view.findViewById(R.id.iconImageView);
                iconNameText = view.findViewById(R.id.iconNameText);
                iconDescriptionText = view.findViewById(R.id.iconDescriptionText);
                permissionStatusText = view.findViewById(R.id.permissionStatusText);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
} 