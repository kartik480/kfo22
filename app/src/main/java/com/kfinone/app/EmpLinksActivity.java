package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EmpLinksActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // Emp Links boxes - KfinOne Panel
    private LinearLayout empManageIconsBox;
    private LinearLayout empDataIconsBox;
    private LinearLayout empWorkIconsBox;

    // Count displays - KfinOne Panel
    private TextView empManageIconsCount;
    private TextView empDataIconsCount;
    private TextView empWorkIconsCount;

    // Emp Links boxes - Managing Director Panel
    private LinearLayout myEmpLinksBox;
    private LinearLayout teamEmpLinksBox;

    // Count displays - Managing Director Panel
    private TextView myEmpLinksCount;
    private TextView teamEmpLinksCount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_links);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);

        // Bottom navigation
        dashboardButton = findViewById(R.id.dashboardButton);
        empLinksButton = findViewById(R.id.empLinksButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);

        // Emp Links boxes - KfinOne Panel
        empManageIconsBox = findViewById(R.id.empManageIconsBox);
        empDataIconsBox = findViewById(R.id.empDataIconsBox);
        empWorkIconsBox = findViewById(R.id.empWorkIconsBox);

        // Count displays - KfinOne Panel
        empManageIconsCount = findViewById(R.id.empManageIconsCount);
        empDataIconsCount = findViewById(R.id.empDataIconsCount);
        empWorkIconsCount = findViewById(R.id.empWorkIconsCount);

        // Emp Links boxes - Managing Director Panel
        myEmpLinksBox = findViewById(R.id.myEmpLinksBox);
        teamEmpLinksBox = findViewById(R.id.teamEmpLinksBox);

        // Count displays - Managing Director Panel
        myEmpLinksCount = findViewById(R.id.myEmpLinksCount);
        teamEmpLinksCount = findViewById(R.id.teamEmpLinksCount);


    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewEmpLink());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            // Check which panel we came from and go back accordingly
            String sourcePanel = getIntent().getStringExtra("SOURCE_PANEL");
            if ("SPECIAL_PANEL".equals(sourcePanel)) {
                Intent intent = new Intent(this, SpecialPanelActivity.class);
                passUserDataToIntent(intent);
                startActivity(intent);
                finish();
            } else {
                // Default to HomeActivity (KfinOne panel)
                Intent intent = new Intent(this, HomeActivity.class);
                passUserDataToIntent(intent);
                startActivity(intent);
                finish();
            }
        });

        empLinksButton.setOnClickListener(v -> {
            // Already on emp links page, just show feedback
            Toast.makeText(this, "Employee Links", Toast.LENGTH_SHORT).show();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Reports activity
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Settings activity
        });

        // Check which panel we came from to show appropriate interface
        String sourcePanel = getIntent().getStringExtra("SOURCE_PANEL");
        boolean isManagingDirectorPanel = "SPECIAL_PANEL".equals(sourcePanel);

        if (isManagingDirectorPanel) {
            // Managing Director Panel - Show My Emp Links and Team Emp Links
            showManagingDirectorInterface();
        } else {
            // KfinOne Panel - Show original Emp Manage, Data, Work Icons
            showKfinOneInterface();
        }
    }

    private void goBack() {
        // Check which panel we came from and go back accordingly
        String sourcePanel = getIntent().getStringExtra("SOURCE_PANEL");
        if ("SPECIAL_PANEL".equals(sourcePanel)) {
            Intent intent = new Intent(this, SpecialPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else {
            // Default to HomeActivity (KfinOne panel)
            Intent intent = new Intent(this, HomeActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        }
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing employee links data...", Toast.LENGTH_SHORT).show();
        // Check which panel we're in and refresh accordingly
        String sourcePanel = getIntent().getStringExtra("SOURCE_PANEL");
        boolean isManagingDirectorPanel = "SPECIAL_PANEL".equals(sourcePanel);
        
        if (isManagingDirectorPanel) {
            loadManagingDirectorEmpLinksData();
        } else {
            loadKfinOneEmpLinksData();
        }
    }

    private void addNewEmpLink() {
        Toast.makeText(this, "Add New Emp Link - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add Emp Link activity
    }

    private void showKfinOneInterface() {
        // Show KfinOne Panel interface (original 3 boxes)
        empManageIconsBox.setVisibility(View.VISIBLE);
        empDataIconsBox.setVisibility(View.VISIBLE);
        empWorkIconsBox.setVisibility(View.VISIBLE);
        
        // Hide Managing Director Panel interface
        myEmpLinksBox.setVisibility(View.GONE);
        teamEmpLinksBox.setVisibility(View.GONE);
        
        // Set up click listeners for KfinOne Panel
        empManageIconsBox.setOnClickListener(v -> {
            // Show options dialog for manage icons
            showManageIconsOptions();
        });

        empDataIconsBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmpDataActivity.class);
            // Pass current user data
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        empWorkIconsBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmpWorkActivity.class);
            // Pass current user data
            passUserDataToIntent(intent);
            startActivity(intent);
        });
        
        loadKfinOneEmpLinksData();
    }

    private void showManageIconsOptions() {
        // Get current user data
        Intent currentIntent = getIntent();
        String tempUserId = null;
        String tempUserName = null;
        
        if (currentIntent != null) {
            tempUserId = currentIntent.getStringExtra("USER_ID");
            if (tempUserId == null) {
                tempUserId = currentIntent.getStringExtra("userId");
            }
            tempUserName = currentIntent.getStringExtra("USERNAME");
            if (tempUserName == null) {
                tempUserName = currentIntent.getStringExtra("userName");
            }
        }

        final String currentUserId = tempUserId;
        final String currentUserName = tempUserName;

        // Create options dialog
        String[] options = {"Manage My Permissions", "Manage Other Employees", "Add Icons", "Team Links"};
        
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Manage Icons Options");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Manage My Permissions
                    if (currentUserId != null && !currentUserId.isEmpty()) {
                        Intent intent = new Intent(this, ManageIconPermissionActivity.class);
                        intent.putExtra("userId", currentUserId);
                        intent.putExtra("userName", currentUserName != null ? currentUserName : "My Permissions");
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "User ID not found. Please login again.", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 1: // Manage Other Employees
                    Intent empIntent = new Intent(this, EmpManageActivity.class);
                    passUserDataToIntent(empIntent);
                    startActivity(empIntent);
                    break;
                case 2: // Add Icons
                    Intent addIntent = new Intent(this, AddIconsActivity.class);
                    passUserDataToIntent(addIntent);
                    startActivity(addIntent);
                    break;
                case 3: // Team Links
                    Intent teamIntent = new Intent(this, TeamLinksActivity.class);
                    passUserDataToIntent(teamIntent);
                    startActivity(teamIntent);
                    break;
            }
        });
        builder.show();
    }

    private void showManagingDirectorInterface() {
        // Show Managing Director Panel interface (2 boxes)
        myEmpLinksBox.setVisibility(View.VISIBLE);
        teamEmpLinksBox.setVisibility(View.VISIBLE);
        
        // Hide KfinOne Panel interface
        empManageIconsBox.setVisibility(View.GONE);
        empDataIconsBox.setVisibility(View.GONE);
        empWorkIconsBox.setVisibility(View.GONE);
        
        // Set up click listeners for Managing Director Panel
        myEmpLinksBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyEmpLinksActivity.class);
            // Pass current user data
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        teamEmpLinksBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, TeamEmpLinksActivity.class);
            // Pass current user data
            passUserDataToIntent(intent);
            startActivity(intent);
        });
        
        loadManagingDirectorEmpLinksData();
    }

    private void loadKfinOneEmpLinksData() {
        // TODO: Load real employee links data from server
        // For now, set some sample data for KfinOne Panel
        empManageIconsCount.setText("Manage Icons");
        empDataIconsCount.setText("Data Icons");
        empWorkIconsCount.setText("Work Icons");
    }

    private void loadManagingDirectorEmpLinksData() {
        // TODO: Load real employee links data from server
        // For now, set some sample data for Managing Director Panel
        myEmpLinksCount.setText("My Employee Links");
        teamEmpLinksCount.setText("Team Employee Links");
    }



    private void passUserDataToIntent(Intent intent) {
        // Get current user data and pass it to the new activity
        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            String userId = currentIntent.getStringExtra("USER_ID");
            String firstName = currentIntent.getStringExtra("FIRST_NAME");
            String lastName = currentIntent.getStringExtra("LAST_NAME");
            String fullName = currentIntent.getStringExtra("USERNAME");
            String sourcePanel = currentIntent.getStringExtra("SOURCE_PANEL");
            
            if (userId != null) {
                intent.putExtra("USER_ID", userId);
                intent.putExtra("userId", userId); // Also pass with lowercase key for permission activities
            }
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            if (fullName != null) {
                intent.putExtra("USERNAME", fullName);
                intent.putExtra("userName", fullName); // Also pass with lowercase key for permission activities
            }
            if (sourcePanel != null) intent.putExtra("SOURCE_PANEL", sourcePanel);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
} 
