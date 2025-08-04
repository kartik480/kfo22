package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
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

public class BusinessHeadActiveEmpListActivity extends AppCompatActivity implements BusinessHeadEmployeeAdapter.OnEmployeeClickListener {

    private static final String TAG = "BusinessHeadActiveEmp";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    private View backButton;
    private TextView titleText;
    private TextView employeeCountText;
    private RecyclerView employeeRecyclerView;
    private ProgressBar loadingProgressBar;
    private LinearLayout emptyStateLayout;

    private BusinessHeadEmployeeAdapter adapter;
    private List<BusinessHeadEmployee> employeeList;
    private RequestQueue requestQueue;

    private String userName;
    private String userId;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_head_active_emp_list);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        loadActiveEmployeeList();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        employeeCountText = findViewById(R.id.employeeCountText);
        employeeRecyclerView = findViewById(R.id.employeeRecyclerView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        
        // Set title
        titleText.setText("Active Employee List");
        
        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);
    }

    private void setupRecyclerView() {
        employeeList = new ArrayList<>();
        adapter = new BusinessHeadEmployeeAdapter(this, employeeList, this);
        employeeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        employeeRecyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> goBack());
    }

    private void goBack() {
        Intent intent = new Intent(this, BusinessHeadEmpMasterActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void passUserDataToIntent(Intent intent) {
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }

    private void loadActiveEmployeeList() {
        showLoading(true);
        
        // Build URL with username parameter
        String url = BASE_URL + "get_business_head_active_employees.php";
        if (userName != null && !userName.isEmpty()) {
            url += "?username=" + userName;
        }
        
        Log.d(TAG, "Fetching active employees from: " + url);
        
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    showLoading(false);
                    Log.d(TAG, "API Response: " + response.toString());
                    
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray data = response.getJSONArray("data");
                            int totalUsers = response.optInt("total_users", 0);
                            
                            Log.d(TAG, "Found " + data.length() + " employees");
                            
                            employeeList.clear();
                            
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject user = data.getJSONObject(i);
                                
                                BusinessHeadEmployee employee = new BusinessHeadEmployee(
                                    user.optString("id", ""),
                                    user.optString("username", ""),
                                    user.optString("firstName", ""),
                                    user.optString("lastName", ""),
                                    user.optString("fullName", ""),
                                    user.optString("mobile", ""),
                                    user.optString("email_id", ""),
                                    user.optString("password", "")
                                );
                                
                                // Set additional fields
                                employee.setDob(user.optString("dob", ""));
                                employee.setEmployeeNo(user.optString("employee_no", ""));
                                employee.setFatherName(user.optString("father_name", ""));
                                employee.setJoiningDate(user.optString("joining_date", ""));
                                employee.setDepartmentId(user.optString("department_id", ""));
                                employee.setDesignationId(user.optString("designation_id", ""));
                                employee.setBranchStateNameId(user.optString("branch_state_name_id", ""));
                                employee.setBranchLocationId(user.optString("branch_location_id", ""));
                                employee.setPresentAddress(user.optString("present_address", ""));
                                employee.setPermanentAddress(user.optString("permanent_address", ""));
                                employee.setStatus(user.optString("status", ""));
                                employee.setRank(user.optString("rank", ""));
                                employee.setAvatar(user.optString("avatar", ""));
                                employee.setHeight(user.optString("height", ""));
                                employee.setWeight(user.optString("weight", ""));
                                employee.setPassportNo(user.optString("passport_no", ""));
                                employee.setPassportValid(user.optString("passport_valid", ""));
                                employee.setLanguages(user.optString("languages", ""));
                                employee.setHobbies(user.optString("hobbies", ""));
                                employee.setBloodGroup(user.optString("blood_group", ""));
                                employee.setEmergencyNo(user.optString("emergency_no", ""));
                                employee.setEmergencyAddress(user.optString("emergency_address", ""));
                                employee.setReferenceName(user.optString("reference_name", ""));
                                employee.setReferenceRelation(user.optString("reference_relation", ""));
                                employee.setReferenceMobile(user.optString("reference_mobile", ""));
                                employee.setReferenceAddress(user.optString("reference_address", ""));
                                employee.setReferenceName2(user.optString("reference_name2", ""));
                                employee.setReferenceRelation2(user.optString("reference_relation2", ""));
                                employee.setReferenceMobile2(user.optString("reference_mobile2", ""));
                                employee.setReferenceAddress2(user.optString("reference_address2", ""));
                                employee.setAccHolderName(user.optString("acc_holder_name", ""));
                                employee.setBankName(user.optString("bank_name", ""));
                                employee.setBranchName(user.optString("branch_name", ""));
                                employee.setAccountNumber(user.optString("account_number", ""));
                                employee.setIfscCode(user.optString("ifsc_code", ""));
                                employee.setSchoolMarksCard(user.optString("school_marksCard", ""));
                                employee.setIntermediateMarksCard(user.optString("intermediate_marksCard", ""));
                                employee.setDegreeCertificate(user.optString("degree_certificate", ""));
                                employee.setPgCertificate(user.optString("pg_certificate", ""));
                                employee.setExperienceLetter(user.optString("experience_letter", ""));
                                employee.setRelievingLetter(user.optString("relieving_letter", ""));
                                employee.setBankPassbook(user.optString("bank_passbook", ""));
                                employee.setPassportDocument(user.optString("passport_document", ""));
                                employee.setAadharDocument(user.optString("aadhar_document", ""));
                                employee.setPancardDocument(user.optString("pancard_document", ""));
                                employee.setResumeDocument(user.optString("resume_document", ""));
                                employee.setJoiningKitDocument(user.optString("joiningKit_document", ""));
                                employee.setReportingTo(user.optString("reportingTo", ""));
                                employee.setOfficialPhone(user.optString("official_phone", ""));
                                employee.setOfficialEmail(user.optString("official_email", ""));
                                employee.setWorkState(user.optString("work_state", ""));
                                employee.setWorkLocation(user.optString("work_location", ""));
                                employee.setAliasName(user.optString("alias_name", ""));
                                employee.setResidentialAddress(user.optString("residential_address", ""));
                                employee.setOfficeAddress(user.optString("office_address", ""));
                                employee.setPanNumber(user.optString("pan_number", ""));
                                employee.setAadhaarNumber(user.optString("aadhaar_number", ""));
                                employee.setAlternativeMobileNumber(user.optString("alternative_mobile_number", ""));
                                employee.setCompanyName(user.optString("company_name", ""));
                                employee.setManageIcons(user.optString("manage_icons", ""));
                                employee.setDataIcons(user.optString("data_icons", ""));
                                employee.setWorkIcons(user.optString("work_icons", ""));
                                employee.setLastWorkingDate(user.optString("last_working_date", ""));
                                employee.setLeavingReason(user.optString("leaving_reason", ""));
                                employee.setReJoiningDate(user.optString("re_joining_date", ""));
                                employee.setCreatedBy(user.optString("createdBy", ""));
                                employee.setCreatedAt(user.optString("created_at", ""));
                                employee.setUpdatedAt(user.optString("updated_at", ""));
                                
                                employeeList.add(employee);
                            }
                            
                            adapter.notifyDataSetChanged();
                            updateEmployeeCount(totalUsers);
                            
                            if (employeeList.isEmpty()) {
                                showEmptyState(true);
                            } else {
                                showEmptyState(false);
                            }
                            
                        } else {
                            String errorMsg = response.optString("message", "Unknown error occurred");
                            Toast.makeText(BusinessHeadActiveEmpListActivity.this, "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                            showEmptyState(true);
                        }
                        
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing response", e);
                        Toast.makeText(BusinessHeadActiveEmpListActivity.this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        showEmptyState(true);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showLoading(false);
                    Log.e(TAG, "Error fetching employees", error);
                    
                    String errorMessage = "Network error occurred";
                    if (error.networkResponse != null) {
                        try {
                            String errorResponse = new String(error.networkResponse.data);
                            JSONObject errorJson = new JSONObject(errorResponse);
                            errorMessage = errorJson.optString("message", "Server error occurred");
                        } catch (Exception e) {
                            errorMessage = "Server error: " + error.networkResponse.statusCode;
                        }
                    }
                    
                    Toast.makeText(BusinessHeadActiveEmpListActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    showEmptyState(true);
                }
            }
        );
        
        requestQueue.add(jsonObjectRequest);
    }

    private void showLoading(boolean show) {
        loadingProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        employeeRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        emptyStateLayout.setVisibility(View.GONE);
    }

    private void showEmptyState(boolean show) {
        emptyStateLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        employeeRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateEmployeeCount(int count) {
        employeeCountText.setText("Total Employees: " + count);
    }

    @Override
    public void onEmployeeClick(BusinessHeadEmployee employee) {
        // Show employee details dialog
        showEmployeeDetailsDialog(employee);
    }

    private void showEmployeeDetailsDialog(BusinessHeadEmployee employee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Employee Details");
        
        StringBuilder details = new StringBuilder();
        details.append("Name: ").append(employee.getFullName()).append("\n\n");
        details.append("Employee ID: ").append(employee.getEmployeeNo()).append("\n\n");
        details.append("Phone: ").append(employee.getMobile() != null ? employee.getMobile() : "N/A").append("\n\n");
        details.append("Email: ").append(employee.getEmailId() != null ? employee.getEmailId() : "N/A").append("\n\n");
        details.append("Username: ").append(employee.getUsername() != null ? employee.getUsername() : "N/A").append("\n\n");
        details.append("Status: ").append(employee.getStatus() != null ? employee.getStatus() : "N/A").append("\n\n");
        details.append("Company: ").append(employee.getCompanyName() != null ? employee.getCompanyName() : "N/A").append("\n\n");
        details.append("Joining Date: ").append(employee.getJoiningDate() != null ? employee.getJoiningDate() : "N/A").append("\n\n");
        details.append("PAN Number: ").append(employee.getPanNumber() != null ? employee.getPanNumber() : "N/A").append("\n\n");
        details.append("Aadhaar Number: ").append(employee.getAadhaarNumber() != null ? employee.getAadhaarNumber() : "N/A");
        
        builder.setMessage(details.toString());
        builder.setPositiveButton("Close", null);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
} 