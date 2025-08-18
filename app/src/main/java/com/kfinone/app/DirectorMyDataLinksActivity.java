package com.kfinone.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DirectorMyDataLinksActivity extends AppCompatActivity {

    private static final String TAG = "DirectorMyDataLinksActivity";
    
    private LinearLayout linksContainer;
    private ExecutorService executor;
    
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;
    
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_my_data_links);

        // Get user data from intent
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            userName = intent.getStringExtra("USERNAME");
            firstName = intent.getStringExtra("FIRST_NAME");
            lastName = intent.getStringExtra("LAST_NAME");
        }

        // Initialize executor service
        executor = Executors.newSingleThreadExecutor();

        initializeViews();
        setupToolbar();
        loadDataIcons();
    }

    private void initializeViews() {
        linksContainer = findViewById(R.id.linksContainer);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Director Panel - My Link Headings");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void loadDataIcons() {
        executor.execute(() -> {
            try {
                // First, get the data_icons IDs from tbl_user
                String userUrl = BASE_URL + "get_director_user_data_icons.php";
                JSONObject userRequest = new JSONObject();
                userRequest.put("user_id", userId);
                
                String userResponse = makeHttpRequest(userUrl, userRequest.toString());
                Log.d(TAG, "User Data Icons API response: " + userResponse);
                
                if (userResponse != null) {
                    JSONObject userJson = new JSONObject(userResponse);
                    if (userJson.getBoolean("success")) {
                        String dataIconsIds = userJson.getString("data_icons");
                        Log.d(TAG, "Data Icons IDs: " + dataIconsIds);
                        
                        // Parse the comma-separated IDs
                        String[] iconIds = dataIconsIds.split(",");
                        List<String> iconIdList = new ArrayList<>();
                        for (String id : iconIds) {
                            String trimmedId = id.trim();
                            if (!trimmedId.isEmpty()) {
                                iconIdList.add(trimmedId);
                            }
                        }
                        
                        // Now fetch the actual icon data from tbl_data_icon
                        if (!iconIdList.isEmpty()) {
                            fetchDataIconDetails(iconIdList);
                        } else {
                            runOnUiThread(() -> {
                                TextView noDataText = new TextView(this);
                                noDataText.setText("No data icons found for this user");
                                noDataText.setTextSize(16);
                                noDataText.setTextColor(getResources().getColor(android.R.color.darker_gray));
                                noDataText.setGravity(Gravity.CENTER);
                                noDataText.setPadding(32, 64, 32, 64);
                                linksContainer.addView(noDataText);
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            try {
                                Toast.makeText(this, "Error: " + userJson.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error: No response from server", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading data icons", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading data icons: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void fetchDataIconDetails(List<String> iconIds) {
        executor.execute(() -> {
            try {
                // Fetch icon details from tbl_data_icon
                String iconUrl = BASE_URL + "get_director_data_icon_details.php";
                JSONObject iconRequest = new JSONObject();
                iconRequest.put("icon_ids", new JSONArray(iconIds));
                
                String iconResponse = makeHttpRequest(iconUrl, iconRequest.toString());
                Log.d(TAG, "Data Icon Details API response: " + iconResponse);
                
                if (iconResponse != null) {
                    JSONObject iconJson = new JSONObject(iconResponse);
                    if (iconJson.getBoolean("success")) {
                        JSONArray iconData = iconJson.getJSONArray("data");
                        List<DataIcon> dataIcons = new ArrayList<>();
                        
                        for (int i = 0; i < iconData.length(); i++) {
                            JSONObject icon = iconData.getJSONObject(i);
                            dataIcons.add(new DataIcon(
                                icon.getString("id"),
                                icon.getString("icon_name"),
                                icon.getString("icon_url"),
                                icon.getString("icon_image"),
                                icon.getString("icon_description")
                            ));
                        }
                        
                        // Display the icons on UI thread
                        runOnUiThread(() -> displayDataIcons(dataIcons));
                    } else {
                        runOnUiThread(() -> {
                            try {
                                Toast.makeText(this, "Error: " + iconJson.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error: No response from icon details API", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching icon details", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error fetching icon details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void displayDataIcons(List<DataIcon> dataIcons) {
        linksContainer.removeAllViews();
        
        if (dataIcons.isEmpty()) {
            TextView noDataText = new TextView(this);
            noDataText.setText("No data icons found");
            noDataText.setTextSize(16);
            noDataText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            noDataText.setGravity(Gravity.CENTER);
            noDataText.setPadding(32, 64, 32, 64);
            linksContainer.addView(noDataText);
            return;
        }
        
        for (DataIcon icon : dataIcons) {
            // Create icon box
            LinearLayout iconBox = createIconBox(icon);
            linksContainer.addView(iconBox);
            
            // Add separator
            View separator = new View(this);
            separator.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
            ));
            separator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            separator.setPadding(0, 8, 0, 8);
            linksContainer.addView(separator);
        }
    }

    private LinearLayout createIconBox(DataIcon icon) {
        LinearLayout iconBox = new LinearLayout(this);
        iconBox.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        iconBox.setOrientation("horizontal".equals("horizontal") ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        iconBox.setPadding(16, 16, 16, 16);
        iconBox.setBackgroundResource(R.drawable.edit_text_background);
        iconBox.setClickable(true);
        iconBox.setFocusable(true);
        iconBox.setForeground(getResources().getDrawable(android.R.drawable.list_selector_background));
        iconBox.setElevation(2);
        
        // Icon image (placeholder for now)
        TextView iconText = new TextView(this);
        iconText.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        iconText.setText("📊");
        iconText.setTextSize(24);
        iconText.setPadding(0, 0, 16, 0);
        iconBox.addView(iconText);
        
        // Icon details
        LinearLayout detailsLayout = new LinearLayout(this);
        detailsLayout.setLayoutParams(new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1
        ));
        detailsLayout.setOrientation(LinearLayout.VERTICAL);
        
        // Icon name
        TextView nameText = new TextView(this);
        nameText.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        nameText.setText(icon.getIconName());
        nameText.setTextSize(16);
        nameText.setTypeface(null, android.graphics.Typeface.BOLD);
        nameText.setTextColor(getResources().getColor(android.R.color.black));
        detailsLayout.addView(nameText);
        
        // Icon description
        if (icon.getIconDescription() != null && !icon.getIconDescription().isEmpty()) {
            TextView descText = new TextView(this);
            descText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            descText.setText(icon.getIconDescription());
            descText.setTextSize(12);
            descText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            descText.setPadding(0, 4, 0, 0);
            detailsLayout.addView(descText);
        }
        
        iconBox.addView(detailsLayout);
        
        // Click listener to open URL
        iconBox.setOnClickListener(v -> {
            if (icon.getIconUrl() != null && !icon.getIconUrl().isEmpty()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(icon.getIconUrl()));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Error opening link: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No URL available for this icon", Toast.LENGTH_SHORT).show();
            }
        });
        
        return iconBox;
    }

    private String makeHttpRequest(String url, String jsonBody) {
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(jsonBody, JSON);
            
            Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
            
            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        } catch (IOException e) {
            Log.e(TAG, "HTTP request failed", e);
            return null;
        }
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        // Navigate back to Data Links panel
        Intent intent = new Intent(this, DirectorDataLinksActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }

    // Data class for Data Icon
    private static class DataIcon {
        private String id;
        private String iconName;
        private String iconUrl;
        private String iconImage;
        private String iconDescription;
        
        public DataIcon(String id, String iconName, String iconUrl, String iconImage, String iconDescription) {
            this.id = id;
            this.iconName = iconName;
            this.iconUrl = iconUrl;
            this.iconImage = iconImage;
            this.iconDescription = iconDescription;
        }
        
        public String getId() { return id; }
        public String getIconName() { return iconName; }
        public String getIconUrl() { return iconUrl; }
        public String getIconImage() { return iconImage; }
        public String getIconDescription() { return iconDescription; }
    }
}
