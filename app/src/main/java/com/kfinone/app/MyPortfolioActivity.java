package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

public class MyPortfolioActivity extends AppCompatActivity implements PortfolioAdapter.OnPortfolioActionListener {

    private RecyclerView portfolioRecyclerView;
    private View emptyStateLayout;
    private TextView backButton;
    private View refreshButton;
    private PortfolioAdapter portfolioAdapter;
    private List<Portfolio> portfolioList;
    private String userName;
    private String userId;
    private OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_portfolio);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        
        if (userName == null || userName.isEmpty()) {
            userName = "Unknown User";
        }

        // Initialize HTTP client
        httpClient = new OkHttpClient();

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
        portfolioAdapter = new PortfolioAdapter(portfolioList, this, this);
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
        // Show loading state
        Toast.makeText(this, "Loading portfolios...", Toast.LENGTH_SHORT).show();
        
        // Build API URL
        String apiUrl = "https://emp.kfinone.com/mobile/api/get_cbo_portfolios.php?username=" + userName;
        
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
                    Toast.makeText(MyPortfolioActivity.this, "Failed to load portfolios: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    updateEmptyState();
                });
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    parsePortfolioData(responseBody);
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(MyPortfolioActivity.this, "Failed to load portfolios. Server error: " + response.code(), Toast.LENGTH_LONG).show();
                        updateEmptyState();
                    });
                }
            }
        });
    }
    
    private void parsePortfolioData(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            boolean success = jsonObject.getBoolean("success");
            
            if (success) {
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray portfoliosArray = data.getJSONArray("portfolios");
                
                portfolioList.clear();
                
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
                    
                    portfolioList.add(portfolio);
                }
                
                runOnUiThread(() -> {
                    portfolioAdapter.notifyDataSetChanged();
                    updateEmptyState();
                    Toast.makeText(MyPortfolioActivity.this, "Loaded " + portfolioList.size() + " portfolios", Toast.LENGTH_SHORT).show();
                });
                
            } else {
                String message = jsonObject.optString("message", "Unknown error");
                runOnUiThread(() -> {
                    Toast.makeText(MyPortfolioActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                    updateEmptyState();
                });
            }
            
        } catch (JSONException e) {
            runOnUiThread(() -> {
                Toast.makeText(MyPortfolioActivity.this, "Error parsing portfolio data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                updateEmptyState();
            });
        }
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

    // Portfolio Action Listener Implementation
    @Override
    public void onViewPortfolio(Portfolio portfolio) {
        Toast.makeText(this, "Viewing portfolio: " + portfolio.getCustomerName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to portfolio details view
    }

    @Override
    public void onEditPortfolio(Portfolio portfolio) {
        Toast.makeText(this, "Editing portfolio: " + portfolio.getCustomerName(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to portfolio edit form
    }

    @Override
    public void onDeletePortfolio(Portfolio portfolio) {
        Toast.makeText(this, "Deleting portfolio: " + portfolio.getCustomerName(), Toast.LENGTH_SHORT).show();
        // TODO: Implement delete functionality
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