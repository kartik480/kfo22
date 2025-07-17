package com.kfinone.app;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivePartnerListActivity extends AppCompatActivity implements ActivePartnerAdapter.OnItemClickListener {

    private RecyclerView activePartnerRecyclerView;
    private ActivePartnerAdapter adapter;
    private List<ActivePartnerItem> partnerList;
    private ExecutorService executorService;
    private static final String API_URL = "https://emp.kfinone.com/mobile/api/get_active_partner_list.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_partner_list);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Active Partner List");
        }

        // Initialize RecyclerView
        activePartnerRecyclerView = findViewById(R.id.activePartnerRecyclerView);
        partnerList = new ArrayList<>();
        adapter = new ActivePartnerAdapter(partnerList, this);
        activePartnerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activePartnerRecyclerView.setAdapter(adapter);

        // Initialize executor service for background tasks
        executorService = Executors.newSingleThreadExecutor();

        // Fetch active partners from API
        fetchActivePartners();
    }

    private void fetchActivePartners() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(API_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();

                        // Debug: Print the response
                        System.out.println("API Response: " + response.toString());

                        // Parse JSON response
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONArray data = jsonResponse.getJSONArray("data");
                            partnerList.clear();
                            
                            // Debug: Print the number of items
                            System.out.println("Number of partners found: " + data.length());
                            
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject item = data.getJSONObject(i);
                                
                                ActivePartnerItem partner = new ActivePartnerItem(
                                    item.optString("alias_name"),
                                    item.optString("first_name"),
                                    item.optString("last_name"),
                                    item.optString("password"),
                                    item.optString("Phone_number"),
                                    item.optString("email_id"),
                                    item.optString("alternative_mobile_number"),
                                    item.optString("company_name"),
                                    item.optString("branch_state_name_id"),
                                    item.optString("branch_location_id"),
                                    item.optString("bank_id"),
                                    item.optString("account_type_id"),
                                    item.optString("office_address"),
                                    item.optString("residential_address"),
                                    item.optString("aadhaar_number"),
                                    item.optString("pan_number"),
                                    item.optString("account_number"),
                                    item.optString("ifsc_code"),
                                    item.optString("rank"),
                                    item.optString("status"),
                                    item.optString("reportingTo"),
                                    item.optString("employee_no"),
                                    item.optString("department"),
                                    item.optString("designation"),
                                    item.optString("branchstate"),
                                    item.optString("branchloaction"),
                                    item.optString("bank_name"),
                                    item.optString("account_type"),
                                    item.optString("partner_type_id"),
                                    item.optString("pan_img"),
                                    item.optString("aadhaar_img"),
                                    item.optString("photo_img"),
                                    item.optString("bankproof_img"),
                                    i + 1
                                );
                                
                                partnerList.add(partner);
                            }

                            // Update UI on main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    // Debug: Show toast with count
                                    Toast.makeText(ActivePartnerListActivity.this, "Loaded " + partnerList.size() + " partners", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Debug: Show error message
                            final String errorMsg = jsonResponse.optString("message", "Unknown error");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ActivePartnerListActivity.this, "API Error: " + errorMsg, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        // Debug: Show HTTP error
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActivePartnerListActivity.this, "HTTP Error: " + responseCode, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ActivePartnerListActivity.this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onEditClick(ActivePartnerItem item) {
        // Handle edit action
        Toast.makeText(this, "Edit: " + item.getFullName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(ActivePartnerItem item) {
        // Handle delete action
        Toast.makeText(this, "Delete: " + item.getFullName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 
