package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DirectorSdsaTeamNewActivity extends AppCompatActivity {
    private static final String TAG = "DirectorSdsaTeamNew";

    // UI Elements
    private AutoCompleteTextView selectUserDropdown;
    private MaterialButton showDataButton;
    private MaterialButton resetButton;
    private LinearLayout dataDisplayArea;
    private RecyclerView teamDataRecyclerView;
    private TeamDataAdapter adapter;
    private List<TeamDataItem> teamDataList;

    // Data
    private List<String> userDisplayList = new ArrayList<>();
    private List<String> userIdList = new ArrayList<>();
    private String selectedUserId = "";

    // Executor for background tasks
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_sdsa_team_new);

        initializeViews();
        setupClickListeners();
        fetchUsersForDropdown();
    }

    private void initializeViews() {
        // Initialize UI elements
        selectUserDropdown = findViewById(R.id.selectUserDropdown);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);
        dataDisplayArea = findViewById(R.id.dataDisplayArea);
        teamDataRecyclerView = findViewById(R.id.teamDataRecyclerView);

        // Initialize RecyclerView
        teamDataList = new ArrayList<>();
        adapter = new TeamDataAdapter(teamDataList);
        teamDataRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamDataRecyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Back button
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Show Data button
        showDataButton.setOnClickListener(v -> {
            if (selectedUserId.isEmpty()) {
                Toast.makeText(this, "Please select a user first", Toast.LENGTH_SHORT).show();
                return;
            }
            loadTeamData(selectedUserId);
        });

        // Reset button
        resetButton.setOnClickListener(v -> {
            selectUserDropdown.setText("");
            selectedUserId = "";
            dataDisplayArea.setVisibility(View.GONE);
            teamDataList.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Reset completed", Toast.LENGTH_SHORT).show();
        });
    }

    private void fetchUsersForDropdown() {
        executor.execute(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_reporting_users_dropdown.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        userDisplayList.clear();
                        userIdList.clear();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject userObj = dataArray.getJSONObject(i);
                            String userName = userObj.getString("name");
                            String userId = userObj.getString("id");
                            userDisplayList.add(userName + " (ID: " + userId + ")");
                            userIdList.add(userId);
                        }
                        
                        runOnUiThread(() -> {
                            ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(
                                this, android.R.layout.simple_dropdown_item_1line, userDisplayList
                            );
                            selectUserDropdown.setAdapter(dropdownAdapter);
                            
                            // Set item click listener to capture selected user ID
                            selectUserDropdown.setOnItemClickListener((parent, view, position, id) -> {
                                selectedUserId = userIdList.get(position);
                                Log.d(TAG, "Selected user ID: " + selectedUserId);
                            });
                        });
                    }
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching users for dropdown", e);
                runOnUiThread(() -> Toast.makeText(this, "Error loading users: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadTeamData(String userId) {
        executor.execute(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_sdsa_users_reporting_to_11.php?userId=" + userId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray dataArray = jsonResponse.getJSONArray("data");
                        teamDataList.clear();
                        
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject item = dataArray.getJSONObject(i);
                            teamDataList.add(new TeamDataItem(
                                item.optString("full_name", ""),
                                item.optString("phone_number", ""),
                                item.optString("email_id", ""),
                                item.optString("employee_no", ""),
                                item.optString("department", ""),
                                item.optString("designation", "")
                            ));
                        }
                        
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            dataDisplayArea.setVisibility(View.VISIBLE);
                            if (teamDataList.isEmpty()) {
                                Toast.makeText(this, "No team data found for selected user", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Failed to load team data", Toast.LENGTH_SHORT).show());
                }
                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error loading team data", e);
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private static class TeamDataItem {
        String name, phone, email, employeeNo, department, designation;
        
        TeamDataItem(String name, String phone, String email, String employeeNo, String department, String designation) {
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.employeeNo = employeeNo;
            this.department = department;
            this.designation = designation;
        }
    }

    private class TeamDataAdapter extends RecyclerView.Adapter<TeamDataAdapter.ViewHolder> {
        private List<TeamDataItem> items;
        
        TeamDataAdapter(List<TeamDataItem> items) {
            this.items = items;
        }
        
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_data, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TeamDataItem item = items.get(position);
            holder.nameText.setText("Name: " + item.name);
            holder.phoneText.setText("Phone: " + item.phone);
            holder.emailText.setText("Email: " + item.email);
            holder.employeeText.setText("Employee No: " + item.employeeNo);
            holder.departmentText.setText("Department: " + item.department);
            holder.designationText.setText("Designation: " + item.designation);
        }
        
        @Override
        public int getItemCount() {
            return items.size();
        }
        
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText, phoneText, emailText, employeeText, departmentText, designationText;
            
            ViewHolder(View view) {
                super(view);
                nameText = view.findViewById(R.id.nameText);
                phoneText = view.findViewById(R.id.phoneText);
                emailText = view.findViewById(R.id.emailText);
                employeeText = view.findViewById(R.id.employeeText);
                departmentText = view.findViewById(R.id.departmentText);
                designationText = view.findViewById(R.id.designationText);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
} 