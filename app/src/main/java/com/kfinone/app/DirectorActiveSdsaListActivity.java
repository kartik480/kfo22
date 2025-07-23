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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class DirectorActiveSdsaListActivity extends AppCompatActivity {
    private static final String TAG = "DirectorActiveSdsaList";
    private RecyclerView sdsaRecyclerView;
    private SdsaAdapter adapter;
    private List<SdsaItem> sdsaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_active_sdsa_list);
        sdsaRecyclerView = findViewById(R.id.sdsaRecyclerView);
        sdsaRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sdsaList = new ArrayList<>();
        adapter = new SdsaAdapter(sdsaList);
        sdsaRecyclerView.setAdapter(adapter);
        loadSdsaData();
    }

    private void loadSdsaData() {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/fetch_active_sdsa.php");
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
                        sdsaList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject sdsa = data.getJSONObject(i);
                            sdsaList.add(new SdsaItem(
                                sdsa.optString("first_name", ""),
                                sdsa.optString("last_name", ""),
                                sdsa.optString("Phone_number", ""),
                                sdsa.optString("email_id", ""),
                                sdsa.optString("password", ""),
                                sdsa.optString("reporting_firstName", ""),
                                sdsa.optString("reporting_lastName", ""),
                                sdsa.optString("reporting_designation_name", "")
                            ));
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } else {
                        runOnUiThread(() -> Toast.makeText(DirectorActiveSdsaListActivity.this, "No SDSA found.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(DirectorActiveSdsaListActivity.this, "Failed to load SDSA data.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading SDSA data", e);
                runOnUiThread(() -> Toast.makeText(DirectorActiveSdsaListActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private static class SdsaItem {
        String firstName, lastName, phone, email, password, reportingFirstName, reportingLastName, reportingDesignation;
        SdsaItem(String firstName, String lastName, String phone, String email, String password, String reportingFirstName, String reportingLastName, String reportingDesignation) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.email = email;
            this.password = password;
            this.reportingFirstName = reportingFirstName;
            this.reportingLastName = reportingLastName;
            this.reportingDesignation = reportingDesignation;
        }
    }

    private class SdsaAdapter extends RecyclerView.Adapter<SdsaAdapter.ViewHolder> {
        private List<SdsaItem> sdsaItems;
        SdsaAdapter(List<SdsaItem> sdsaItems) {
            this.sdsaItems = sdsaItems;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_director_active_sdsa, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SdsaItem item = sdsaItems.get(position);
            holder.nameText.setText("Name: " + item.firstName + " " + item.lastName);
            holder.phoneText.setText("Phone: " + item.phone);
            holder.emailText.setText("Email: " + item.email);
            holder.passwordText.setText("Password: " + item.password);
            holder.reportingText.setText("Reporting To: " + item.reportingFirstName + " " + item.reportingLastName + " (" + item.reportingDesignation + ")");
            holder.actionButton.setOnClickListener(v -> {
                // TODO: Action for SDSA row
            });
        }
        @Override
        public int getItemCount() {
            return sdsaItems.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText, phoneText, emailText, passwordText, reportingText;
            Button actionButton;
            ViewHolder(View view) {
                super(view);
                nameText = view.findViewById(R.id.nameText);
                phoneText = view.findViewById(R.id.phoneText);
                emailText = view.findViewById(R.id.emailText);
                passwordText = view.findViewById(R.id.passwordText);
                reportingText = view.findViewById(R.id.reportingText);
                actionButton = view.findViewById(R.id.actionButton);
            }
        }
    }
} 