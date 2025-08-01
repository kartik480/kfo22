package com.kfinone.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ManagingDirectorPartnersTeamActivity extends AppCompatActivity implements PartnerUserAdapter.OnPartnerUserActionListener {
    private static final String TAG = "MDPartnersTeamActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    private RecyclerView partnerUsersRecyclerView;
    private PartnerUserAdapter partnerUserAdapter;
    private List<PartnerUser> allPartnerUsersList;
    private List<PartnerUser> filteredPartnerUsersList;
    private RequestQueue requestQueue;

    // UI Elements
    private ImageButton backButton;
    private TextInputEditText searchPartnerInput;
    private LinearLayout noPartnerUsersLayout;
    private LinearLayout loadingLayout;
    private TextView totalPartnersText;
    private TextView activePartnersText;
    private TextView inactivePartnersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managing_director_partners_team);

        initializeViews();
        setupRecyclerView();
        setupSearchFunctionality();
        setupVolley();
        loadPartnerData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        searchPartnerInput = findViewById(R.id.searchPartnerInput);
        noPartnerUsersLayout = findViewById(R.id.noPartnerUsersLayout);
        loadingLayout = findViewById(R.id.loadingLayout);
        partnerUsersRecyclerView = findViewById(R.id.partnerUsersRecyclerView);
        totalPartnersText = findViewById(R.id.totalPartnersText);
        activePartnersText = findViewById(R.id.activePartnersText);
        inactivePartnersText = findViewById(R.id.inactivePartnersText);

        backButton.setOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        allPartnerUsersList = new ArrayList<>();
        filteredPartnerUsersList = new ArrayList<>();
        partnerUserAdapter = new PartnerUserAdapter(this, filteredPartnerUsersList);
        partnerUserAdapter.setActionListener(this);
        partnerUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        partnerUsersRecyclerView.setAdapter(partnerUserAdapter);
    }

    private void setupSearchFunctionality() {
        searchPartnerInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPartnerUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupVolley() {
        requestQueue = Volley.newRequestQueue(this);
    }

    private void loadPartnerData() {
        showLoading(true);
        String url = BASE_URL + "managing_director_partners_team_new.php";
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                showLoading(false);
                try {
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
                        allPartnerUsersList = new ArrayList<>();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject partnerUserObj = data.getJSONObject(i);
                            PartnerUser partnerUser = new PartnerUser();
                            
                            partnerUser.setId(partnerUserObj.optString("id", ""));
                            partnerUser.setUsername(partnerUserObj.optString("username", ""));
                            partnerUser.setAliasName(partnerUserObj.optString("alias_name", ""));
                            partnerUser.setFirstName(partnerUserObj.optString("first_name", ""));
                            partnerUser.setLastName(partnerUserObj.optString("last_name", ""));
                            partnerUser.setPassword(partnerUserObj.optString("password", ""));
                            partnerUser.setPhoneNumber(partnerUserObj.optString("Phone_number", ""));
                            partnerUser.setEmailId(partnerUserObj.optString("email_id", ""));
                            partnerUser.setAlternativeMobileNumber(partnerUserObj.optString("alternative_mobile_number", ""));
                            partnerUser.setCompanyName(partnerUserObj.optString("company_name", ""));
                            partnerUser.setBranchStateNameId(partnerUserObj.optString("branch_state_name_id", ""));
                            partnerUser.setBranchLocationId(partnerUserObj.optString("branch_location_id", ""));
                            partnerUser.setBankId(partnerUserObj.optString("bank_id", ""));
                            partnerUser.setAccountTypeId(partnerUserObj.optString("account_type_id", ""));
                            partnerUser.setOfficeAddress(partnerUserObj.optString("office_address", ""));
                            partnerUser.setResidentialAddress(partnerUserObj.optString("residential_address", ""));
                            partnerUser.setAadhaarNumber(partnerUserObj.optString("aadhaar_number", ""));
                            partnerUser.setPanNumber(partnerUserObj.optString("pan_number", ""));
                            partnerUser.setAccountNumber(partnerUserObj.optString("account_number", ""));
                            partnerUser.setIfscCode(partnerUserObj.optString("ifsc_code", ""));
                            partnerUser.setRank(partnerUserObj.optString("rank", ""));
                            partnerUser.setStatus(partnerUserObj.optString("status", ""));
                            partnerUser.setReportingTo(partnerUserObj.optString("reportingTo", ""));
                            partnerUser.setEmployeeNo(partnerUserObj.optString("employee_no", ""));
                            partnerUser.setDepartment(partnerUserObj.optString("department", ""));
                            partnerUser.setDesignation(partnerUserObj.optString("designation", ""));
                            partnerUser.setBranchState(partnerUserObj.optString("branchstate", ""));
                            partnerUser.setBranchLocation(partnerUserObj.optString("branchloaction", ""));
                            partnerUser.setBankName(partnerUserObj.optString("bank_name", ""));
                            partnerUser.setAccountType(partnerUserObj.optString("account_type", ""));
                            partnerUser.setPartnerTypeId(partnerUserObj.optString("partner_type_id", ""));
                            partnerUser.setPanImg(partnerUserObj.optString("pan_img", ""));
                            partnerUser.setAadhaarImg(partnerUserObj.optString("aadhaar_img", ""));
                            partnerUser.setPhotoImg(partnerUserObj.optString("photo_img", ""));
                            partnerUser.setBankproofImg(partnerUserObj.optString("bankproof_img", ""));
                            partnerUser.setUserId(partnerUserObj.optString("user_id", ""));
                            partnerUser.setCreatedAt(partnerUserObj.optString("created_at", ""));
                            partnerUser.setCreatedBy(partnerUserObj.optString("createdBy", ""));
                            partnerUser.setUpdatedAt(partnerUserObj.optString("updated_at", ""));
                            partnerUser.setCreatorName(partnerUserObj.optString("creator_name", ""));
                            partnerUser.setCreatorDesignationId(partnerUserObj.optString("creator_designation_id", ""));
                            partnerUser.setCreatorDesignationName(partnerUserObj.optString("creator_designation_name", ""));
                            
                            allPartnerUsersList.add(partnerUser);
                        }
                        
                        filteredPartnerUsersList.clear();
                        filteredPartnerUsersList.addAll(allPartnerUsersList);
                        partnerUserAdapter.notifyDataSetChanged();
                        updateStats();
                        updateUI();
                        
                    } else {
                        String message = response.optString("message", "Unknown error occurred");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        showNoPartnerUsersMessage();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    showNoPartnerUsersMessage();
                }
            },
            error -> {
                showLoading(false);
                Log.e(TAG, "Error fetching partner users: " + error.getMessage());
                Toast.makeText(this, "Error fetching partner users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                showNoPartnerUsersMessage();
            }
        );
        
        requestQueue.add(request);
    }

    private void filterPartnerUsers(String query) {
        if (query.isEmpty()) {
            filteredPartnerUsersList.clear();
            filteredPartnerUsersList.addAll(allPartnerUsersList);
        } else {
            List<PartnerUser> filteredList = new ArrayList<>();
            String lowerQuery = query.toLowerCase();
            
            for (PartnerUser partnerUser : allPartnerUsersList) {
                if (partnerUser.getFullName().toLowerCase().contains(lowerQuery) ||
                    partnerUser.getUsername().toLowerCase().contains(lowerQuery) ||
                    partnerUser.getEmailId().toLowerCase().contains(lowerQuery) ||
                    partnerUser.getPhoneNumber().contains(query) ||
                    partnerUser.getCompanyName().toLowerCase().contains(lowerQuery) ||
                    partnerUser.getCreatorName().toLowerCase().contains(lowerQuery) ||
                    partnerUser.getCreatorDesignationName().toLowerCase().contains(lowerQuery)) {
                    filteredList.add(partnerUser);
                }
            }
            
            filteredPartnerUsersList.clear();
            filteredPartnerUsersList.addAll(filteredList);
        }
        
        partnerUserAdapter.notifyDataSetChanged();
        updateUI();
    }

    private void updateStats() {
        int total = allPartnerUsersList.size();
        int active = 0;
        int inactive = 0;
        
        for (PartnerUser partnerUser : allPartnerUsersList) {
            if ("1".equals(partnerUser.getStatus()) || "Active".equalsIgnoreCase(partnerUser.getStatus())) {
                active++;
            } else {
                inactive++;
            }
        }
        
        totalPartnersText.setText("Total: " + total);
        activePartnersText.setText("Active: " + active);
        inactivePartnersText.setText("Inactive: " + inactive);
    }

    private void updateUI() {
        if (filteredPartnerUsersList.isEmpty()) {
            showNoPartnerUsersMessage();
        } else {
            hideNoPartnerUsersMessage();
        }
    }

    private void showLoading(boolean show) {
        loadingLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        partnerUsersRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        noPartnerUsersLayout.setVisibility(View.GONE);
    }

    private void showNoPartnerUsersMessage() {
        noPartnerUsersLayout.setVisibility(View.VISIBLE);
        partnerUsersRecyclerView.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
    }

    private void hideNoPartnerUsersMessage() {
        noPartnerUsersLayout.setVisibility(View.GONE);
        partnerUsersRecyclerView.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onEditPartnerUser(PartnerUser partnerUser) {
        Toast.makeText(this, "Edit functionality for " + partnerUser.getFullName(), Toast.LENGTH_SHORT).show();
        // TODO: Implement edit functionality
    }

    @Override
    public void onDeletePartnerUser(PartnerUser partnerUser) {
        Toast.makeText(this, "Delete functionality for " + partnerUser.getFullName(), Toast.LENGTH_SHORT).show();
        // TODO: Implement delete functionality
    }

    @Override
    public void onViewDetails(PartnerUser partnerUser) {
        Toast.makeText(this, "View details for " + partnerUser.getFullName(), Toast.LENGTH_SHORT).show();
        // TODO: Implement view details functionality
    }
} 