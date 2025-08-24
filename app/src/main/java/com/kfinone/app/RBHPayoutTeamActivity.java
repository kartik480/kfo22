package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RBHPayoutTeamActivity extends AppCompatActivity {
    private static final String TAG = "RBHPayoutTeamActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";
    
    private ImageButton backButton;
    private TextView titleText, welcomeText;
    private RecyclerView payoutTypesRecyclerView;
    private MaterialCardView noDataCard;
    
    private List<PayoutType> payoutTypes = new ArrayList<>();
    private PayoutTypeAdapter payoutTypeAdapter;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    // User data
    private String userName;
    private String userId;
    private String userDesignation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rbh_payout_team);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        userDesignation = intent.getStringExtra("USER_DESIGNATION");
        
        // Debug logging
        Log.d(TAG, "Received userName: " + userName);
        Log.d(TAG, "Received userId: " + userId);
        Log.d(TAG, "Received userDesignation: " + userDesignation);
        
        if (userName == null || userName.isEmpty()) {
            userName = "RBH";
        }
        if (userDesignation == null || userDesignation.isEmpty()) {
            userDesignation = "RBH";
        }
        
        // Critical check: Ensure we have a valid userId
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "CRITICAL ERROR: No valid userId received!");
            Log.e(TAG, "This will cause API calls to fail with user_id=null");
        } else {
            Log.d(TAG, "✓ Valid userId received: " + userId);
        }
        
        initializeViews();
        setupClickListeners();
        updateWelcomeMessage();
        loadPayoutTypes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        welcomeText = findViewById(R.id.welcomeText);
        payoutTypesRecyclerView = findViewById(R.id.payoutTypesRecyclerView);
        noDataCard = findViewById(R.id.noDataCard);
        
        // Setup RecyclerView
        payoutTypesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        payoutTypeAdapter = new PayoutTypeAdapter(payoutTypes);
        payoutTypesRecyclerView.setAdapter(payoutTypeAdapter);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());
    }
    
    private void updateWelcomeMessage() {
        welcomeText.setText("Welcome, " + userName + " (" + userDesignation + ")");
        titleText.setText("My Assigned Payout Types");
    }
    
    private void goBack() {
        Intent intent = new Intent(this, RBHPayoutPanelActivity.class);
        intent.putExtra("USERNAME", userName);
        intent.putExtra("USER_ID", userId);
        intent.putExtra("USER_DESIGNATION", userDesignation);
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onBackPressed() {
        goBack();
    }
    
    private void loadPayoutTypes() {
        executor.execute(() -> {
            try {
                // Use the new endpoint that fetches payout types from tbl_payout_type using user's payout_icons
                String url = BASE_URL + "get_user_payout_types_details.php?user_id=" + userId;
                Log.d(TAG, "Fetching payout types details for user " + userId + " from: " + url);
                
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
                
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                Log.d(TAG, "API Response: " + responseBody);
                
                JSONObject jsonResponse = new JSONObject(responseBody);
                String status = jsonResponse.getString("status");
                
                if ("success".equals(status)) {
                    JSONArray dataArray = jsonResponse.getJSONArray("data");
                    List<PayoutType> newPayoutTypes = new ArrayList<>();
                    
                    // Log additional response details for debugging
                    if (jsonResponse.has("payout_icons_count")) {
                        Log.d(TAG, "User has " + jsonResponse.getInt("payout_icons_count") + " payout icons assigned");
                    }
                    if (jsonResponse.has("count")) {
                        Log.d(TAG, "Found " + jsonResponse.getInt("count") + " payout types in tbl_payout_type");
                    }
                    
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject payoutType = dataArray.getJSONObject(i);
                        int id = payoutType.getInt("id");
                        String name = payoutType.getString("payout_name");
                        Log.d(TAG, "Payout Type: ID=" + id + ", Name=" + name);
                        newPayoutTypes.add(new PayoutType(id, name));
                    }
                    
                    runOnUiThread(() -> {
                        payoutTypes.clear();
                        payoutTypes.addAll(newPayoutTypes);
                        payoutTypeAdapter.notifyDataSetChanged();
                        
                        if (payoutTypes.isEmpty()) {
                            noDataCard.setVisibility(View.VISIBLE);
                            payoutTypesRecyclerView.setVisibility(View.GONE);
                            Log.d(TAG, "No payout types found for user " + userId + " - checking if user has payout_icons assigned");
                        } else {
                            noDataCard.setVisibility(View.GONE);
                            payoutTypesRecyclerView.setVisibility(View.VISIBLE);
                            Log.d(TAG, "Successfully loaded " + payoutTypes.size() + " payout types from tbl_payout_type for user " + userId);
                        }
                    });
                    
                } else {
                    String message = jsonResponse.getString("message");
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
                        noDataCard.setVisibility(View.VISIBLE);
                        payoutTypesRecyclerView.setVisibility(View.GONE);
                        Log.e(TAG, "API returned error: " + message);
                        
                        // Log additional error details if available
                        if (jsonResponse.has("payout_icons_count")) {
                            try {
                                Log.e(TAG, "User has " + jsonResponse.getInt("payout_icons_count") + " payout icons but API returned error");
                            } catch (JSONException e) {
                                Log.e(TAG, "Error parsing payout_icons_count from error response");
                            }
                        }
                    });
                }
                
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error loading payout types", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    noDataCard.setVisibility(View.VISIBLE);
                    payoutTypesRecyclerView.setVisibility(View.GONE);
                });
            }
        });
    }
}
