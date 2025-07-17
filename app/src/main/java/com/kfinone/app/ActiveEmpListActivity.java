package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class ActiveEmpListActivity extends AppCompatActivity {
    private static final String TAG = "ActiveEmpListActivity";
    private RecyclerView recyclerView;
    private EmployeeAdapter adapter;
    private List<Employee> employeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_emp_list);

        Log.d(TAG, "Activity created");

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.employeeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        employeeList = new ArrayList<>();
        adapter = new EmployeeAdapter(employeeList);
        recyclerView.setAdapter(adapter);

        // Fetch real users from server
        fetchUsersFromServer();

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void fetchUsersFromServer() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting to fetch users from server...");
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_active_users_fixed.php");
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
                        Log.d(TAG, "Found " + data.length() + " users in response");
                        
                        employeeList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject user = data.getJSONObject(i);
                            Employee employee = new Employee(
                                    user.getString("id"),
                                    user.getString("fullName"),
                                    user.getString("employeeNo"),
                                    user.getString("mobile"),
                                    user.getString("email"),
                                    user.getString("status")
                            );
                            
                            // Set permission values if available
                            if (user.has("emp_manage_permission")) {
                                employee.empManagePermission = user.getString("emp_manage_permission");
                            }
                            if (user.has("emp_data_permission")) {
                                employee.empDataPermission = user.getString("emp_data_permission");
                            }
                            if (user.has("emp_work_permission")) {
                                employee.empWorkPermission = user.getString("emp_work_permission");
                            }
                            if (user.has("payout_permission")) {
                                employee.payoutPermission = user.getString("payout_permission");
                            }
                            
                            employeeList.add(employee);
                        }
                        Log.d(TAG, "Added " + employeeList.size() + " users to list");
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Adapter notified of data change");
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_LONG).show());
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> Toast.makeText(this, "Server error: " + responseCode, Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception in fetchUsersFromServer: " + e.getMessage(), e);
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void handlePermissionAction(Employee employee, String selectedPermission, Spinner spinner) {
        // Reset spinner to "Select Action"
        spinner.setSelection(0);
        
        // Handle different permission actions
        switch (selectedPermission) {
            case "Emp Manage Permission":
                // Launch Manage Icon Permission Activity
                Intent intent = new Intent(ActiveEmpListActivity.this, ManageIconPermissionActivity.class);
                intent.putExtra("userId", employee.userId);
                intent.putExtra("userName", employee.fullName);
                startActivity(intent);
                break;
            case "Emp Data Permission":
                // Launch Data Icon Permission Activity
                Intent dataIntent = new Intent(ActiveEmpListActivity.this, DataIconPermissionActivity.class);
                dataIntent.putExtra("userId", employee.userId);
                dataIntent.putExtra("userName", employee.fullName);
                startActivity(dataIntent);
                break;
            case "Emp Work Permission":
                // Launch Work Icon Permission Activity
                Intent workIntent = new Intent(ActiveEmpListActivity.this, WorkIconPermissionActivity.class);
                workIntent.putExtra("userId", employee.userId);
                workIntent.putExtra("userName", employee.fullName);
                startActivity(workIntent);
                break;
            case "Payout Permission":
                updateEmployeePermission(employee.userId, "payout_permission", "Yes");
                Toast.makeText(this, "Payout Permission granted to " + employee.fullName, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void updateEmployeePermission(String userId, String permissionType, String permissionValue) {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/update_user_permission.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                // Send the user ID, permission type, and permission value
                String jsonInput = "{\"userId\":\"" + userId + "\",\"permissionType\":\"" + permissionType + "\",\"permissionValue\":\"" + permissionValue + "\"}";
                try (java.io.OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    JSONObject json = new JSONObject(response.toString());
                    if (json.getString("status").equals("success")) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Permission updated successfully", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Error: " + json.optString("message"), Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Server error: " + responseCode, Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void updateUserStatus(String userId, String newStatus, int position) {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/update_user_status.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                // Send the user ID and new status
                String jsonInput = "{\"userId\":\"" + userId + "\",\"status\":\"" + newStatus + "\"}";
                try (java.io.OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    JSONObject json = new JSONObject(response.toString());
                    if (json.getString("status").equals("success")) {
                        runOnUiThread(() -> {
                            // Remove the user from the list
                            employeeList.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, employeeList.size());
                            Toast.makeText(this, "User moved to inactive list", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Error: " + json.optString("message"), Toast.LENGTH_LONG).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Server error: " + responseCode, Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private static class Employee {
        String userId;
        String fullName;
        String employeeNo;
        String mobile;
        String email;
        String status;
        String empManagePermission;
        String empDataPermission;
        String empWorkPermission;
        String payoutPermission;

        Employee(String userId, String fullName, String employeeNo, String mobile, String email, String status) {
            this.userId = userId;
            this.fullName = fullName;
            this.employeeNo = employeeNo;
            this.mobile = mobile;
            this.email = email;
            this.status = status;
            this.empManagePermission = "No";
            this.empDataPermission = "No";
            this.empWorkPermission = "No";
            this.payoutPermission = "No";
        }
    }

    private class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {
        private List<Employee> employees;

        EmployeeAdapter(List<Employee> employees) {
            this.employees = employees;
            Log.d(TAG, "Adapter created with " + employees.size() + " employees");
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_table, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Employee employee = employees.get(position);
            holder.fullNameText.setText(employee.fullName);
            holder.empIdText.setText(employee.employeeNo);
            holder.mobileText.setText(employee.mobile);
            holder.emailText.setText(employee.email);
            holder.statusText.setText(employee.status);

            // Display current permissions status
            String currentPermissions = "Permissions: ";
            if ("Yes".equals(employee.empManagePermission)) currentPermissions += "Manage ";
            if ("Yes".equals(employee.empDataPermission)) currentPermissions += "Data ";
            if ("Yes".equals(employee.empWorkPermission)) currentPermissions += "Work ";
            if ("Yes".equals(employee.payoutPermission)) currentPermissions += "Payout ";
            if (currentPermissions.equals("Permissions: ")) {
                currentPermissions = "Permissions: None";
            }
            holder.permissionsText.setText(currentPermissions);

            // Set up the action spinner with permission options
            String[] permissionOptions = {
                "Select Action",
                "Emp Manage Permission",
                "Emp Data Permission", 
                "Emp Work Permission",
                "Payout Permission"
            };
            
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                holder.itemView.getContext(),
                android.R.layout.simple_spinner_item,
                permissionOptions
            );
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.actionSpinner.setAdapter(spinnerAdapter);
            
            // Set spinner selection listener
            holder.actionSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int spinnerPosition, long id) {
                    if (spinnerPosition > 0) { // Skip "Select Action"
                        String selectedPermission = permissionOptions[spinnerPosition];
                        handlePermissionAction(employee, selectedPermission, holder.actionSpinner);
                    }
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    // Do nothing
                }
            });

            holder.editButton.setOnClickListener(v -> {
                Toast.makeText(ActiveEmpListActivity.this, 
                    "Edit " + employee.fullName, Toast.LENGTH_SHORT).show();
            });

            holder.deleteButton.setOnClickListener(v -> {
                updateUserStatus(employee.userId, "Inactive", position);
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
            TextView statusText;
            TextView permissionsText;
            Spinner actionSpinner;
            ImageButton editButton;
            ImageButton deleteButton;

            ViewHolder(View view) {
                super(view);
                fullNameText = view.findViewById(R.id.fullNameText);
                empIdText = view.findViewById(R.id.empIdText);
                mobileText = view.findViewById(R.id.mobileText);
                emailText = view.findViewById(R.id.emailText);
                statusText = view.findViewById(R.id.statusText);
                permissionsText = view.findViewById(R.id.permissionsText);
                actionSpinner = view.findViewById(R.id.actionSpinner);
                editButton = view.findViewById(R.id.editButton);
                deleteButton = view.findViewById(R.id.deleteButton);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Check if we came from Director panel
        String sourcePanel = getIntent().getStringExtra("SOURCE_PANEL");
        if ("DIRECTOR_PANEL".equals(sourcePanel)) {
            // Navigate back to Director Employee Activity
            Intent intent = new Intent(this, DirectorEmployeeActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        } else {
            // Default behavior
            super.onBackPressed();
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
} 
