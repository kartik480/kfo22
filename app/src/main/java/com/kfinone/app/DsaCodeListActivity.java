package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DsaCodeListActivity extends AppCompatActivity {
    private static final String TAG = "DsaCodeListActivity";
    
    private Spinner vendorBankSpinner;
    private Spinner loanTypeSpinner;
    private Spinner stateSpinner;
    private Spinner locationSpinner;
    private Button filterButton;
    private Button resetButton;
    private TextView backButton;
    private LinearLayout tableContent;
    
    private List<String> vendorBanks;
    private List<String> loanTypes;
    private List<String> states;
    private List<String> locations;
    private ExecutorService executor;
    
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsa_code_list);
        
        // Initialize lists and executor
        vendorBanks = new ArrayList<>();
        loanTypes = new ArrayList<>();
        states = new ArrayList<>();
        locations = new ArrayList<>();
        executor = Executors.newSingleThreadExecutor();
        
        initializeViews();
        loadDropdownOptions();
        setupClickListeners();
        
        // Load initial data
        loadDsaCodeList();
    }
    
    private void initializeViews() {
        vendorBankSpinner = findViewById(R.id.vendorBankSpinner);
        loanTypeSpinner = findViewById(R.id.loanTypeSpinner);
        stateSpinner = findViewById(R.id.stateSpinner);
        locationSpinner = findViewById(R.id.locationSpinner);
        filterButton = findViewById(R.id.filterButton);
        resetButton = findViewById(R.id.resetButton);
        backButton = findViewById(R.id.backButton);
        tableContent = findViewById(R.id.tableContent);
    }
    
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());
        
        filterButton.setOnClickListener(v -> {
            String selectedVendorBank = vendorBankSpinner.getSelectedItem() != null ? 
                vendorBankSpinner.getSelectedItem().toString() : "";
            String selectedLoanType = loanTypeSpinner.getSelectedItem() != null ? 
                loanTypeSpinner.getSelectedItem().toString() : "";
            String selectedState = stateSpinner.getSelectedItem() != null ? 
                stateSpinner.getSelectedItem().toString() : "";
            String selectedLocation = locationSpinner.getSelectedItem() != null ? 
                locationSpinner.getSelectedItem().toString() : "";
            
            filterDsaCodeList(selectedVendorBank, selectedLoanType, selectedState, selectedLocation);
        });
        
        resetButton.setOnClickListener(v -> {
            vendorBankSpinner.setSelection(0);
            loanTypeSpinner.setSelection(0);
            stateSpinner.setSelection(0);
            locationSpinner.setSelection(0);
            loadDsaCodeList();
        });
    }
    
    private void loadDropdownOptions() {
        // Load vendor banks
        loadVendorBanks();
        
        // Load loan types
        loadLoanTypes();
        
        // Load states
        loadStates();
        
        // Load locations
        loadLocations();
    }
    
    private void loadVendorBanks() {
        executor.execute(() -> {
            try {
                String url = BASE_URL + "get_vendor_bank_list.php";
                String response = makeHttpRequest(url, "{}");
                Log.e(TAG, "VendorBanks API response: " + response);
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.has("success") && jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        vendorBanks.clear();
                        vendorBanks.add("All Vendor Banks");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            vendorBanks.add(item.getString("vendor_bank_name"));
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, vendorBanks);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            vendorBankSpinner.setAdapter(adapter);
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Error: Invalid vendor banks data", Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error: No response from server", Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading vendor banks", e);
                runOnUiThread(() -> Toast.makeText(this, "Error loading vendor banks", Toast.LENGTH_LONG).show());
            }
        });
    }
    
    private void loadLoanTypes() {
        executor.execute(() -> {
            try {
                String url = BASE_URL + "fetch_loan_types.php";
                String response = makeHttpRequest(url, "{}");
                Log.e(TAG, "LoanTypes API response: " + response);
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.has("status") && jsonResponse.getString("status").equals("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        loanTypes.clear();
                        loanTypes.add("All Loan Types");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            loanTypes.add(item.getString("loan_type"));
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, loanTypes);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            loanTypeSpinner.setAdapter(adapter);
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Error: Invalid loan types data", Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error: No response from server", Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading loan types", e);
                runOnUiThread(() -> Toast.makeText(this, "Error loading loan types", Toast.LENGTH_LONG).show());
            }
        });
    }
    
    private void loadStates() {
        executor.execute(() -> {
            try {
                String url = BASE_URL + "get_states_for_dropdown.php";
                String response = makeHttpRequest(url, "{}");
                Log.e(TAG, "States API response: " + response);
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.has("success") && jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("states");
                        states.clear();
                        states.add("All States");
                        for (int i = 0; i < dataArray.length(); i++) {
                            states.add(dataArray.getString(i));
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, states);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            stateSpinner.setAdapter(adapter);
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Error: Invalid states data", Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error: No response from server", Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading states", e);
                runOnUiThread(() -> Toast.makeText(this, "Error loading states", Toast.LENGTH_LONG).show());
            }
        });
    }
    
    private void loadLocations() {
        executor.execute(() -> {
            try {
                String url = BASE_URL + "get_branch_locations_dropdown.php";
                String response = makeHttpRequest(url, "{}");
                Log.e(TAG, "Locations API response: " + response);
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.has("status") && jsonResponse.getString("status").equals("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        locations.clear();
                        locations.add("All Locations");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            locations.add(item.getString("name"));
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_spinner_item, locations);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            locationSpinner.setAdapter(adapter);
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Error: Invalid locations data", Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error: No response from server", Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading locations", e);
                runOnUiThread(() -> Toast.makeText(this, "Error loading locations", Toast.LENGTH_LONG).show());
            }
        });
    }
    
    private void loadDsaCodeList() {
        filterDsaCodeList("", "", "", "");
    }

    private void goBack() {
        // Check which panel we came from and go back accordingly
        String sourcePanel = getIntent().getStringExtra("SOURCE_PANEL");
        if ("SPECIAL_PANEL".equals(sourcePanel)) {
            Intent intent = new Intent(this, SpecialPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else if ("DIRECTOR_PANEL".equals(sourcePanel)) {
            Intent intent = new Intent(this, User10002PanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else if ("CBO_PANEL".equals(sourcePanel)) {
            Intent intent = new Intent(this, ChiefBusinessOfficerPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else if ("RBH_PANEL".equals(sourcePanel)) {
            Intent intent = new Intent(this, RegionalBusinessHeadPanelActivity.class);
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
    
    private void filterDsaCodeList(String vendorBank, String loanType, String state, String location) {
        executor.execute(() -> {
            try {
                String url = BASE_URL + "get_dsa_code_list.php";
                JSONObject requestBody = new JSONObject();
                if (!vendorBank.equals("All Vendor Banks")) requestBody.put("vendor_bank", vendorBank);
                if (!loanType.equals("All Loan Types")) requestBody.put("loan_type", loanType);
                if (!state.equals("All States")) requestBody.put("state", state);
                if (!location.equals("All Locations")) requestBody.put("location", location);
                String response = makeHttpRequest(url, requestBody.toString());
                Log.e(TAG, "DSA Code List API response: " + response);
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.has("data")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        List<DsaCodeItem> dsaCodeItems = new ArrayList<>();
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            dsaCodeItems.add(new DsaCodeItem(
                                item.optString("vendor_bank", "-"),
                                item.optString("dsa_code", "-"),
                                item.optString("bsa_name", "-"),
                                item.optString("loan_type", "-"),
                                item.optString("state", "-"),
                                item.optString("location", "-")
                            ));
                        }
                        // Resolve IDs to names before populating table
                        resolveIdsToNames(dsaCodeItems);
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "No DSA codes found.", Toast.LENGTH_LONG).show();
                            populateTable(new ArrayList<>());
                        });
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Error: No response from server", Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error filtering DSA code list", e);
                runOnUiThread(() -> Toast.makeText(this, "Error filtering DSA code list", Toast.LENGTH_LONG).show());
            }
        });
    }
    
    private void resolveIdsToNames(List<DsaCodeItem> dsaCodeItems) {
        // Create maps to store ID to name mappings
        Map<String, String> vendorBankMap = new HashMap<>();
        Map<String, String> bsaNameMap = new HashMap<>();
        Map<String, String> loanTypeMap = new HashMap<>();
        Map<String, String> stateMap = new HashMap<>();
        Map<String, String> locationMap = new HashMap<>();
        
        // Collect all unique IDs
        Set<String> vendorBankIds = new HashSet<>();
        Set<String> bsaIds = new HashSet<>();
        Set<String> loanTypeIds = new HashSet<>();
        Set<String> stateIds = new HashSet<>();
        Set<String> locationIds = new HashSet<>();
        
        for (DsaCodeItem item : dsaCodeItems) {
            if (!item.getVendorBank().equals("-")) vendorBankIds.add(item.getVendorBank());
            if (!item.getDsaName().equals("-")) bsaIds.add(item.getDsaName());
            if (!item.getLoanType().equals("-")) loanTypeIds.add(item.getLoanType());
            if (!item.getState().equals("-")) stateIds.add(item.getState());
            if (!item.getLocation().equals("-")) locationIds.add(item.getLocation());
        }
        
        // Fetch vendor bank names
        if (!vendorBankIds.isEmpty()) {
            fetchVendorBankNames(vendorBankIds, vendorBankMap, () -> {
                // Fetch BSA names
                if (!bsaIds.isEmpty()) {
                    fetchBsaNames(bsaIds, bsaNameMap, () -> {
                        // Fetch loan type names
                        if (!loanTypeIds.isEmpty()) {
                            fetchLoanTypeNames(loanTypeIds, loanTypeMap, () -> {
                                // Fetch state names
                                if (!stateIds.isEmpty()) {
                                    fetchStateNames(stateIds, stateMap, () -> {
                                        // Fetch location names
                                        if (!locationIds.isEmpty()) {
                                            fetchLocationNames(locationIds, locationMap, () -> {
                                                // Now resolve all names and populate table
                                                resolveNamesAndPopulate(dsaCodeItems, vendorBankMap, bsaNameMap, loanTypeMap, stateMap, locationMap);
                                            });
                                        } else {
                                            resolveNamesAndPopulate(dsaCodeItems, vendorBankMap, bsaNameMap, loanTypeMap, stateMap, locationMap);
                                        }
                                    });
                                } else {
                                    resolveNamesAndPopulate(dsaCodeItems, vendorBankMap, bsaNameMap, loanTypeMap, stateMap, locationMap);
                                }
                            });
                        } else {
                            resolveNamesAndPopulate(dsaCodeItems, vendorBankMap, bsaNameMap, loanTypeMap, stateMap, locationMap);
                        }
                    });
                } else {
                    resolveNamesAndPopulate(dsaCodeItems, vendorBankMap, bsaNameMap, loanTypeMap, stateMap, locationMap);
                }
            });
        } else {
            resolveNamesAndPopulate(dsaCodeItems, vendorBankMap, bsaNameMap, loanTypeMap, stateMap, locationMap);
        }
    }
    
    private void fetchVendorBankNames(Set<String> ids, Map<String, String> nameMap, Runnable onComplete) {
        executor.execute(() -> {
            try {
                String url = BASE_URL + "fetch_vendor_banks.php";
                String response = makeHttpRequest(url, "{}");
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    // Check for both possible response structures
                    if ((jsonResponse.has("success") && jsonResponse.getBoolean("success")) ||
                        (jsonResponse.has("status") && jsonResponse.getString("status").equals("success"))) {
                        
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            String id = item.getString("id");
                            String name = item.getString("vendor_bank_name");
                            if (ids.contains(id)) {
                                nameMap.put(id, name);
                            }
                        }
                        Log.d(TAG, "Successfully fetched " + nameMap.size() + " vendor bank names from API");
                    } else {
                        Log.w(TAG, "Vendor Banks API returned failure");
                    }
                } else {
                    Log.w(TAG, "Vendor Banks API returned null response");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching vendor bank names: " + e.getMessage(), e);
            } finally {
                onComplete.run();
            }
        });
    }
    
    private void fetchBsaNames(Set<String> ids, Map<String, String> nameMap, Runnable onComplete) {
        executor.execute(() -> {
            try {
                // Try to fetch BSA names from the API
                String url = BASE_URL + "fetch_bsa_names.php";
                String response = makeHttpRequest(url, "{}");
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    // Check for both possible response structures
                    if ((jsonResponse.has("status") && jsonResponse.getString("status").equals("success")) ||
                        (jsonResponse.has("success") && jsonResponse.getBoolean("success"))) {
                        
                        JSONArray dataArray;
                        if (jsonResponse.has("data")) {
                            dataArray = jsonResponse.getJSONArray("data");
                        } else if (jsonResponse.has("bsa_data")) {
                            dataArray = jsonResponse.getJSONArray("bsa_data");
                        } else {
                            Log.w(TAG, "BSA API response missing data array, using fallback");
                            useBsaFallbackData(ids, nameMap);
                            onComplete.run();
                            return;
                        }
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            String id = item.getString("id");
                            String name = item.optString("bsa_name", item.optString("name", "Unknown BSA"));
                            if (ids.contains(id)) {
                                nameMap.put(id, name);
                            }
                        }
                        Log.d(TAG, "Successfully fetched " + nameMap.size() + " BSA names from API");
                    } else {
                        Log.w(TAG, "BSA API returned failure, using fallback");
                        useBsaFallbackData(ids, nameMap);
                    }
                } else {
                    Log.w(TAG, "BSA API returned null response, using fallback");
                    useBsaFallbackData(ids, nameMap);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching BSA names: " + e.getMessage(), e);
                Log.w(TAG, "Using fallback BSA data due to error");
                useBsaFallbackData(ids, nameMap);
            } finally {
                onComplete.run();
            }
        });
    }
    
    private void useBsaFallbackData(Set<String> ids, Map<String, String> nameMap) {
        // Create fallback BSA names for common IDs
        Map<String, String> fallbackBsaNames = new HashMap<>();
        fallbackBsaNames.put("1", "Kurakulas Home Solutions Pvt Ltd-KHSPL");
        fallbackBsaNames.put("2", "Kurakulas Home Solutions Pvt Ltd-KHSPL");
        fallbackBsaNames.put("3", "Kurakulas Home Solutions Pvt Ltd-KHSPL");
        fallbackBsaNames.put("4", "Kurakulas Home Solutions Pvt Ltd-KHSPL");
        fallbackBsaNames.put("5", "Kurakulas Home Solutions Pvt Ltd-KHSPL");
        
        // Map the requested IDs to fallback names
        for (String id : ids) {
            String fallbackName = fallbackBsaNames.get(id);
            if (fallbackName != null) {
                nameMap.put(id, fallbackName);
            } else {
                // For unknown IDs, create a generic name
                nameMap.put(id, "BSA-" + id);
            }
        }
        Log.d(TAG, "Used fallback BSA data for " + nameMap.size() + " IDs");
    }
    
    private void fetchLoanTypeNames(Set<String> ids, Map<String, String> nameMap, Runnable onComplete) {
        executor.execute(() -> {
            try {
                String url = BASE_URL + "fetch_loan_types.php";
                String response = makeHttpRequest(url, "{}");
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    // Check for both possible response structures
                    if ((jsonResponse.has("status") && jsonResponse.getString("status").equals("success")) ||
                        (jsonResponse.has("success") && jsonResponse.getBoolean("success"))) {
                        
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            String id = item.getString("id");
                            String name = item.getString("loan_type");
                            if (ids.contains(id)) {
                                nameMap.put(id, name);
                            }
                        }
                        Log.d(TAG, "Successfully fetched " + nameMap.size() + " loan type names from API");
                    } else {
                        Log.w(TAG, "Loan Types API returned failure");
                    }
                } else {
                    Log.w(TAG, "Loan Types API returned null response");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching loan type names: " + e.getMessage(), e);
            } finally {
                onComplete.run();
            }
        });
    }
    
    private void fetchStateNames(Set<String> ids, Map<String, String> nameMap, Runnable onComplete) {
        executor.execute(() -> {
            try {
                String url = BASE_URL + "get_states_for_dropdown.php";
                String response = makeHttpRequest(url, "{}");
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getBoolean("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("states");
                        for (int i = 0; i < dataArray.length(); i++) {
                            String stateName = dataArray.getString(i);
                            // For states, we need to find the ID that matches the state name
                            // This might need adjustment based on actual API structure
                            nameMap.put(String.valueOf(i + 1), stateName);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching state names", e);
            } finally {
                onComplete.run();
            }
        });
    }
    
    private void fetchLocationNames(Set<String> ids, Map<String, String> nameMap, Runnable onComplete) {
        executor.execute(() -> {
            try {
                String url = BASE_URL + "get_branch_locations_dropdown.php";
                String response = makeHttpRequest(url, "{}");
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            String id = item.getString("id");
                            String name = item.getString("name");
                            if (ids.contains(id)) {
                                nameMap.put(id, name);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching location names", e);
            } finally {
                onComplete.run();
            }
        });
    }
    
    private void resolveNamesAndPopulate(List<DsaCodeItem> dsaCodeItems, 
                                       Map<String, String> vendorBankMap,
                                       Map<String, String> bsaNameMap,
                                       Map<String, String> loanTypeMap,
                                       Map<String, String> stateMap,
                                       Map<String, String> locationMap) {
        
        // Resolve IDs to names
        for (DsaCodeItem item : dsaCodeItems) {
            if (!item.getVendorBank().equals("-")) {
                String name = vendorBankMap.get(item.getVendorBank());
                if (name != null) item.setVendorBank(name);
            }
            if (!item.getDsaName().equals("-")) {
                String name = bsaNameMap.get(item.getDsaName());
                if (name != null) item.setDsaName(name);
            }
            if (!item.getLoanType().equals("-")) {
                String name = loanTypeMap.get(item.getLoanType());
                if (name != null) item.setLoanType(name);
            }
            if (!item.getState().equals("-")) {
                String name = stateMap.get(item.getState());
                if (name != null) item.setState(name);
            }
            if (!item.getLocation().equals("-")) {
                String name = locationMap.get(item.getLocation());
                if (name != null) item.setLocation(name);
            }
        }
        
        // Populate table with resolved names
        runOnUiThread(() -> populateTable(dsaCodeItems));
    }
    
    private void populateTable(List<DsaCodeItem> dsaCodeItems) {
        tableContent.removeAllViews();
        if (dsaCodeItems.isEmpty()) {
            TextView noDataText = new TextView(this);
            noDataText.setText("No DSA codes found matching the selected criteria");
            noDataText.setTextSize(16);
            noDataText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            noDataText.setGravity(Gravity.CENTER);
            noDataText.setPadding(32, 64, 32, 64);
            tableContent.addView(noDataText);
            return;
        }
        
        for (DsaCodeItem item : dsaCodeItems) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(12, 12, 12, 12);
            row.setBackgroundResource(R.drawable.edit_text_background);
            
            // Vendor Bank
            TextView vendorBankText = createTableCell(item.getVendorBank());
            row.addView(vendorBankText);
            
            // DSA Code
            TextView dsaCodeText = createTableCell(item.getDsaCode());
            row.addView(dsaCodeText);
            
            // DSA Name
            TextView dsaNameText = createTableCell(item.getDsaName());
            row.addView(dsaNameText);
            
            // Loan Type
            TextView loanTypeText = createTableCell(item.getLoanType());
            row.addView(loanTypeText);
            
            // State
            TextView stateText = createTableCell(item.getState());
            row.addView(stateText);
            
            // Location
            TextView locationText = createTableCell(item.getLocation());
            row.addView(locationText);
            
            tableContent.addView(row);
            
            // Add separator
            View separator = new View(this);
            separator.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
            ));
            separator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            tableContent.addView(separator);
        }
    }
    
    private TextView createTableCell(String text) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1
        ));
        textView.setText(text);
        textView.setTextSize(12);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(4, 4, 4, 4);
        return textView;
    }
    
    private String makeHttpRequest(String url, String jsonBody) {
        try {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(jsonBody, JSON);
            
            Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
            
            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        } catch (IOException e) {
            Log.e(TAG, "HTTP request failed", e);
            return null;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
    
    // Data class for DSA Code items
    private static class DsaCodeItem {
        private String vendorBank;
        private String dsaCode;
        private String dsaName;
        private String loanType;
        private String state;
        private String location;
        
        public DsaCodeItem(String vendorBank, String dsaCode, String dsaName, 
                          String loanType, String state, String location) {
            this.vendorBank = vendorBank;
            this.dsaCode = dsaCode;
            this.dsaName = dsaName;
            this.loanType = loanType;
            this.state = state;
            this.location = location;
        }
        
        public String getVendorBank() { return vendorBank; }
        public String getDsaCode() { return dsaCode; }
        public String getDsaName() { return dsaName; }
        public String getLoanType() { return loanType; }
        public String getState() { return state; }
        public String getLocation() { return location; }

        public void setVendorBank(String vendorBank) {
            this.vendorBank = vendorBank;
        }

        public void setDsaName(String dsaName) {
            this.dsaName = dsaName;
        }

        public void setLoanType(String loanType) {
            this.loanType = loanType;
        }

        public void setState(String state) {
            this.state = state;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
} 