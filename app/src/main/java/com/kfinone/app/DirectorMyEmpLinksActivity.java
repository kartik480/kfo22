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
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Director My Employee Links");
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Navigate back to Director Employee Links Activity
        Intent intent = new Intent(this, DirectorEmpLinksActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    // Employee data class
    private static class Employee {
        String fullName;
        String employeeId;
        String mobile;
        String email;
        Employee(String fullName, String employeeId, String mobile, String email) {
            this.fullName = fullName;
            this.employeeId = employeeId;
            this.mobile = mobile;
            this.email = email;
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
            // No data to bind for now (empty state)
        }
        @Override
        public int getItemCount() {
            return employees.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
} 