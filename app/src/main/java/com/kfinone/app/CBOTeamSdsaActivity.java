package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CBOTeamSdsaActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // RBH User Dropdown elements
    private Spinner rbhUserSpinner;
    private LinearLayout selectedUserInfo;
    private TextView selectedUserName;
    private TextView selectedUserDetails;
    private Button viewSdsaTeamButton;

    // User data
    private String userName;
    private String userId;

    // RBH Users data
    private List<RbhUser> rbhUserList;
    private RbhUser selectedRbhUser;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_team_sdsa);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        initializeViews();
        setupClickListeners();
        loadTeamSdsaData();
        fetchRbhUsers();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
        refreshButton = findViewById(R.id.refreshButton);
        addButton = findViewById(R.id.addButton);

        // Bottom navigation
        dashboardButton = findViewById(R.id.dashboardButton);
        empLinksButton = findViewById(R.id.empLinksButton);
        reportsButton = findViewById(R.id.reportsButton);
        settingsButton = findViewById(R.id.settingsButton);

        // RBH User Dropdown elements
        rbhUserSpinner = findViewById(R.id.rbhUserSpinner);
        selectedUserInfo = findViewById(R.id.selectedUserInfo);
        selectedUserName = findViewById(R.id.selectedUserName);
        selectedUserDetails = findViewById(R.id.selectedUserDetails);
        viewSdsaTeamButton = findViewById(R.id.viewSdsaTeamButton);

        executorService = Executors.newSingleThreadExecutor();
        rbhUserList = new ArrayList<>();
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewSdsa());

        // Bottom navigation
        dashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChiefBusinessOfficerPanelActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        empLinksButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CBOEmpLinksActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
            finish();
        });

        reportsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Reports activity
        });

        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "Settings - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Settings activity
        });

        // RBH User Spinner
        rbhUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && position <= rbhUserList.size()) {
                    selectedRbhUser = rbhUserList.get(position - 1);
                    showSelectedUserInfo();
                    viewSdsaTeamButton.setEnabled(true);
                } else {
                    selectedRbhUser = null;
                    hideSelectedUserInfo();
                    viewSdsaTeamButton.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRbhUser = null;
                hideSelectedUserInfo();
                viewSdsaTeamButton.setEnabled(false);
            }
        });

        // View SDSA Team Button
        viewSdsaTeamButton.setOnClickListener(v -> {
            if (selectedRbhUser != null) {
                viewSdsaTeam(selectedRbhUser);
            } else {
                Toast.makeText(this, "Please select a Regional Business Head first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSelectedUserInfo() {
        if (selectedRbhUser != null) {
            selectedUserName.setText("Selected User: " + selectedRbhUser.getFullName());
            selectedUserDetails.setText("Designation: " + selectedRbhUser.getDesignationName() + 
                                     " | Department: " + selectedRbhUser.getDepartmentName());
            selectedUserInfo.setVisibility(View.VISIBLE);
        }
    }

    private void hideSelectedUserInfo() {
        selectedUserInfo.setVisibility(View.GONE);
    }

    private void viewSdsaTeam(RbhUser rbhUser) {
        // Navigate to RBH My SDSA activity with the selected RBH user ID
        Intent intent = new Intent(this, RBHMySdsaActivity.class);
        intent.putExtra("USER_ID", rbhUser.getId());
        intent.putExtra("USERNAME", rbhUser.getUsername());
        intent.putExtra("SELECTED_RBH_USER", rbhUser.getFullName());
        startActivity(intent);
    }

    private void fetchRbhUsers() {
        android.util.Log.d("CBOTeamSdsa", "Starting to fetch RBH users...");
        executorService.execute(() -> {
            try {
                String apiUrl = "https://emp.kfinone.com/mobile/api/get_rbh_users_for_dropdown.php";
                android.util.Log.d("CBOTeamSdsa", "Making API call to: " + apiUrl);
                String response = makeGetRequest(apiUrl);
                
                runOnUiThread(() -> {
                    android.util.Log.d("CBOTeamSdsa", "Response received: " + (response != null ? response.length() : 0) + " characters");
                    if (response != null && !response.isEmpty()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            android.util.Log.d("CBOTeamSdsa", "JSON parsed successfully");
                            if (jsonResponse.getBoolean("success")) {
                                JSONArray usersArray = jsonResponse.getJSONArray("users");
                                rbhUserList.clear();
                                
                                for (int i = 0; i < usersArray.length(); i++) {
                                    JSONObject userObj = usersArray.getJSONObject(i);
                                    RbhUser user = new RbhUser(
                                        userObj.optString("id"),
                                        userObj.optString("username"),
                                        userObj.optString("firstName"),
                                        userObj.optString("lastName"),
                                        userObj.optString("email"),
                                        userObj.optString("phone"),
                                        userObj.optString("status"),
                                        userObj.optString("designation_name"),
                                        userObj.optString("department_name"),
                                        userObj.optString("fullName"),
                                        userObj.optString("displayName")
                                    );
                                    rbhUserList.add(user);
                                }
                                
                                android.util.Log.d("CBOTeamSdsa", "Setting up spinner with " + rbhUserList.size() + " users");
                                setupRbhUserSpinner();
                                Toast.makeText(this, "Loaded " + rbhUserList.size() + " RBH users", Toast.LENGTH_SHORT).show();
                            } else {
                                String errorMessage = jsonResponse.getString("message");
                                android.util.Log.e("CBOTeamSdsa", "API Error: " + errorMessage);
                                Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            android.util.Log.e("CBOTeamSdsa", "JSON Parse Error: " + e.getMessage());
                            Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        android.util.Log.e("CBOTeamSdsa", "No response from server");
                        Toast.makeText(this, "No response from server", Toast.LENGTH_SHORT).show();
                    }
                });
                
            } catch (Exception e) {
                android.util.Log.e("CBOTeamSdsa", "Exception fetching RBH users: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error fetching RBH users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setupRbhUserSpinner() {
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Select Regional Business Head"); // Default option
        
        for (RbhUser user : rbhUserList) {
            spinnerItems.add(user.getDisplayName());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rbhUserSpinner.setAdapter(adapter);
    }

    private String makeGetRequest(String urlString) throws IOException {
        android.util.Log.d("CBOTeamSdsa", "Making GET request to: " + urlString);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        
        try {
            int responseCode = connection.getResponseCode();
            android.util.Log.d("CBOTeamSdsa", "Response code: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line);
                }
                errorReader.close();
                return null;
            }
        } finally {
            connection.disconnect();
        }
    }

    private void goBack() {
        Intent intent = new Intent(this, CBOSdsaActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing team SDSA data...", Toast.LENGTH_SHORT).show();
        loadTeamSdsaData();
        fetchRbhUsers();
    }

    private void addNewSdsa() {
        Toast.makeText(this, "Add New SDSA - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add SDSA activity
    }

    private void loadTeamSdsaData() {
        // TODO: Load real team SDSA data from server
        // For now, show placeholder content
        Toast.makeText(this, "Loading team SDSA data...", Toast.LENGTH_SHORT).show();
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
} 