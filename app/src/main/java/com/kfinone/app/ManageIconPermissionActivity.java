package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class ManageIconPermissionActivity extends AppCompatActivity {
    private static final String TAG = "ManageIconPermissionActivity";
    private RecyclerView recyclerView;
    private ManageIconAdapter adapter;
    private List<ManageIcon> iconList;
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_icon_permission);

        // Get user data from intent
        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            userId = getIntent().getStringExtra("USER_ID"); // Try alternative key
        }
        userName = getIntent().getStringExtra("userName");
        if (userName == null) {
            userName = getIntent().getStringExtra("USERNAME"); // Try alternative key
        }

        Log.d(TAG, "Activity created for user: " + userName + " (ID: " + userId + ")");

        // Check if user ID is available
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User ID not found. Cannot load permissions.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "User ID is null or empty. Cannot proceed.");
            finish();
            return;
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.manageIconRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        iconList = new ArrayList<>();
        adapter = new ManageIconAdapter(iconList);
        recyclerView.setAdapter(adapter);

        // Set title with user name
        TextView titleText = findViewById(R.id.titleText);
        titleText.setText("Manage Icon Permissions - " + userName);

        // Fetch manage icons from server
        fetchManageIconsFromServer();

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fetchManageIconsFromServer() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting to fetch manage icons from server...");
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_manage_icons_for_permission.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                
                // Send the user ID to get their specific permissions
                String jsonInput = "{\"userId\":\"" + userId + "\"}";
                try (java.io.OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);
                
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
                        Log.d(TAG, "Found " + data.length() + " manage icons in response");
                        
                        iconList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject icon = data.getJSONObject(i);
                            ManageIcon manageIcon = new ManageIcon(
                                    icon.getString("id"),
                                    icon.getString("icon_name"),
                                    icon.getString("icon_image"),
                                    icon.getString("icon_description"),
                                    icon.getString("status")
                            );
                            
                            // Check if user has permission for this icon
                            if (icon.has("has_permission")) {
                                manageIcon.hasPermission = "Yes".equals(icon.getString("has_permission"));
                            }
                            
                            iconList.add(manageIcon);
                        }
                        Log.d(TAG, "Added " + iconList.size() + " manage icons to list");
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Adapter notified of data change");
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show());
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> Toast.makeText(this, "Server error: " + responseCode, Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception in fetchManageIconsFromServer: " + e.getMessage(), e);
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void updateIconPermission(String iconId, String userId, boolean hasPermission) {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/update_icon_permission.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                // Send the icon ID, user ID, and permission status
                String jsonInput = "{\"iconId\":\"" + iconId + "\",\"userId\":\"" + userId + "\",\"hasPermission\":\"" + (hasPermission ? "Yes" : "No") + "\"}";
                try (java.io.OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    JSONObject json = new JSONObject(response.toString());
                    if (json.getString("status").equals("success")) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Permission updated successfully", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Error: " + json.optString("message"), Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Server error: " + responseCode, Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private static class ManageIcon {
        String id;
        String iconName;
        String iconImage;
        String iconDescription;
        String status;
        boolean hasPermission;

        ManageIcon(String id, String iconName, String iconImage, String iconDescription, String status) {
            this.id = id;
            this.iconName = iconName;
            this.iconImage = iconImage;
            this.iconDescription = iconDescription;
            this.status = status;
            this.hasPermission = false; // Default to false, will be updated from API
        }
    }

    private class ManageIconAdapter extends RecyclerView.Adapter<ManageIconAdapter.ViewHolder> {
        private List<ManageIcon> icons;

        ManageIconAdapter(List<ManageIcon> icons) {
            this.icons = icons;
            Log.d(TAG, "Adapter created with " + icons.size() + " icons");
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_manage_icon_permission, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ManageIcon icon = icons.get(position);
            holder.iconNameText.setText(icon.iconName);
            holder.iconDescriptionText.setText(icon.iconDescription);
            holder.permissionCheckBox.setChecked(icon.hasPermission);

            // Set icon image if available (you might need to load from URL)
            if (icon.iconImage != null && !icon.iconImage.isEmpty()) {
                // For now, we'll use a placeholder. You can implement image loading here
                holder.iconImageView.setImageResource(R.drawable.ic_launcher_foreground);
            }

            holder.permissionCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                icon.hasPermission = isChecked;
                updateIconPermission(icon.id, userId, isChecked);
            });
        }

        @Override
        public int getItemCount() {
            return icons.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView iconImageView;
            TextView iconNameText;
            TextView iconDescriptionText;
            CheckBox permissionCheckBox;

            ViewHolder(View view) {
                super(view);
                iconImageView = view.findViewById(R.id.iconImageView);
                iconNameText = view.findViewById(R.id.iconNameText);
                iconDescriptionText = view.findViewById(R.id.iconDescriptionText);
                permissionCheckBox = view.findViewById(R.id.permissionCheckBox);
            }
        }
    }
} 