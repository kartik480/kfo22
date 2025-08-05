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

public class DirectorMySdsaActivity extends AppCompatActivity {
    private static final String TAG = "DirectorMySdsaActivity";

    // UI Elements
    private RecyclerView sdsaRecyclerView;
    private SdsaAdapter adapter;
    private List<SdsaItem> sdsaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_my_sdsa);

        initializeViews();
        setupClickListeners();
        // Get directorId from intent or fallback to default (11)
        String directorId = getIntent().getStringExtra("DIRECTOR_ID");
        if (directorId == null || directorId.isEmpty()) directorId = "11";
        loadSdsaData(directorId);
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

    // Update loadSdsaData to accept directorId
    private void loadSdsaData(String directorId) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Loading SDSA data for directorId: " + directorId);
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_director_sdsa_users.php?director_id=" + directorId);
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
                            
                            // Parse manage icons
                            List<String> manageIcons = new ArrayList<>();
                            if (sdsa.has("manage_icons")) {
                                JSONArray iconsArray = sdsa.getJSONArray("manage_icons");
                                for (int j = 0; j < iconsArray.length(); j++) {
                                    manageIcons.add(iconsArray.getString(j));
                                }
                            }
                            
                            // If no manage icons, add default ones
                            if (manageIcons.isEmpty()) {
                                manageIcons.add("Dashboard");
                                manageIcons.add("Settings");
                                manageIcons.add("Analytics");
                            }
                            
                            sdsaList.add(new SdsaItem(
                                sdsa.optString("full_name", ""),
                                sdsa.optString("employee_no", "EMP" + String.format("%03d", i + 1)),
                                sdsa.optString("phone_number", ""),
                                sdsa.optString("email_id", ""),
                                sdsa.optString("password", ""),
                                sdsa.optString("id", ""),
                                sdsa.optString("reportingTo", ""),
                                manageIcons
                            ));
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } else {
                        runOnUiThread(() -> Toast.makeText(DirectorMySdsaActivity.this, "No SDSA found.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(DirectorMySdsaActivity.this, "Failed to load SDSA data.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading SDSA data", e);
                runOnUiThread(() -> Toast.makeText(DirectorMySdsaActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private static class SdsaItem {
        String name;
        String employeeId;
        String phoneNumber;
        String email;
        String password;
        String id;
        String reportingTo;
        List<String> manageIcons;
        
        SdsaItem(String name, String employeeId, String phoneNumber, String email, String password, String id, String reportingTo, List<String> manageIcons) {
            this.name = name;
            this.employeeId = employeeId;
            this.phoneNumber = phoneNumber;
            this.email = email;
            this.password = password;
            this.id = id;
            this.reportingTo = reportingTo;
            this.manageIcons = manageIcons;
        }
    }

    private class SdsaAdapter extends RecyclerView.Adapter<SdsaAdapter.ViewHolder> {
        private List<SdsaItem> sdsaItems;
        SdsaAdapter(List<SdsaItem> sdsaItems) {
            this.sdsaItems = sdsaItems;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_director_sdsa, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SdsaItem item = sdsaItems.get(position);
            
            // Format the display text according to the requested structure
            String displayText = "Name: " + item.name + "\n" +
                               "Employee ID: " + item.employeeId + "\n" +
                               "Phone: " + item.phoneNumber + "\n" +
                               "Email: " + item.email + "\n" +
                               "Manage Icons: " + String.join(", ", item.manageIcons);
            
            holder.nameText.setText(displayText);
            holder.phoneText.setVisibility(View.GONE);
            holder.emailText.setVisibility(View.GONE);
            holder.passwordText.setVisibility(View.GONE);
            
            holder.actionButton.setOnClickListener(v -> {
                // Show action options for SDSA row
                showActionOptions(item);
            });
        }
        
        private void showActionOptions(SdsaItem item) {
            // Create a simple action dialog
            String[] actions = {"Edit", "View Details", "Deactivate"};
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DirectorMySdsaActivity.this);
            builder.setTitle("Actions for " + item.name);
            builder.setItems(actions, (dialog, which) -> {
                switch (which) {
                    case 0:
                        Toast.makeText(DirectorMySdsaActivity.this, "Edit " + item.name, Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(DirectorMySdsaActivity.this, "View details for " + item.name, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(DirectorMySdsaActivity.this, "Deactivate " + item.name, Toast.LENGTH_SHORT).show();
                        break;
                }
            });
            builder.show();
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