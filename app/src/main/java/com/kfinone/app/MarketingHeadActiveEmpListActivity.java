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

public class MarketingHeadActiveEmpListActivity extends AppCompatActivity {
    private static final String TAG = "MarketingHeadActiveEmpList";
    private static final String API_BASE_URL = "https://emp.kfinone.com/mobile/api";

    private TextView titleText, errorText, employeeCount;
    private ProgressBar progressBar;
    private RecyclerView usersRecyclerView;
    private MarketingHeadActiveEmpListAdapter adapter;
    private List<MarketingHeadUser> userList = new ArrayList<>();
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
        setContentView(R.layout.activity_marketing_head_active_emp_list);

        userName = getIntent().getStringExtra("USERNAME");
        userId = getIntent().getStringExtra("USER_ID");

        Log.d(TAG, "Received userName: " + userName);
        Log.d(TAG, "Received userId: " + userId);

        if (userName == null || userName.isEmpty()) {
            userName = "Marketing Head";
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
        employeeCount = findViewById(R.id.employeeCount);

        titleText.setText("Marketing Head Active Employee List");
        if (employeeCount != null) {
            employeeCount.setText("Total Employees: 0");
        }
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
        adapter = new MarketingHeadActiveEmpListAdapter(this, userList);
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

        String url = API_BASE_URL + "/get_marketing_head_active_emp_list.php";
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
                JSONArray activeEmployees = response.getJSONArray("active_employees");
                int totalCount = response.optInt("total_count", 0);
                
                Log.d(TAG, "Active employees count: " + activeEmployees.length());
                Log.d(TAG, "Total count: " + totalCount);
                
                userList.clear();

                for (int i = 0; i < activeEmployees.length(); i++) {
                    JSONObject userObj = activeEmployees.getJSONObject(i);
                    
                    // Extract all the fields from tbl_user
                                              String firstName = userObj.optString("firstName", "");
                          String lastName = userObj.optString("lastName", "");
                          String phoneNumber = userObj.optString("mobile", "");
                          String email = userObj.optString("email_id", "");
                          String userStatus = userObj.optString("status", "");
                          String reportingTo = userObj.optString("reportingTo", "");
                          String aliasName = userObj.optString("alias_name", "");
                          String rank = userObj.optString("rank", "");
                          String designation = userObj.optString("designation_name", "");
                          String department = userObj.optString("department_name", "");
                          String employeeNo = userObj.optString("employeeNo", "");
                          String workState = userObj.optString("work_state", "");
                          String workLocation = userObj.optString("work_location", "");
                    
                    // Log the user data for debugging
                    Log.d(TAG, "Processing user: " + firstName + " " + lastName + " (designation: " + designation + ")");
                    
                                              MarketingHeadUser user = new MarketingHeadUser(
                              userObj.optString("id", ""),
                              userObj.optString("username", ""),
                              aliasName,
                              firstName,
                              lastName,
                              userObj.optString("password", ""),
                              phoneNumber,
                              email,
                              userObj.optString("birth_date", ""),
                              userObj.optString("branch_state_name_id", ""),
                              userObj.optString("branch_location_id", ""),
                              userObj.optString("acc_holder_name", ""),
                              userObj.optString("bank_name", ""),
                              userObj.optString("account_type", ""),
                              userObj.optString("branch_name", ""),
                              userObj.optString("account_number", ""),
                              userObj.optString("ifsc_code", ""),
                              rank,
                              userStatus,
                              reportingTo,
                              designation,
                              department,
                              employeeNo,
                              workState,
                              workLocation,
                              userObj.optString("residential_address", ""),
                              userObj.optString("office_address", ""),
                              userObj.optString("pan_number", ""),
                              userObj.optString("aadhaar_number", ""),
                              userObj.optString("manage_icons", ""),
                              userObj.optString("data_icons", ""),
                              userObj.optString("ref_name_1", ""),
                              userObj.optString("ref_relation_1", ""),
                              userObj.optString("ref_mobile_1", ""),
                              userObj.optString("ref_address_1", ""),
                              userObj.optString("ref_name_2", ""),
                              userObj.optString("ref_relation_2", ""),
                              userObj.optString("ref_mobile_2", ""),
                              userObj.optString("ref_address_2", ""),
                              userObj.optString("official_phone", ""),
                              userObj.optString("official_email", ""),
                              userObj.optString("createdBy", ""),
                              userObj.optString("created_at", ""),
                              userObj.optString("updated_at", "")
                          );
                    
                    userList.add(user);
                }

                // Update UI on main thread
                runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                    if (employeeCount != null) {
                        employeeCount.setText("Total Employees: " + totalCount);
                    }
                    showLoading(false);
                });

            } else {
                String message = response.optString("message", "Unknown error occurred");
                showError("API Error: " + message);
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage());
            showError("Error parsing response: " + e.getMessage());
        }
    }

    private void showLoading(boolean show) {
        runOnUiThread(() -> {
            if (progressBar != null) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            showLoading(false);
            if (errorText != null) {
                errorText.setText(message);
                errorText.setVisibility(View.VISIBLE);
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }

    private void hideError() {
        runOnUiThread(() -> {
            if (errorText != null) {
                errorText.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }
}
