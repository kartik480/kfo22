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
import android.util.Log;
import android.widget.Toast;

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
        loadAllDropdownOptions();
    }

    private void loadAllDropdownOptions() {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/director_agent_dropdowns.php");
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
                    Log.d("DirectorAddAgent", "Dropdowns response: " + response.toString());
                    JSONObject json = new JSONObject(response.toString());
                    // Partner Types
                    JSONArray partnerTypes = json.optJSONArray("partner_types");
                    List<String> partnerTypeOptions = new ArrayList<>();
                    if (partnerTypes != null && partnerTypes.length() > 0) {
                        for (int i = 0; i < partnerTypes.length(); i++) {
                            JSONObject item = partnerTypes.getJSONObject(i);
                            partnerTypeOptions.add(item.optString("name", ""));
                        }
                    }
                    // Branch States
                    JSONArray branchStates = json.optJSONArray("branch_states");
                    List<String> branchStateOptions = new ArrayList<>();
                    if (branchStates != null && branchStates.length() > 0) {
                        for (int i = 0; i < branchStates.length(); i++) {
                            JSONObject item = branchStates.getJSONObject(i);
                            branchStateOptions.add(item.optString("name", ""));
                        }
                    }
                    // Branch Locations
                    JSONArray branchLocations = json.optJSONArray("branch_locations");
                    List<String> branchLocationOptions = new ArrayList<>();
                    if (branchLocations != null && branchLocations.length() > 0) {
                        for (int i = 0; i < branchLocations.length(); i++) {
                            JSONObject item = branchLocations.getJSONObject(i);
                            branchLocationOptions.add(item.optString("name", ""));
                        }
                    }
                    runOnUiThread(() -> {
                        partnerTypeDropdown.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, partnerTypeOptions));
                        branchStateDropdown.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchStateOptions));
                        branchLocationDropdown.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchLocationOptions));
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Dropdown API error: HTTP " + responseCode, Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e("DirectorAddAgent", "Dropdowns error: ", e);
                runOnUiThread(() -> Toast.makeText(this, "Dropdown error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
} 