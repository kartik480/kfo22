package com.kfinone.app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class DirectorMyAgentActivity extends AppCompatActivity {
    private Spinner spinnerPartnerType, spinnerBranchState, spinnerBranchLocation;
    private Button btnFilter, btnReset;
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
            getSupportActionBar().setTitle("Director My Agent");
        }

        spinnerPartnerType = findViewById(R.id.spinner_partner_type);
        spinnerBranchState = findViewById(R.id.spinner_branch_state);
        spinnerBranchLocation = findViewById(R.id.spinner_branch_location);
        btnFilter = findViewById(R.id.btn_filter);
        btnReset = findViewById(R.id.btn_reset);

        requestQueue = Volley.newRequestQueue(this);

        loadDropdownOptions();

        btnFilter.setOnClickListener(v -> filterAgents());
        btnReset.setOnClickListener(v -> {
            spinnerPartnerType.setSelection(0);
            spinnerBranchState.setSelection(0);
            spinnerBranchLocation.setSelection(0);
            Toast.makeText(this, "Filters reset", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadDropdownOptions() {
        // Load sample data for spinners (replace with actual API calls)
        partnerTypeNames.add("Select Agent Type");
        partnerTypeNames.add("Individual");
        partnerTypeNames.add("Corporate");
        partnerTypeNames.add("Partner");

        branchStateNames.add("Select Branch State");
        branchStateNames.add("Maharashtra");
        branchStateNames.add("Delhi");
        branchStateNames.add("Karnataka");
        branchStateNames.add("Tamil Nadu");

        branchLocationNames.add("Select Branch Location");
        branchLocationNames.add("Mumbai");
        branchLocationNames.add("Pune");
        branchLocationNames.add("Delhi");
        branchLocationNames.add("Bangalore");

        // Set up spinners
        ArrayAdapter<String> partnerTypeAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, partnerTypeNames);
        partnerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPartnerType.setAdapter(partnerTypeAdapter);

        ArrayAdapter<String> branchStateAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, branchStateNames);
        branchStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranchState.setAdapter(branchStateAdapter);

        ArrayAdapter<String> branchLocationAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, branchLocationNames);
        branchLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranchLocation.setAdapter(branchLocationAdapter);
    }

    private void filterAgents() {
        String selectedAgentType = spinnerPartnerType.getSelectedItem().toString();
        String selectedBranchState = spinnerBranchState.getSelectedItem().toString();
        String selectedBranchLocation = spinnerBranchLocation.getSelectedItem().toString();

        // Check if any filter is selected
        if (selectedAgentType.equals("Select Agent Type") && 
            selectedBranchState.equals("Select Branch State") && 
            selectedBranchLocation.equals("Select Branch Location")) {
            Toast.makeText(this, "Please select at least one filter", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show filter applied message
        String filterMessage = "Filters applied: ";
        if (!selectedAgentType.equals("Select Agent Type")) {
            filterMessage += selectedAgentType;
        }
        if (!selectedBranchState.equals("Select Branch State")) {
            filterMessage += (filterMessage.endsWith(": ") ? "" : ", ") + selectedBranchState;
        }
        if (!selectedBranchLocation.equals("Select Branch Location")) {
            filterMessage += (filterMessage.endsWith(": ") ? "" : ", ") + selectedBranchLocation;
        }
        
        Toast.makeText(this, filterMessage, Toast.LENGTH_LONG).show();
        
        // TODO: Implement actual filtering logic here
        // For now, just show the message
    }
} 