package com.kfinone.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.widget.LinearLayout;

public class ManagingDirectorActiveEmpListActivity extends AppCompatActivity {
    private static final String TAG = "ManagingDirectorActiveEmpList";

    // UI Elements
    private RecyclerView employeeRecyclerView;
    private TextView noDataMessage;
    private LinearLayout statisticsText;
    
    // Data
    private List<ActiveEmployee> employeeList;
    private ActiveEmployeeAdapter employeeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managing_director_active_emp_list);

        initializeViews();
        setupToolbar();
        loadActiveEmployeeList();
    }

    private void initializeViews() {
        employeeRecyclerView = findViewById(R.id.employeeRecyclerView);
        noDataMessage = findViewById(R.id.noDataMessage);
        statisticsText = findViewById(R.id.statisticsText);
        
        // Initialize employee list and adapter
        employeeList = new ArrayList<>();
        employeeAdapter = new ActiveEmployeeAdapter(employeeList, this);
        
        // Setup RecyclerView
        employeeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        employeeRecyclerView.setAdapter(employeeAdapter);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Active Employee List");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void loadActiveEmployeeList() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Loading Managing Director active employee list...");
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_managing_director_active_emp_list.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Managing Director active emp list response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d(TAG, "Managing Director active emp list response: " + responseString);

                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        JSONObject statistics = json.getJSONObject("statistics");
                        
                        runOnUiThread(() -> {
                            displayEmployeeList(data, statistics);
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            showError("Error loading employee list: " + errorMsg);
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        showError("Server error: " + responseCode);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception loading Managing Director active emp list: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    showError("Error: " + e.getMessage());
                });
            }
        }).start();
    }

    private void displayEmployeeList(JSONArray data, JSONObject statistics) {
        try {
            employeeList.clear();
            
            for (int i = 0; i < data.length(); i++) {
                JSONObject employee = data.getJSONObject(i);
                ActiveEmployee activeEmployee = new ActiveEmployee(
                    employee.getString("id"),
                    employee.getString("firstName"),
                    employee.getString("lastName"),
                    employee.getString("employee_no"),
                    employee.getString("mobile"),
                    employee.getString("email_id"),
                    employee.getString("manage_icons"),
                    employee.getString("designation_name"),
                    employee.getString("department_name"),
                    employee.getString("status"),
                    employee.getString("created_at")
                );
                employeeList.add(activeEmployee);
            }
            
            employeeAdapter.notifyDataSetChanged();
            
            // Update statistics
            if (statistics != null) {
                int totalMembers = statistics.optInt("total_team_members", 0);
                int activeMembers = statistics.optInt("active_members", 0);
                
                // Find the TextView inside the statistics LinearLayout
                TextView statsTextView = statisticsText.findViewById(R.id.statsTextView);
                if (statsTextView != null) {
                    statsTextView.setText("Total: " + totalMembers + " | Active: " + activeMembers);
                }
            }
            
            if (employeeList.isEmpty()) {
                showNoDataMessage();
            } else {
                showEmployeeList();
                Toast.makeText(this, "Loaded " + employeeList.size() + " active employees", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing employee data: " + e.getMessage(), e);
            showError("Error parsing employee data");
        }
    }

    private void showEmployeeList() {
        employeeRecyclerView.setVisibility(View.VISIBLE);
        noDataMessage.setVisibility(View.GONE);
        statisticsText.setVisibility(View.VISIBLE);
    }

    private void showNoDataMessage() {
        employeeRecyclerView.setVisibility(View.GONE);
        noDataMessage.setVisibility(View.VISIBLE);
        statisticsText.setVisibility(View.GONE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        showNoDataMessage();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    // Active Employee data class
    public static class ActiveEmployee {
        private String id;
        private String firstName;
        private String lastName;
        private String employeeNo;
        private String mobile;
        private String email;
        private String manageIcons;
        private String designation;
        private String department;
        private String status;
        private String createdAt;

        public ActiveEmployee(String id, String firstName, String lastName, String employeeNo,
                           String mobile, String email, String manageIcons, String designation, 
                           String department, String status, String createdAt) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.employeeNo = employeeNo;
            this.mobile = mobile;
            this.email = email;
            this.manageIcons = manageIcons;
            this.designation = designation;
            this.department = department;
            this.status = status;
            this.createdAt = createdAt;
        }

        // Getters
        public String getId() { return id; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmployeeNo() { return employeeNo; }
        public String getMobile() { return mobile; }
        public String getEmail() { return email; }
        public String getManageIcons() { return manageIcons; }
        public String getDesignation() { return designation; }
        public String getDepartment() { return department; }
        public String getStatus() { return status; }
        public String getCreatedAt() { return createdAt; }
        public String getFullName() { return firstName + " " + lastName; }
    }
}
