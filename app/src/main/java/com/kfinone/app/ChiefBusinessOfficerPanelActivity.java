package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.cardview.widget.CardView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChiefBusinessOfficerPanelActivity extends AppCompatActivity {
    private RecyclerView teamRecyclerView;
    private TeamMemberAdapter teamAdapter;
    private List<TeamMember> teamMembers;
    private TextView totalTeamCount, activeProjectsCount, welcomeText, viewAllButton;
    private CardView teamCard, portfolioCard, reportsCard, settingsCard;
    private Button logoutButton;
    private View notificationIcon, profileIcon, menuButton;
    private String userName;
    private BottomNavigationView cboBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chief_business_officer_panel);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        if (userName == null || userName.isEmpty()) {
            userName = "CBO"; // Default fallback
        }
        
        initializeViews();
        setupRecyclerView();
        loadSampleData();
        setupClickListeners();
        updateStats();
        updateWelcomeMessage();
    }

    private void initializeViews() {
        teamRecyclerView = findViewById(R.id.teamRecyclerView);
        totalTeamCount = findViewById(R.id.totalTeamCount);
        activeProjectsCount = findViewById(R.id.activeProjectsCount);
        welcomeText = findViewById(R.id.welcomeText);
        viewAllButton = findViewById(R.id.viewAllButton);
        
        teamCard = findViewById(R.id.teamCard);
        portfolioCard = findViewById(R.id.portfolioCard);
        reportsCard = findViewById(R.id.reportsCard);
        settingsCard = findViewById(R.id.settingsCard);
        
        logoutButton = findViewById(R.id.logoutButton);
        notificationIcon = findViewById(R.id.notificationIcon);
        profileIcon = findViewById(R.id.profileIcon);
        menuButton = findViewById(R.id.menuButton);
        cboBottomNav = findViewById(R.id.cboBottomNav);
    }

    private void setupRecyclerView() {
        teamMembers = new ArrayList<>();
        teamAdapter = new TeamMemberAdapter(teamMembers, this);
        teamRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamRecyclerView.setAdapter(teamAdapter);
    }

    private void setupClickListeners() {
        // Menu button click listener
        menuButton.setOnClickListener(v -> {
            showMenuOptions();
        });

        // Card click listeners
        teamCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOTeamActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });

        portfolioCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOPortfolioActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });

        reportsCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOReportsActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });

        settingsCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOSettingsActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });

        // Button click listeners
        logoutButton.setOnClickListener(v -> {
            // Show confirmation dialog
            showLogoutConfirmation();
        });

        viewAllButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOTeamActivity.class);
            intent.putExtra("USERNAME", userName);
            startActivity(intent);
        });

        notificationIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
            // TODO: Open notifications panel
        });

        profileIcon.setOnClickListener(v -> {
            Toast.makeText(this, "Profile Settings", Toast.LENGTH_SHORT).show();
            // TODO: Open profile settings
        });

        cboBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                // Already on dashboard
                return true;
            } else if (itemId == R.id.nav_team) {
                startActivity(new Intent(this, CBOTeamActivity.class).putExtra("USERNAME", userName));
                return true;
            } else if (itemId == R.id.nav_portfolio) {
                startActivity(new Intent(this, CBOPortfolioActivity.class).putExtra("USERNAME", userName));
                return true;
            } else if (itemId == R.id.nav_reports) {
                startActivity(new Intent(this, CBOReportsActivity.class).putExtra("USERNAME", userName));
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, CBOSettingsActivity.class).putExtra("USERNAME", userName));
                return true;
            }
            return false;
        });
    }

    private void updateWelcomeMessage() {
        // Update welcome message with username
        String welcomeMessage = "Welcome back, " + userName;
        welcomeText.setText(welcomeMessage);
    }

    private void showMenuOptions() {
        String[] menuOptions = {
            "Dashboard",
            "Team Management", 
            "Portfolio Management",
            "Reports & Analytics",
            "Settings",
            "Profile",
            "Notifications",
            "Help & Support",
            "About",
            "Logout"
        };

        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Menu")
            .setItems(menuOptions, (dialog, which) -> {
                switch (which) {
                    case 0: // Dashboard
                        // Already on dashboard
                        break;
                    case 1: // Team Management
                        Intent teamIntent = new Intent(this, CBOTeamActivity.class);
                        teamIntent.putExtra("USERNAME", userName);
                        startActivity(teamIntent);
                        break;
                    case 2: // Portfolio Management
                        Intent portfolioIntent = new Intent(this, CBOPortfolioActivity.class);
                        portfolioIntent.putExtra("USERNAME", userName);
                        startActivity(portfolioIntent);
                        break;
                    case 3: // Reports & Analytics
                        Intent reportsIntent = new Intent(this, CBOReportsActivity.class);
                        reportsIntent.putExtra("USERNAME", userName);
                        startActivity(reportsIntent);
                        break;
                    case 4: // Settings
                        Intent settingsIntent = new Intent(this, CBOSettingsActivity.class);
                        settingsIntent.putExtra("USERNAME", userName);
                        startActivity(settingsIntent);
                        break;
                    case 5: // Profile
                        Toast.makeText(this, "Profile Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case 6: // Notifications
                        Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
                        break;
                    case 7: // Help & Support
                        Toast.makeText(this, "Help & Support", Toast.LENGTH_SHORT).show();
                        break;
                    case 8: // About
                        showAboutDialog();
                        break;
                    case 9: // Logout
                        showLogoutConfirmation();
                        break;
                }
            })
            .show();
    }

    private void showAboutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("About KfinOne CBO Panel")
            .setMessage("Chief Business Officer Dashboard\n\n" +
                       "Version: 1.0\n" +
                       "Designed for executive management\n" +
                       "© 2024 KfinOne Technologies")
            .setPositiveButton("OK", null)
            .show();
    }

    private void loadSampleData() {
        teamMembers.clear();
        
        // Add sample team members
        teamMembers.add(new TeamMember(
            "1", "John", "Smith", "Senior Manager",
            "john.smith@kfinone.com", "9876543210", "EMP001", "CBO"
        ));
        
        teamMembers.add(new TeamMember(
            "2", "Sarah", "Johnson", "Team Lead",
            "sarah.johnson@kfinone.com", "9876543211", "EMP002", "CBO"
        ));
        
        teamMembers.add(new TeamMember(
            "3", "Michael", "Davis", "Project Manager",
            "michael.davis@kfinone.com", "9876543212", "EMP003", "CBO"
        ));
        
        teamMembers.add(new TeamMember(
            "4", "Emily", "Wilson", "Business Analyst",
            "emily.wilson@kfinone.com", "9876543213", "EMP004", "CBO"
        ));
        
        teamMembers.add(new TeamMember(
            "5", "David", "Brown", "Senior Developer",
            "david.brown@kfinone.com", "9876543214", "EMP005", "CBO"
        ));

        teamAdapter.notifyDataSetChanged();
    }

    private void updateStats() {
        // Update team count
        totalTeamCount.setText(String.valueOf(teamMembers.size()));
        
        // Update active projects count (sample data)
        activeProjectsCount.setText("23");
    }

    private void showLogoutConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes", (dialog, which) -> {
                // Perform logout
                Intent intent = new Intent(this, EnhancedLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("No", null)
            .show();
    }

    @Override
    public void onBackPressed() {
        // Show exit confirmation
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit the application?")
            .setPositiveButton("Yes", (dialog, which) -> {
                finish();
            })
            .setNegativeButton("No", null)
            .show();
    }
} 