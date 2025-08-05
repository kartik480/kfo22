package com.kfinone.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DirectorPartnerTeamActivity extends AppCompatActivity implements PartnerUserAdapter.OnPartnerUserActionListener {
    private static final String TAG = "DirectorPartnerTeam";
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
    
    // New UI Elements for User Selection
    private AutoCompleteTextView userDropdown;
    private MaterialButton showDataButton;
    private MaterialButton resetButton;
    
    // Data for dropdown
    private List<User> usersList;
    private User selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managing_director_partners_team);

        initializeViews();
        setupRecyclerView();
        setupSearchFunctionality();
        setupVolley();
        loadUsersForDropdown(); // Load CBO and RBH users for dropdown
        loadPartnerData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        searchPartnerInput = findViewById(R.id.searchPartnerInput);
        noPartnerUsersLayout = findViewById(R.id.noPartnerUsersLayout);
        loadingLayout = findViewById(R.id.loadingLayout);
        totalPartnersText = findViewById(R.id.totalPartnersText);
        activePartnersText = findViewById(R.id.activePartnersText);
        inactivePartnersText = findViewById(R.id.inactivePartnersText);
        
        // User selection elements
        userDropdown = findViewById(R.id.userDropdown);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);
        
        // Setup back button
        backButton.setOnClickListener(v -> finish());
        
        // Setup show data button
        showDataButton.setOnClickListener(v -> {
            if (selectedUser != null) {
                showSelectedUserData();
            } else {
                Toast.makeText(this, "Please select a user first", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Setup reset button
        resetButton.setOnClickListener(v -> resetToAllData());
    }

    private void setupRecyclerView() {
        partnerUsersRecyclerView = findViewById(R.id.partnerUsersRecyclerView);
        partnerUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        allPartnerUsersList = new ArrayList<>();
        filteredPartnerUsersList = new ArrayList<>();
        partnerUserAdapter = new PartnerUserAdapter(this, filteredPartnerUsersList);
        partnerUserAdapter.setActionListener(this);
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
    
    private void loadUsersForDropdown() {
        Log.d(TAG, "Loading CBO and RBH users for dropdown...");
        
        String url = BASE_URL + "get_director_partner_team_dropdown.php";
        Log.d(TAG, "Loading users from URL: " + url);
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    Log.d(TAG, "API Response: " + response.toString());
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
                        Log.d(TAG, "Number of CBO/RBH users received: " + data.length());
                        usersList = new ArrayList<>();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject userObj = data.getJSONObject(i);
                            User user = new User();
                            
                            user.setId(userObj.optString("id", ""));
                            user.setUsername(userObj.optString("username", ""));
                            user.setFirstName(userObj.optString("firstName", ""));
                            user.setLastName(userObj.optString("lastName", ""));
                            user.setFullName(userObj.optString("fullName", ""));
                            user.setEmailId(userObj.optString("email_id", ""));
                            user.setMobile(userObj.optString("mobile", ""));
                            user.setEmployeeNo(userObj.optString("employee_no", ""));
                            user.setDesignationName(userObj.optString("designation_name", ""));
                            user.setDepartmentName(userObj.optString("department_name", ""));
                            user.setStatus(userObj.optString("status", ""));
                            
                            usersList.add(user);
                            Log.d(TAG, "Added CBO/RBH user: " + user.getFullName() + " (" + user.getDesignationName() + ")");
                        }
                        
                        Log.d(TAG, "Total CBO/RBH users in list: " + usersList.size());
                        setupUserDropdown();
                        
                    } else {
                        String message = response.optString("message", "Unknown error occurred");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                Log.e(TAG, "Error fetching CBO/RBH users for dropdown: " + error.getMessage());
                Toast.makeText(this, "Error fetching users for dropdown: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        );
        
        requestQueue.add(request);
    }
    
    private void setupUserDropdown() {
        Log.d(TAG, "Setting up user dropdown. Users list size: " + (usersList != null ? usersList.size() : "null"));
        
        if (usersList == null || usersList.isEmpty()) {
            Log.e(TAG, "Users list is null or empty, cannot setup dropdown");
            return;
        }
        
        ArrayAdapter<User> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, usersList);
        userDropdown.setAdapter(adapter);
        
        // Force the dropdown to show items
        userDropdown.setThreshold(1);
        
        userDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedUser = (User) parent.getItemAtPosition(position);
            Log.d(TAG, "Selected CBO/RBH user: " + selectedUser.getFullName() + " (ID: " + selectedUser.getId() + ", Designation: " + selectedUser.getDesignationName() + ")");
        });
        
        Log.d(TAG, "User dropdown setup completed");
    }

    private void loadPartnerData() {
        showLoading(true);
        String url = BASE_URL + "get_partner_users_by_cbo_rbh_creators_simple.php";
        
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                showLoading(false);
                try {
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
                        allPartnerUsersList.clear();
                        
                        // Parse the safe data structure with only existing fields
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject partnerObj = data.getJSONObject(i);
                            PartnerUser partnerUser = new PartnerUser();
                            
                            // Basic Information
                            partnerUser.setId(partnerObj.optString("id", ""));
                            partnerUser.setUsername(partnerObj.optString("username", ""));
                            partnerUser.setFirstName(partnerObj.optString("first_name", ""));
                            partnerUser.setLastName(partnerObj.optString("last_name", ""));
                            
                            // Contact Information
                            partnerUser.setPhoneNumber(partnerObj.optString("Phone_number", ""));
                            partnerUser.setEmailId(partnerObj.optString("email_id", ""));
                            
                            // Company Information
                            partnerUser.setCompanyName(partnerObj.optString("company_name", ""));
                            partnerUser.setDepartment(partnerObj.optString("department", ""));
                            partnerUser.setDesignation(partnerObj.optString("designation", ""));
                            partnerUser.setEmployeeNo(partnerObj.optString("employee_no", ""));
                            
                            // Status and Reporting
                            partnerUser.setStatus(partnerObj.optString("status", ""));
                            partnerUser.setReportingTo(partnerObj.optString("reportingTo", ""));
                            partnerUser.setRank(partnerObj.optString("rank", ""));
                            
                            // Partner Type
                            partnerUser.setPartnerTypeId(partnerObj.optString("partner_type_id", ""));
                            
                            // Timestamps
                            partnerUser.setCreatedAt(partnerObj.optString("created_at", ""));
                            partnerUser.setCreatedBy(partnerObj.optString("createdBy", ""));
                            
                            // Creator Information (from JOIN)
                            partnerUser.setCreatorName(partnerObj.optString("creator_username", ""));
                            partnerUser.setCreatorDesignationName(partnerObj.optString("creator_first_name", "") + " " + partnerObj.optString("creator_last_name", ""));
                            
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
                Log.e(TAG, "Error fetching partner data: " + error.getMessage());
                Toast.makeText(this, "Error fetching partner data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                showNoPartnerUsersMessage();
            }
        );
        
        requestQueue.add(request);
    }

    private void filterPartnerUsers(String query) {
        filteredPartnerUsersList.clear();
        
        if (query.isEmpty()) {
            filteredPartnerUsersList.addAll(allPartnerUsersList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (PartnerUser partner : allPartnerUsersList) {
                if (partner.getFullName().toLowerCase().contains(lowerQuery) ||
                    partner.getEmailId().toLowerCase().contains(lowerQuery) ||
                    partner.getPhoneNumber().toLowerCase().contains(lowerQuery) ||
                    partner.getCompanyName().toLowerCase().contains(lowerQuery) ||
                    partner.getDepartment().toLowerCase().contains(lowerQuery) ||
                    partner.getDesignation().toLowerCase().contains(lowerQuery)) {
                    filteredPartnerUsersList.add(partner);
                }
            }
        }
        
        partnerUserAdapter.notifyDataSetChanged();
        updateStats();
    }

    private void updateStats() {
        int total = filteredPartnerUsersList.size();
        int active = 0;
        int inactive = 0;
        
        for (PartnerUser partner : filteredPartnerUsersList) {
            if ("Active".equalsIgnoreCase(partner.getStatus()) || "1".equals(partner.getStatus())) {
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
    }

    private void showNoPartnerUsersMessage() {
        noPartnerUsersLayout.setVisibility(View.VISIBLE);
        partnerUsersRecyclerView.setVisibility(View.GONE);
    }

    private void hideNoPartnerUsersMessage() {
        noPartnerUsersLayout.setVisibility(View.GONE);
        partnerUsersRecyclerView.setVisibility(View.VISIBLE);
    }
    
    private void showSelectedUserData() {
        if (selectedUser == null) {
            Toast.makeText(this, "No user selected", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Log.d(TAG, "Showing data for selected user: " + selectedUser.getFullName() + " (ID: " + selectedUser.getId() + ")");
        
        // Filter partner users to show only those reporting to the selected user
        filteredPartnerUsersList.clear();
        for (PartnerUser partner : allPartnerUsersList) {
            if (selectedUser.getId().equals(partner.getReportingTo())) {
                filteredPartnerUsersList.add(partner);
            }
        }
        
        partnerUserAdapter.notifyDataSetChanged();
        updateStats();
        updateUI();
        
        Toast.makeText(this, "Showing partners reporting to " + selectedUser.getFullName() + " (" + selectedUser.getDesignationName() + ")", Toast.LENGTH_SHORT).show();
    }
    
    private void resetToAllData() {
        selectedUser = null;
        userDropdown.setText("");
        filteredPartnerUsersList.clear();
        filteredPartnerUsersList.addAll(allPartnerUsersList);
        partnerUserAdapter.notifyDataSetChanged();
        updateStats();
        updateUI();
        Toast.makeText(this, "Showing all partner users", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditPartnerUser(PartnerUser partnerUser) {
        // TODO: Implement edit functionality
        Toast.makeText(this, "Edit functionality coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeletePartnerUser(PartnerUser partnerUser) {
        // TODO: Implement delete functionality
        Toast.makeText(this, "Delete functionality coming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewDetails(PartnerUser partnerUser) {
        // TODO: Implement view details functionality
        Toast.makeText(this, "View details functionality coming soon", Toast.LENGTH_SHORT).show();
    }
} 