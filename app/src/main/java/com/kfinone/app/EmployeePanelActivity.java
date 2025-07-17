package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmployeePanelActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout employeeButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // Employee boxes
    private LinearLayout myEmpBox;
    private LinearLayout empTeamBox;

    // Count displays
    private TextView myEmpCount;
    private TextView empTeamCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_panel);

        initializeViews();
        setupClickListeners();
        loadEmployeeData();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);

        // Bottom navigation
        dashboardButton = findViewById(R.id.dashboardButton);
        employeeButton = findViewById(R.id.employeeButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);

        // Employee boxes
        myEmpBox = findViewById(R.id.myEmpBox);
        empTeamBox = findViewById(R.id.empTeamBox);

        // Count displays
        myEmpCount = findViewById(R.id.myEmpCount);
        empTeamCount = findViewById(R.id.empTeamCount);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewEmployee());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SpecialPanelActivity.class);
            startActivity(intent);
            finish();
        });

        employeeButton.setOnClickListener(v -> {
            // Already on employee page, just show feedback
            Toast.makeText(this, "Employee Management", Toast.LENGTH_SHORT).show();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Reports activity
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Settings activity
        });

        // Employee box click listeners
        myEmpBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyEmpActivity.class);
            startActivity(intent);
        });

        empTeamBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmpTeamActivity.class);
            startActivity(intent);
        });
    }

    private void loadEmployeeData() {
        // Fetch real employee data from server
        fetchEmployeeDataFromServer();
    }

    private void fetchEmployeeDataFromServer() {
        new Thread(() -> {
            try {
                Log.d("EmployeePanel", "Starting to fetch employee data from server...");
                
                // Try the employee panel endpoint directly
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_emp_panel_users.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d("EmployeePanel", "Employee panel endpoint response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d("EmployeePanel", "Server response: " + responseString);

                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        Log.d("EmployeePanel", "Found " + data.length() + " employees in response");
                        
                        // Count employees by designation
                        int chiefBusinessOfficers = 0;
                        int regionalBusinessHeads = 0;
                        int directors = 0;
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject employee = data.getJSONObject(i);
                            String designation = employee.getString("designation_name");
                            
                            switch (designation) {
                                case "Chief Business Officer":
                                    chiefBusinessOfficers++;
                                    break;
                                case "Regional Business Head":
                                    regionalBusinessHeads++;
                                    break;
                                case "Director":
                                    directors++;
                                    break;
                            }
                        }
                        
                        final int totalEmployees = data.length();
                        final int totalTeams = chiefBusinessOfficers + regionalBusinessHeads + directors;
                        
                        runOnUiThread(() -> {
                            myEmpCount.setText(totalEmployees + " employees");
                            empTeamCount.setText(totalTeams + " teams");
                            Log.d("EmployeePanel", "Updated UI with " + totalEmployees + " employees and " + totalTeams + " teams");
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e("EmployeePanel", "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                            // Set default values on error
                            myEmpCount.setText("0 employees");
                            empTeamCount.setText("0 teams");
                        });
                    }
                } else if (responseCode == 404) {
                    // API endpoint not found - use fallback data
                    Log.w("EmployeePanel", "API endpoint not found (404). Using fallback data.");
                    runOnUiThread(() -> {
                        Toast.makeText(this, "API not available yet. Using sample data.", Toast.LENGTH_LONG).show();
                        // Set fallback values
                        myEmpCount.setText("5 employees");
                        empTeamCount.setText("3 teams");
                    });
                } else {
                    Log.e("EmployeePanel", "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                        // Set default values on error
                        myEmpCount.setText("0 employees");
                        empTeamCount.setText("0 teams");
                    });
                }
            } catch (Exception e) {
                Log.e("EmployeePanel", "Exception in fetchEmployeeDataFromServer: " + e.getMessage(), e);
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    // Set default values on error
                    myEmpCount.setText("0 employees");
                    empTeamCount.setText("0 teams");
                });
            }
        }).start();
    }

    private void goBack() {
        // Go back to special panel
        Intent intent = new Intent(this, SpecialPanelActivity.class);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing employee data...", Toast.LENGTH_SHORT).show();
        fetchEmployeeDataFromServer();
    }

    private void addNewEmployee() {
        Toast.makeText(this, "Add New Employee - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add Employee activity
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
} 