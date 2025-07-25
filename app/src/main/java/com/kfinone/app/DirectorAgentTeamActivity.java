package com.kfinone.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DirectorAgentTeamActivity extends AppCompatActivity {
    private Spinner selectUserSpinner;
    private Button showDataButton, resetButton;
    private RequestQueue requestQueue;
    private List<User> userList = new ArrayList<>();
    private static class User {
        String id;
        String display;
        User(String id, String display) {
            this.id = id;
            this.display = display;
        }
        @Override
        public String toString() { return display; }
    }

    private RecyclerView agentRecyclerView;
    private AgentListAdapter agentListAdapter;
    private List<AgentItem> agentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_agent_team);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Team Agent");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        selectUserSpinner = findViewById(R.id.selectUserSpinner);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);

        requestQueue = Volley.newRequestQueue(this);
        loadUsersForDropdown();

        agentRecyclerView = findViewById(R.id.agentRecyclerView);
        agentListAdapter = new AgentListAdapter(agentList);
        agentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        agentRecyclerView.setAdapter(agentListAdapter);

        showDataButton.setOnClickListener(v -> {
            int selectedPosition = selectUserSpinner.getSelectedItemPosition();
            if (selectedPosition == 0) {
                Toast.makeText(this, "Please select a user", Toast.LENGTH_SHORT).show();
                return;
            }
            String selectedUserId = userList.get(selectedPosition).id;
            fetchAgentData(selectedUserId);
        });

        resetButton.setOnClickListener(v -> {
            selectUserSpinner.setSelection(0);
            agentList.clear();
            agentListAdapter.notifyDataSetChanged();
        });
    }

    private void loadUsersForDropdown() {
        String url = "https://emp.kfinone.com/mobile/api/director_fetch_designated_users.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
                        userList.clear();
                        List<String> userNames = new ArrayList<>();
                        userNames.add("Select User");
                        userList.add(new User("", "Select User"));
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject userObj = data.getJSONObject(i);
                            String id = userObj.getString("id");
                            String fullName = userObj.getString("fullName");
                            String designation = userObj.optString("designation_name", "");
                            String display = fullName;
                            if (designation != null && !designation.isEmpty() && !"null".equalsIgnoreCase(designation)) {
                                display += " (" + designation + ")";
                            }
                            userNames.add(display);
                            userList.add(new User(id, display));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this, android.R.layout.simple_spinner_item, userNames
                        );
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        selectUserSpinner.setAdapter(adapter);
                    } else {
                        String message = response.optString("message", "Failed to load users");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            },
            error -> Toast.makeText(this, "Error fetching users", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    private void fetchAgentData(String userId) {
        String url = "https://emp.kfinone.com/mobile/api/get_agent_data_by_user.php?user_id=" + userId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
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
                        agentListAdapter.notifyDataSetChanged();
                        if (agentList.isEmpty()) {
                            Toast.makeText(this, "No agents found for this user", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch agent data", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Error parsing agent data", Toast.LENGTH_SHORT).show();
                }
            },
            error -> Toast.makeText(this, "Error fetching agent data", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 