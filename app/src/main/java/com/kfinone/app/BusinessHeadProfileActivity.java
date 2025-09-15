package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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

public class BusinessHeadProfileActivity extends AppCompatActivity {

    private static final String TAG = "BusinessHeadProfile";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    private Spinner vendorBankSpinner;
    private Spinner loanTypeSpinner;
    private Button filterButton;
    private Button resetButton;
    private ListView profileListView;
    private ProfileListAdapter profileAdapter;
    private List<DirectorProfileActivity.ProfileItem> profileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_profile);

        initializeViews();
        setupClickListeners();
        loadVendorBanks();
        loadLoanTypes();
        loadProfiles();
    }

    private void initializeViews() {
        vendorBankSpinner = findViewById(R.id.vendorBankSpinner);
        loanTypeSpinner = findViewById(R.id.loanTypeSpinner);
        filterButton = findViewById(R.id.filterButton);
        resetButton = findViewById(R.id.resetButton);
        profileListView = findViewById(R.id.profileListView);

        profileList = new ArrayList<>();
        profileAdapter = new ProfileListAdapter(this, profileList);
        profileListView.setAdapter(profileAdapter);
    }

    private void setupClickListeners() {
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
        filterButton.setOnClickListener(v -> applyFilters());
        resetButton.setOnClickListener(v -> resetFilters());
    }

    private void loadVendorBanks() {
        String url = BASE_URL + "get_vendor_banks.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<String> vendorBanks = new ArrayList<>();
                            vendorBanks.add("All Vendor Banks");
                            
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject bank = response.getJSONObject(i);
                                vendorBanks.add(bank.getString("vendor_bank_name"));
                            }
                            
                            android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                                    BusinessHeadProfileActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    vendorBanks
                            );
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            vendorBankSpinner.setAdapter(adapter);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing vendor banks", e);
                            loadFallbackVendorBanks();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error loading vendor banks", error);
                        Toast.makeText(BusinessHeadProfileActivity.this, "Error loading vendor banks - using fallback data", Toast.LENGTH_SHORT).show();
                        loadFallbackVendorBanks();
                    }
                });

        queue.add(request);
    }

    private void loadLoanTypes() {
        String url = BASE_URL + "get_loan_types.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            List<String> loanTypes = new ArrayList<>();
                            loanTypes.add("All Loan Types");
                            
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject loanType = response.getJSONObject(i);
                                loanTypes.add(loanType.getString("loan_type"));
                            }
                            
                            android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                                    BusinessHeadProfileActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    loanTypes
                            );
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            loanTypeSpinner.setAdapter(adapter);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing loan types", e);
                            loadFallbackLoanTypes();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error loading loan types", error);
                        Toast.makeText(BusinessHeadProfileActivity.this, "Error loading loan types - using fallback data", Toast.LENGTH_SHORT).show();
                        loadFallbackLoanTypes();
                    }
                });

        queue.add(request);
    }

    private void loadProfiles() {
        String url = BASE_URL + "get_profiles.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            profileList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject profile = response.getJSONObject(i);
                                DirectorProfileActivity.ProfileItem item = new DirectorProfileActivity.ProfileItem(
                                        profile.getString("vendor_bank"),
                                        profile.getString("loan_type"),
                                        profile.optString("image", ""),
                                        profile.optString("file", "")
                                );
                                profileList.add(item);
                            }
                            profileAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing profiles", e);
                            Toast.makeText(BusinessHeadProfileActivity.this, "Error parsing profiles", Toast.LENGTH_SHORT).show();
                            loadFallbackProfiles();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error loading profiles", error);
                        Toast.makeText(BusinessHeadProfileActivity.this, "Error loading profiles - using fallback data", Toast.LENGTH_SHORT).show();
                        loadFallbackProfiles();
                    }
                });

        queue.add(request);
    }

    private void applyFilters() {
        String selectedVendorBank = vendorBankSpinner.getSelectedItem().toString();
        String selectedLoanType = loanTypeSpinner.getSelectedItem().toString();

        List<DirectorProfileActivity.ProfileItem> filteredList = new ArrayList<>();
        
        for (DirectorProfileActivity.ProfileItem profile : profileList) {
            boolean matchesVendorBank = selectedVendorBank.equals("All Vendor Banks") || 
                                     profile.getVendorBank().equals(selectedVendorBank);
            boolean matchesLoanType = selectedLoanType.equals("All Loan Types") || 
                                    profile.getLoanType().equals(selectedLoanType);
            
            if (matchesVendorBank && matchesLoanType) {
                filteredList.add(profile);
            }
        }

        profileAdapter = new ProfileListAdapter(this, filteredList);
        profileListView.setAdapter(profileAdapter);
    }

    private void resetFilters() {
        vendorBankSpinner.setSelection(0);
        loanTypeSpinner.setSelection(0);
        profileAdapter = new ProfileListAdapter(this, profileList);
        profileListView.setAdapter(profileAdapter);
    }

    private void loadFallbackVendorBanks() {
        List<String> fallbackBanks = new ArrayList<>();
        fallbackBanks.add("All Vendor Banks");
        fallbackBanks.add("HDFC Bank");
        fallbackBanks.add("ICICI Bank");
        fallbackBanks.add("SBI Bank");
        fallbackBanks.add("Axis Bank");
        fallbackBanks.add("Kotak Bank");

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, fallbackBanks);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vendorBankSpinner.setAdapter(adapter);
    }

    private void loadFallbackLoanTypes() {
        List<String> fallbackTypes = new ArrayList<>();
        fallbackTypes.add("All Loan Types");
        fallbackTypes.add("Personal Loan");
        fallbackTypes.add("Home Loan");
        fallbackTypes.add("Car Loan");
        fallbackTypes.add("Business Loan");
        fallbackTypes.add("Education Loan");

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, fallbackTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loanTypeSpinner.setAdapter(adapter);
    }

    private void loadFallbackProfiles() {
        profileList.clear();
        // No fallback profiles - show empty list
        profileAdapter.notifyDataSetChanged();
    }

    private void passUserDataToIntent(android.content.Intent intent) {
        // Pass user data if needed
        if (getIntent().getStringExtra("user_id") != null) {
            intent.putExtra("user_id", getIntent().getStringExtra("user_id"));
        }
        if (getIntent().getStringExtra("user_name") != null) {
            intent.putExtra("user_name", getIntent().getStringExtra("user_name"));
        }
        if (getIntent().getStringExtra("user_role") != null) {
            intent.putExtra("user_role", getIntent().getStringExtra("user_role"));
        }
    }
}
