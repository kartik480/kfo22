package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class MyInsuranceActivity extends AppCompatActivity {

    private RecyclerView insuranceRecyclerView;
    private InsuranceAdapter insuranceAdapter;
    private List<InsurancePolicy> insuranceList;
    private List<InsurancePolicy> filteredList;

    private TextInputEditText searchInput;
    private Chip allChip, activeChip, expiredChip, pendingChip;
    private LinearLayout emptyStateLayout, loadingLayout;
    private Button addInsuranceButton;
    private TextView backButton;
    private View searchButton, filterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_insurance);

        initializeViews();
        setupClickListeners();
        setupSearchAndFilter();
        loadInsuranceData();
    }

    private void initializeViews() {
        insuranceRecyclerView = findViewById(R.id.insuranceRecyclerView);
        searchInput = findViewById(R.id.searchInput);
        allChip = findViewById(R.id.allChip);
        activeChip = findViewById(R.id.activeChip);
        expiredChip = findViewById(R.id.expiredChip);
        pendingChip = findViewById(R.id.pendingChip);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        loadingLayout = findViewById(R.id.loadingLayout);
        addInsuranceButton = findViewById(R.id.addInsuranceButton);
        backButton = findViewById(R.id.backButton);
        searchButton = findViewById(R.id.searchButton);
        filterButton = findViewById(R.id.filterButton);

        // Setup RecyclerView
        insuranceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        insuranceList = new ArrayList<>();
        filteredList = new ArrayList<>();
        insuranceAdapter = new InsuranceAdapter(filteredList, this);
        insuranceRecyclerView.setAdapter(insuranceAdapter);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());
        addInsuranceButton.setOnClickListener(v -> addNewInsurance());
        searchButton.setOnClickListener(v -> toggleSearch());
        filterButton.setOnClickListener(v -> toggleFilter());

        // Chip click listeners
        allChip.setOnClickListener(v -> filterByStatus("All"));
        activeChip.setOnClickListener(v -> filterByStatus("Active"));
        expiredChip.setOnClickListener(v -> filterByStatus("Expired"));
        pendingChip.setOnClickListener(v -> filterByStatus("Pending"));
    }

    private void setupSearchAndFilter() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterInsurance(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void goBack() {
        // Check which panel we came from and go back accordingly
        String sourcePanel = getIntent().getStringExtra("SOURCE_PANEL");
        if ("CBO_PANEL".equals(sourcePanel)) {
            Intent intent = new Intent(this, CBOInsurancePanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else if ("RBH_PANEL".equals(sourcePanel)) {
            Intent intent = new Intent(this, RBHInsurancePanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else {
            // Default to InsurancePanelActivity (Special Panel)
            Intent intent = new Intent(this, InsurancePanelActivity.class);
            startActivity(intent);
            finish();
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

    private void addNewInsurance() {
        Intent intent = new Intent(this, AddInsuranceActivity.class);
        // Pass the same SOURCE_PANEL that this activity received
        String sourcePanel = getIntent().getStringExtra("SOURCE_PANEL");
        if (sourcePanel != null) {
            intent.putExtra("SOURCE_PANEL", sourcePanel);
        }
        startActivity(intent);
    }

    private void toggleSearch() {
        if (searchInput.getVisibility() == View.VISIBLE) {
            searchInput.setVisibility(View.GONE);
            searchInput.setText("");
        } else {
            searchInput.setVisibility(View.VISIBLE);
            searchInput.requestFocus();
        }
    }

    private void toggleFilter() {
        // Toggle filter chips visibility
        // For now, just show a toast message
        Toast.makeText(this, "Filter options are always visible", Toast.LENGTH_SHORT).show();
    }

    private void filterByStatus(String status) {
        // Update chip states
        allChip.setChecked(status.equals("All"));
        activeChip.setChecked(status.equals("Active"));
        expiredChip.setChecked(status.equals("Expired"));
        pendingChip.setChecked(status.equals("Pending"));

        // Filter the list
        filteredList.clear();
        for (InsurancePolicy policy : insuranceList) {
            if (status.equals("All") || policy.getStatus().equals(status)) {
                filteredList.add(policy);
            }
        }
        insuranceAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void filterInsurance(String query) {
        filteredList.clear();
        for (InsurancePolicy policy : insuranceList) {
            if (policy.getCustomerName().toLowerCase().contains(query.toLowerCase()) ||
                policy.getPolicyNumber().toLowerCase().contains(query.toLowerCase()) ||
                policy.getInsuranceType().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(policy);
            }
        }
        insuranceAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void loadInsuranceData() {
        showLoading(true);
        
        // Simulate API call delay
        insuranceRecyclerView.postDelayed(() -> {
            loadSampleData();
            showLoading(false);
            updateEmptyState();
        }, 1000);
    }

    private void loadSampleData() {
        insuranceList.clear();
        // Only fetch real data here. No sample data.
        // insuranceAdapter.notifyDataSetChanged();
    }

    private void showLoading(boolean show) {
        if (show) {
            loadingLayout.setVisibility(View.VISIBLE);
            insuranceRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.GONE);
        } else {
            loadingLayout.setVisibility(View.GONE);
            insuranceRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updateEmptyState() {
        if (filteredList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            insuranceRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            insuranceRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    // Insurance Policy data class
    public static class InsurancePolicy {
        private String customerName;
        private String policyNumber;
        private String insuranceType;
        private String premiumAmount;
        private String coverageAmount;
        private String expiryDate;
        private String status;

        public InsurancePolicy(String customerName, String policyNumber, String insuranceType,
                             String premiumAmount, String coverageAmount, String expiryDate, String status) {
            this.customerName = customerName;
            this.policyNumber = policyNumber;
            this.insuranceType = insuranceType;
            this.premiumAmount = premiumAmount;
            this.coverageAmount = coverageAmount;
            this.expiryDate = expiryDate;
            this.status = status;
        }

        // Getters
        public String getCustomerName() { return customerName; }
        public String getPolicyNumber() { return policyNumber; }
        public String getInsuranceType() { return insuranceType; }
        public String getPremiumAmount() { return premiumAmount; }
        public String getCoverageAmount() { return coverageAmount; }
        public String getExpiryDate() { return expiryDate; }
        public String getStatus() { return status; }
    }
} 