package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RBHActiveEmpListActivity extends AppCompatActivity {
    
    private static final String TAG = "RBHActiveEmpList";
    private static final String API_BASE_URL = "https://kfinone.com/api/";
    
    private RecyclerView employeesRecyclerView;
    private RBHActiveEmployeeAdapter employeeAdapter;
    private List<RBHEmployee> employeeList;
    private List<RBHEmployee> filteredEmployeeList;
    
    private TextView totalEmployeesCount, activeEmployeesCount;
    private EditText searchEditText;
    private ImageButton backButton, refreshButton;
    private ProgressBar loadingProgress;
    private View emptyStateLayout;
    
    private String userName;
    private String userId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rbh_active_emp_list);
        
        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        
        if (userName == null || userName.isEmpty()) {
            userName = "RBH User";
        }
        
        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        setupSearch();
        
        // Load employees data
        loadEmployeesData();
    }
    
    private void initializeViews() {
        employeesRecyclerView = findViewById(R.id.employeesRecyclerView);
        totalEmployeesCount = findViewById(R.id.totalEmployeesCount);
        activeEmployeesCount = findViewById(R.id.activeEmployeesCount);
        searchEditText = findViewById(R.id.searchEditText);
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        loadingProgress = findViewById(R.id.loadingProgress);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        
        employeeList = new ArrayList<>();
        filteredEmployeeList = new ArrayList<>();
    }
    
    private void setupClickListeners() {
        backButton.setOnClickListener(v -> {
            finish();
        });
        
        refreshButton.setOnClickListener(v -> {
            loadEmployeesData();
        });
    }
    
    private void setupRecyclerView() {
        employeeAdapter = new RBHActiveEmployeeAdapter(filteredEmployeeList, employee -> {
            // Handle employee item click
            showEmployeeDetails(employee);
        });
        
        employeesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        employeesRecyclerView.setAdapter(employeeAdapter);
    }
    
    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            
            @Override
            public void afterTextChanged(Editable s) {
                filterEmployees(s.toString());
            }
        });
    }
    
    private void filterEmployees(String query) {
        filteredEmployeeList.clear();
        
        if (query.isEmpty()) {
            filteredEmployeeList.addAll(employeeList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (RBHEmployee employee : employeeList) {
                if (employee.getName().toLowerCase().contains(lowerCaseQuery) ||
                    employee.getEmployeeId().toLowerCase().contains(lowerCaseQuery) ||
                    employee.getDesignation().toLowerCase().contains(lowerCaseQuery) ||
                    employee.getDepartment().toLowerCase().contains(lowerCaseQuery) ||
                    employee.getMobile().toLowerCase().contains(lowerCaseQuery) ||
                    employee.getEmail().toLowerCase().contains(lowerCaseQuery)) {
                    filteredEmployeeList.add(employee);
                }
            }
        }
        
        employeeAdapter.notifyDataSetChanged();
        updateEmptyState();
    }
    
    private void loadEmployeesData() {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showLoading(true);
        
        new Thread(() -> {
            try {
                String urlString = API_BASE_URL + "get_rbh_active_emp_list.php?user_id=" + userId;
                URL url = new URL(urlString);
                
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                
                int responseCode = conn.getResponseCode();
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
                    
                    String responseString = response.toString();
                    Log.d(TAG, "Server response: " + responseString);
                    
                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray employeesArray = json.getJSONArray("employees");
                        int totalCount = json.getInt("total_employees");
                        
                        employeeList.clear();
                        for (int i = 0; i < employeesArray.length(); i++) {
                            JSONObject employeeJson = employeesArray.getJSONObject(i);
                            RBHEmployee employee = parseEmployeeFromJson(employeeJson);
                            employeeList.add(employee);
                        }
                        
                        runOnUiThread(() -> {
                            updateUI(totalCount, employeeList.size());
                            filterEmployees(searchEditText.getText().toString());
                            showLoading(false);
                        });
                        
                    } else {
                        String errorMessage = json.getString("message");
                        runOnUiThread(() -> {
                            Toast.makeText(RBHActiveEmpListActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            showLoading(false);
                        });
                    }
                    
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(RBHActiveEmpListActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                        showLoading(false);
                    });
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading employees data", e);
                runOnUiThread(() -> {
                    Toast.makeText(RBHActiveEmpListActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    showLoading(false);
                });
            }
        }).start();
    }
    
    private RBHEmployee parseEmployeeFromJson(JSONObject json) throws JSONException {
        RBHEmployee employee = new RBHEmployee();
        
        employee.setId(json.getString("id"));
        employee.setUsername(json.getString("username"));
        employee.setFirstName(json.getString("firstName"));
        employee.setLastName(json.getString("lastName"));
        employee.setMobile(json.optString("mobile", ""));
        employee.setEmail(json.optString("email_id", ""));
        employee.setEmployeeId(json.optString("employee_no", ""));
        employee.setDob(json.optString("dob", ""));
        employee.setJoiningDate(json.optString("joining_date", ""));
        employee.setStatus(json.optString("status", ""));
        employee.setReportingTo(json.optString("reportingTo", ""));
        employee.setOfficialPhone(json.optString("official_phone", ""));
        employee.setOfficialEmail(json.optString("official_email", ""));
        employee.setWorkState(json.optString("work_state", ""));
        employee.setWorkLocation(json.optString("work_location", ""));
        employee.setAliasName(json.optString("alias_name", ""));
        employee.setResidentialAddress(json.optString("residential_address", ""));
        employee.setOfficeAddress(json.optString("office_address", ""));
        employee.setPanNumber(json.optString("pan_number", ""));
        employee.setAadhaarNumber(json.optString("aadhaar_number", ""));
        employee.setAlternativeMobile(json.optString("alternative_mobile_number", ""));
        employee.setCompanyName(json.optString("company_name", ""));
        employee.setLastWorkingDate(json.optString("last_working_date", ""));
        employee.setLeavingReason(json.optString("leaving_reason", ""));
        employee.setReJoiningDate(json.optString("re_joining_date", ""));
        employee.setCreatedAt(json.optString("created_at", ""));
        employee.setUpdatedAt(json.optString("updated_at", ""));
        employee.setDesignation(json.optString("designation_name", ""));
        employee.setDepartment(json.optString("department_name", ""));
        employee.setBranchStateName(json.optString("branch_state_name", ""));
        employee.setBranchLocationName(json.optString("branch_location_name", ""));
        
        return employee;
    }
    
    private void updateUI(int totalCount, int activeCount) {
        totalEmployeesCount.setText(String.valueOf(totalCount));
        activeEmployeesCount.setText(String.valueOf(activeCount));
    }
    
    private void showLoading(boolean show) {
        loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        employeesRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
    }
    
    private void updateEmptyState() {
        if (filteredEmployeeList.isEmpty()) {
            if (employeeList.isEmpty()) {
                // No employees at all
                emptyStateLayout.setVisibility(View.VISIBLE);
                employeesRecyclerView.setVisibility(View.GONE);
            } else {
                // No search results
                emptyStateLayout.setVisibility(View.VISIBLE);
                employeesRecyclerView.setVisibility(View.GONE);
            }
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            employeesRecyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    private void showEmployeeDetails(RBHEmployee employee) {
        // Show employee details in a dialog or navigate to details activity
        Toast.makeText(this, "Employee: " + employee.getName(), Toast.LENGTH_SHORT).show();
        // TODO: Implement employee details view
    }
    
    @Override
    public void onBackPressed() {
        finish();
    }
} 