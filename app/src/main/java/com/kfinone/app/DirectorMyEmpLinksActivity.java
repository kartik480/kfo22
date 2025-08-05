package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.android.volley.toolbox.StringRequest;

public class DirectorMyEmpLinksActivity extends AppCompatActivity {
    
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;
    private RecyclerView recyclerView;
    private EmployeeAdapter adapter;
    private List<Employee> employeeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_my_emp_links);

        // Get user data from intent
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            userName = intent.getStringExtra("USERNAME");
            firstName = intent.getStringExtra("FIRST_NAME");
            lastName = intent.getStringExtra("LAST_NAME");
        }

        setupToolbar();
        recyclerView = findViewById(R.id.recyclerView);
        employeeList = new ArrayList<>(); // Start with empty list
        adapter = new EmployeeAdapter(employeeList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fetchEmployeeData();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Director - Employees Reporting to ID 11");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }

    private void fetchEmployeeData() {
        String url = "https://emp.kfinone.com/mobile/api/Director.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray usersArray = data.getJSONArray("users_reporting_to_11");
                            employeeList.clear();
                            
                            for (int i = 0; i < usersArray.length(); i++) {
                                JSONObject obj = usersArray.getJSONObject(i);
                                String fullName = obj.optString("firstName", "") + " " + obj.optString("lastName", "");
                                String employeeId = obj.optString("employee_no", "");
                                String mobile = obj.optString("mobile", "");
                                String email = obj.optString("email_id", "");
                                String status = obj.optString("status", "");
                                String username = obj.optString("username", "");
                                
                                employeeList.add(new Employee(fullName, employeeId, mobile, email, status, username));
                            }
                            adapter.notifyDataSetChanged();
                            
                            // Show manager details if available
                            if (data.has("manager_details")) {
                                JSONObject manager = data.getJSONObject("manager_details");
                                String managerName = manager.optString("firstName", "") + " " + manager.optString("lastName", "");
                                Toast.makeText(DirectorMyEmpLinksActivity.this, 
                                    "Showing employees reporting to: " + managerName, 
                                    Toast.LENGTH_LONG).show();
                            }
                        } else {
                            String errorMessage = response.optString("message", "No employees found");
                            Toast.makeText(DirectorMyEmpLinksActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DirectorMyEmpLinksActivity.this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(DirectorMyEmpLinksActivity.this, "Failed to fetch employees: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        );
        queue.add(jsonObjectRequest);
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

    // Employee data class
    private static class Employee {
        String fullName;
        String employeeId;
        String mobile;
        String email;
        String status;
        String username;
        
        Employee(String fullName, String employeeId, String mobile, String email, String status, String username) {
            this.fullName = fullName;
            this.employeeId = employeeId;
            this.mobile = mobile;
            this.email = email;
            this.status = status;
            this.username = username;
        }
    }

    // RecyclerView Adapter
    private class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {
        private List<Employee> employees;
        EmployeeAdapter(List<Employee> employees) {
            this.employees = employees;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_director_emp_list, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Employee emp = employees.get(position);
            holder.fullNameText.setText("Name: " + emp.fullName);
            holder.employeeIdText.setText("Employee ID: " + emp.employeeId);
            holder.mobileText.setText("Phone: " + emp.mobile);
            holder.emailText.setText("Email: " + emp.email);
            holder.passwordText.setText("Status: " + emp.status + " | Username: " + emp.username);
            holder.actionButton.setText("View Details");
            holder.actionButton.setOnClickListener(v -> {
                Toast.makeText(holder.itemView.getContext(), 
                    "Employee: " + emp.fullName + "\nStatus: " + emp.status + "\nUsername: " + emp.username, 
                    Toast.LENGTH_LONG).show();
            });
        }
        @Override
        public int getItemCount() {
            return employees.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView fullNameText, employeeIdText, mobileText, emailText, passwordText;
            android.widget.Button actionButton;
            ViewHolder(View itemView) {
                super(itemView);
                fullNameText = itemView.findViewById(R.id.fullNameText);
                employeeIdText = itemView.findViewById(R.id.employeeIdText);
                mobileText = itemView.findViewById(R.id.mobileText);
                emailText = itemView.findViewById(R.id.emailText);
                passwordText = itemView.findViewById(R.id.passwordText);
                actionButton = itemView.findViewById(R.id.actionButton);
            }
        }
    }
} 