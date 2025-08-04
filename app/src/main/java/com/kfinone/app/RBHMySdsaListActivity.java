package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class RBHMySdsaListActivity extends AppCompatActivity {
    private TextView welcomeText, totalSdsaCount, activeSdsaCount;
    private View notificationIcon, profileIcon, menuButton, backButton;
    private String userName;
    private String userId;
    private BottomNavigationView rbhBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar and make fullscreen
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_rbh_my_sdsa_list);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        if (userName == null || userName.isEmpty()) {
            userName = "RBH"; // Default fallback
        }
        if (userId == null || userId.isEmpty()) {
            userId = "1"; // Default fallback
        }
        
        initializeViews();
        setupClickListeners();
        updateWelcomeMessage();
        loadSdsaData();
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        totalSdsaCount = findViewById(R.id.totalSdsaCount);
        activeSdsaCount = findViewById(R.id.activeSdsaCount);
        
        notificationIcon = findViewById(R.id.notificationIcon);
        profileIcon = findViewById(R.id.profileIcon);
        menuButton = findViewById(R.id.menuButton);
        backButton = findViewById(R.id.backButton);
        rbhBottomNav = findViewById(R.id.rbhBottomNav);
    }

    private void setupClickListeners() {
        // Back button click listener
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        // Menu button click listener
        menuButton.setOnClickListener(v -> {
            showMenuOptions();
        });

        notificationIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
            // TODO: Open notifications panel
        });

        profileIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Profile Settings", Toast.LENGTH_SHORT).show();
            // TODO: Open profile settings
        });

        rbhBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, RegionalBusinessHeadPanelActivity.class).putExtra("USERNAME", userName));
                return true;
            } else if (itemId == R.id.nav_team) {
                startActivity(new Intent(this, RBHTeamActivity.class).putExtra("USERNAME", userName));
                return true;
            } else if (itemId == R.id.nav_portfolio) {
                startActivity(new Intent(this, RBHPortfolioActivity.class).putExtra("USERNAME", userName));
                return true;
            } else if (itemId == R.id.nav_reports) {
                startActivity(new Intent(this, RBHReportsActivity.class).putExtra("USERNAME", userName));
                return true;
            }
            return false;
        });
    }

    private void updateWelcomeMessage() {
        welcomeText.setText("Welcome back, " + userName);
    }

    private void loadSdsaData() {
        // TODO: Implement API call to fetch SDSA data
        // For now, set placeholder values
        totalSdsaCount.setText("0");
        activeSdsaCount.setText("0");
        
        Toast.makeText(this, "My SDSA functionality coming soon!", Toast.LENGTH_LONG).show();
    }

    private void showMenuOptions() {
        String[] options = {"About", "Help", "Logout"};
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
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
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("About RBH My SDSA List");
        builder.setMessage("RBH My SDSA List v1.0\n\n" +
                "This feature allows Regional Business Heads to view and manage their SDSA team members, track performance, and access detailed analytics.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void showLogoutConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, RBHMySdsaPanelActivity.class);
        intent.putExtra("USERNAME", userName);
        startActivity(intent);
        finish();
    }
} 