package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
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
import java.util.HashMap;

public class DirectorSdsaTeamActivity extends AppCompatActivity {
    private static final String TAG = "DirectorSdsaTeamActivity";

    // UI Elements
    private RecyclerView sdsaRecyclerView;
    private SdsaAdapter adapter;
    private List<SdsaItem> sdsaList;
    private Spinner selectUserDropdown;
    private List<String> userDisplayList = new ArrayList<>();
    private List<String> userIdList = new ArrayList<>();
    private HashMap<String, String> userIdToDesignation = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_sdsa_team);

        initializeViews();
        setupClickListeners();
        // Load SDSA users by default
        loadSdsaTeamData("");
    }

    private void initializeViews() {
        // Initialize RecyclerView
        sdsaRecyclerView = findViewById(R.id.sdsaRecyclerView);
        sdsaRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sdsaList = new ArrayList<>();
        adapter = new SdsaAdapter(sdsaList);
        sdsaRecyclerView.setAdapter(adapter);
        // Initialize Spinner
        selectUserDropdown = findViewById(R.id.selectUserDropdown);
        fetchCboRbhUsers();
        // Set listener for dropdown selection
        selectUserDropdown.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < userIdList.size()) {
                    String selectedUserId = userIdList.get(position);
                    loadSdsaTeamData(selectedUserId);
                }
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupClickListeners() {
        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
        });
    }

    // Load SDSA users reporting to Chief Business Officer and Regional Business Head
    private void loadSdsaTeamData(String reportingToId) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Loading SDSA Team data for reportingTo: " + reportingToId);
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_sdsa_users_reporting_to_designated.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                int responseCode = conn.getResponseCode();
                Log.d(TAG, "SDSA Team data response code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
                    String responseString = response.toString();
                    Log.d(TAG, "SDSA Team data response: " + responseString);
                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONArray data = json.getJSONArray("data");
                        sdsaList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject sdsa = data.getJSONObject(i);
                            sdsaList.add(new SdsaItem(
                                sdsa.optString("first_name", ""),
                                sdsa.optString("last_name", ""),
                                sdsa.optString("phone_number", ""),
                                sdsa.optString("email_id", ""),
                                sdsa.optString("password", ""),
                                sdsa.optString("id", ""),
                                sdsa.optString("reportingTo", "")
                            ));
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } else {
                        runOnUiThread(() -> Toast.makeText(DirectorSdsaTeamActivity.this, "No SDSA Team found.", Toast.LENGTH_SHORT).show());
                        sdsaList.clear();
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(DirectorSdsaTeamActivity.this, "Failed to load SDSA Team data.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading SDSA Team data", e);
                runOnUiThread(() -> Toast.makeText(DirectorSdsaTeamActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void fetchCboRbhUsers() {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_director_designated_users_fixed.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
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
                        JSONArray data = json.getJSONArray("data");
                        userDisplayList.clear();
                        userIdList.clear();
                        userIdToDesignation.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject user = data.getJSONObject(i);
                            String userId = user.getString("id");
                            String fullName = user.getString("full_name");
                            String designation = user.getString("designation_name");
                            
                            // Add debugging logs
                            Log.d(TAG, "User " + i + ": ID=" + userId + ", Name=" + fullName + ", Designation=" + designation);
                            
                            String displayText = fullName + " (" + designation + ")";
                            userDisplayList.add(displayText);
                            userIdList.add(userId);
                            userIdToDesignation.put(userId, designation);
                            
                            Log.d(TAG, "Dropdown text: " + displayText);
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                DirectorSdsaTeamActivity.this,
                                android.R.layout.simple_spinner_item,
                                userDisplayList
                            );
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            selectUserDropdown.setAdapter(adapter);
                        });
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching CBO/RBH users", e);
            }
        }).start();
    }

    private static class SdsaItem {
        String firstName;
        String lastName;
        String phoneNumber;
        String email;
        String password;
        String id;
        String reportingTo;
        SdsaItem(String firstName, String lastName, String phoneNumber, String email, String password, String id, String reportingTo) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.password = password;
            this.id = id;
            this.reportingTo = reportingTo;
        }
    }

    private class SdsaAdapter extends RecyclerView.Adapter<SdsaAdapter.ViewHolder> {
        private List<SdsaItem> sdsaItems;
        SdsaAdapter(List<SdsaItem> sdsaItems) {
            this.sdsaItems = sdsaItems;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_director_sdsa_team, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SdsaItem item = sdsaItems.get(position);
            holder.nameText.setText("Name: " + item.firstName + " " + item.lastName);
            holder.phoneText.setText("Phone: " + item.phoneNumber);
            holder.emailText.setText("Email: " + item.email);
            holder.passwordText.setText("Password: " + item.password);
            holder.actionButton.setOnClickListener(v -> {
                // TODO: Action for SDSA row
            });
        }
        @Override
        public int getItemCount() {
            return sdsaItems.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText, phoneText, emailText, passwordText;
            Button actionButton;
            ViewHolder(View view) {
                super(view);
                nameText = view.findViewById(R.id.nameText);
                phoneText = view.findViewById(R.id.phoneText);
                emailText = view.findViewById(R.id.emailText);
                passwordText = view.findViewById(R.id.passwordText);
                actionButton = view.findViewById(R.id.actionButton);
            }
        }
    }
} 