package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
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

public class EmpDesignationActivity extends AppCompatActivity {
    private static final String TAG = "EmpDesignationActivity";
    private RecyclerView recyclerView;
    private DesignationAdapter adapter;
    private List<Designation> designationList;
    private AutoCompleteTextView departmentDropdown;
    private TextInputEditText designationNameInput;
    private MaterialButton submitButton;
    private String selectedDepartment;
    private List<String> departmentNames = new ArrayList<>();
    private List<String> departmentIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_designation);

        // Initialize views
        ImageButton backButton = findViewById(R.id.backButton);
        recyclerView = findViewById(R.id.designationRecyclerView);
        departmentDropdown = findViewById(R.id.departmentDropdown);
        designationNameInput = findViewById(R.id.designationNameInput);
        submitButton = findViewById(R.id.submitButton);

        // Setup back button
        backButton.setOnClickListener(v -> finish());

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        designationList = new ArrayList<>();
        adapter = new DesignationAdapter(designationList);
        recyclerView.setAdapter(adapter);

        // Fetch data from server
        fetchDepartmentsFromServer();
        fetchDesignationsFromServer();

        // Setup submit button
        submitButton.setOnClickListener(v -> {
            String designationName = designationNameInput.getText().toString().trim();
            if (!designationName.isEmpty()) {
                addDesignationToServer(designationName);
            } else {
                Toast.makeText(this, "Please enter designation name", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Add refresh functionality - you can add a refresh button in the layout
        // For now, let's add a long press on the submit button to refresh
        submitButton.setOnLongClickListener(v -> {
            fetchDesignationsFromServer();
            Toast.makeText(this, "Refreshing designations list...", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void fetchDepartmentsFromServer() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting to fetch departments from server...");
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_departments_dropdown.php");
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
                        
                        departmentNames.clear();
                        departmentIds.clear();
                        
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject dept = data.getJSONObject(i);
                            departmentNames.add(dept.getString("departmentName"));
                            departmentIds.add(dept.getString("id"));
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_dropdown_item_1line, departmentNames
                            );
                            departmentDropdown.setAdapter(adapter);
                            
                            // Set up item click listener to capture selected department
                            departmentDropdown.setOnItemClickListener((parent, view, position, id) -> {
                                selectedDepartment = departmentIds.get(position);
                                Log.d(TAG, "Selected department: " + departmentNames.get(position) + " (ID: " + selectedDepartment + ")");
                            });
                            
                            Log.d(TAG, "Loaded " + departmentNames.size() + " departments");
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

    private void addDesignationToServer(String designationName) {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/add_designation.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                String postData = "designation_name=" + designationName;
                if (selectedDepartment != null) {
                    postData += "&department_id=" + selectedDepartment;
                }
                conn.getOutputStream().write(postData.getBytes());

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
                            designationList.add(new Designation("", designationName, "N/A", "Active"));
                            adapter.notifyItemInserted(designationList.size() - 1);
                            designationNameInput.setText("");
                            departmentDropdown.setText("");
                            selectedDepartment = null;
                            Toast.makeText(this, "Designation added successfully", Toast.LENGTH_SHORT).show();
                            
                            // Refresh the list to show the new designation with proper department name
                            fetchDesignationsFromServer();
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

    private void deleteDesignationFromServer(String designationId, int position) {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/delete_designation.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                String postData = "designation_id=" + designationId;
                conn.getOutputStream().write(postData.getBytes());

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
                            // Remove from local list
                            designationList.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, designationList.size());
                            Toast.makeText(this, "Designation deleted successfully", Toast.LENGTH_SHORT).show();
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

    private void fetchDesignationsFromServer() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting to fetch designations from server...");
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_designations_with_departments.php");
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
                        Log.d(TAG, "Found " + data.length() + " designations in response");
                        
                        designationList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject desig = data.getJSONObject(i);
                            String designationId = desig.optString("id", "");
                            String designationName = desig.optString("designation_name", "");
                            String status = desig.optString("status", "Active");
                            String departmentName = desig.optString("department_name", "N/A");
                            
                            designationList.add(new Designation(
                                    designationId,
                                    designationName,
                                    departmentName,
                                    status
                            ));
                        }
                        Log.d(TAG, "Added " + designationList.size() + " designations to list");
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Adapter notified of data change");
                            
                            // Show success message with count
                            if (designationList.size() > 0) {
                                Toast.makeText(this, "Loaded " + designationList.size() + " designations", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "No designations found", Toast.LENGTH_SHORT).show();
                            }
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
                Log.e(TAG, "Exception in fetchDesignationsFromServer: " + e.getMessage(), e);
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private static class Designation {
        String id;
        String name;
        String department;
        String status;

        Designation(String id, String name, String department, String status) {
            this.id = id;
            this.name = name;
            this.department = department;
            this.status = status;
        }
    }

    private class DesignationAdapter extends RecyclerView.Adapter<DesignationAdapter.ViewHolder> {
        private List<Designation> designations;

        DesignationAdapter(List<Designation> designations) {
            this.designations = designations;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_designation, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Designation designation = designations.get(position);
            holder.nameText.setText(designation.name);
            holder.departmentText.setText(designation.department);
            holder.statusText.setText(designation.status);

            holder.editButton.setOnClickListener(v -> {
                // TODO: Implement edit functionality
                Toast.makeText(EmpDesignationActivity.this, "Edit clicked for " + designation.name, Toast.LENGTH_SHORT).show();
            });

            holder.deleteButton.setOnClickListener(v -> {
                // Show confirmation dialog
                new android.app.AlertDialog.Builder(EmpDesignationActivity.this)
                    .setTitle("Delete Designation")
                    .setMessage("Are you sure you want to delete '" + designation.name + "'?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deleteDesignationFromServer(designation.id, position);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            });
        }

        @Override
        public int getItemCount() {
            return designations.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText, departmentText, statusText;
            ImageButton editButton, deleteButton;

            ViewHolder(View view) {
                super(view);
                nameText = view.findViewById(R.id.nameText);
                departmentText = view.findViewById(R.id.departmentText);
                statusText = view.findViewById(R.id.statusText);
                editButton = view.findViewById(R.id.editButton);
                deleteButton = view.findViewById(R.id.deleteButton);
            }
        }
    }
} 
