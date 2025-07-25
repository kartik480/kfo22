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
        portfolioList.clear();
        // Only fetch real data here. No sample data.
        // portfolioAdapter.notifyDataSetChanged();
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