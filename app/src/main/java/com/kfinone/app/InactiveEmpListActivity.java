package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class InactiveEmpListActivity extends AppCompatActivity {
    private static final String TAG = "InactiveEmpListActivity";
    private RecyclerView recyclerView;
    private EmployeeAdapter adapter;
    private List<Employee> employees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inactive_emp_list);

        // Set up back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize with empty list
        employees = new ArrayList<>();
        adapter = new EmployeeAdapter(employees);
        recyclerView.setAdapter(adapter);

        // Fetch inactive users from server
        fetchInactiveUsersFromServer();
    }

    private void fetchInactiveUsersFromServer() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting to fetch inactive users from server...");
                URL url = new URL("https://emp.kfinone.com/mobile/api/debug_inactive_users.php");
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
                        Log.d(TAG, "Found " + data.length() + " inactive users in response");
                        
                        employees.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject user = data.getJSONObject(i);
                            employees.add(new Employee(
                                    user.getString("id"),
                                    user.getString("fullName"),
                                    user.getString("employeeNo"),
                                    user.getString("mobile"),
                                    user.getString("email"),
                                    user.getString("status")
                            ));
                        }
                        Log.d(TAG, "Added " + employees.size() + " inactive users to list");
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
                Log.e(TAG, "Exception in fetchInactiveUsersFromServer: " + e.getMessage(), e);
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

        Employee(String userId, String fullName, String employeeNo, String mobile, String email, String status) {
            this.userId = userId;
            this.fullName = fullName;
            this.employeeNo = employeeNo;
            this.mobile = mobile;
            this.email = email;
            this.status = status;
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

            ViewHolder(View view) {
                super(view);
                fullNameText = view.findViewById(R.id.fullNameText);
                empIdText = view.findViewById(R.id.empIdText);
                mobileText = view.findViewById(R.id.mobileText);
                emailText = view.findViewById(R.id.emailText);
                statusText = view.findViewById(R.id.statusText);
            }
        }
    }
} 
