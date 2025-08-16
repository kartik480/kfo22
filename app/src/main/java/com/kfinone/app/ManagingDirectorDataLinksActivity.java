package com.kfinone.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
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

public class ManagingDirectorDataLinksActivity extends AppCompatActivity {

    private static final String TAG = "ManagingDirectorDataLinks";
    
    // UI Elements
    private ImageView backButton;
    private ImageView refreshButton;
    private TextView welcomeText;
    private TextView linksCountText;
    private ProgressBar loadingProgressBar;
    private TextView errorMessage;
    private LinearLayout dataLinksGrid;
    
    // Data
    private List<DataIcon> dataIcons;
    private RequestQueue requestQueue;
    
    // User data
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_managing_director_data_links);

        initializeViews();
        setupClickListeners();
        getUserData();
        fetchDataIcons();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        welcomeText = findViewById(R.id.welcomeText);
        linksCountText = findViewById(R.id.linksCountText);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        errorMessage = findViewById(R.id.errorMessage);
        dataLinksGrid = findViewById(R.id.dataLinksGrid);
        
        requestQueue = Volley.newRequestQueue(this);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            finish();
        });
        
        refreshButton.setOnClickListener(v -> {
            fetchDataIcons();
        });
        
        // Add test button for debugging
        findViewById(R.id.testButton).setOnClickListener(v -> {
            addTestCards();
        });
    }

    private void getUserData() {
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            userName = intent.getStringExtra("USERNAME");
            
            if (userName != null && !userName.isEmpty()) {
                welcomeText.setText("Welcome to Data Links, " + userName + "!");
            }
        }
    }

    private void fetchDataIcons() {
        showLoading(true);
        hideError();
        
        // First, get the data_icons from tbl_user for the current user
        String userUrl = "https://emp.kfinone.com/mobile/api/get_user_data_icons.php?user_id=" + userId;
        
        JsonObjectRequest userRequest = new JsonObjectRequest(Request.Method.GET, userUrl, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.optBoolean("success", false)) {
                            JSONObject userData = response.getJSONObject("user");
                            String dataIconsStr = userData.optString("data_icons", "");
                            
                            if (!dataIconsStr.isEmpty()) {
                                try {
                                    // Parse the data_icons JSON array
                                    JSONArray iconIdsArray = new JSONArray(dataIconsStr);
                                    String[] iconIds = new String[iconIdsArray.length()];
                                    for (int i = 0; i < iconIdsArray.length(); i++) {
                                        iconIds[i] = iconIdsArray.getString(i);
                                    }
                                    fetchIconDetails(iconIds);
                                } catch (JSONException e) {
                                    // Fallback: try comma-separated string
                                    String[] iconIds = dataIconsStr.split(",");
                                    fetchIconDetails(iconIds);
                                }
                            } else {
                                showError("No data icons found for this user");
                            }
                        } else {
                            String errorMsg = response.optString("error", "Unknown error occurred");
                            showError("Error: " + errorMsg);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing user data: " + e.getMessage());
                        showError("Error parsing user data");
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error fetching user data: " + error.getMessage());
                    showError("Error fetching user data");
                }
            }
        );
        
        requestQueue.add(userRequest);
    }

    private void fetchIconDetails(String[] iconIds) {
        if (iconIds.length == 0) {
            showError("No icon IDs found");
            return;
        }
        
        // Build comma-separated list of IDs for the query
        StringBuilder idsBuilder = new StringBuilder();
        for (int i = 0; i < iconIds.length; i++) {
            if (i > 0) idsBuilder.append(",");
            idsBuilder.append(iconIds[i].trim());
        }
        
        String iconsUrl = "https://emp.kfinone.com/mobile/api/get_data_icons.php?ids=" + idsBuilder.toString();
        
        // Log the URL for debugging
        Log.d(TAG, "Fetching data icons from URL: " + iconsUrl);
        Log.d(TAG, "Icon IDs: " + idsBuilder.toString());
        
        JsonObjectRequest iconsRequest = new JsonObjectRequest(Request.Method.GET, iconsUrl, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.optBoolean("success", false)) {
                            JSONArray iconsArray = response.getJSONArray("icons");
                            dataIcons = new ArrayList<>();
                            
                            Log.d(TAG, "Received " + iconsArray.length() + " data icons from API");
                            
                            for (int i = 0; i < iconsArray.length(); i++) {
                                JSONObject iconData = iconsArray.getJSONObject(i);
                                
                                Log.d(TAG, "Data Icon " + i + ": " + iconData.toString());
                                
                                DataIcon icon = new DataIcon(
                                    iconData.optString("id"),
                                    iconData.optString("icon_name"),
                                    iconData.optString("icon_url"),
                                    iconData.optString("icon_image"),
                                    iconData.optString("icon_description")
                                );
                                
                                Log.d(TAG, "Created DataIcon: " + icon.getIconName() + " - " + icon.getIconDescription());
                                dataIcons.add(icon);
                            }
                            
                            Log.d(TAG, "Total DataIcon objects created: " + dataIcons.size());
                            displayDataIcons();
                        } else {
                            String errorMsg = response.optString("error", "Unknown error occurred");
                            showError("Error fetching data icons: " + errorMsg);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing icon data: " + e.getMessage());
                        showError("Error parsing icon data");
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error fetching data icons: " + error.getMessage());
                    showError("Error fetching data icons");
                }
            }
        );
        
        requestQueue.add(iconsRequest);
    }

    private void displayDataIcons() {
        if (dataIcons == null || dataIcons.isEmpty()) {
            showError("No data icons available");
            return;
        }
        
        Log.d(TAG, "Displaying " + dataIcons.size() + " data icons");
        
        dataLinksGrid.removeAllViews();
        
        // Create a 2-column grid using LinearLayouts
        LinearLayout currentRow = null;
        int activeCount = 0;
        
        for (int i = 0; i < dataIcons.size(); i++) {
            DataIcon icon = dataIcons.get(i);
            Log.d(TAG, "Data Icon: " + icon.getIconName() + " - " + icon.getIconDescription());
            
            // Create new row for every 2 icons
            if (i % 2 == 0) {
                currentRow = new LinearLayout(this);
                currentRow.setOrientation(LinearLayout.HORIZONTAL);
                currentRow.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                dataLinksGrid.addView(currentRow);
            }
            
            // Show all data icons
            CardView iconCard = createIconCard(icon);
            currentRow.addView(iconCard);
            activeCount++;
        }
        
        linksCountText.setText("Total Data Links: " + activeCount);
        showLoading(false);
        
        // Make sure the grid is visible
        dataLinksGrid.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.GONE);
        
        Log.d(TAG, "Display completed. Added " + activeCount + " data icons to grid");
    }

    private CardView createIconCard(DataIcon icon) {
        CardView cardView = new CardView(this);
        
        // Use LinearLayout.LayoutParams for proper positioning
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            getResources().getDisplayMetrics().widthPixels / 2 - 48, // 2 columns with margins
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(8, 8, 8, 8);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(12);
        cardView.setCardElevation(4);
        cardView.setCardBackgroundColor(getResources().getColor(android.R.color.white));
        
        // Card content
        LinearLayout cardContent = new LinearLayout(this);
        cardContent.setOrientation(LinearLayout.VERTICAL);
        cardContent.setPadding(16, 16, 16, 16);
        cardContent.setGravity(android.view.Gravity.CENTER);
        
        // Icon image
        ImageView iconImageView = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(48, 48);
        iconImageView.setLayoutParams(iconParams);
        iconImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        
        // Set default icon (we'll implement image loading later if needed)
        iconImageView.setImageResource(R.drawable.ic_people);
        
        // Icon name
        TextView iconNameText = new TextView(this);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nameParams.setMargins(0, 8, 0, 4);
        iconNameText.setLayoutParams(nameParams);
        iconNameText.setText(icon.getIconName());
        iconNameText.setTextSize(14);
        iconNameText.setTextColor(getResources().getColor(android.R.color.black));
        iconNameText.setTypeface(null, android.graphics.Typeface.BOLD);
        iconNameText.setGravity(android.view.Gravity.CENTER);
        
        // Icon description
        TextView iconDescText = new TextView(this);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        descParams.setMargins(0, 0, 0, 8);
        iconDescText.setLayoutParams(descParams);
        iconDescText.setText(icon.getIconDescription());
        iconDescText.setTextSize(10);
        iconDescText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        iconDescText.setGravity(android.view.Gravity.CENTER);
        iconDescText.setMaxLines(2);
        iconDescText.setEllipsize(android.text.TextUtils.TruncateAt.END);
        
        // Add views to card content
        cardContent.addView(iconImageView);
        cardContent.addView(iconNameText);
        cardContent.addView(iconDescText);
        
        // Add card content to card view
        cardView.addView(cardContent);
        
        // Set click listener to open URL
        cardView.setOnClickListener(v -> {
            openIconUrl(icon);
        });
        
        return cardView;
    }

    private void openIconUrl(DataIcon icon) {
        if (icon.getIconUrl() != null && !icon.getIconUrl().isEmpty()) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(icon.getIconUrl()));
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error opening URL: " + e.getMessage());
                Toast.makeText(this, "Error opening link: " + icon.getIconName(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No URL available for: " + icon.getIconName(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading(boolean show) {
        if (show) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            dataLinksGrid.setVisibility(View.GONE);
            errorMessage.setVisibility(View.GONE);
        } else {
            loadingProgressBar.setVisibility(View.GONE);
        }
    }
    
    private void addTestCards() {
        Log.d(TAG, "Adding test data cards to grid");
        dataLinksGrid.removeAllViews();
        
        // Create a test row
        LinearLayout testRow = new LinearLayout(this);
        testRow.setOrientation(LinearLayout.HORIZONTAL);
        testRow.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        
        // Create test DataIcon objects
        DataIcon testIcon1 = new DataIcon("1", "Test Data Icon 1", "https://example.com", "", "Test Data Description 1");
        DataIcon testIcon2 = new DataIcon("2", "Test Data Icon 2", "https://example.com", "", "Test Data Description 2");
        
        CardView card1 = createIconCard(testIcon1);
        CardView card2 = createIconCard(testIcon2);
        
        testRow.addView(card1);
        testRow.addView(card2);
        dataLinksGrid.addView(testRow);
        
        dataLinksGrid.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.GONE);
        
        Log.d(TAG, "Test data cards added. Grid should now be visible.");
    }

    private void showError(String message) {
        showLoading(false);
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
        dataLinksGrid.setVisibility(View.GONE);
    }

    private void hideError() {
        errorMessage.setVisibility(View.GONE);
    }

    // Data class for data icons
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
