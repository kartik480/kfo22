package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EmpDepartmentActivity extends AppCompatActivity {
    private static final String TAG = "EmpDepartmentActivity";
    private RecyclerView recyclerView;
    private DepartmentAdapter adapter;
    private List<Department> departmentList;
    private TextInputEditText departmentNameInput;
    private MaterialButton submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_department);

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        recyclerView = findViewById(R.id.departmentRecyclerView);
        departmentNameInput = findViewById(R.id.departmentNameInput);
        submitButton = findViewById(R.id.submitButton);

        // Setup back button
        backButton.setOnClickListener(v -> finish());

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        departmentList = new ArrayList<>();
        adapter = new DepartmentAdapter(departmentList);
        recyclerView.setAdapter(adapter);

        // Fetch departments from server
        fetchDepartmentsFromServer();

        // Setup submit button
        submitButton.setOnClickListener(v -> {
            String departmentName = departmentNameInput.getText().toString().trim();
            if (!departmentName.isEmpty()) {
                addDepartmentToServer(departmentName);
            } else {
                Toast.makeText(this, "Please enter department name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDepartmentsFromServer() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting to fetch departments from server...");
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_department_list_simple.php");
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
                        Log.d(TAG, "Found " + data.length() + " departments in response");
                        
                        departmentList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject dept = data.getJSONObject(i);
                            departmentList.add(new Department(
                                    dept.getString("department_name"),
                                    "Active"
                            ));
                        }
                        Log.d(TAG, "Added " + departmentList.size() + " departments to list");
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
                Log.e(TAG, "Exception in fetchDepartmentsFromServer: " + e.getMessage(), e);
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void addDepartmentToServer(String departmentName) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Adding department: " + departmentName);
                URL url = new URL("https://emp.kfinone.com/mobile/api/add_department.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                // Send JSON data
                String jsonInput = "{\"departmentName\":\"" + departmentName + "\"}";
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
                            // Add to local list
                            departmentList.add(new Department(departmentName, "Active"));
                            adapter.notifyItemInserted(departmentList.size() - 1);
                            departmentNameInput.setText("");
                            Toast.makeText(this, "Department added successfully", Toast.LENGTH_SHORT).show();
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

    private static class Department {
        String name;
        String status;

        Department(String name, String status) {
            this.name = name;
            this.status = status;
        }
    }

    private class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHolder> {
        private List<Department> departments;

        DepartmentAdapter(List<Department> departments) {
            this.departments = departments;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_department, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Department department = departments.get(position);
            holder.nameText.setText(department.name);
            holder.statusText.setText(department.status);

            holder.editButton.setOnClickListener(v -> {
                // TODO: Implement edit functionality
                Toast.makeText(EmpDepartmentActivity.this, "Edit clicked for " + department.name, Toast.LENGTH_SHORT).show();
            });

            holder.deleteButton.setOnClickListener(v -> {
                // TODO: Implement delete functionality
                Toast.makeText(EmpDepartmentActivity.this, "Delete clicked for " + department.name, Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return departments.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText;
            TextView statusText;
            ImageButton editButton;
            ImageButton deleteButton;

            ViewHolder(View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.nameText);
                statusText = itemView.findViewById(R.id.statusText);
                editButton = itemView.findViewById(R.id.editButton);
                deleteButton = itemView.findViewById(R.id.deleteButton);
            }
        }
    }
} 
