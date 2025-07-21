package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.List;

public class CBOMyWorkLinksActivity extends AppCompatActivity {

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

    private RecyclerView recyclerView;
    private MyEmpLinksAdapter adapter;
    private List<MyEmpLinksActivity.IconPermissionItem> iconList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_my_work_links);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        initializeViews();
        setupClickListeners();
        recyclerView = findViewById(R.id.recyclerView); // Add a RecyclerView to your layout
        iconList = new ArrayList<>();
        adapter = new MyEmpLinksAdapter(iconList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        loadMyWorkLinksData();
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
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
        refreshButton.setOnClickListener(v -> refreshData());
        addButton.setOnClickListener(v -> addNewWorkLink());

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
        Intent intent = new Intent(this, CBOWorkLinksActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing my work links...", Toast.LENGTH_SHORT).show();
        loadMyWorkLinksData();
    }

    private void addNewWorkLink() {
        Toast.makeText(this, "Add New Work Link - Coming Soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to Add Work Link activity
    }

    private void loadMyWorkLinksData() {
        if (userId == null || userId.isEmpty()) return;
        String url = "https://emp.kfinone.com/mobile/api/get_user_work_icons.php";
        JSONObject requestBody = new JSONObject();
        try { requestBody.put("userId", userId); } catch (JSONException e) { return; }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, requestBody,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if ("success".equals(response.getString("status"))) {
                            parseWorkIcons(response.getJSONArray("data"));
                        }
                    } catch (JSONException e) { }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) { }
            });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
    private void parseWorkIcons(JSONArray data) {
        iconList.clear();
        try {
            for (int i = 0; i < data.length(); i++) {
                JSONObject icon = data.getJSONObject(i);
                MyEmpLinksActivity.IconPermissionItem item = new MyEmpLinksActivity.IconPermissionItem(
                    null,
                    icon.optString("icon_name", ""),
                    icon.optString("icon_image", ""),
                    icon.optString("icon_description", ""),
                    "Yes",
                    "Work"
                );
                iconList.add(item);
            }
        } catch (JSONException e) { }
        adapter.notifyDataSetChanged();
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