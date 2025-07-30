package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private MaterialCardView agentMasterCard;
    private MaterialCardView dsaNameCard;
    private MaterialCardView typeOfLoansCard;
    private MaterialCardView bankersCard;
    private MaterialCardView empMasterCard;
    private MaterialCardView empLinksCard;
    private MaterialCardView locationMasterCard;
    private MaterialCardView sdsaMasterCard;
    private MaterialCardView dsaCodeMasterCard;
    private MaterialCardView bankMasterCard;
    private MaterialCardView partnerMasterCard;
    private MaterialCardView payoutCard;
    private MaterialCardView uploadDocumentCard;
    private MaterialCardView policyCard;
    private MaterialCardView cboCard;
    private MaterialCardView rbhCard;
    private MaterialCardView bhEmpMasterCard;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton menuButton;
    private TextView welcomeUsername;
    private String fullName;
    private String firstName;
    private String lastName;
    private String userId;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Aggressive fullscreen approach
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN |
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_FULLSCREEN |
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        
        // Set status bar to transparent
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        
        // Additional flags to ensure complete fullscreen
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        
        setContentView(R.layout.activity_home);
        
        // Check for app updates
        checkForAppUpdates();

        // Get user information from Intent and display it
        welcomeUsername = findViewById(R.id.welcomeUsername);
        Intent intent = getIntent();
        if (intent != null) {
            // Get first name and last name
            firstName = intent.getStringExtra("FIRST_NAME");
            lastName = intent.getStringExtra("LAST_NAME");
            
            // Get full name as fallback
            fullName = intent.getStringExtra("USERNAME");
            
            // Get user ID
            userId = intent.getStringExtra("USER_ID");
            
            // Create welcome message with first name
            String welcomeMessage;
            if (firstName != null && !firstName.isEmpty() && firstName.length() > 1) {
                // Use firstName if it's more than 1 character
                welcomeMessage = firstName;
            } else if (fullName != null && !fullName.isEmpty()) {
                // Try to extract first name from full name
                String[] nameParts = fullName.split("\\s+");
                if (nameParts.length > 0 && nameParts[0].length() > 1) {
                    welcomeMessage = nameParts[0];
                } else {
                    welcomeMessage = fullName;
                }
            } else {
                welcomeMessage = "User";
            }
            
            welcomeUsername.setText(welcomeMessage);
        }
        
        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        menuButton = findViewById(R.id.menuButton);
        bottomNavigationView = findViewById(R.id.bottomNav);
        
        // Set up navigation view
        navigationView.setNavigationItemSelectedListener(this);
        
        // Set up bottom navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                // Already on home, do nothing
                return true;
            } else if (id == R.id.navigation_files) {
                // TODO: Handle files navigation
                Toast.makeText(this, "Files clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.navigation_agents) {
                startActivity(new Intent(this, AgentActivity.class));
                return true;
            } else if (id == R.id.navigation_more) {
                startActivity(new Intent(this, MoreActivity.class));
                return true;
            }
            return false;
        });
        
        agentMasterCard = findViewById(R.id.agentMasterCard);
        dsaNameCard = findViewById(R.id.dsaNameCard);
        typeOfLoansCard = findViewById(R.id.typeOfLoansCard);
        bankersCard = findViewById(R.id.bankersCard);
        empMasterCard = findViewById(R.id.empMasterCard);
        empLinksCard = findViewById(R.id.empLinksCard);
        locationMasterCard = findViewById(R.id.locationMasterCard);
        sdsaMasterCard = findViewById(R.id.sdsaMasterCard);
        dsaCodeMasterCard = findViewById(R.id.dsaCodeMasterCard);
        bankMasterCard = findViewById(R.id.bankMasterCard);
        partnerMasterCard = findViewById(R.id.partnerMasterCard);
        payoutCard = findViewById(R.id.payoutCard);
        uploadDocumentCard = findViewById(R.id.uploadDocumentCard);
        policyCard = findViewById(R.id.policyCard);
        cboCard = findViewById(R.id.cboCard);
        rbhCard = findViewById(R.id.rbhCard);
        bhEmpMasterCard = findViewById(R.id.bhEmpMasterCard);

        // Set up menu button click listener
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Set up top navbar icon click listeners
        ImageButton helpButton = findViewById(R.id.helpButton);
        ImageButton notificationsButton = findViewById(R.id.notificationsButton);
        ImageButton accountButton = findViewById(R.id.accountButton);

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Help clicked", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to help activity or show help dialog
            }
        });

        notificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Notifications clicked", Toast.LENGTH_SHORT).show();
                // TODO: Navigate to notifications activity or show notifications
            }
        });

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        // Set click listeners for cards
        agentMasterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AgentMasterActivity.class);
                startActivity(intent);
            }
        });

        dsaNameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DsaNameActivity.class);
                startActivity(intent);
            }
        });

        typeOfLoansCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LoanTypeActivity.class);
                startActivity(intent);
            }
        });

        bankersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, BankersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        empMasterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, EmpMasterActivity.class);
                // Pass user data
                if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
                if (lastName != null) intent.putExtra("LAST_NAME", lastName);
                if (fullName != null) intent.putExtra("USERNAME", fullName);
                if (userId != null) intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });

        empLinksCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, EmpLinksActivity.class);
                intent.putExtra("SOURCE_PANEL", "HOME_PANEL");
                // Pass user data
                if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
                if (lastName != null) intent.putExtra("LAST_NAME", lastName);
                if (fullName != null) intent.putExtra("USERNAME", fullName);
                startActivity(intent);
            }
        });

        locationMasterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, LocationMasterActivity.class);
                startActivity(intent);
            }
        });

        sdsaMasterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SdsaMasterActivity.class);
                startActivity(intent);
            }
        });

        dsaCodeMasterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DsaCodeMasterActivity.class);
                startActivity(intent);
            }
        });

        bankMasterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, BankMasterActivity.class);
                startActivity(intent);
            }
        });

        partnerMasterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PartnerPanelActivity.class);
                startActivity(intent);
            }
        });

        payoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PayoutActivity.class);
                startActivity(intent);
            }
        });

        uploadDocumentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DocumentUploadActivity.class);
                startActivity(intent);
            }
        });

        policyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(HomeActivity.this, "Policy clicked - Opening Policy panel...", Toast.LENGTH_SHORT).show();
                    Log.d("HomeActivity", "Policy card clicked - starting PolicyActivity");
                    Intent intent = new Intent(HomeActivity.this, PolicyActivity.class);
                    intent.putExtra("SOURCE_PANEL", "HOME_PANEL");
                    // Pass user data
                    if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
                    if (lastName != null) intent.putExtra("LAST_NAME", lastName);
                    if (fullName != null) intent.putExtra("USERNAME", fullName);
                    startActivity(intent);
                    Log.d("HomeActivity", "PolicyActivity started successfully");
                } catch (Exception e) {
                    Log.e("HomeActivity", "Error starting PolicyActivity: " + e.getMessage(), e);
                    Toast.makeText(HomeActivity.this, "Error opening Policy panel: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Set click listeners for admin cards
        cboCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SuperAdminCBOActivity.class);
                // Pass user data
                if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
                if (lastName != null) intent.putExtra("LAST_NAME", lastName);
                if (fullName != null) intent.putExtra("USERNAME", fullName);
                if (userId != null) intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });

        rbhCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SuperAdminRBHActivity.class);
                // Pass user data
                if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
                if (lastName != null) intent.putExtra("LAST_NAME", lastName);
                if (fullName != null) intent.putExtra("USERNAME", fullName);
                if (userId != null) intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });

        bhEmpMasterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SuperAdminBHEmpMasterActivity.class);
                // Pass user data
                if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
                if (lastName != null) intent.putExtra("LAST_NAME", lastName);
                if (fullName != null) intent.putExtra("USERNAME", fullName);
                if (userId != null) intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        // Handle navigation view item clicks here
        if (id == R.id.nav_home) {
            // Already on home, just close drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_profile) {
            // TODO: Navigate to profile
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            // TODO: Navigate to settings
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_emp_master) {
            Intent intent = new Intent(this, EmpMasterActivity.class);
            // Pass user data
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            if (fullName != null) intent.putExtra("USERNAME", fullName);
            if (userId != null) intent.putExtra("USER_ID", userId);
            startActivity(intent);
        } else if (id == R.id.nav_location_master) {
            startActivity(new Intent(this, LocationMasterActivity.class));
        } else if (id == R.id.nav_sdsa_master) {
            startActivity(new Intent(this, SdsaMasterActivity.class));
        } else if (id == R.id.nav_dsa_code_master) {
            startActivity(new Intent(this, DsaCodeMasterActivity.class));
        } else if (id == R.id.nav_bank_master) {
            startActivity(new Intent(this, BankMasterActivity.class));
        } else if (id == R.id.nav_partner_master) {
            startActivity(new Intent(this, PartnerPanelActivity.class));
        } else if (id == R.id.nav_agent_master) {
            Toast.makeText(this, "Opening Agent Panel...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AgentActivity.class));
        } else if (id == R.id.nav_dsa_name) {
            startActivity(new Intent(this, DsaNameActivity.class));
        } else if (id == R.id.nav_loan_types) {
            startActivity(new Intent(this, LoanTypeActivity.class));
        } else if (id == R.id.nav_bankers) {
            Intent intent = new Intent(this, BankersActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (id == R.id.nav_emp_links) {
            Intent intent = new Intent(this, EmpLinksActivity.class);
            intent.putExtra("SOURCE_PANEL", "HOME_PANEL");
            // Pass user data
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            if (fullName != null) intent.putExtra("USERNAME", fullName);
            startActivity(intent);
        } else if (id == R.id.nav_payout) {
            startActivity(new Intent(this, PayoutActivity.class));
        } else if (id == R.id.nav_policy) {
            Intent intent = new Intent(this, PolicyActivity.class);
            intent.putExtra("SOURCE_PANEL", "HOME_PANEL");
            // Pass user data
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            if (fullName != null) intent.putExtra("USERNAME", fullName);
            startActivity(intent);
        } else if (id == R.id.nav_document_upload) {
            startActivity(new Intent(this, DocumentUploadActivity.class));
        } else if (id == R.id.nav_special_panel) {
            startActivity(new Intent(this, SpecialPanelActivity.class));
        } else if (id == R.id.nav_dsa_code_list) {
            Intent intent = new Intent(this, DsaCodeListActivity.class);
            intent.putExtra("SOURCE_PANEL", "HOME_PANEL");
            // Pass user data
            if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
            if (lastName != null) intent.putExtra("LAST_NAME", lastName);
            if (fullName != null) intent.putExtra("USERNAME", fullName);
            startActivity(intent);
        } else if (id == R.id.nav_bankers_panel) {
            startActivity(new Intent(this, BankersPanelActivity.class));
        } else if (id == R.id.nav_portfolio_panel) {
            startActivity(new Intent(this, PortfolioPanelActivity.class));
        } else if (id == R.id.nav_insurance_panel) {
            startActivity(new Intent(this, InsurancePanelActivity.class));
        } else if (id == R.id.nav_doc_check_list) {
            startActivity(new Intent(this, DocCheckListActivity.class));
        } else if (id == R.id.nav_more) {
            startActivity(new Intent(this, MoreActivity.class));
        } else if (id == R.id.nav_help) {
            // TODO: Navigate to help center
            Toast.makeText(this, "Help Center clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_feedback) {
            // TODO: Navigate to feedback
            Toast.makeText(this, "Send Feedback clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_about) {
            // TODO: Navigate to about
            Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            // TODO: Handle logout
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Re-apply fullscreen when window gains focus
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    
    /**
     * Check for app updates and show dialog if update is available
     */
    private void checkForAppUpdates() {
        try {
            AppUpdateChecker updateChecker = new AppUpdateChecker(this);
            updateChecker.checkForUpdates();
        } catch (Exception e) {
            Log.e("HomeActivity", "Error checking for updates", e);
        }
    }
} 
