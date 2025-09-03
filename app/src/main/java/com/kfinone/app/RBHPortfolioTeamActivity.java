package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class RBHPortfolioTeamActivity extends AppCompatActivity implements RBHPortfolioAdapter.OnPortfolioActionListener {
    
    private static final String TAG = "RBHPortfolioTeam";
    
    private String userId;
    private String username;
    private String firstName;
    private String lastName;
    
    // UI Elements
    private AutoCompleteTextView sdsaUserDropdown;
    private Button showDataButton;
    private Button resetButton;
    private RecyclerView portfolioRecyclerView;
    private LinearLayout emptyStateLayout;
    private TextView dataCountText;
    
    // Data
    private List<SDSAUser> sdsaUsers = new ArrayList<>();
    private List<RBHPortfolio> allPortfolios = new ArrayList<>();
    private List<RBHPortfolio> filteredPortfolios = new ArrayList<>();
    private RBHPortfolioAdapter portfolioAdapter;
    private ArrayAdapter<String> sdsaDropdownAdapter;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rbh_portfolio_team);
        
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
        loadSDSAUsers();
    }

    private void initializeViews() {
        sdsaUserDropdown = findViewById(R.id.sdsaUserDropdown);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);
        portfolioRecyclerView = findViewById(R.id.portfolioRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        dataCountText = findViewById(R.id.dataCountText);
        
        // Setup RecyclerView
        portfolioAdapter = new RBHPortfolioAdapter(filteredPortfolios);
        portfolioAdapter.setActionListener(this);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        portfolioRecyclerView.setLayoutManager(layoutManager);
        portfolioRecyclerView.setAdapter(portfolioAdapter);
        portfolioRecyclerView.setHasFixedSize(true);
        portfolioRecyclerView.setNestedScrollingEnabled(true);
    }

    private void setupClickListeners() {
        // Back button click listener
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
        
        // Show Data button
        showDataButton.setOnClickListener(v -> filterData());
        
        // Reset button
        resetButton.setOnClickListener(v -> resetFilters());
    }
    
    private void loadSDSAUsers() {
        // For now, show empty dropdown - will be implemented with actual API later
        Log.d(TAG, "Loading SDSA users for RBH: " + username);
        setupSDSADropdown();
    }
    
    private void setupSDSADropdown() {
        List<String> dropdownItems = new ArrayList<>();
        dropdownItems.add("All SDSA Users");
        
        // Add sample SDSA users for now
        dropdownItems.add("SDSA User 1");
        dropdownItems.add("SDSA User 2");
        dropdownItems.add("SDSA User 3");
        
        sdsaDropdownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, dropdownItems);
        sdsaUserDropdown.setAdapter(sdsaDropdownAdapter);
        
        // Enable dropdown functionality
        sdsaUserDropdown.setThreshold(0);
        sdsaUserDropdown.setOnClickListener(v -> {
            Log.d(TAG, "SDSA dropdown clicked, showing dropdown");
            sdsaUserDropdown.showDropDown();
        });
        
        // Handle item selection
        sdsaUserDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            Log.d(TAG, "SDSA user selected: " + selectedItem);
            sdsaUserDropdown.setText(selectedItem);
            sdsaUserDropdown.dismissDropDown();
        });
    }
    
    private void filterData() {
        String selectedSDSA = sdsaUserDropdown.getText().toString().trim();
        
        if (selectedSDSA.isEmpty() || selectedSDSA.equals("All SDSA Users")) {
            filteredPortfolios.clear();
            filteredPortfolios.addAll(allPortfolios);
        } else {
            // Filter portfolios by selected SDSA user
            filteredPortfolios.clear();
            for (RBHPortfolio portfolio : allPortfolios) {
                if (portfolio.getCreatedBy().equals(selectedSDSA)) {
                    filteredPortfolios.add(portfolio);
                }
            }
        }
        
        updateDataDisplay();
    }
    
    private void resetFilters() {
        sdsaUserDropdown.setText("");
        filteredPortfolios.clear();
        filteredPortfolios.addAll(allPortfolios);
        updateDataDisplay();
    }
    
    private void updateDataDisplay() {
        portfolioAdapter.notifyDataSetChanged();
        dataCountText.setText("Total Portfolios: " + filteredPortfolios.size());
        
        if (filteredPortfolios.isEmpty()) {
            portfolioRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            portfolioRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onViewPortfolio(RBHPortfolio portfolio) {
        Log.d(TAG, "View portfolio clicked for: " + portfolio.getCustomerName());
        Toast.makeText(this, "View portfolio: " + portfolio.getCustomerName(), Toast.LENGTH_SHORT).show();
        // TODO: Implement view portfolio functionality
    }
    
    @Override
    public void onEditPortfolio(RBHPortfolio portfolio) {
        Log.d(TAG, "Edit portfolio clicked for: " + portfolio.getCustomerName());
        Toast.makeText(this, "Edit portfolio: " + portfolio.getCustomerName(), Toast.LENGTH_SHORT).show();
        // TODO: Implement edit portfolio functionality
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
    
    // Inner class for SDSA User data
    public static class SDSAUser {
        private String id;
        private String username;
        private String firstName;
        private String lastName;
        private String emailId;
        private String mobile;
        private String status;

        public SDSAUser(String id, String username, String firstName, String lastName,
                       String emailId, String mobile, String status) {
            this.id = id;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.emailId = emailId;
            this.mobile = mobile;
            this.status = status;
        }

        public String getDisplayName() {
            return firstName + " " + lastName + " (" + username + ")";
        }

        // Getters
        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmailId() { return emailId; }
        public String getMobile() { return mobile; }
        public String getStatus() { return status; }
    }
}
