package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.DefaultRetryPolicy;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class BHPayoutTeamActivity extends AppCompatActivity {
    private static final String TAG = "BHPayoutTeamActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";
    
    private ImageButton backButton;
    private TextView titleText, welcomeText;
    private RecyclerView payoutTypesRecyclerView;
    private MaterialCardView noDataCard;
    
    private List<PayoutType> payoutTypes = new ArrayList<>();
    private PayoutTypeAdapter payoutTypeAdapter;
    private RequestQueue requestQueue;
    
    // User data
    private String userName;
    private String userId;
    private String userDesignation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bh_payout_team);
        
        // Initialize Volley queue early for better performance
        requestQueue = Volley.newRequestQueue(this);
        
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
            userName = "BH";
        }
        if (userDesignation == null || userDesignation.isEmpty()) {
            userDesignation = "BH";
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
        
        // Load data immediately with optimized Volley
        loadPayoutTypes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll("BHPayoutTeamActivity");
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Cancel ongoing network requests when activity is paused
        if (requestQueue != null) {
            requestQueue.cancelAll("BHPayoutTeamActivity");
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reinitialize Volley queue if needed
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
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
        Intent intent = new Intent(this, BHPayoutPanelActivity.class);
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
        // Use the new endpoint that fetches payout types from tbl_payout_type using user's payout_icons
        String url = BASE_URL + "get_user_payout_types_details.php?user_id=" + userId;
        Log.d(TAG, "Fetching payout types details for user " + userId + " from: " + url);
        
        // Simple GET request with user_id parameter
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "API Response: " + response.toString());
                    try {
                        String status = response.getString("status");
                        
                        if ("success".equals(status)) {
                            JSONArray dataArray = response.getJSONArray("data");
                            List<PayoutType> newPayoutTypes = new ArrayList<>();
                            
                            // Log additional response details for debugging
                            if (response.has("payout_icons_count")) {
                                Log.d(TAG, "User has " + response.getInt("payout_icons_count") + " payout icons assigned");
                            }
                            if (response.has("count")) {
                                Log.d(TAG, "Found " + response.getInt("count") + " payout types in tbl_payout_type");
                            }
                            
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject payoutType = dataArray.getJSONObject(i);
                                int id = payoutType.getInt("id");
                                String name = payoutType.getString("payout_name");
                                Log.d(TAG, "Payout Type: ID=" + id + ", Name=" + name);
                                newPayoutTypes.add(new PayoutType(id, name));
                            }
                            
                            // Update UI on main thread
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
                            
                        } else {
                            String message = response.getString("message");
                            Toast.makeText(BHPayoutTeamActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                            noDataCard.setVisibility(View.VISIBLE);
                            payoutTypesRecyclerView.setVisibility(View.GONE);
                            Log.e(TAG, "API returned error: " + message);
                            
                            // Log additional error details if available
                            if (response.has("payout_icons_count")) {
                                Log.e(TAG, "User has " + response.getInt("payout_icons_count") + " payout icons but API returned error");
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON response", e);
                        Toast.makeText(BHPayoutTeamActivity.this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        noDataCard.setVisibility(View.VISIBLE);
                        payoutTypesRecyclerView.setVisibility(View.GONE);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Volley Error loading payout types for user " + userId, error);
                    String errorMessage = "Network error: " + error.getMessage();
                    if (error.networkResponse != null) {
                        errorMessage += " (Status: " + error.networkResponse.statusCode + ")";
                    }
                    Toast.makeText(BHPayoutTeamActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    noDataCard.setVisibility(View.VISIBLE);
                    payoutTypesRecyclerView.setVisibility(View.GONE);
                }
            }
        );
        
        // Add aggressive timeout and retry policy for maximum speed
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
            5000,  // 5 seconds timeout - much faster!
            0,      // No retries - fail fast
            1.0f    // No backoff multiplier
        ));
        
        // Add to Volley queue
        if (requestQueue != null) {
            requestQueue.add(jsonRequest);
        }
    }
}

