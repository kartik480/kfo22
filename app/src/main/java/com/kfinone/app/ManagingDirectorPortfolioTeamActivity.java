package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManagingDirectorPortfolioTeamActivity extends AppCompatActivity {

    private TextView backButton;
    private AutoCompleteTextView userDropdown;
    private MaterialButton showDataButton;
    private MaterialButton resetButton;
    private RecyclerView portfolioRecyclerView;
    private LinearLayout emptyStateLayout;
    private TextView dataCountText;
    private String userName;
    private String userId;
    private OkHttpClient httpClient;
    private List<MDPortfolio> allPortfolios;
    private List<MDPortfolio> filteredPortfolios;
    private MDPortfolioAdapter portfolioAdapter;
    private List<TeamMember> allTeamMembers;
    private List<TeamMember> filteredTeamMembers;
    private List<MDTeamUser> teamUsers;
    private ArrayAdapter<String> userDropdownAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managing_director_portfolio_team);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        
        if (userName == null || userName.isEmpty()) {
            userName = "Managing Director";
        }

        initializeViews();
        setupClickListeners();
        loadTeamUsers();
        loadPortfolioData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        userDropdown = findViewById(R.id.userDropdown);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);
        portfolioRecyclerView = findViewById(R.id.portfolioRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        dataCountText = findViewById(R.id.dataCountText);
        
        // Initialize HTTP client
        httpClient = new OkHttpClient();
        
        // Initialize lists
        allPortfolios = new ArrayList<>();
        filteredPortfolios = new ArrayList<>();
        allTeamMembers = new ArrayList<>();
        filteredTeamMembers = new ArrayList<>();
        teamUsers = new ArrayList<>();
        
        // Setup RecyclerView
        portfolioAdapter = new MDPortfolioAdapter(filteredPortfolios);
        portfolioRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        portfolioRecyclerView.setAdapter(portfolioAdapter);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());

        showDataButton.setOnClickListener(v -> {
            filterData();
        });

        resetButton.setOnClickListener(v -> {
            resetFilters();
        });
    }

    private void setupDropdown() {
        // Create dropdown options
        List<String> dropdownOptions = new ArrayList<>();
        dropdownOptions.add("All Users");
        
        // Add individual users
        for (MDTeamUser user : teamUsers) {
            dropdownOptions.add(user.getDisplayName());
        }
        
        // Setup adapter
        userDropdownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dropdownOptions);
        userDropdown.setAdapter(userDropdownAdapter);
    }

    private void filterData() {
        String selectedUser = userDropdown.getText().toString().trim();
        
        if (TextUtils.isEmpty(selectedUser)) {
            Toast.makeText(this, "Please select a user to filter", Toast.LENGTH_SHORT).show();
            return;
        }

        // Filter portfolios based on selected user
        filteredPortfolios.clear();
        
        if ("All Users".equals(selectedUser)) {
            filteredPortfolios.addAll(allPortfolios);
        } else {
            // Find the selected user
            MDTeamUser selectedTeamUser = findUserByDisplayName(selectedUser);
            if (selectedTeamUser != null) {
                for (MDPortfolio portfolio : allPortfolios) {
                    // Check if the portfolio was created by the selected user
                    if (portfolio.getCreatedBy() != null && portfolio.getCreatedBy().equals(selectedTeamUser.getUsername())) {
                        filteredPortfolios.add(portfolio);
                    }
                }
            }
        }
        
        // Update UI
        updateDataDisplay();
        Toast.makeText(this, "Showing " + filteredPortfolios.size() + " portfolios for: " + selectedUser, Toast.LENGTH_SHORT).show();
    }
    
    private MDTeamUser findUserByDisplayName(String displayName) {
        for (MDTeamUser user : teamUsers) {
            if (user.getDisplayName().equals(displayName)) {
                return user;
            }
        }
        return null;
    }

    private void resetFilters() {
        userDropdown.setText("");
        filteredPortfolios.clear();
        filteredPortfolios.addAll(allPortfolios);
        updateDataDisplay();
        Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
    }
    
    private void loadTeamUsers() {
        String apiUrl = "https://emp.kfinone.com/mobile/api/get_md_portfolio_team_users.php";
        
        Request request = new Request.Builder()
                .url(apiUrl)
                .build();
        
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(ManagingDirectorPortfolioTeamActivity.this, 
                            "Failed to load team users: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> parseTeamUsersData(responseBody));
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ManagingDirectorPortfolioTeamActivity.this, 
                                "Failed to load team users", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
    
    private void parseTeamUsersData(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            boolean success = jsonResponse.getBoolean("success");
            
            if (success) {
                JSONObject data = jsonResponse.getJSONObject("data");
                JSONArray usersArray = data.getJSONArray("users");
                
                teamUsers.clear();
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject userJson = usersArray.getJSONObject(i);
                    
                    MDTeamUser user = new MDTeamUser(
                            userJson.getString("id"),
                            userJson.getString("username"),
                            userJson.getString("firstName"),
                            userJson.getString("lastName"),
                            userJson.getString("designation_id"),
                            userJson.getString("designation_name"),
                            userJson.getString("full_name"),
                            userJson.getString("email_id"),
                            userJson.getString("mobile"),
                            userJson.getString("status")
                    );
                    
                    teamUsers.add(user);
                }
                
                // Setup dropdown with loaded users
                setupDropdown();
                
                Toast.makeText(this, "Loaded " + teamUsers.size() + " team users", Toast.LENGTH_SHORT).show();
            } else {
                String message = jsonResponse.getString("message");
                Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Error parsing team users data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadPortfolioData() {
        String apiUrl = "https://emp.kfinone.com/mobile/api/get_md_portfolio_team_data.php";
        if (userName != null && !userName.isEmpty()) {
            apiUrl += "?username=" + userName;
        }
        
        Request request = new Request.Builder()
                .url(apiUrl)
                .build();
        
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(ManagingDirectorPortfolioTeamActivity.this, 
                            "Failed to load portfolio data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> parsePortfolioData(responseBody));
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(ManagingDirectorPortfolioTeamActivity.this, 
                                "Failed to load portfolio data", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
    
    private void parsePortfolioData(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            boolean success = jsonResponse.getBoolean("success");
            
            if (success) {
                JSONObject data = jsonResponse.getJSONObject("data");
                JSONArray portfoliosArray = data.getJSONArray("portfolios");
                
                allPortfolios.clear();
                for (int i = 0; i < portfoliosArray.length(); i++) {
                    JSONObject portfolioJson = portfoliosArray.getJSONObject(i);
                    
                    MDPortfolio portfolio = new MDPortfolio(
                            portfolioJson.getString("id"),
                            portfolioJson.getString("customer_name"),
                            portfolioJson.getString("company_name"),
                            portfolioJson.getString("Phone_number"),
                            portfolioJson.getString("alternative_Phone_number"),
                            portfolioJson.getString("email_id"),
                            portfolioJson.getString("state"),
                            portfolioJson.getString("location"),
                            portfolioJson.getString("sub_location"),
                            portfolioJson.getString("pin_code"),
                            portfolioJson.getString("customer_type"),
                            portfolioJson.getString("industry_type"),
                            portfolioJson.getString("business_type"),
                            portfolioJson.getString("birth_date"),
                            portfolioJson.getString("address"),
                            portfolioJson.getString("createdBy"),
                            portfolioJson.getString("status"),
                            portfolioJson.getString("created_at"),
                            portfolioJson.getString("updated_at"),
                            portfolioJson.getString("creator_username"),
                            portfolioJson.getString("creator_first_name"),
                            portfolioJson.getString("creator_last_name"),
                            portfolioJson.getString("creator_designation_id"),
                            portfolioJson.getString("creator_designation_name"),
                            portfolioJson.getString("creator_full_name")
                    );
                    
                    allPortfolios.add(portfolio);
                }
                
                // Initialize filtered list with all portfolios
                filteredPortfolios.clear();
                filteredPortfolios.addAll(allPortfolios);
                
                // Update UI
                updateDataDisplay();
                
                Toast.makeText(this, "Loaded " + allPortfolios.size() + " portfolios", Toast.LENGTH_SHORT).show();
            } else {
                String message = jsonResponse.getString("message");
                Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Error parsing portfolio data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void updateDataDisplay() {
        if (filteredPortfolios.isEmpty()) {
            portfolioRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
            dataCountText.setText("0 items");
        } else {
            portfolioRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
            dataCountText.setText(filteredPortfolios.size() + " items");
            portfolioAdapter.notifyDataSetChanged();
        }
    }

    private void goBack() {
        // Navigate back to Portfolio Panel
        Intent intent = new Intent(this, PortfolioPanelActivity.class);
        passUserDataToIntent(intent);
        intent.putExtra("SOURCE_PANEL", "SPECIAL_PANEL");
        startActivity(intent);
        finish();
    }

    private void passUserDataToIntent(Intent intent) {
        // Get current user data and pass it to the new activity
        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            String userId = currentIntent.getStringExtra("USER_ID");
            String firstName = currentIntent.getStringExtra("FIRST_NAME");
            String lastName = currentIntent.getStringExtra("LAST_NAME");
            String fullName = currentIntent.getStringExtra("USERNAME");
            
            if (userId != null) intent.putExtra("USER_ID", userId);
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            if (fullName != null) intent.putExtra("USERNAME", fullName);
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
