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
        // Get directorId from intent or fallback to default (e.g., "8")
        String directorId = getIntent().getStringExtra("DIRECTOR_ID");
        if (directorId == null || directorId.isEmpty()) directorId = "8";
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
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_active_sdsa.php?reportingTo=" + directorId);
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
                                sdsa.getString("fullName"),
                                sdsa.getString("phoneNumber"),
                                sdsa.getString("emailId"),
                                sdsa.getString("password"),
                                sdsa.getString("id"),
                                sdsa.getString("reportingTo")
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
        String phoneNumber;
        String email;
        String password;
        String id;
        String reportingTo;
        SdsaItem(String name, String phoneNumber, String email, String password, String id, String reportingTo) {
            this.name = name;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_director_sdsa, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SdsaItem item = sdsaItems.get(position);
            holder.nameText.setText(item.name);
            holder.phoneText.setText(item.phoneNumber);
            holder.emailText.setText(item.email);
            holder.passwordText.setText(item.password);
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