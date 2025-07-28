package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class MySdsaActivity extends AppCompatActivity {
    private static final String TAG = "MySdsaActivity";

    // UI Elements
    private RecyclerView sdsaRecyclerView;
    private SdsaAdapter adapter;
    private List<SdsaItem> sdsaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sdsa);

        initializeViews();
        setupClickListeners();
        loadSdsaData();
    }

    private void initializeViews() {
        // Initialize RecyclerView
        sdsaRecyclerView = findViewById(R.id.sdsaRecyclerView);
        sdsaRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        sdsaList = new ArrayList<>();
        adapter = new SdsaAdapter(sdsaList);
        sdsaRecyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
        });
    }

    private void loadSdsaData() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Loading SDSA data for K RAJESH KUMAR's team...");
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_my_sdsa_users.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "SDSA data response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d(TAG, "SDSA data response: " + responseString);

                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        
                        sdsaList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject sdsa = data.getJSONObject(i);
                            sdsaList.add(new SdsaItem(
                                sdsa.optString("id", ""),
                                sdsa.optString("username", ""),
                                sdsa.optString("first_name", ""),
                                sdsa.optString("last_name", ""),
                                sdsa.optString("full_name", ""),
                                sdsa.optString("phone_number", ""),
                                sdsa.optString("email_id", ""),
                                sdsa.optString("employee_no", ""),
                                sdsa.optString("department", ""),
                                sdsa.optString("designation", ""),
                                sdsa.optString("branchloaction", ""),
                                sdsa.optString("status", ""),
                                sdsa.optString("reportingTo", "")
                            ));
                        }
                        
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Updated SDSA list with " + sdsaList.size() + " items");
                            Toast.makeText(MySdsaActivity.this, 
                                "Loaded " + sdsaList.size() + " SDSA users reporting to K RAJESH KUMAR", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        runOnUiThread(() -> {
                            Toast.makeText(MySdsaActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    runOnUiThread(() -> {
                        Toast.makeText(MySdsaActivity.this, "Server error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception loading SDSA data: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(MySdsaActivity.this, "Error loading SDSA data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    // SDSA Item data class with essential fields only
    private static class SdsaItem {
        String id;
        String username;
        String firstName;
        String lastName;
        String fullName;
        String phoneNumber;
        String emailId;
        String employeeNo;
        String department;
        String designation;
        String branchLocation;
        String status;
        String reportingTo;

        SdsaItem(String id, String username, String firstName, String lastName, 
                String fullName, String phoneNumber, String emailId, String employeeNo, 
                String department, String designation, String branchLocation, 
                String status, String reportingTo) {
            this.id = id;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.fullName = fullName;
            this.phoneNumber = phoneNumber;
            this.emailId = emailId;
            this.employeeNo = employeeNo;
            this.department = department;
            this.designation = designation;
            this.branchLocation = branchLocation;
            this.status = status;
            this.reportingTo = reportingTo;
        }
    }

    // SDSA Adapter for RecyclerView
    private class SdsaAdapter extends RecyclerView.Adapter<SdsaAdapter.ViewHolder> {
        private List<SdsaItem> sdsaItems;

        SdsaAdapter(List<SdsaItem> sdsaItems) {
            this.sdsaItems = sdsaItems;
            Log.d(TAG, "SDSA adapter created with " + sdsaItems.size() + " items");
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sdsa, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SdsaItem item = sdsaItems.get(position);
            
            // Set comprehensive user information
            holder.nameText.setText("Name: " + item.fullName);
            holder.phoneText.setText("Phone: " + item.phoneNumber);
            holder.emailText.setText("Email: " + item.emailId);
            holder.passwordText.setText("Employee ID: " + item.employeeNo);
            
            // Add additional information if available
            String additionalInfo = "";
            if (!item.designation.isEmpty()) {
                additionalInfo += "Designation: " + item.designation + "\n";
            }
            if (!item.department.isEmpty()) {
                additionalInfo += "Department: " + item.department + "\n";
            }
            if (!item.branchLocation.isEmpty()) {
                additionalInfo += "Branch: " + item.branchLocation + "\n";
            }
            if (!item.status.isEmpty()) {
                additionalInfo += "Status: " + item.status;
            }
            
            if (!additionalInfo.isEmpty()) {
                holder.additionalInfoText.setText(additionalInfo);
                holder.additionalInfoText.setVisibility(View.VISIBLE);
            } else {
                holder.additionalInfoText.setVisibility(View.GONE);
            }

            holder.editButton.setOnClickListener(v -> {
                Toast.makeText(MySdsaActivity.this, 
                    "Edit " + item.fullName, Toast.LENGTH_SHORT).show();
            });

            holder.deleteButton.setOnClickListener(v -> {
                Toast.makeText(MySdsaActivity.this, 
                    "Delete " + item.fullName, Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return sdsaItems.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText;
            TextView phoneText;
            TextView emailText;
            TextView passwordText;
            TextView additionalInfoText;
            Button editButton;
            Button deleteButton;

            ViewHolder(View view) {
                super(view);
                nameText = view.findViewById(R.id.nameText);
                phoneText = view.findViewById(R.id.phoneText);
                emailText = view.findViewById(R.id.emailText);
                passwordText = view.findViewById(R.id.passwordText);
                additionalInfoText = view.findViewById(R.id.additionalInfoText);
                editButton = view.findViewById(R.id.editButton);
                deleteButton = view.findViewById(R.id.deleteButton);
            }
        }
    }
} 