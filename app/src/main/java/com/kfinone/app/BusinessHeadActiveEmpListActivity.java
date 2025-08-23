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

public class BusinessHeadActiveEmpListActivity extends AppCompatActivity {
    private static final String TAG = "BusinessHeadActiveEmpList";
    private static final String API_BASE_URL = "https://emp.kfinone.com/mobile/api";

    private TextView titleText, errorText;
    private ProgressBar progressBar;
    private RecyclerView usersRecyclerView;
    private BusinessHeadActiveEmpListAdapter adapter;
    private List<BusinessHeadUser> userList = new ArrayList<>();
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
        setContentView(R.layout.activity_business_head_active_emp_list);

        userName = getIntent().getStringExtra("USERNAME");
        userId = getIntent().getStringExtra("USER_ID");

        Log.d(TAG, "Received userName: " + userName);
        Log.d(TAG, "Received userId: " + userId);

        if (userName == null || userName.isEmpty()) {
            userName = "Business Head";
        }

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupVolley();
        loadActiveEmployees();
    }

    private void initializeViews() {
        titleText = findViewById(R.id.titleText);
        progressBar = findViewById(R.id.progressBar);
        errorText = findViewById(R.id.errorText);
        usersRecyclerView = findViewById(R.id.usersRecyclerView);

        titleText.setText("Employee List");
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("All Employees");
        }
    }

    private void setupRecyclerView() {
        adapter = new BusinessHeadActiveEmpListAdapter(this, userList);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(adapter);
    }

    private void setupVolley() {
        requestQueue = Volley.newRequestQueue(this);
    }

    private void loadActiveEmployees() {
        showLoading(true);
        hideError();

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

        String url = API_BASE_URL + "/get_business_head_active_emp_list.php";
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
                    parseActiveEmployeesResponse(response);
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

    private void parseActiveEmployeesResponse(JSONObject response) {
        try {
            String status = response.getString("status");

            if ("success".equals(status)) {
                JSONArray users = response.getJSONArray("users");
                userList.clear();

                for (int i = 0; i < users.length(); i++) {
                    JSONObject userObj = users.getJSONObject(i);
                    BusinessHeadUser user = new BusinessHeadUser(
                        userObj.optString("id"),
                        userObj.optString("username"),
                        userObj.optString("alias_name"),
                        userObj.optString("first_name"),
                        userObj.optString("last_name"),
                        userObj.optString("password"),
                        userObj.optString("Phone_number"),
                        userObj.optString("email_id"),
                        userObj.optString("alternative_mobile_number"),
                        userObj.optString("company_name"),
                        userObj.optString("branch_state_name_id"),
                        userObj.optString("branch_location_id"),
                        userObj.optString("bank_id"),
                        userObj.optString("account_type_id"),
                        userObj.optString("office_address"),
                        userObj.optString("residential_address"),
                        userObj.optString("aadhaar_number"),
                        userObj.optString("pan_number"),
                        userObj.optString("account_number"),
                        userObj.optString("ifsc_code"),
                        userObj.optString("rank"),
                        userObj.optString("status"),
                        userObj.optString("reportingTo"),
                        userObj.optString("pan_img"),
                        userObj.optString("aadhaar_img"),
                        userObj.optString("photo_img"),
                        userObj.optString("bankproof_img"),
                        userObj.optString("resume_file"),
                        userObj.optString("data_icons"),
                        userObj.optString("work_icons"),
                        userObj.optString("user_id"),
                        userObj.optString("createdBy"),
                        userObj.optString("created_at"),
                        userObj.optString("updated_at")
                    );
                    userList.add(user);
                }

                adapter.notifyDataSetChanged();
                showLoading(false);

                if (userList.isEmpty()) {
                    showError("No employees found for this user");
                } else {
                    Toast.makeText(this, "Found " + userList.size() + " employees", Toast.LENGTH_SHORT).show();
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
        usersRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
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