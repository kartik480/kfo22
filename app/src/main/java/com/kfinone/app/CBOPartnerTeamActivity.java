package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class CBOPartnerTeamActivity extends AppCompatActivity {

    private static final String TAG = "CBOPartnerTeam";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    // Top navigation elements
    private View backButton;
    private View refreshButton;
    private View addButton;

    // Bottom navigation elements
    private LinearLayout dashboardButton;
    private LinearLayout empLinksButton;
    private LinearLayout reportsButton;
    private LinearLayout settingsButton;

    // User data
    private String userName;
    private String userId;

    // ListView for Regional Business Head users
    private ListView rbhUsersListView;
    private ArrayAdapter<String> rbhUsersAdapter;
    private List<String> rbhUsersList;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_partner_team);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        initializeViews();
        setupClickListeners();
        setupVolley();
        loadRBHUsers();
        loadPartnerTeamData();
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

        // ListView for Regional Business Head users
        rbhUsersListView = findViewById(R.id.rbhUsersListView);
        rbhUsersList = new ArrayList<>();
        rbhUsersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, rbhUsersList);
        rbhUsersListView.setAdapter(rbhUsersAdapter);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewPartner());

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
    }

    private void goBack() {
        Intent intent = new Intent(this, CBOPartnerActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing partner team data...", Toast.LENGTH_SHORT).show();
        loadRBHUsers();
        loadPartnerTeamData();
    }

    private void addNewPartner() {
        Toast.makeText(this, "Add New Partner - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add Partner activity
    }

    private void setupVolley() {
        requestQueue = Volley.newRequestQueue(this);
    }

    private void loadRBHUsers() {
        String url = BASE_URL + "cbo_partner_team_rbh_users.php";
        
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray dataArray = response.getJSONArray("data");
                            rbhUsersList.clear();
                            
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject user = dataArray.getJSONObject(i);
                                String fullName = user.getString("full_name");
                                String username = user.getString("username");
                                String creatorName = user.getString("creator_name");
                                String displayText = fullName + " (" + username + ") - Created by: " + creatorName;
                                rbhUsersList.add(displayText);
                            }
                            
                            rbhUsersAdapter.notifyDataSetChanged();
                            
                            if (rbhUsersList.isEmpty()) {
                                Toast.makeText(CBOPartnerTeamActivity.this, "No partner users found created by Regional Business Head users", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(CBOPartnerTeamActivity.this, "Error: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        Toast.makeText(CBOPartnerTeamActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Volley error: " + error.getMessage());
                    Toast.makeText(CBOPartnerTeamActivity.this, "Error loading partner users created by Regional Business Head users", Toast.LENGTH_SHORT).show();
                }
            }
        );
        
        requestQueue.add(jsonObjectRequest);
    }

    private void loadPartnerTeamData() {
        // TODO: Load real partner team data from server
        // For now, show placeholder content
        Toast.makeText(this, "Loading partner team data...", Toast.LENGTH_SHORT).show();
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
} 