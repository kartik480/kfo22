package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BranchFullPayoutActivity extends AppCompatActivity {
    private static final String TAG = "BranchFullPayoutActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";
    
    private ImageButton backButton;
    private Spinner vendorBankSpinner;
    private Spinner loanTypeSpinner;
    private RecyclerView payoutListRecyclerView;
    private MaterialCardView filterButton;
    private TextView titleText;
    
    private List<VendorBank> vendorBanks = new ArrayList<>();
    private List<LoanType> loanTypes = new ArrayList<>();
    private List<PayoutItem> allPayoutItems = new ArrayList<>();
    private List<PayoutItem> filteredPayoutItems = new ArrayList<>();
    private PayoutAdapter payoutAdapter;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_full_payout);
        
        initializeViews();
        setupClickListeners();
        loadDropdownData();
        loadPayoutList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        vendorBankSpinner = findViewById(R.id.vendorBankSpinner);
        loanTypeSpinner = findViewById(R.id.loanTypeSpinner);
        payoutListRecyclerView = findViewById(R.id.payoutListRecyclerView);
        filterButton = findViewById(R.id.filterButton);
        titleText = findViewById(R.id.titleText);
        
        titleText.setText("Branch/Full Payout");
        
        // Setup RecyclerView
        payoutListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        payoutAdapter = new PayoutAdapter(filteredPayoutItems);
        payoutListRecyclerView.setAdapter(payoutAdapter);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        
        filterButton.setOnClickListener(v -> {
            String selectedVendorBank = vendorBankSpinner.getSelectedItem().toString();
            String selectedLoanType = loanTypeSpinner.getSelectedItem().toString();
            
            if (selectedVendorBank.equals("Select Vendor Bank") && selectedLoanType.equals("Select Loan Type")) {
                filteredPayoutItems.clear();
                filteredPayoutItems.addAll(allPayoutItems);
            } else {
                filterPayouts(selectedVendorBank, selectedLoanType);
            }
            payoutAdapter.notifyDataSetChanged();
        });
    }

    private void loadDropdownData() {
        loadVendorBanks();
        loadLoanTypes();
    }

    private void loadVendorBanks() {
        executor.execute(() -> {
            try {
                String urlString = BASE_URL + "get_vendor_bank_list.php";
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    Log.e(TAG, "VendorBanks API response: " + response.toString());

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        vendorBanks.clear();
                        vendorBanks.add(new VendorBank("0", "Select Vendor Bank"));
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            String id = item.getString("id");
                            String name = item.getString("vendor_bank_name");
                            vendorBanks.add(new VendorBank(id, name));
                        }

                        runOnUiThread(() -> {
                            try {
                                ArrayAdapter<VendorBank> adapter = new ArrayAdapter<>(
                                    BranchFullPayoutActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    vendorBanks
                                );
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                vendorBankSpinner.setAdapter(adapter);
                            } catch (Exception e) {
                                Log.e(TAG, "Error setting vendor bank adapter: " + e.getMessage());
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "VendorBanks API failed with response code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading vendor banks: " + e.getMessage());
                runOnUiThread(() -> {
                    try {
                        // Add fallback data
                        vendorBanks.clear();
                        vendorBanks.add(new VendorBank("0", "Select Vendor Bank"));
                        vendorBanks.add(new VendorBank("1", "HDFC Bank"));
                        vendorBanks.add(new VendorBank("2", "ICICI Bank"));
                        vendorBanks.add(new VendorBank("3", "State Bank of India"));
                        
                        ArrayAdapter<VendorBank> adapter = new ArrayAdapter<>(
                            BranchFullPayoutActivity.this,
                            android.R.layout.simple_spinner_item,
                            vendorBanks
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        vendorBankSpinner.setAdapter(adapter);
                    } catch (Exception ex) {
                        Log.e(TAG, "Error setting fallback vendor bank adapter: " + ex.getMessage());
                    }
                });
            }
        });
    }

    private void loadLoanTypes() {
        executor.execute(() -> {
            try {
                String urlString = BASE_URL + "fetch_loan_types.php";
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    Log.e(TAG, "LoanTypes API response: " + response.toString());

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        loanTypes.clear();
                        loanTypes.add(new LoanType("0", "Select Loan Type"));
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            String id = item.getString("id");
                            String name = item.getString("loan_type"); // Fixed: was "loan_type_name"
                            loanTypes.add(new LoanType(id, name));
                        }

                        runOnUiThread(() -> {
                            try {
                                ArrayAdapter<LoanType> adapter = new ArrayAdapter<>(
                                    BranchFullPayoutActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    loanTypes
                                );
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                loanTypeSpinner.setAdapter(adapter);
                            } catch (Exception e) {
                                Log.e(TAG, "Error setting loan type adapter: " + e.getMessage());
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "LoanTypes API failed with response code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading loan types: " + e.getMessage());
                runOnUiThread(() -> {
                    try {
                        // Add fallback data
                        loanTypes.clear();
                        loanTypes.add(new LoanType("0", "Select Loan Type"));
                        loanTypes.add(new LoanType("1", "Personal Loan"));
                        loanTypes.add(new LoanType("2", "Home Loan"));
                        loanTypes.add(new LoanType("3", "Business Loan"));
                        
                        ArrayAdapter<LoanType> adapter = new ArrayAdapter<>(
                            BranchFullPayoutActivity.this,
                            android.R.layout.simple_spinner_item,
                            loanTypes
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        loanTypeSpinner.setAdapter(adapter);
                    } catch (Exception ex) {
                        Log.e(TAG, "Error setting fallback loan type adapter: " + ex.getMessage());
                    }
                });
            }
        });
    }

    private void loadPayoutList() {
        executor.execute(() -> {
            try {
                String urlString = BASE_URL + "get_branch_full_payouts.php";
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    Log.e(TAG, "Payout List API response: " + response.toString());

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        allPayoutItems.clear();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            
                            // Handle empty or null user_id
                            int userId = 0;
                            String userIdStr = item.getString("user_id");
                            if (userIdStr != null && !userIdStr.trim().isEmpty()) {
                                try {
                                    userId = Integer.parseInt(userIdStr);
                                } catch (NumberFormatException e) {
                                    userId = 0; // Default to 0 if parsing fails
                                }
                            }
                            
                            // Handle null category_name
                            String categoryName = item.getString("category_name");
                            if (categoryName == null || categoryName.equals("null")) {
                                categoryName = "N/A";
                            }
                            
                            PayoutItem payoutItem = new PayoutItem(
                                Integer.parseInt(item.getString("id")),
                                userId,
                                item.getString("payout_type_name"),
                                item.getString("vendor_bank_name"),
                                item.getString("loan_type_name"),
                                categoryName,
                                item.getString("payout")
                            );
                            allPayoutItems.add(payoutItem);
                        }

                        runOnUiThread(() -> {
                            try {
                                filteredPayoutItems.clear();
                                filteredPayoutItems.addAll(allPayoutItems);
                                payoutAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Log.e(TAG, "Error updating payout adapter: " + e.getMessage());
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "Payout List API failed with response code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading payout list: " + e.getMessage());
                runOnUiThread(() -> {
                    try {
                        // Add sample data
                        allPayoutItems.clear();
                        allPayoutItems.add(new PayoutItem(1, 0, "Branch/Full Payout", "HDFC Bank", "Personal Loan", "Branch", "₹50,000"));
                        allPayoutItems.add(new PayoutItem(2, 0, "Branch/Full Payout", "ICICI Bank", "Home Loan", "Branch", "₹5,00,000"));
                        allPayoutItems.add(new PayoutItem(3, 0, "Branch/Full Payout", "State Bank of India", "Business Loan", "Branch", "₹10,00,000"));
                        
                        filteredPayoutItems.clear();
                        filteredPayoutItems.addAll(allPayoutItems);
                        payoutAdapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        Log.e(TAG, "Error setting fallback payout data: " + ex.getMessage());
                    }
                });
            }
        });
    }

    private void filterPayouts(String vendorBank, String loanType) {
        filteredPayoutItems.clear();
        
        for (PayoutItem item : allPayoutItems) {
            boolean matchesVendorBank = vendorBank.equals("Select Vendor Bank") || item.getVendorBankName().equals(vendorBank);
            boolean matchesLoanType = loanType.equals("Select Loan Type") || item.getLoanTypeName().equals(loanType);
            
            if (matchesVendorBank && matchesLoanType) {
                filteredPayoutItems.add(item);
            }
        }
    }

    // Helper classes
    public static class VendorBank {
        private String id;
        private String name;

        public VendorBank(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public String getName() { return name; }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class LoanType {
        private String id;
        private String name;

        public LoanType(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public String getName() { return name; }

        @Override
        public String toString() {
            return name;
        }
    }
} 