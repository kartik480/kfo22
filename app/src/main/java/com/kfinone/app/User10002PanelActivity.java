package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class User10002PanelActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "User10002PanelActivity";

    // Drawer layout
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // Top navigation elements
    private View menuButton;
    private TextView titleText;
    private View accountIcon;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;
    private LinearLayout profileButton;

    // Dashboard cards
    private MaterialCardView totalEmpCard;
    private MaterialCardView totalSdsaCard;
    private MaterialCardView totalPartnerCard;
    private MaterialCardView totalPortfolioCard;
    private MaterialCardView totalAgentCard;
    private MaterialCardView totalPayoutCard;

    // Card counts
    private TextView totalEmpCount;
    private TextView totalSdsaCount;
    private TextView totalPartnerCount;
    private TextView totalPortfolioCount;
    private TextView totalAgentCount;
    private TextView totalPayoutCount;

    // Change indicators
    private TextView totalEmpChange;
    private TextView totalSdsaChange;
    private TextView totalPartnerChange;
    private TextView totalPortfolioChange;
    private TextView totalAgentChange;
    private TextView totalPayoutChange;

    // Welcome elements
    private TextView welcomeText;
    private TextView userInfoText;
    private TextView specialMessageText;

    // Quick access boxes
    private MaterialCardView empLinksBox;
    private MaterialCardView dataLinksBox;
    private MaterialCardView workLinksBox;
    private MaterialCardView employeeBox;
    private MaterialCardView sdsaBox;
    private MaterialCardView partnerBox;
    private MaterialCardView agentBox;
    private MaterialCardView payoutBox;
    private MaterialCardView dsaCodesBox;
    private MaterialCardView bankersBox;
    private MaterialCardView portfolioBox;
    private MaterialCardView addInsuranceBox;

    // User data
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_10002_panel);

        initializeViews();
        setupUserData();
        setupClickListeners();
        loadDashboardData();
        updateWelcomeMessage();
        setupAnimations();

        // Set all Executive Analytics values to 0
        if (totalEmpCount != null) totalEmpCount.setText("0");
        if (totalSdsaCount != null) totalSdsaCount.setText("0");
        if (totalPartnerCount != null) totalPartnerCount.setText("0");
        if (totalPortfolioCount != null) totalPortfolioCount.setText("0");
        if (totalAgentCount != null) totalAgentCount.setText("0");
        if (totalPayoutCount != null) totalPayoutCount.setText("0");
        if (totalEmpChange != null) totalEmpChange.setText("0");
        if (totalSdsaChange != null) totalSdsaChange.setText("0");
        if (totalPartnerChange != null) totalPartnerChange.setText("0");
        if (totalPortfolioChange != null) totalPortfolioChange.setText("0");
        if (totalAgentChange != null) totalAgentChange.setText("0");
        if (totalPayoutChange != null) totalPayoutChange.setText("0");
    }

    private void initializeViews() {
        // Drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        
        // Top navigation
        menuButton = findViewById(R.id.menuButton);
        titleText = findViewById(R.id.titleText);
        accountIcon = findViewById(R.id.accountIcon);
        if (titleText != null) {
            titleText.setText("Director Dashboard");
        }

        // Bottom navigation
        dashboardButton = findViewById(R.id.dashboardButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);
        profileButton = findViewById(R.id.profileButton);

        // Dashboard cards
        totalEmpCard = findViewById(R.id.totalEmpCard);
        totalSdsaCard = findViewById(R.id.totalSdsaCard);
        totalPartnerCard = findViewById(R.id.totalPartnerCard);
        totalPortfolioCard = findViewById(R.id.totalPortfolioCard);
        totalAgentCard = findViewById(R.id.totalAgentCard);
        totalPayoutCard = findViewById(R.id.totalPayoutCard);

        // Card counts
        totalEmpCount = findViewById(R.id.totalEmpCount);
        totalSdsaCount = findViewById(R.id.totalSdsaCount);
        totalPartnerCount = findViewById(R.id.totalPartnerCount);
        totalPortfolioCount = findViewById(R.id.totalPortfolioCount);
        totalAgentCount = findViewById(R.id.totalAgentCount);
        totalPayoutCount = findViewById(R.id.totalPayoutCount);

        // Change indicators
        totalEmpChange = findViewById(R.id.totalEmpChange);
        totalSdsaChange = findViewById(R.id.totalSdsaChange);
        totalPartnerChange = findViewById(R.id.totalPartnerChange);
        totalPortfolioChange = findViewById(R.id.totalPortfolioChange);
        totalAgentChange = findViewById(R.id.totalAgentChange);
        totalPayoutChange = findViewById(R.id.totalPayoutChange);

        // Welcome elements
        welcomeText = findViewById(R.id.welcomeText);
        userInfoText = findViewById(R.id.userInfoText);
        specialMessageText = findViewById(R.id.specialMessageText);

        // Quick access boxes
        empLinksBox = findViewById(R.id.empLinksBox);
        dataLinksBox = findViewById(R.id.dataLinksBox);
        workLinksBox = findViewById(R.id.workLinksBox);
        employeeBox = findViewById(R.id.employeeBox);
        sdsaBox = findViewById(R.id.sdsaBox);
        partnerBox = findViewById(R.id.partnerBox);
        agentBox = findViewById(R.id.agentBox);
        payoutBox = findViewById(R.id.payoutBox);
        dsaCodesBox = findViewById(R.id.dsaCodesBox);
        bankersBox = findViewById(R.id.bankersBox);
        portfolioBox = findViewById(R.id.portfolioBox);
        addInsuranceBox = findViewById(R.id.addInsuranceBox);

        // Set up navigation view
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupUserData() {
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            userName = intent.getStringExtra("USERNAME");
            firstName = intent.getStringExtra("FIRST_NAME");
            lastName = intent.getStringExtra("LAST_NAME");
        }
    }

    private void setupClickListeners() {
        // Top navigation
        menuButton.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        accountIcon.setOnClickListener(v -> {
            Intent intent = new Intent(this, AccountActivity.class);
            intent.putExtra("FULL_NAME", userName);
            startActivity(intent);
        });

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Toast.makeText(this, "Dashboard", Toast.LENGTH_SHORT).show();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AccountActivity.class);
            intent.putExtra("FULL_NAME", userName);
            startActivity(intent);
        });

        // Dashboard card click listeners
        totalEmpCard.setOnClickListener(v -> {
            String empCount = totalEmpCount.getText().toString();
            Toast.makeText(this, "Total Employees: " + empCount, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ActiveEmpListActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        totalSdsaCard.setOnClickListener(v -> {
            String sdsaCount = totalSdsaCount.getText().toString();
            Toast.makeText(this, "Total SDSA: " + sdsaCount, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MySdsaActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        totalPartnerCard.setOnClickListener(v -> {
            String partnerCount = totalPartnerCount.getText().toString();
            Toast.makeText(this, "Total Partners: " + partnerCount, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MyPartnerActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        totalPortfolioCard.setOnClickListener(v -> {
            String portfolioCount = totalPortfolioCount.getText().toString();
            Toast.makeText(this, "Total Portfolio: " + portfolioCount, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MyPortfolioActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        totalAgentCard.setOnClickListener(v -> {
            String agentCount = totalAgentCount.getText().toString();
            Toast.makeText(this, "Total Agents: " + agentCount, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MyAgentActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        totalPayoutCard.setOnClickListener(v -> {
            String payoutCount = totalPayoutCount.getText().toString();
            Toast.makeText(this, "Total Payouts: " + payoutCount, Toast.LENGTH_SHORT).show();
            // TODO: Navigate to payout activity
        });

        // Quick access boxes
        empLinksBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, DirectorEmpLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        dataLinksBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, DirectorDataLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        workLinksBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, DirectorWorkLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        employeeBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, DirectorEmployeeActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        sdsaBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, DirectorSdsaActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        partnerBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, DirectorPartnerActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        agentBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, DirectorAgentActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        payoutBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, DirectorPayoutActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        dsaCodesBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, DirectorDsaCodeActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        bankersBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, DirectorBankersActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        portfolioBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, DirectorPortfolioActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        addInsuranceBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, DirectorInsurancePanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });
    }

    private void setupAnimations() {
        // Add entrance animations for cards
        android.view.animation.Animation fadeIn = android.view.animation.AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeIn.setDuration(800);

        totalEmpCard.startAnimation(fadeIn);
        totalSdsaCard.startAnimation(fadeIn);
        totalPartnerCard.startAnimation(fadeIn);
        totalPortfolioCard.startAnimation(fadeIn);
        totalAgentCard.startAnimation(fadeIn);
        totalPayoutCard.startAnimation(fadeIn);
    }

    private void loadDashboardData() {
        // Load dashboard data from server or show sample data
        // For now, show sample data
        totalEmpCount.setText("156");
        totalSdsaCount.setText("89");
        totalPartnerCount.setText("45");
        totalPortfolioCount.setText("234");
        totalAgentCount.setText("67");
        totalPayoutCount.setText("12");

        totalEmpChange.setText("+12% this month");
        totalSdsaChange.setText("+8% this month");
        totalPartnerChange.setText("+15% this month");
        totalPortfolioChange.setText("+22% this month");
        totalAgentChange.setText("+5% this month");
        totalPayoutChange.setText("+18% this month");
    }

    private void updateWelcomeMessage() {
        String welcomeMessage;
        if (firstName != null && !firstName.isEmpty() && firstName.length() > 1) {
            welcomeMessage = "Welcome, Director " + firstName + "!";
        } else if (userName != null && !userName.isEmpty()) {
            String[] nameParts = userName.split("\\s+");
            if (nameParts.length > 0 && nameParts[0].length() > 1) {
                welcomeMessage = "Welcome, Director " + nameParts[0] + "!";
            } else {
                welcomeMessage = "Welcome, Director " + userName + "!";
            }
        } else {
            welcomeMessage = "Welcome, Director!";
        }
        
        welcomeText.setText(welcomeMessage);
        userInfoText.setText("Director ID: " + (userId != null ? userId : "10002") + " | Executive Access | Last Login: Today");
        specialMessageText.setText("Executive Dashboard - Premium Management Features");
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.nav_home) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, AccountActivity.class);
            intent.putExtra("FULL_NAME", userName);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            // Navigate back to login
            Intent intent = new Intent(this, EnhancedLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
} 