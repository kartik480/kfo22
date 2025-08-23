package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class BusinessHeadEmpLinksActivity extends AppCompatActivity {
    private static final String TAG = "BusinessHeadEmpLinks";
    private static final String API_BASE_URL = "https://emp.kfinone.com/mobile/api";

    private TextView titleText, errorText;
    private ProgressBar progressBar;
    private RecyclerView iconsRecyclerView;
    private BusinessHeadEmpLinksAdapter adapter;
    private List<BusinessHeadManageIcon> iconList = new ArrayList<>();
    private RequestQueue requestQueue;
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_business_head_emp_links);
        
        // Get user data from intent
        userName = getIntent().getStringExtra("USERNAME");
        userId = getIntent().getStringExtra("USER_ID");
        
        // Debug logging
        Log.d(TAG, "Received userName: " + userName);
        Log.d(TAG, "Received userId: " + userId);
        
        if (userName == null || userName.isEmpty()) {
            userName = "Business Head";
        }
        
        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupVolley();
        loadManageIcons();
    }

    private void initializeViews() {
        titleText = findViewById(R.id.titleText);
        progressBar = findViewById(R.id.progressBar);
        errorText = findViewById(R.id.errorText);
        iconsRecyclerView = findViewById(R.id.iconsRecyclerView);
        
        titleText.setText("My Link Headings");
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Emp Links");
        }
    }

    private void setupRecyclerView() {
        adapter = new BusinessHeadEmpLinksAdapter(this, iconList);
        iconsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        iconsRecyclerView.setAdapter(adapter);
    }

    private void setupVolley() {
        requestQueue = Volley.newRequestQueue(this);
    }

    private void loadManageIcons() {
        showLoading(true);
        hideError();
        
        // Build request body
        JSONObject requestBody = new JSONObject();
        try {
            if (userId != null && !userId.isEmpty()) {
                requestBody.put("user_id", userId);
            } else if (userName != null && !userName.isEmpty()) {
                requestBody.put("username", userName);
            } else {
                showError("No user information available");
                return;
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error creating request body: " + e.getMessage());
            showError("Error creating request: " + e.getMessage());
            return;
        }

        String url = API_BASE_URL + "/get_business_head_manage_icons.php";
        Log.d(TAG, "Making API call to: " + url);
        Log.d(TAG, "Request body: " + requestBody.toString());

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
            Request.Method.POST,
            url,
            requestBody,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "API Response: " + response.toString());
                    parseManageIconsResponse(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Volley Error: " + error.getMessage());
                    String errorMessage = "Network error: " + error.getMessage();
                    if (error.networkResponse != null) {
                        errorMessage += " (Status: " + error.networkResponse.statusCode + ")";
                    }
                    showError(errorMessage);
                }
            }
        );

        requestQueue.add(jsonRequest);
    }

    private void parseManageIconsResponse(JSONObject response) {
        try {
            String status = response.getString("status");
            
            if ("success".equals(status)) {
                JSONArray data = response.getJSONArray("data");
                iconList.clear();
                
                for (int i = 0; i < data.length(); i++) {
                    JSONObject iconObj = data.getJSONObject(i);
                    BusinessHeadManageIcon icon = new BusinessHeadManageIcon(
                        iconObj.optString("id"),
                        iconObj.optString("icon_name"),
                        iconObj.optString("icon_url"),
                        iconObj.optString("icon_image"),
                        iconObj.optString("icon_description"),
                        iconObj.optString("status")
                    );
                    iconList.add(icon);
                }
                
                adapter.notifyDataSetChanged();
                showLoading(false);
                
                if (iconList.isEmpty()) {
                    showError("No manage icons found for this user");
                } else {
                    Toast.makeText(this, "Found " + iconList.size() + " icons", Toast.LENGTH_SHORT).show();
                }
                
            } else {
                String message = response.optString("message", "Unknown error");
                showError("API Error: " + message);
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "JSON Parse Error: " + e.getMessage());
            showError("Error parsing response: " + e.getMessage());
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        iconsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        showLoading(false);
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
        Log.e(TAG, "Error: " + message);
    }

    private void hideError() {
        errorText.setVisibility(View.GONE);
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
}
