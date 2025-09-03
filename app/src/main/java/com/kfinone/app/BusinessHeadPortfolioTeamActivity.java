package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class BusinessHeadPortfolioTeamActivity extends AppCompatActivity implements RBHPortfolioAdapter.OnPortfolioActionListener {

    private static final String TAG = "BusinessHeadPortfolioTeam";
    
    private TextView backButton;
    private String userName;
    private String userId;
    private String firstName;
    private String lastName;
    
    // UI Elements
    private RecyclerView portfolioRecyclerView;
    private LinearLayout emptyStateLayout;
    private TextView dataCountText;
    
    // Data
    private List<RBHPortfolio> allPortfolios = new ArrayList<>();
    private List<RBHPortfolio> filteredPortfolios = new ArrayList<>();
    private RBHPortfolioAdapter portfolioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_portfolio_team);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupClickListeners();
        loadSamplePortfolioData();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
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
        backButton.setOnClickListener(v -> goBack());
    }

    private void goBack() {
        // Simply finish this activity to return to the previous one
        // This preserves the user data in the BusinessHeadPortfolioActivity
        finish();
    }
    
    private void loadSamplePortfolioData() {
        // Clear all portfolios - no sample data
        allPortfolios.clear();
        filteredPortfolios.clear();
        
        // Update the display to show empty state
        updateDataDisplay();
        
        Log.d(TAG, "Portfolio data cleared - showing empty state");
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
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }
} 