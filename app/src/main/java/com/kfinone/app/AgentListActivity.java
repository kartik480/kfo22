package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AgentListActivity extends AppCompatActivity {
    private static final String TAG = "AgentListActivity";
    private RecyclerView recyclerView;
    private AgentListAdapter adapter;
    private ProgressBar progressBar;
    private List<AgentItem> agentList;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_list);

        Log.d(TAG, "AgentListActivity onCreate started");

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Agent List");
        }

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        
        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        agentList = new ArrayList<>();
        adapter = new AgentListAdapter(agentList);
        recyclerView.setAdapter(adapter);

        Log.d(TAG, "RecyclerView setup completed, adapter attached");

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Fetch all agent data
        fetchAgentData();
    }

    private void fetchAgentData() {
        Log.d(TAG, "fetchAgentData started");
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://pznstudio.shop/kfinone/fetch_agent_data.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "Response received: " + response.toString());
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray data = response.getJSONArray("data");
                            Log.d(TAG, "Data array length: " + data.length());
                            parseAgentData(data);
                        } else {
                            String errorMsg = "Error: " + response.getString("message");
                            Log.e(TAG, errorMsg);
                            Toast.makeText(AgentListActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        String errorMsg = "Error parsing response: " + e.getMessage();
                        Log.e(TAG, errorMsg, e);
                        Toast.makeText(AgentListActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    String errorMsg = "Error fetching data: " + error.getMessage();
                    Log.e(TAG, errorMsg, error);
                    Toast.makeText(AgentListActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                });

        requestQueue.add(request);
    }

    private void parseAgentData(JSONArray response) {
        Log.d(TAG, "parseAgentData started with " + response.length() + " items");
        List<AgentItem> newAgentList = new ArrayList<>();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject agent = response.getJSONObject(i);
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
                newAgentList.add(agentItem);
                Log.d(TAG, "Added agent: " + agentItem.getFullName());
            }
            agentList.clear();
            agentList.addAll(newAgentList);
            adapter.notifyDataSetChanged();
            
            Log.d(TAG, "Agent list updated, total items: " + agentList.size());
            
            if (newAgentList.isEmpty()) {
                Log.w(TAG, "No agent data found");
                Toast.makeText(this, "No agent data found", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Successfully loaded " + newAgentList.size() + " agents");
                Toast.makeText(this, "Found " + newAgentList.size() + " agents!", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            String errorMsg = "Error parsing data: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 
