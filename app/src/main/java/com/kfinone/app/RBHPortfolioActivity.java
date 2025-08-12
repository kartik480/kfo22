package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RBHPortfolioActivity extends AppCompatActivity implements PortfolioAdapter.OnPortfolioActionListener {
    
    private TextView welcomeText;
    private View backButton, menuButton, notificationIcon, profileIcon;
    private String userName;
    private String userId;
    private BottomNavigationView rbhBottomNav;
    
    // Portfolio list components
    private RecyclerView portfolioRecyclerView;
    private PortfolioAdapter portfolioAdapter;
    private List<Portfolio> portfolioList;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar and make fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_rbh_portfolio);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        if (userName == null || userName.isEmpty()) {
            userName = "RBH"; // Default fallback
        }
        
        initializeViews();
        setupClickListeners();
        updateWelcomeMessage();
        setupRecyclerView();
        loadPortfolioData();
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        backButton = findViewById(R.id.backButton);
        menuButton = findViewById(R.id.menuButton);
        notificationIcon = findViewById(R.id.notificationIcon);
        profileIcon = findViewById(R.id.profileIcon);
        
        portfolioRecyclerView = findViewById(R.id.portfolioRecyclerView);
        rbhBottomNav = findViewById(R.id.rbhBottomNav);
        
        executorService = Executors.newSingleThreadExecutor();
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Menu button
        menuButton.setOnClickListener(v -> showMenuOptions());

        // Notification icon
        notificationIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
            // TODO: Open notifications panel
        });

        // Profile icon
        profileIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Profile Settings", Toast.LENGTH_SHORT).show();
            // TODO: Open profile settings
        });

        // Bottom Navigation
        rbhBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_team) {
                Intent intent = new Intent(this, RBHTeamActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_portfolio) {
                // Already in portfolio, do nothing
                return true;
            } else if (itemId == R.id.nav_reports) {
                Intent intent = new Intent(this, RBHReportsActivity.class);
                intent.putExtra("USERNAME", userName);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }

    private void updateWelcomeMessage() {
        welcomeText.setText("Welcome back, " + userName);
    }

    private void setupRecyclerView() {
        portfolioList = new ArrayList<>();
        portfolioAdapter = new PortfolioAdapter(portfolioList, this, this);
        portfolioRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        portfolioRecyclerView.setAdapter(portfolioAdapter);
    }

    private void loadPortfolioData() {
        // For now, load sample data
        // TODO: Replace with actual API call to fetch portfolio data
        loadSamplePortfolioData();
    }

    private void loadSamplePortfolioData() {
        portfolioList.clear();
        
        // Add sample portfolio items
        portfolioList.add(new Portfolio(
            "1", "John Doe", "ABC Corporation", "+91 98765 43210",
            "Maharashtra", "Mumbai", "RBH User", "Active",
            "john.doe@abc.com", "123 Business Street, Mumbai", "2024-01-15", "2024-01-15"
        ));
        
        portfolioList.add(new Portfolio(
            "2", "Jane Smith", "XYZ Industries", "+91 87654 32109",
            "Karnataka", "Bangalore", "RBH User", "Active",
            "jane.smith@xyz.com", "456 Tech Park, Bangalore", "2024-01-10", "2024-01-10"
        ));
        
        portfolioList.add(new Portfolio(
            "3", "Mike Johnson", "DEF Solutions", "+91 76543 21098",
            "Telangana", "Hyderabad", "RBH User", "Inactive",
            "mike.johnson@def.com", "789 Innovation Hub, Hyderabad", "2024-01-05", "2024-01-05"
        ));
        
        portfolioAdapter.notifyDataSetChanged();
        
        Toast.makeText(this, "Loaded " + portfolioList.size() + " portfolio items", Toast.LENGTH_SHORT).show();
    }

    private void showMenuOptions() {
        String[] options = {"About", "Help", "Logout"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Menu Options");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    showAboutDialog();
                    break;
                case 1:
                    Toast.makeText(this, "Help - Coming Soon", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    showLogoutConfirmation();
                    break;
            }
        });
        builder.show();
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About RBH Portfolio");
        builder.setMessage("RBH Portfolio Management v1.0\n\n" +
                "This panel allows Regional Business Heads to view and manage their portfolio customers, " +
                "track performance, and access detailed analytics.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void showLogoutConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Intent intent = new Intent(this, EnhancedLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton("No", null);
        builder.show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Portfolio");
        builder.setMessage("Are you sure you want to delete the portfolio for " + portfolio.getCustomerName() + "?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            // Remove from list
            portfolioList.remove(portfolio);
            portfolioAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Portfolio deleted successfully", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
} 