package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MyPortfolioActivity extends AppCompatActivity {

    private RecyclerView portfolioRecyclerView;
    private View emptyStateLayout;
    private TextView backButton;
    private View refreshButton;
    private PortfolioAdapter portfolioAdapter;
    private List<PortfolioItem> portfolioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_portfolio);

        initializeViews();
        setupClickListeners();
        loadPortfolioData();
    }

    private void initializeViews() {
        portfolioRecyclerView = findViewById(R.id.portfolioRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);

        // Setup RecyclerView
        portfolioRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        portfolioList = new ArrayList<>();
        portfolioAdapter = new PortfolioAdapter(portfolioList);
        portfolioRecyclerView.setAdapter(portfolioAdapter);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> onBackPressed());

        refreshButton.setOnClickListener(v -> {
            Toast.makeText(this, "Refreshing portfolios...", Toast.LENGTH_SHORT).show();
            loadPortfolioData();
        });
    }

    private void loadPortfolioData() {
        // TODO: Load portfolio data from server
        // For now, show sample data
        portfolioList.clear();
        
        // Add sample portfolios with all required fields
        portfolioList.add(new PortfolioItem("1", "John Doe", "ABC Corporation", "9876543210", "Maharashtra", "Mumbai", "Admin"));
        portfolioList.add(new PortfolioItem("2", "Jane Smith", "XYZ Industries", "8765432109", "Delhi", "New Delhi", "Manager"));
        portfolioList.add(new PortfolioItem("3", "Mike Johnson", "Tech Solutions Ltd", "7654321098", "Karnataka", "Bangalore", "Admin"));
        portfolioList.add(new PortfolioItem("4", "Sarah Wilson", "Global Enterprises", "6543210987", "Tamil Nadu", "Chennai", "Supervisor"));
        portfolioList.add(new PortfolioItem("5", "David Brown", "Innovation Corp", "5432109876", "Gujarat", "Ahmedabad", "Manager"));
        portfolioList.add(new PortfolioItem("6", "Emily Davis", "Future Tech", "4321098765", "Uttar Pradesh", "Lucknow", "Admin"));
        portfolioList.add(new PortfolioItem("7", "Robert Wilson", "Smart Solutions", "3210987654", "West Bengal", "Kolkata", "Supervisor"));
        portfolioList.add(new PortfolioItem("8", "Lisa Anderson", "Digital Dynamics", "2109876543", "Telangana", "Hyderabad", "Manager"));
        
        portfolioAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (portfolioList.isEmpty()) {
            portfolioRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            portfolioRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    // Portfolio Item class with all required fields
    public static class PortfolioItem {
        private String id;
        private String customerName;
        private String companyName;
        private String mobile;
        private String state;
        private String location;
        private String createdBy;

        public PortfolioItem(String id, String customerName, String companyName, String mobile, 
                           String state, String location, String createdBy) {
            this.id = id;
            this.customerName = customerName;
            this.companyName = companyName;
            this.mobile = mobile;
            this.state = state;
            this.location = location;
            this.createdBy = createdBy;
        }

        // Getters
        public String getId() { return id; }
        public String getCustomerName() { return customerName; }
        public String getCompanyName() { return companyName; }
        public String getMobile() { return mobile; }
        public String getState() { return state; }
        public String getLocation() { return location; }
        public String getCreatedBy() { return createdBy; }
    }

    @Override
    public void onBackPressed() {
        // Check if we came from Director panel
        String sourcePanel = getIntent().getStringExtra("SOURCE_PANEL");
        if ("DIRECTOR_PANEL".equals(sourcePanel)) {
            // Navigate back to Director Portfolio Activity
            Intent intent = new Intent(this, DirectorPortfolioActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else {
            // Default behavior
            super.onBackPressed();
        }
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
} 