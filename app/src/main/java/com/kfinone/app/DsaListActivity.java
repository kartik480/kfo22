package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kfinone.app.databinding.ActivityDsaListBinding;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.google.android.material.textfield.TextInputEditText;
import android.text.TextUtils;
import android.widget.Toast;
import org.json.JSONException;
import android.content.Intent;
import com.kfinone.app.DirectorDsaCodeActivity;

public class DsaListActivity extends AppCompatActivity {
    private static final String TAG = "DsaListActivity";
    private ActivityDsaListBinding binding;
    private AutoCompleteTextView vendorBankSpinner;
    private AutoCompleteTextView loanTypeSpinner;
    private AutoCompleteTextView stateSpinner;
    private AutoCompleteTextView locationSpinner;
    private TextInputEditText dsaCodeInput;
    private TextView backButton;
    private List<String> vendorBanks;
    private List<String> loanTypes;
    private List<String> states;
    private List<String> locations;
    private RequestQueue requestQueue;
    private RecyclerView recyclerView;
    private DsaListAdapter dsaListAdapter;
    private List<DsaItem> dsaItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDsaListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vendorBanks = new ArrayList<>();
        loanTypes = new ArrayList<>();
        states = new ArrayList<>();
        locations = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        initializeViews();
        setupRecyclerView();
        loadDropdownOptionsFromDatabase();
        setupButtons();
        loadAllDsaRecords();
    }

    private void initializeViews() {
        vendorBankSpinner = binding.vendorBankSpinner;
        loanTypeSpinner = binding.loanTypeSpinner;
        stateSpinner = binding.stateSpinner;
        locationSpinner = binding.locationSpinner;
        dsaCodeInput = binding.dsaCodeInput;
        backButton = binding.backButton;
        recyclerView = findViewById(R.id.dsaRecyclerView);
    }

    private void setupRecyclerView() {
        dsaListAdapter = new DsaListAdapter(dsaItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dsaListAdapter);
    }

    private void loadDropdownOptionsFromDatabase() {
        loadVendorBanksForDropdown();
        loadLoanTypesForDropdown();
        loadBranchStatesForDropdown();
        loadBranchLocationsForDropdown();
    }

    private void loadVendorBanksForDropdown() {
        String url = "https://emp.kfinone.com/mobile/api/fetch_vendor_banks.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    JSONArray vendorBanksArray = response.getJSONArray("data");
                    List<String> newVendorBanks = new ArrayList<>();
                    newVendorBanks.add("Select Vendor Bank");
                    for (int i = 0; i < vendorBanksArray.length(); i++) {
                        JSONObject vendorBank = vendorBanksArray.getJSONObject(i);
                        newVendorBanks.add(vendorBank.getString("vendor_bank_name"));
                    }
                    vendorBanks.clear();
                    vendorBanks.addAll(newVendorBanks);
                    ArrayAdapter<String> vendorBankAdapter = new ArrayAdapter<>(
                        DsaListActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        vendorBanks
                    );
                    vendorBankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    vendorBankSpinner.setAdapter(vendorBankAdapter);
                } catch (JSONException e) {
                    showError("Failed to parse vendor banks");
                }
            },
            error -> showError("Failed to load vendor banks")
        );
        requestQueue.add(request);
    }

    private void loadLoanTypesForDropdown() {
        String url = "https://emp.kfinone.com/mobile/api/fetch_loan_types.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    JSONArray loanTypesArray = response.getJSONArray("data");
                    List<String> newLoanTypes = new ArrayList<>();
                    newLoanTypes.add("Select Loan Type");
                    for (int i = 0; i < loanTypesArray.length(); i++) {
                        JSONObject loanType = loanTypesArray.getJSONObject(i);
                        newLoanTypes.add(loanType.getString("loan_type"));
                    }
                    loanTypes.clear();
                    loanTypes.addAll(newLoanTypes);
                    ArrayAdapter<String> loanTypeAdapter = new ArrayAdapter<>(
                        DsaListActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        loanTypes
                    );
                    loanTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    loanTypeSpinner.setAdapter(loanTypeAdapter);
                } catch (JSONException e) {
                    showError("Failed to parse loan types");
                }
            },
            error -> showError("Failed to load loan types")
        );
        requestQueue.add(request);
    }

    private void loadBranchStatesForDropdown() {
        String url = "https://emp.kfinone.com/mobile/api/fetch_branch_states.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    JSONArray statesArray = response.getJSONArray("data");
                    List<String> newStates = new ArrayList<>();
                    newStates.add("Select State");
                    for (int i = 0; i < statesArray.length(); i++) {
                        JSONObject state = statesArray.getJSONObject(i);
                        newStates.add(state.getString("branch_state_name"));
                    }
                    states.clear();
                    states.addAll(newStates);
                    ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(
                        DsaListActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        states
                    );
                    stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    stateSpinner.setAdapter(stateAdapter);
                } catch (JSONException e) {
                    showError("Failed to parse states");
                }
            },
            error -> showError("Failed to load states")
        );
        requestQueue.add(request);
    }

    private void loadBranchLocationsForDropdown() {
        String url = "https://emp.kfinone.com/mobile/api/fetch_branch_locations.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    JSONArray locationsArray = response.getJSONArray("data");
                    List<String> newLocations = new ArrayList<>();
                    newLocations.add("Select Location");
                    for (int i = 0; i < locationsArray.length(); i++) {
                        JSONObject location = locationsArray.getJSONObject(i);
                        newLocations.add(location.getString("branch_location"));
                    }
                    locations.clear();
                    locations.addAll(newLocations);
                    ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                        DsaListActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        locations
                    );
                    locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    locationSpinner.setAdapter(locationAdapter);
                } catch (JSONException e) {
                    showError("Failed to parse locations");
                }
            },
            error -> showError("Failed to load locations")
        );
        requestQueue.add(request);
    }

    private void fetchDsaList(String vendorBank, String loanType, String state, String location, String dsaCode) {
        StringBuilder urlBuilder = new StringBuilder("https://emp.kfinone.com/mobile/api/get_dsa_list.php?");
        try {
            if (!vendorBank.isEmpty() && !vendorBank.equals("Select Vendor Bank")) {
                urlBuilder.append("vendor_bank=").append(java.net.URLEncoder.encode(vendorBank, "UTF-8")).append("&");
            }
            if (!loanType.isEmpty() && !loanType.equals("Select Loan Type")) {
                urlBuilder.append("loan_type=").append(java.net.URLEncoder.encode(loanType, "UTF-8")).append("&");
            }
            if (!state.isEmpty() && !state.equals("Select State")) {
                urlBuilder.append("state=").append(java.net.URLEncoder.encode(state, "UTF-8")).append("&");
            }
            if (!location.isEmpty() && !location.equals("Select Location")) {
                urlBuilder.append("location=").append(java.net.URLEncoder.encode(location, "UTF-8")).append("&");
            }
            if (!dsaCode.isEmpty()) {
                urlBuilder.append("dsa_code=").append(java.net.URLEncoder.encode(dsaCode, "UTF-8")).append("&");
            }
        } catch (Exception e) {
            showError("Failed to encode filter params");
            return;
        }
        String urlString = urlBuilder.toString();
        if (urlString.endsWith("&")) {
            urlString = urlString.substring(0, urlString.length() - 1);
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlString, null,
            response -> {
                try {
                    JSONArray dataArray = response.getJSONArray("data");
                    dsaItems.clear();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject item = dataArray.getJSONObject(i);
                        DsaItem dsaItem = new DsaItem(
                            item.getString("vendor_bank_name"),
                            item.getString("dsa_code"),
                            item.getString("bsa_name"),
                            item.getString("loan_type"),
                            item.getString("branch_state_name"),
                            item.getString("branch_location")
                        );
                        dsaItems.add(dsaItem);
                    }
                    dsaListAdapter.notifyDataSetChanged();
                    if (dsaItems.isEmpty()) {
                        showError("No DSA records found matching your filter criteria");
                    }
                } catch (JSONException e) {
                    showError("Failed to parse DSA data");
                }
            },
            error -> showError("Failed to fetch DSA data")
        );
        requestQueue.add(request);
    }

    private void loadAllDsaRecords() {
        String url = "https://emp.kfinone.com/mobile/api/get_dsa_list.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    JSONArray dataArray = response.getJSONArray("data");
                    dsaItems.clear();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject item = dataArray.getJSONObject(i);
                        DsaItem dsaItem = new DsaItem(
                            item.getString("vendor_bank_name"),
                            item.getString("dsa_code"),
                            item.getString("bsa_name"),
                            item.getString("loan_type"),
                            item.getString("branch_state_name"),
                            item.getString("branch_location")
                        );
                        dsaItems.add(dsaItem);
                    }
                    dsaListAdapter.notifyDataSetChanged();
                    showError("Loaded " + dsaItems.size() + " DSA records for showcase");
                } catch (JSONException e) {
                    showError("Failed to parse DSA data");
                }
            },
            error -> showError("Failed to load DSA records")
        );
        requestQueue.add(request);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void setupButtons() {
        backButton.setOnClickListener(v -> onBackPressed());

        binding.filterButton.setOnClickListener(v -> {
            String vendorBank = vendorBankSpinner.getText().toString();
            String loanType = loanTypeSpinner.getText().toString();
            String state = stateSpinner.getText().toString();
            String location = locationSpinner.getText().toString();
            String dsaCode = dsaCodeInput.getText().toString();

            boolean hasFilters = (!vendorBank.isEmpty() && !vendorBank.equals("Select Vendor Bank")) ||
                               (!loanType.isEmpty() && !loanType.equals("Select Loan Type")) ||
                               (!state.isEmpty() && !state.equals("Select State")) ||
                               (!location.isEmpty() && !location.equals("Select Location")) ||
                               (!dsaCode.isEmpty());

            if (!hasFilters) {
                showError("No filters applied. Please select at least one filter criteria.");
                return;
            }

            fetchDsaList(vendorBank, loanType, state, location, dsaCode);
        });

        binding.resetButton.setOnClickListener(v -> {
            vendorBankSpinner.setText("");
            loanTypeSpinner.setText("");
            stateSpinner.setText("");
            locationSpinner.setText("");
            dsaCodeInput.setText("");
            loadAllDsaRecords();
        });
    }

    @Override
    public void onBackPressed() {
        String sourcePanel = getIntent().getStringExtra("SOURCE_PANEL");
        if ("DIRECTOR_PANEL".equals(sourcePanel)) {
            Intent intent = new Intent(this, DirectorDsaCodeActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void passUserDataToIntent(Intent intent) {
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
} 
