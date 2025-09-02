package com.kfinone.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
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

public class PortfolioTeamActivity extends AppCompatActivity {

    private RecyclerView teamRecyclerView;
    private View emptyStateLayout;
    private TextView backButton;
    private View addMemberButton;

    private AutoCompleteTextView userDropdown;
    private MaterialButton showDataButton;
    private MaterialButton resetButton;
    private TeamAdapter teamAdapter;
    private List<TeamMember> teamList;
    private List<TeamMember> allTeamList; // Store all team members for filtering
    private List<RBHUser> rbhUsers; // Store RBH users for dropdown
    private List<Portfolio> rbhPortfolios; // Store portfolios created by RBH users
    private OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_team);

        // Initialize HTTP client
        httpClient = new OkHttpClient();
        
        initializeViews();
        setupClickListeners();
        loadRBHUsers();
        loadRBHPortfolios();
        loadTeamData();
    }

    private void initializeViews() {
        teamRecyclerView = findViewById(R.id.teamRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        backButton = findViewById(R.id.backButton);
        addMemberButton = findViewById(R.id.addMemberButton);

        userDropdown = findViewById(R.id.userDropdown);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);

        // Setup RecyclerView
        teamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamList = new ArrayList<>();
        allTeamList = new ArrayList<>();
        rbhUsers = new ArrayList<>();
        rbhPortfolios = new ArrayList<>();
        teamAdapter = new TeamAdapter(teamList);
        teamRecyclerView.setAdapter(teamAdapter);
    }

    private void loadRBHUsers() {
        // Build API URL
        String apiUrl = "https://emp.kfinone.com/mobile/api/get_rbh_users_for_dropdown.php";
        
        // Create request
        Request request = new Request.Builder()
                .url(apiUrl)
                .get()
                .build();
        
        // Make API call
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(PortfolioTeamActivity.this, "Failed to load users: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    parseRBHUsersData(responseBody);
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(PortfolioTeamActivity.this, "Failed to load users. Server error: " + response.code(), Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
    
    private void parseRBHUsersData(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            boolean success = jsonObject.getBoolean("success");
            
            if (success) {
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray usersArray = data.getJSONArray("users");
                
                rbhUsers.clear();
                
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject userJson = usersArray.getJSONObject(i);
                    
                    RBHUser user = new RBHUser(
                        userJson.optString("id", ""),
                        userJson.optString("username", ""),
                        userJson.optString("firstName", ""),
                        userJson.optString("lastName", ""),
                        userJson.optString("designation_id", ""),
                        userJson.optString("designation_name", ""),
                        userJson.optString("full_name", ""),
                        userJson.optString("email_id", ""),
                        userJson.optString("mobile", ""),
                        userJson.optString("status", "")
                    );
                    
                    rbhUsers.add(user);
                }
                
                runOnUiThread(() -> {
                    setupDropdown();
                    Toast.makeText(PortfolioTeamActivity.this, "Loaded " + rbhUsers.size() + " RBH users", Toast.LENGTH_SHORT).show();
                });
                
            } else {
                String message = jsonObject.optString("message", "Unknown error");
                runOnUiThread(() -> {
                    Toast.makeText(PortfolioTeamActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                });
            }
            
        } catch (JSONException e) {
            runOnUiThread(() -> {
                Toast.makeText(PortfolioTeamActivity.this, "Error parsing user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
    }
    
    private void setupDropdown() {
        // Create dropdown options from RBH users
        List<String> userOptions = new ArrayList<>();
        userOptions.add("All Users"); // Add default option
        
        for (RBHUser user : rbhUsers) {
            userOptions.add(user.getFullName() + " (" + user.getUsername() + ")");
        }
        
        ArrayAdapter<String> userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, userOptions);
        userDropdown.setAdapter(userAdapter);
    }
    
    private void loadRBHPortfolios() {
        // Build API URL
        String apiUrl = "https://emp.kfinone.com/mobile/api/get_rbh_portfolios.php";
        
        // Create request
        Request request = new Request.Builder()
                .url(apiUrl)
                .get()
                .build();
        
        // Make API call
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(PortfolioTeamActivity.this, "Failed to load portfolios: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    parseRBHPortfoliosData(responseBody);
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(PortfolioTeamActivity.this, "Failed to load portfolios. Server error: " + response.code(), Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
    
    private void parseRBHPortfoliosData(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            boolean success = jsonObject.getBoolean("success");
            
            if (success) {
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray portfoliosArray = data.getJSONArray("portfolios");
                
                rbhPortfolios.clear();
                
                for (int i = 0; i < portfoliosArray.length(); i++) {
                    JSONObject portfolioJson = portfoliosArray.getJSONObject(i);
                    
                    Portfolio portfolio = new Portfolio(
                        portfolioJson.optString("id", ""),
                        portfolioJson.optString("customer_name", ""),
                        portfolioJson.optString("company_name", ""),
                        portfolioJson.optString("Phone_number", ""),
                        portfolioJson.optString("alternative_Phone_number", ""),
                        portfolioJson.optString("email_id", ""),
                        portfolioJson.optString("state", ""),
                        portfolioJson.optString("location", ""),
                        portfolioJson.optString("sub_location", ""),
                        portfolioJson.optString("pin_code", ""),
                        portfolioJson.optString("customer_type", ""),
                        portfolioJson.optString("industry_type", ""),
                        portfolioJson.optString("business_type", ""),
                        portfolioJson.optString("birth_date", ""),
                        portfolioJson.optString("address", ""),
                        portfolioJson.optString("createdBy", ""),
                        portfolioJson.optString("status", ""),
                        portfolioJson.optString("created_at", ""),
                        portfolioJson.optString("updated_at", "")
                    );
                    
                    rbhPortfolios.add(portfolio);
                }
                
                runOnUiThread(() -> {
                    updateTeamListWithPortfolios();
                    Toast.makeText(PortfolioTeamActivity.this, "Loaded " + rbhPortfolios.size() + " RBH portfolios", Toast.LENGTH_SHORT).show();
                });
                
            } else {
                String message = jsonObject.optString("message", "Unknown error");
                runOnUiThread(() -> {
                    Toast.makeText(PortfolioTeamActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                });
            }
            
        } catch (JSONException e) {
            runOnUiThread(() -> {
                Toast.makeText(PortfolioTeamActivity.this, "Error parsing portfolio data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
    }
    
    private void updateTeamListWithPortfolios() {
        // Convert portfolios to team members for display
        allTeamList.clear();
        
        for (Portfolio portfolio : rbhPortfolios) {
            // Create a team member representation of the portfolio
            TeamMember teamMember = new TeamMember(
                portfolio.getId(),
                portfolio.getCustomerName(),
                portfolio.getCompanyName(),
                portfolio.getStatus(),
                portfolio.getEmailId(),
                portfolio.getCreatedBy() // Use createdBy as userType
            );
            
            allTeamList.add(teamMember);
        }
        
        // Initially show all data
        teamList.clear();
        teamList.addAll(allTeamList);
        teamAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());

        addMemberButton.setOnClickListener(v -> {
            Toast.makeText(this, "Add Team Member - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Add Team Member activity
        });

        showDataButton.setOnClickListener(v -> {
            filterTeamData();
        });

        resetButton.setOnClickListener(v -> {
            resetFilters();
        });
    }

    private void loadTeamData() {
        // Team data is now loaded from RBH portfolios
        // This method is kept for compatibility but data is loaded via loadRBHPortfolios()
        updateEmptyState();
    }

    private void filterTeamData() {
        String selectedUser = userDropdown.getText().toString().trim();
        
        if (TextUtils.isEmpty(selectedUser)) {
            Toast.makeText(this, "Please select a user to filter", Toast.LENGTH_SHORT).show();
            return;
        }

        teamList.clear();
        
        if ("All Users".equals(selectedUser)) {
            // Show all portfolios
            teamList.addAll(allTeamList);
        } else {
            // Extract username from the dropdown selection format "Full Name (username)"
            String username = extractUsernameFromSelection(selectedUser);
            
            // Filter by selected RBH user
            for (TeamMember member : allTeamList) {
                if (username.equals(member.getUserType())) {
                    teamList.add(member);
                }
            }
        }
        
        teamAdapter.notifyDataSetChanged();
        updateEmptyState();
        
        Toast.makeText(this, "Showing portfolios for: " + selectedUser, Toast.LENGTH_SHORT).show();
    }
    
    private String extractUsernameFromSelection(String selection) {
        // Extract username from format "Full Name (username)"
        if (selection.contains("(") && selection.contains(")")) {
            int startIndex = selection.lastIndexOf("(") + 1;
            int endIndex = selection.lastIndexOf(")");
            if (startIndex > 0 && endIndex > startIndex) {
                return selection.substring(startIndex, endIndex);
            }
        }
        return selection; // Return as is if format doesn't match
    }

    private void resetFilters() {
        userDropdown.setText("");
        teamList.clear();
        teamList.addAll(allTeamList);
        teamAdapter.notifyDataSetChanged();
        updateEmptyState();
        
        Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
    }



    private void updateEmptyState() {
        if (teamList.isEmpty()) {
            teamRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            teamRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    // Team Member class with additional user type field
    public static class TeamMember {
        private String id;
        private String name;
        private String role;
        private String status;
        private String email;
        private String userType;

        public TeamMember(String id, String name, String role, String status, String email, String userType) {
            this.id = id;
            this.name = name;
            this.role = role;
            this.status = status;
            this.email = email;
            this.userType = userType;
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public String getRole() { return role; }
        public String getStatus() { return status; }
        public String getEmail() { return email; }
        public String getUserType() { return userType; }
    }
    
    // RBH User class for dropdown
    public static class RBHUser {
        private String id;
        private String username;
        private String firstName;
        private String lastName;
        private String designationId;
        private String designationName;
        private String fullName;
        private String emailId;
        private String mobile;
        private String status;

        public RBHUser(String id, String username, String firstName, String lastName, 
                      String designationId, String designationName, String fullName, 
                      String emailId, String mobile, String status) {
            this.id = id;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.designationId = designationId;
            this.designationName = designationName;
            this.fullName = fullName;
            this.emailId = emailId;
            this.mobile = mobile;
            this.status = status;
        }

        // Getters
        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getDesignationId() { return designationId; }
        public String getDesignationName() { return designationName; }
        public String getFullName() { return fullName; }
        public String getEmailId() { return emailId; }
        public String getMobile() { return mobile; }
        public String getStatus() { return status; }
    }
} 