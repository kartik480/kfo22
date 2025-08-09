package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class RBHMyAgentListActivity extends AppCompatActivity {
    private static final String TAG = "RBHMyAgentListActivity";
    private static final String API_BASE_URL = "https://emp.kfinone.com/mobile/api";

    private TextView totalAgentCount, activeAgentCount, welcomeText;
    private Spinner spinnerAgentType, spinnerBranchState, spinnerBranchLocation;
    private Button btnFilter, btnReset;
    private LinearLayout agentListContainer;
    private String userId;
    private String userName;
    private List<AgentItem> agentList = new ArrayList<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_rbh_my_agent_list);
        
        // Get user data from intent
        userName = getIntent().getStringExtra("USERNAME");
        userId = getIntent().getStringExtra("USER_ID");
        
        // Debug logging
        Log.d(TAG, "Received userName: " + userName);
        Log.d(TAG, "Received userId: " + userId);
        
        if (userName == null || userName.isEmpty()) {
            userName = "RBH";
        }
        
        // Check if userName is actually a full name (contains spaces) and extract the correct username
        if (userName != null && userName.contains(" ")) {
            // This is likely a full name, we need to get the actual username from the login response
            // For now, we'll use the userId to get the username from the API
            Log.d(TAG, "Full name detected: " + userName + ", will use userId for API call");
        }
        
        initializeViews();
        setupToolbar();
        setupSpinners();
        setupClickListeners();
        loadAgentData();
        updateWelcomeMessage(userName);
    }

    private void initializeViews() {
        welcomeText = findViewById(R.id.welcomeText);
        totalAgentCount = findViewById(R.id.totalAgentCount);
        activeAgentCount = findViewById(R.id.activeAgentCount);
        spinnerAgentType = findViewById(R.id.spinnerAgentType);
        spinnerBranchState = findViewById(R.id.spinnerBranchState);
        spinnerBranchLocation = findViewById(R.id.spinnerBranchLocation);
        btnFilter = findViewById(R.id.btnFilter);
        btnReset = findViewById(R.id.btnReset);
        agentListContainer = findViewById(R.id.agentListContainer);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Agent List");
        }
    }

    private void setupSpinners() {
        // Agent Type options
        String[] agentTypes = {"All Agent Types", "Individual Agent", "Corporate Agent", "Broker"};
        ArrayAdapter<String> agentTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, agentTypes);
        agentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAgentType.setAdapter(agentTypeAdapter);

        // Branch State options
        String[] branchStates = {"All States", "Maharashtra", "Delhi", "Karnataka", "Tamil Nadu", "Gujarat"};
        ArrayAdapter<String> branchStateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchStates);
        branchStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranchState.setAdapter(branchStateAdapter);

        // Branch Location options
        String[] branchLocations = {"All Locations", "Mumbai", "Pune", "Nagpur", "Thane", "Nashik"};
        ArrayAdapter<String> branchLocationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchLocations);
        branchLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranchLocation.setAdapter(branchLocationAdapter);
    }

    private void setupClickListeners() {
        btnFilter.setOnClickListener(v -> {
            String selectedAgentType = spinnerAgentType.getSelectedItem().toString();
            String selectedBranchState = spinnerBranchState.getSelectedItem().toString();
            String selectedBranchLocation = spinnerBranchLocation.getSelectedItem().toString();
            
            Toast.makeText(this, "Filtering agents...", Toast.LENGTH_SHORT).show();
            applyFilters(selectedAgentType, selectedBranchState, selectedBranchLocation);
        });

        btnReset.setOnClickListener(v -> {
            spinnerAgentType.setSelection(0);
            spinnerBranchState.setSelection(0);
            spinnerBranchLocation.setSelection(0);
            Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
            loadAgentData(); // Reload all agents
        });
    }

    private void applyFilters(String agentType, String branchState, String branchLocation) {
        // TODO: Implement API call to filter agents based on selected criteria
        String filterMessage = "Applied filters:\n";
        if (!agentType.equals("All Agent Types")) filterMessage += "Agent Type: " + agentType + "\n";
        if (!branchState.equals("All States")) filterMessage += "Branch State: " + branchState + "\n";
        if (!branchLocation.equals("All Locations")) filterMessage += "Branch Location: " + branchLocation;
        
        Toast.makeText(this, filterMessage, Toast.LENGTH_LONG).show();
    }

    private void loadAgentData() {
        if (agentListContainer == null) {
            Log.e(TAG, "agentListContainer is null");
            return;
        }

        // Show loading message
        agentListContainer.removeAllViews();
        TextView loadingText = new TextView(this);
        loadingText.setText("Loading agent data...");
        loadingText.setTextSize(16);
        loadingText.setPadding(20, 20, 20, 20);
        agentListContainer.addView(loadingText);

        executor.execute(() -> {
            try {
                String urlString = API_BASE_URL + "/rbh_my_agent_data.php";
                
                // Prioritize user_id over username for more reliable results
                if (userId != null && !userId.isEmpty()) {
                    urlString += "?user_id=" + userId;
                } else if (userName != null && !userName.isEmpty()) {
                    urlString += "?username=" + userName;
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "No user information available", Toast.LENGTH_SHORT).show();
                        agentListContainer.removeAllViews();
                        TextView errorText = new TextView(this);
                        errorText.setText("No user information available");
                        errorText.setTextSize(16);
                        errorText.setPadding(20, 20, 20, 20);
                        agentListContainer.addView(errorText);
                    });
                    return;
                }

                Log.d(TAG, "Making API call to: " + urlString);

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                BufferedReader reader;
                if (responseCode >= 200 && responseCode < 300) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                connection.disconnect();

                String jsonResponse = response.toString();
                Log.d(TAG, "Full response: " + jsonResponse);

                JSONObject jsonObject = new JSONObject(jsonResponse);
                
                runOnUiThread(() -> {
                    try {
                        if (jsonObject.getString("status").equals("success")) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            agentList.clear();
                            
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject agent = data.getJSONObject(i);
                                AgentItem agentItem = new AgentItem(
                                    agent.optString("full_name", ""),
                                    agent.optString("company_name", ""),
                                    agent.optString("Phone_number", ""),
                                    agent.optString("alternative_Phone_number", ""),
                                    agent.optString("email_id", ""),
                                    agent.optString("partnerType", ""),
                                    agent.optString("state", ""),
                                    agent.optString("location", ""),
                                    agent.optString("address", ""),
                                    agent.optString("visiting_card", ""),
                                    agent.optString("created_user", ""),
                                    agent.optString("createdBy", "")
                                );
                                agentList.add(agentItem);
                            }

                            // Update statistics
                            JSONObject stats = jsonObject.optJSONObject("statistics");
                            if (stats != null) {
                                totalAgentCount.setText(String.valueOf(stats.optInt("total_agents", 0)));
                                activeAgentCount.setText(String.valueOf(stats.optInt("active_agents", 0)));
                            }

                            // Display agents
                            displayAgents();
                            
                            Toast.makeText(this, "Found " + agentList.size() + " agents!", Toast.LENGTH_SHORT).show();
                        } else {
                            String errorMessage = jsonObject.optString("message", "Unknown error");
                            Log.e(TAG, "API Error: " + errorMessage);
                            Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            
                            agentListContainer.removeAllViews();
                            TextView errorText = new TextView(this);
                            errorText.setText("Error loading agent data: " + errorMessage);
                            errorText.setTextSize(16);
                            errorText.setPadding(20, 20, 20, 20);
                            agentListContainer.addView(errorText);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response: " + e.getMessage());
                        Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        
                        agentListContainer.removeAllViews();
                        TextView errorText = new TextView(this);
                        errorText.setText("Error parsing response: " + e.getMessage());
                        errorText.setTextSize(16);
                        errorText.setPadding(20, 20, 20, 20);
                        agentListContainer.addView(errorText);
                    }
                });
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading agent data: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error loading agent data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    
                    agentListContainer.removeAllViews();
                    TextView errorText = new TextView(this);
                    errorText.setText("Error loading agent data: " + e.getMessage());
                    errorText.setTextSize(16);
                    errorText.setPadding(20, 20, 20, 20);
                    agentListContainer.addView(errorText);
                });
            }
        });
    }

    private void displayAgents() {
        agentListContainer.removeAllViews();
        
        if (agentList.isEmpty()) {
            TextView noDataText = new TextView(this);
            noDataText.setText("No agents found for this RBH user.");
            noDataText.setTextSize(16);
            noDataText.setPadding(20, 20, 20, 20);
            agentListContainer.addView(noDataText);
            return;
        }

        // Add header
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setPadding(10, 10, 10, 10);
        headerLayout.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        String[] headers = {"Name", "Company", "Phone", "Type", "State", "Location"};
        float[] weights = {2.0f, 2.0f, 1.5f, 1.0f, 1.0f, 1.0f};

        for (int i = 0; i < headers.length; i++) {
            TextView headerText = new TextView(this);
            headerText.setText(headers[i]);
            headerText.setTextSize(12);
            headerText.setTextColor(getResources().getColor(android.R.color.white));
            headerText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weights[i]));
            headerLayout.addView(headerText);
        }

        agentListContainer.addView(headerLayout);

        // Add agent rows
        for (AgentItem agent : agentList) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setPadding(10, 8, 10, 8);
            rowLayout.setBackgroundResource(android.R.drawable.list_selector_background);

            // Name
            TextView nameText = new TextView(this);
            nameText.setText(agent.getFullName());
            nameText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2.0f));
            rowLayout.addView(nameText);

            // Company
            TextView companyText = new TextView(this);
            companyText.setText(agent.getCompanyName());
            companyText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2.0f));
            rowLayout.addView(companyText);

            // Phone
            TextView phoneText = new TextView(this);
            phoneText.setText(agent.getPhoneNumber());
            phoneText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f));
            rowLayout.addView(phoneText);

            // Type
            TextView typeText = new TextView(this);
            typeText.setText(agent.getPartnerType());
            typeText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            rowLayout.addView(typeText);

            // State
            TextView stateText = new TextView(this);
            stateText.setText(agent.getState());
            stateText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            rowLayout.addView(stateText);

            // Location
            TextView locationText = new TextView(this);
            locationText.setText(agent.getLocation());
            locationText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            rowLayout.addView(locationText);

            agentListContainer.addView(rowLayout);
        }
    }

    private void updateWelcomeMessage(String userName) {
        if (welcomeText != null) {
            welcomeText.setText("Welcome, " + userName + "!");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
} 