package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

public class DirectorDynamicPayoutPanelActivity extends AppCompatActivity {

    private static final String TAG = "DirectorDynamicPayout";

    // UI Elements
    private ImageView backButton;
    private TextView dataCountText;
    private RecyclerView payoutRecyclerView;
    private LinearLayout emptyStateLayout;

    // Data
    private List<PayoutBox> payoutList = new ArrayList<>();
    private PayoutBoxAdapter payoutAdapter;
    private RequestQueue requestQueue;

    // User data
    private String userId;
    private String username;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_dynamic_payout_panel);

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Get user data from intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupClickListeners();
        loadPayoutData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        dataCountText = findViewById(R.id.dataCountText);
        payoutRecyclerView = findViewById(R.id.payoutRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        // Setup RecyclerView
        payoutAdapter = new PayoutBoxAdapter(payoutList, this::onPayoutBoxClick);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        payoutRecyclerView.setLayoutManager(layoutManager);
        payoutRecyclerView.setAdapter(payoutAdapter);
        payoutRecyclerView.setHasFixedSize(true);
        payoutRecyclerView.setNestedScrollingEnabled(true);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void loadPayoutData() {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User ID not available", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://emp.kfinone.com/mobile/api/get_user_payout_data.php?user_id=" + userId;
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "Payout data response: " + response.toString());
                    parsePayoutData(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error loading payout data: " + error.getMessage());
                    Toast.makeText(DirectorDynamicPayoutPanelActivity.this, "Error loading payout data", Toast.LENGTH_SHORT).show();
                    showEmptyState();
                }
            }
        );

        requestQueue.add(request);
    }

    private void parsePayoutData(JSONObject response) {
        payoutList.clear();
        try {
            String status = response.getString("status");
            if ("success".equals(status)) {
                JSONArray data = response.getJSONArray("data");
                Log.d(TAG, "Parsing payout data. Response length: " + data.length());
                
                for (int i = 0; i < data.length(); i++) {
                    JSONObject payout = data.getJSONObject(i);
                    PayoutBox payoutBox = new PayoutBox(
                        payout.optString("id", ""),
                        payout.optString("user_id", ""),
                        payout.optString("payout_type_id", ""),
                        payout.optString("loan_type_id", ""),
                        payout.optString("vendor_bank_id", ""),
                        payout.optString("category_id", ""),
                        payout.optString("payout", ""),
                        payout.optString("status", ""),
                        payout.optString("createdBy", ""),
                        payout.optString("created_user", ""),
                        payout.optString("created_at", ""),
                        payout.optString("updated_at", ""),
                        payout.optString("payout_name", ""),
                        payout.optString("payout_type_table_id", "")
                    );
                    payoutList.add(payoutBox);
                    Log.d(TAG, "Added payout: " + payoutBox.getDisplayName());
                }
                
                Log.d(TAG, "Total payouts loaded: " + payoutList.size());
                updateDataDisplay();
                
                if (payoutList.isEmpty()) {
                    Toast.makeText(this, "No payouts found for this user", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Loaded " + payoutList.size() + " payouts", Toast.LENGTH_SHORT).show();
                }
            } else {
                String message = response.optString("message", "Failed to load payout data");
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing payout data: " + e.getMessage());
            Toast.makeText(this, "Error parsing payout data", Toast.LENGTH_SHORT).show();
            showEmptyState();
        }
    }

    private void updateDataDisplay() {
        Log.d(TAG, "Updating data display. Payout count: " + payoutList.size());
        payoutAdapter.notifyDataSetChanged();
        dataCountText.setText("Total Payouts: " + payoutList.size());
        
        if (payoutList.isEmpty()) {
            Log.d(TAG, "No payouts to display, showing empty state");
            payoutRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Showing " + payoutList.size() + " payouts in RecyclerView");
            payoutRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    private void showEmptyState() {
        payoutList.clear();
        updateDataDisplay();
    }

    private void onPayoutBoxClick(PayoutBox payoutBox) {
        // Handle payout box click - you can add specific actions here
        Toast.makeText(this, "Clicked: " + payoutBox.getDisplayName(), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Payout box clicked: " + payoutBox.getDisplayName());
        
        // You can add navigation to a detailed payout view here
        // Intent intent = new Intent(this, PayoutDetailActivity.class);
        // intent.putExtra("PAYOUT_ID", payoutBox.getId());
        // startActivity(intent);
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
        if (username != null) intent.putExtra("USERNAME", username);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
}
