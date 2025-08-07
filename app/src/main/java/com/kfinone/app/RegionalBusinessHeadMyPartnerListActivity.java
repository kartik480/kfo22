package com.kfinone.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegionalBusinessHeadMyPartnerListActivity extends AppCompatActivity {
    private TextView totalPartnerCount, activePartnerCount, welcomeText;
    private ListView partnerListView;
    private EditText searchEditText;
    private Button refreshButton;
    private ProgressBar loadingProgress;
    private View emptyStateLayout;
    
    private String userId;
    private String userName;
    private PartnerAdapter partnerAdapter;
    private List<PartnerUser> partnerList;
    
    private static final String API_BASE_URL = "https://emp.kfinone.com/mobile/api/";
    private static final String RBH_PARTNER_USERS_API = "rbh_my_partner_users.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar and make fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_regional_business_head_my_partner_list);
        
        // Get user data from intent
        userName = getIntent().getStringExtra("USERNAME");
        userId = getIntent().getStringExtra("USER_ID");
        if (userName == null || userName.isEmpty()) {
            userName = "RBH"; // Default fallback
        }
        
        initializeViews();
        setupToolbar();
        setupClickListeners();
        setupSearchFunctionality();
        initializePartnerList();
        loadPartnerData();
        updateWelcomeMessage(userName);
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        totalPartnerCount = findViewById(R.id.totalPartnerCount);
        activePartnerCount = findViewById(R.id.activePartnerCount);
        partnerListView = findViewById(R.id.partnerListView);
        searchEditText = findViewById(R.id.searchEditText);
        refreshButton = findViewById(R.id.refreshButton);
        loadingProgress = findViewById(R.id.loadingProgress);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Partner List Active");
        }
    }

    private void setupClickListeners() {
        refreshButton.setOnClickListener(v -> loadPartnerData());
    }

    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (partnerAdapter != null) {
                    partnerAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void initializePartnerList() {
        partnerList = new ArrayList<>();
        partnerAdapter = new PartnerAdapter(this, partnerList);
        partnerListView.setAdapter(partnerAdapter);
    }

    private void loadPartnerData() {
        showLoading(true);
        
        String url = API_BASE_URL + RBH_PARTNER_USERS_API + "?username=" + userName;
        
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(RegionalBusinessHeadMyPartnerListActivity.this, 
                        "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseBody = response.body().string();
                
                runOnUiThread(() -> {
                    showLoading(false);
                    handleApiResponse(responseBody);
                });
            }
        });
    }

    private void handleApiResponse(String responseBody) {
        try {
            // Debug: Log the response
            System.out.println("API Response: " + responseBody);
            
            JSONObject jsonResponse = new JSONObject(responseBody);
            String status = jsonResponse.getString("status");
            
            System.out.println("Status: " + status);
            
            if ("success".equals(status)) {
                System.out.println("✅ Success status detected!");
                
                // Parse statistics
                JSONObject statistics = jsonResponse.getJSONObject("statistics");
                int totalPartners = statistics.getInt("total_partners");
                int activePartners = statistics.getInt("active_partners");
                
                System.out.println("Total Partners: " + totalPartners);
                System.out.println("Active Partners: " + activePartners);
                
                // Update UI with statistics
                totalPartnerCount.setText(String.valueOf(totalPartners));
                activePartnerCount.setText(String.valueOf(activePartners));
                
                // Parse partner data
                JSONArray dataArray = jsonResponse.getJSONArray("data");
                System.out.println("Data array length: " + dataArray.length());
                
                partnerList.clear();
                
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject partnerJson = dataArray.getJSONObject(i);
                    PartnerUser partner = parsePartnerFromJson(partnerJson);
                    partnerList.add(partner);
                    System.out.println("Added partner: " + partner.getFirstName() + " " + partner.getLastName());
                }
                
                System.out.println("Final partner list size: " + partnerList.size());
                
                // Update adapter
                System.out.println("About to update adapter with " + partnerList.size() + " partners");
                partnerAdapter.updateData(partnerList);
                System.out.println("Adapter updated successfully");
                
                // Show/hide empty state
                if (partnerList.isEmpty()) {
                    System.out.println("Partner list is empty, showing empty state");
                    showEmptyState(true);
                } else {
                    System.out.println("Partner list has data, hiding empty state");
                    showEmptyState(false);
                }
                
            } else {
                String message = jsonResponse.getString("message");
                System.out.println("❌ Error status: " + message);
                Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
                showEmptyState(true);
            }
            
        } catch (JSONException e) {
            System.out.println("JSON Exception: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
            showEmptyState(true);
        } catch (Exception e) {
            System.out.println("General Exception: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            showEmptyState(true);
        }
    }

    private PartnerUser parsePartnerFromJson(JSONObject json) throws JSONException {
        PartnerUser partner = new PartnerUser();
        
        partner.setId(json.optString("id"));
        partner.setUsername(json.optString("username"));
        partner.setAliasName(json.optString("alias_name"));
        partner.setFirstName(json.optString("first_name"));
        partner.setLastName(json.optString("last_name"));
        partner.setPassword(json.optString("password"));
        partner.setPhoneNumber(json.optString("Phone_number"));
        partner.setEmailId(json.optString("email_id"));
        partner.setAlternativeMobileNumber(json.optString("alternative_mobile_number"));
        partner.setCompanyName(json.optString("company_name"));
        partner.setBranchStateNameId(json.optString("branch_state_name_id"));
        partner.setBranchLocationId(json.optString("branch_location_id"));
        partner.setBankId(json.optString("bank_id"));
        partner.setAccountTypeId(json.optString("account_type_id"));
        partner.setOfficeAddress(json.optString("office_address"));
        partner.setResidentialAddress(json.optString("residential_address"));
        partner.setAadhaarNumber(json.optString("aadhaar_number"));
        partner.setPanNumber(json.optString("pan_number"));
        partner.setAccountNumber(json.optString("account_number"));
        partner.setIfscCode(json.optString("ifsc_code"));
        partner.setRank(json.optString("rank"));
        partner.setStatus(json.optString("status"));
        partner.setReportingTo(json.optString("reportingTo"));
        partner.setEmployeeNo(json.optString("employee_no"));
        partner.setDepartment(json.optString("department"));
        partner.setDesignation(json.optString("designation"));
        partner.setBranchState(json.optString("branchstate"));
        partner.setBranchLocation(json.optString("branchloaction"));
        partner.setBankName(json.optString("bank_name"));
        partner.setAccountType(json.optString("account_type"));
        partner.setPartnerTypeId(json.optString("partner_type_id"));
        partner.setPanImg(json.optString("pan_img"));
        partner.setAadhaarImg(json.optString("aadhaar_img"));
        partner.setPhotoImg(json.optString("photo_img"));
        partner.setBankproofImg(json.optString("bankproof_img"));
        partner.setUserId(json.optString("user_id"));
        partner.setCreatedAt(json.optString("created_at"));
        partner.setCreatedBy(json.optString("createdBy"));
        partner.setUpdatedAt(json.optString("updated_at"));
        partner.setFullName(json.optString("full_name"));
        
        return partner;
    }

    private void showLoading(boolean show) {
        loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        partnerListView.setVisibility(show ? View.GONE : View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    private void showEmptyState(boolean show) {
        emptyStateLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        partnerListView.setVisibility(show ? View.GONE : View.VISIBLE);
        loadingProgress.setVisibility(View.GONE);
    }

    private void updateWelcomeMessage(String userName) {
        if (welcomeText != null) {
            welcomeText.setText("Welcome, " + userName + "!");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
} 