package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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

public class MyEmpActivity extends AppCompatActivity {
    private static final String TAG = "MyEmpActivity";

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // RecyclerView for employee list
    private RecyclerView recyclerView;
    private EmployeeAdapter adapter;
    private List<Employee> employeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_emp);

        initializeViews();
        setupClickListeners();
        fetchEmployeeData();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.employeeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        employeeList = new ArrayList<>();
        adapter = new EmployeeAdapter(employeeList);
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewEmployee());
    }

    private void goBack() {
        // Go back to employee panel
        Intent intent = new Intent(this, EmployeePanelActivity.class);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing employee list...", Toast.LENGTH_SHORT).show();
        fetchEmployeeData();
    }

    private void fetchEmployeeData() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting to fetch employee data from server...");
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_emp_panel_users.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);
                
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
                        JSONArray data = json.getJSONArray("data");
                        Log.d(TAG, "Found " + data.length() + " employees in response");
                        
                        employeeList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject employee = data.getJSONObject(i);
                            employeeList.add(new Employee(
                                    employee.getString("id"),
                                    employee.getString("fullName"),
                                    employee.getString("employee_no"),
                                    employee.getString("mobile"),
                                    employee.getString("email_id"),
                                    employee.getString("designation_name")
                            ));
                        }
                        
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Updated UI with " + employeeList.size() + " employees");
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception in fetchEmployeeData: " + e.getMessage(), e);
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void addNewEmployee() {
        Toast.makeText(this, "Add New Employee - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add Employee activity
    }

    private void editEmployee(String empId, String empName) {
        Toast.makeText(this, "Edit " + empName + " (" + empId + ") - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Edit Employee activity
    }

    private void deleteEmployee(String empId, String empName) {
        Toast.makeText(this, "Delete " + empName + " (" + empId + ") - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Show confirmation dialog and delete employee
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    // Employee data class
    private static class Employee {
        String id;
        String fullName;
        String employeeNo;
        String mobile;
        String email;
        String designation;

        Employee(String id, String fullName, String employeeNo, String mobile, String email, String designation) {
            this.id = id;
            this.fullName = fullName;
            this.employeeNo = employeeNo;
            this.mobile = mobile;
            this.email = email;
            this.designation = designation;
        }
    }

    // Employee adapter for RecyclerView
    private class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {
        private List<Employee> employees;

        EmployeeAdapter(List<Employee> employees) {
            this.employees = employees;
            Log.d(TAG, "Adapter created with " + employees.size() + " employees");
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_employee, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Employee employee = employees.get(position);
            holder.fullNameText.setText(employee.fullName);
            holder.empIdText.setText(employee.employeeNo);
            holder.mobileText.setText(employee.mobile);
            holder.emailText.setText(employee.email);
            holder.designationText.setText(employee.designation);

            holder.editButton.setOnClickListener(v -> {
                Toast.makeText(MyEmpActivity.this, 
                    "Edit " + employee.fullName, Toast.LENGTH_SHORT).show();
            });

            holder.deleteButton.setOnClickListener(v -> {
                Toast.makeText(MyEmpActivity.this, 
                    "Delete " + employee.fullName, Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return employees.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView fullNameText;
            TextView empIdText;
            TextView mobileText;
            TextView emailText;
            TextView designationText;
            Button editButton;
            Button deleteButton;

            ViewHolder(View view) {
                super(view);
                fullNameText = view.findViewById(R.id.fullNameText);
                empIdText = view.findViewById(R.id.empIdText);
                mobileText = view.findViewById(R.id.mobileText);
                emailText = view.findViewById(R.id.emailText);
                designationText = view.findViewById(R.id.designationText);
                editButton = view.findViewById(R.id.editButton);
                deleteButton = view.findViewById(R.id.deleteButton);
            }
        }
    }
} 