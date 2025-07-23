package com.kfinone.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DirectorPartnerAddActivity extends AppCompatActivity {
    private static final String TAG = "DirectorPartnerAddActivity";
    private Spinner bankNameDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_add_partner);
        bankNameDropdown = findViewById(R.id.bankNameDropdown);
        fetchBankNames();
    }

    private void fetchBankNames() {
        new Thread(() -> {
            try {
                String urlString = "https://emp.kfinone.com/mobile/api/director_bank_dropdown.php";
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Bank API HTTP response code: " + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    String responseStr = response.toString();
                    Log.d(TAG, "Bank API response: " + responseStr);
                    if (TextUtils.isEmpty(responseStr) || !responseStr.trim().startsWith("{")) {
                        Log.e(TAG, "Bank API response is not valid JSON: " + responseStr);
                        runOnUiThread(() -> Toast.makeText(DirectorPartnerAddActivity.this, "Bank API error: Invalid response", Toast.LENGTH_LONG).show());
                        return;
                    }
                    try {
                        JSONObject jsonResponse = new JSONObject(responseStr);
                        JSONArray dataArray = jsonResponse.optJSONArray("data");
                        if (dataArray != null) {
                            List<String> bankNames = new ArrayList<>();
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject item = dataArray.getJSONObject(i);
                                bankNames.add(item.getString("bank_name"));
                            }
                            new Handler(Looper.getMainLooper()).post(() -> {
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    DirectorPartnerAddActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    bankNames
                                );
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                bankNameDropdown.setAdapter(adapter);
                            });
                        } else {
                            Log.e(TAG, "Bank API JSON missing 'data': " + responseStr);
                            runOnUiThread(() -> Toast.makeText(DirectorPartnerAddActivity.this, "Bank API error: No data", Toast.LENGTH_LONG).show());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception parsing bank JSON: " + e.getMessage(), e);
                        runOnUiThread(() -> Toast.makeText(DirectorPartnerAddActivity.this, "Bank API error: Parse error", Toast.LENGTH_LONG).show());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading banks: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(DirectorPartnerAddActivity.this, "Bank API error: Exception", Toast.LENGTH_LONG).show());
            }
        }).start();
    }
} 