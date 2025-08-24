package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessHeadMyPartnerActivity extends AppCompatActivity {
    
    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // User data
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;
    private String sourcePanel;
    
    // UI elements
    private ListView partnerListView;
    private ProgressBar loadingProgress;
    private TextView errorMessage;
    private TextView partnerCount;
    
    // Data
    private BusinessHeadPartnerAdapter partnerAdapter;
    private List<Map<String, Object>> partnerList;
    private RequestQueue requestQueue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_my_partner);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        sourcePanel = intent.getStringExtra("SOURCE_PANEL");
        
        // Debug: Log the received values
        Log.d("BusinessHeadMyPartner", "Received userName: " + userName);
        Log.d("BusinessHeadMyPartner", "Received userId: " + userId);
        Log.d("BusinessHeadMyPartner", "Received firstName: " + firstName);
        Log.d("BusinessHeadMyPartner", "Received lastName: " + lastName);
        Log.d("BusinessHeadMyPartner", "Received sourcePanel: " + sourcePanel);
        
        initializeViews();
        setupClickListeners();
        initializeData();
        loadPartnerList();
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
        
        // Partner list views
        partnerListView = findViewById(R.id.partnerListView);
        loadingProgress = findViewById(R.id.loadingProgress);
        errorMessage = findViewById(R.id.errorMessage);
        partnerCount = findViewById(R.id.partnerCount);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewPartner());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessHeadPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        empLinksButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessHeadEmpLinksActivity.class);
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

    private void goBack() {
        Intent intent;
        
        // Navigate back based on source panel
        if ("BH_PANEL".equals(sourcePanel)) {
            intent = new Intent(this, BusinessHeadPanelActivity.class);
        } else if ("BH_EMP_MASTER".equals(sourcePanel)) {
            intent = new Intent(this, BusinessHeadEmpMasterActivity.class);
        } else {
            // Default fallback
            intent = new Intent(this, BusinessHeadPanelActivity.class);
        }
        
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing partner list...", Toast.LENGTH_SHORT).show();
        loadPartnerList();
    }

    private void addNewPartner() {
        Toast.makeText(this, "Add New Partner - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to add partner activity
    }
    
    private void initializeData() {
        partnerList = new ArrayList<>();
        partnerAdapter = new BusinessHeadPartnerAdapter(this, partnerList);
        partnerListView.setAdapter(partnerAdapter);
        requestQueue = Volley.newRequestQueue(this);
    }

    private void loadPartnerList() {
        Log.d("BusinessHeadMyPartner", "Loading partner list for username: " + userName);
        
        if (userName == null || userName.isEmpty()) {
            Log.e("BusinessHeadMyPartner", "Username is null or empty");
            showError("Username not available");
            return;
        }

        showLoading(true);
        hideError();

        // API URL for Business Head My Partners (now working!)
        String url = "https://emp.kfinone.com/mobile/api/get_business_head_my_partners.php";
        Log.d("BusinessHeadMyPartner", "API URL: " + url);

        // Create request body with user_id
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("user_id", userId);
            requestBody.put("username", userName);
        } catch (JSONException e) {
            Log.e("BusinessHeadMyPartner", "Error creating request body: " + e.getMessage());
            showError("Error creating request");
            return;
        }
        
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.POST,
            url,
            requestBody,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    showLoading(false);
                    try {
                        // Debug: Log the full response
                        Log.d("BusinessHeadMyPartner", "API Response: " + response.toString());
                        
                        String status = response.getString("status");
                        Log.d("BusinessHeadMyPartner", "Status: " + status);
                        
                        if ("success".equals(status)) {
                            // Parse the working API response structure
                            JSONObject data = response.getJSONObject("data");
                            JSONArray partnerUsers = data.getJSONArray("partner_users");
                            
                            Log.d("BusinessHeadMyPartner", "Partner users count: " + partnerUsers.length());
                            
                            List<Map<String, Object>> newPartnerList = new ArrayList<>();
                            
                            for (int i = 0; i < partnerUsers.length(); i++) {
                                JSONObject partner = partnerUsers.getJSONObject(i);
                                
                                Map<String, Object> partnerData = new HashMap<>();
                                partnerData.put("id", partner.optString("id", "N/A"));
                                partnerData.put("username", partner.optString("username", "N/A"));
                                partnerData.put("alias_name", partner.optString("alias_name", "N/A"));
                                partnerData.put("first_name", partner.optString("first_name", "N/A"));
                                partnerData.put("last_name", partner.optString("last_name", "N/A"));
                                partnerData.put("Phone_number", partner.optString("Phone_number", "N/A"));
                                partnerData.put("email_id", partner.optString("email_id", "N/A"));
                                partnerData.put("alternative_mobile_number", partner.optString("alternative_mobile_number", "N/A"));
                                partnerData.put("company_name", partner.optString("company_name", "N/A"));
                                partnerData.put("status", partner.optString("status", "N/A"));
                                partnerData.put("rank", partner.optString("rank", "N/A"));
                                partnerData.put("reportingTo", partner.optString("reportingTo", "N/A"));
                                partnerData.put("employee_no", partner.optString("employee_no", "N/A"));
                                partnerData.put("department", partner.optString("department", "N/A"));
                                partnerData.put("designation", partner.optString("designation", "N/A"));
                                partnerData.put("branchstate", partner.optString("branchstate", "N/A"));
                                partnerData.put("branchloaction", partner.optString("branchloaction", "N/A"));
                                partnerData.put("bank_name", partner.optString("bank_name", "N/A"));
                                partnerData.put("account_type", partner.optString("account_type", "N/A"));
                                partnerData.put("partner_type_id", partner.optString("partner_type_id", "N/A"));
                                partnerData.put("created_at", partner.optString("created_at", "N/A"));
                                partnerData.put("createdBy", partner.optString("createdBy", "N/A"));
                                
                                newPartnerList.add(partnerData);
                            }
                            
                            partnerList.clear();
                            partnerList.addAll(newPartnerList);
                            partnerAdapter.notifyDataSetChanged();
                            
                                                            // Update partner count
                                partnerCount.setText("Total Partners: " + partnerList.size());
                            
                            if (partnerList.isEmpty()) {
                                showEmptyState();
                            } else {
                                hideEmptyState();
                                Toast.makeText(BusinessHeadMyPartnerActivity.this, 
                                    "Found " + partnerList.size() + " partners", 
                                    Toast.LENGTH_SHORT).show();
                            }
                            
                        } else {
                            String message = response.optString("message", "Unknown error");
                            showError("API Error: " + message);
                        }
                        
                    } catch (JSONException e) {
                        Log.e("BusinessHeadMyPartner", "JSON Parse Error: " + e.getMessage());
                        showError("Error parsing response: " + e.getMessage());
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showLoading(false);
                    String errorMessage = "Network error: " + error.getMessage();
                    if (error.networkResponse != null) {
                        errorMessage += " (Status: " + error.networkResponse.statusCode + ")";
                    }
                    showError(errorMessage);
                    Log.e("BusinessHeadMyPartner", "Volley Error: " + error.getMessage());
                }
            }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void showLoading(boolean show) {
        loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        partnerListView.setVisibility(show ? View.GONE : View.VISIBLE);
        errorMessage.setVisibility(View.GONE);
    }

    private void showError(String message) {
        showLoading(false);
        errorMessage.setText(message);
        errorMessage.setVisibility(View.VISIBLE);
        partnerListView.setVisibility(View.GONE);
    }

    private void hideError() {
        errorMessage.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        showLoading(false);
        errorMessage.setText("No partners found for this Business Head user");
        errorMessage.setVisibility(View.VISIBLE);
        partnerListView.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        errorMessage.setVisibility(View.GONE);
        partnerListView.setVisibility(View.VISIBLE);
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
        intent.putExtra("SOURCE_PANEL", "BH_MY_PARTNER");
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
} 