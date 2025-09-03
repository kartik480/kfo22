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

public class RBHMyPortfolioActivity extends AppCompatActivity implements RBHPortfolioAdapter.OnPortfolioActionListener {
    
    private static final String TAG = "RBHMyPortfolio";
    
    private String userId;
    private String username;
    private String firstName;
    private String lastName;
    
    // UI Elements
    private RecyclerView portfolioRecyclerView;
    private LinearLayout emptyStateLayout;
    private RBHPortfolioAdapter portfolioAdapter;
    private List<RBHPortfolio> portfolioList;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rbh_my_portfolio);
        
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
        loadPortfolioData();
    }

    private void initializeViews() {
        portfolioRecyclerView = findViewById(R.id.portfolioRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        
        // Setup RecyclerView
        portfolioList = new ArrayList<>();
        portfolioAdapter = new RBHPortfolioAdapter(portfolioList);
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
    }
    
    private void loadPortfolioData() {
        // For now, show empty state - will be implemented with actual API later
        Log.d(TAG, "Loading portfolio data for user: " + username);
        updateDataDisplay();
    }
    
    private void updateDataDisplay() {
        portfolioAdapter.notifyDataSetChanged();
        
        if (portfolioList.isEmpty()) {
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
}
