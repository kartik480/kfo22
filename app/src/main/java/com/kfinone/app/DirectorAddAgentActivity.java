package com.kfinone.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DirectorAddAgentActivity extends AppCompatActivity {
    private Spinner partnerTypeDropdown, branchStateDropdown, branchLocationDropdown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_add_agent);
        partnerTypeDropdown = findViewById(R.id.partnerTypeDropdown);
        branchStateDropdown = findViewById(R.id.branchStateDropdown);
        branchLocationDropdown = findViewById(R.id.branchLocationDropdown);
        MaterialToolbar backToolbar = findViewById(R.id.backToolbar);
        backToolbar.setNavigationOnClickListener(v -> finish());
        loadPartnerTypeOptions();
        loadBranchStateOptions();
        loadBranchLocationOptions();
    }
    private void loadPartnerTypeOptions() {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/director_partner_type_dropdown.php");
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
                    JSONArray data = json.optJSONArray("data");
                    if (data != null) {
                        List<String> options = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);
                            options.add(item.optString("partner_type", ""));
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            partnerTypeDropdown.setAdapter(adapter);
                        });
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }
    private void loadBranchStateOptions() {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/director_branch_state_dropdown.php");
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
                    JSONArray data = json.optJSONArray("data");
                    if (data != null) {
                        List<String> options = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);
                            options.add(item.optString("branch_state_name", ""));
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            branchStateDropdown.setAdapter(adapter);
                        });
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }
    private void loadBranchLocationOptions() {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/director_branch_location_dropdown.php");
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
                    JSONArray data = json.optJSONArray("data");
                    if (data != null) {
                        List<String> options = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);
                            options.add(item.optString("branch_location", ""));
                        }
                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            branchLocationDropdown.setAdapter(adapter);
                        });
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }
} 