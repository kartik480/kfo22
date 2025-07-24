package com.kfinone.app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectorMyAgentActivity extends AppCompatActivity {
    private Spinner spinnerPartnerType, spinnerBranchState, spinnerBranchLocation;
    private Button btnFilter, btnReset;
    private RecyclerView recyclerAgents;
    private AgentListAdapter agentListAdapter;
    private List<AgentItem> agentList = new ArrayList<>();
    private RequestQueue requestQueue;

    // For mapping dropdown display names to IDs
    private List<String> partnerTypeNames = new ArrayList<>();
    private List<String> partnerTypeIds = new ArrayList<>();
    private List<String> branchStateNames = new ArrayList<>();
    private List<String> branchStateIds = new ArrayList<>();
    private List<String> branchLocationNames = new ArrayList<>();
    private List<String> branchLocationIds = new ArrayList<>();

    private static final String BASE_URL = "https://yourdomain.com/api/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_my_agent);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Agents");
        }

        spinnerPartnerType = findViewById(R.id.spinner_partner_type);
        spinnerBranchState = findViewById(R.id.spinner_branch_state);
        spinnerBranchLocation = findViewById(R.id.spinner_branch_location);
        btnFilter = findViewById(R.id.btn_filter);
        btnReset = findViewById(R.id.btn_reset);
        recyclerAgents = findViewById(R.id.recycler_agents);

        agentListAdapter = new AgentListAdapter(agentList);
        recyclerAgents.setLayoutManager(new LinearLayoutManager(this));
        recyclerAgents.setAdapter(agentListAdapter);

        requestQueue = Volley.newRequestQueue(this);

        loadDropdownOptions();
        loadAllAgents();

        btnFilter.setOnClickListener(v -> filterAgents());
        btnReset.setOnClickListener(v -> {
            spinnerPartnerType.setSelection(0);
            spinnerBranchState.setSelection(0);
            spinnerBranchLocation.setSelection(0);
            loadAllAgents();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadDropdownOptions() {
        String url = BASE_URL + "director_agent_dropdowns.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    JSONArray partnerTypes = response.getJSONArray("partner_types");
                    partnerTypeNames.clear(); partnerTypeIds.clear();
                    partnerTypeNames.add("All Types"); partnerTypeIds.add("0");
                    for (int i = 0; i < partnerTypes.length(); i++) {
                        JSONObject obj = partnerTypes.getJSONObject(i);
                        partnerTypeNames.add(obj.getString("name"));
                        partnerTypeIds.add(obj.getString("id"));
                    }
                    ArrayAdapter<String> partnerTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, partnerTypeNames);
                    partnerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPartnerType.setAdapter(partnerTypeAdapter);

                    JSONArray branchStates = response.getJSONArray("branch_states");
                    branchStateNames.clear(); branchStateIds.clear();
                    branchStateNames.add("All States"); branchStateIds.add("0");
                    for (int i = 0; i < branchStates.length(); i++) {
                        JSONObject obj = branchStates.getJSONObject(i);
                        branchStateNames.add(obj.getString("name"));
                        branchStateIds.add(obj.getString("id"));
                    }
                    ArrayAdapter<String> branchStateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchStateNames);
                    branchStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBranchState.setAdapter(branchStateAdapter);

                    JSONArray branchLocations = response.getJSONArray("branch_locations");
                    branchLocationNames.clear(); branchLocationIds.clear();
                    branchLocationNames.add("All Locations"); branchLocationIds.add("0");
                    for (int i = 0; i < branchLocations.length(); i++) {
                        JSONObject obj = branchLocations.getJSONObject(i);
                        branchLocationNames.add(obj.getString("name"));
                        branchLocationIds.add(obj.getString("id"));
                    }
                    ArrayAdapter<String> branchLocationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchLocationNames);
                    branchLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBranchLocation.setAdapter(branchLocationAdapter);
                } catch (JSONException e) {
                    Toast.makeText(this, "Dropdown parse error", Toast.LENGTH_SHORT).show();
                }
            },
            error -> Toast.makeText(this, "Failed to load dropdowns", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    private void loadAllAgents() {
        String url = BASE_URL + "get_agent_list.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    if (response.getString("status").equals("success")) {
                        JSONArray data = response.getJSONArray("data");
                        List<AgentItem> agents = parseAgentList(data);
                        agentListAdapter.updateData(agents);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load agents", Toast.LENGTH_SHORT).show();
                }
            },
            error -> Toast.makeText(this, "Failed to load agents", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    private void filterAgents() {
        int partnerTypePos = spinnerPartnerType.getSelectedItemPosition();
        int branchStatePos = spinnerBranchState.getSelectedItemPosition();
        int branchLocationPos = spinnerBranchLocation.getSelectedItemPosition();
        String partnerTypeId = partnerTypeIds.get(partnerTypePos);
        String branchStateId = branchStateIds.get(branchStatePos);
        String branchLocationId = branchLocationIds.get(branchLocationPos);

        Map<String, String> params = new HashMap<>();
        params.put("partner_type_id", partnerTypeId);
        params.put("state_id", branchStateId);
        params.put("location_id", branchLocationId);

        JSONObject jsonParams = new JSONObject(params);
        String url = BASE_URL + "filter_agents.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonParams,
            response -> {
                try {
                    if (response.getString("status").equals("success")) {
                        JSONArray data = response.getJSONArray("data");
                        List<AgentItem> agents = parseAgentList(data);
                        agentListAdapter.updateData(agents);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to filter agents", Toast.LENGTH_SHORT).show();
                }
            },
            error -> Toast.makeText(this, "Failed to filter agents", Toast.LENGTH_SHORT).show()
        );
        requestQueue.add(request);
    }

    private List<AgentItem> parseAgentList(JSONArray data) throws JSONException {
        List<AgentItem> agents = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject obj = data.getJSONObject(i);
            agents.add(new AgentItem(
                    obj.optString("full_name"),
                    obj.optString("company_name"),
                    obj.optString("Phone_number"),
                    obj.optString("alternative_Phone_number"),
                    obj.optString("email_id"),
                    obj.optString("partnerType"),
                    obj.optString("state"),
                    obj.optString("location"),
                    obj.optString("address"),
                    obj.optString("visiting_card"),
                    obj.optString("created_user"),
                    obj.optString("createdBy")
            ));
        }
        return agents;
    }
} 