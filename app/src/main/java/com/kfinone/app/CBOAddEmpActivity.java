package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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
import org.json.JSONObject;
import org.json.JSONException;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;
import android.widget.EditText;

public class CBOAddEmpActivity extends AppCompatActivity {

    // Top navigation elements
    private View backButton;

    // User data
    private String userName;
    private String userId;

    // Declare input fields
    EditText usernameInput, firstNameInput, lastNameInput, emailInput, passwordInput, employeeIdInput, aliasNameInput, residentialAddressInput, officeAddressInput, panNumberInput, aadhaarNumberInput, altPhoneInput, companyNameInput, accountNumberInput, ifscCodeInput, phoneInput;

    // Dropdown fields
    private AutoCompleteTextView branchStateInput, branchLocationInput, bankNameInput, accountTypeInput, reportingToInput;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbo_add_emp);

        // Get user data from intent
        Intent intent = getIntent();
        userName = intent.getStringExtra("USERNAME");
        userId = intent.getStringExtra("USER_ID");

        initializeViews();
        setupClickListeners();
        loadAddEmployeeData();

        // Initialize EditText fields
        usernameInput = findViewById(R.id.usernameInput);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        employeeIdInput = findViewById(R.id.employeeIdInput);
        aliasNameInput = findViewById(R.id.aliasNameInput);
        residentialAddressInput = findViewById(R.id.residentialAddressInput);
        officeAddressInput = findViewById(R.id.officeAddressInput);
        panNumberInput = findViewById(R.id.panNumberInput);
        aadhaarNumberInput = findViewById(R.id.aadhaarNumberInput);
        altPhoneInput = findViewById(R.id.altPhoneInput);
        companyNameInput = findViewById(R.id.companyNameInput);
        accountNumberInput = findViewById(R.id.accountNumberInput);
        ifscCodeInput = findViewById(R.id.ifscCodeInput);
        phoneInput = findViewById(R.id.phoneInput);

        // Initialize dropdowns
        branchStateInput = findViewById(R.id.branchStateInput);
        branchLocationInput = findViewById(R.id.branchLocationInput);
        bankNameInput = findViewById(R.id.bankNameInput);
        accountTypeInput = findViewById(R.id.accountTypeInput);
        reportingToInput = findViewById(R.id.reportingToInput);

        fetchDropdownOptions();
        findViewById(R.id.submitButton).setOnClickListener(v -> submitEmployeeForm());
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        // Top navigation
        backButton.setOnClickListener(v -> goBack());
    }

    private void goBack() {
        Intent intent = new Intent(this, CBOEmpMasterActivity.class);
        passUserDataToIntent(intent);
        startActivity(intent);
        finish();
    }

    private void refreshData() {
        Toast.makeText(this, "Refreshing add employee form...", Toast.LENGTH_SHORT).show();
        loadAddEmployeeData();
    }

    private void saveNewEmployee() {
        Toast.makeText(this, "Saving new employee...", Toast.LENGTH_SHORT).show();
        // TODO: Implement employee saving logic
        // For now, just show a success message and go back
        Toast.makeText(this, "Employee added successfully!", Toast.LENGTH_SHORT).show();
        goBack();
    }

    private void loadAddEmployeeData() {
        // TODO: Load any necessary data for the add employee form
        // For now, show placeholder content
        Toast.makeText(this, "Loading add employee form...", Toast.LENGTH_SHORT).show();
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        intent.putExtra("SOURCE_PANEL", "CBO_PANEL");
    }

    private void fetchDropdownOptions() {
        String url = "https://emp.kfinone.com/mobile/api/cboAddEmployee.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        // Branch States
                        JSONArray branchStates = response.getJSONArray("branch_states");
                        ArrayAdapter<String> branchStateAdapter = new ArrayAdapter<>(CBOAddEmpActivity.this, android.R.layout.simple_dropdown_item_1line);
                        for (int i = 0; i < branchStates.length(); i++) {
                            branchStateAdapter.add(branchStates.getString(i));
                        }
                        branchStateInput.setAdapter(branchStateAdapter);

                        // Branch Locations
                        JSONArray branchLocations = response.getJSONArray("branch_locations");
                        ArrayAdapter<String> branchLocationAdapter = new ArrayAdapter<>(CBOAddEmpActivity.this, android.R.layout.simple_dropdown_item_1line);
                        for (int i = 0; i < branchLocations.length(); i++) {
                            branchLocationAdapter.add(branchLocations.getString(i));
                        }
                        branchLocationInput.setAdapter(branchLocationAdapter);

                        // Bank Names
                        JSONArray bankNames = response.getJSONArray("bank_names");
                        ArrayAdapter<String> bankNameAdapter = new ArrayAdapter<>(CBOAddEmpActivity.this, android.R.layout.simple_dropdown_item_1line);
                        for (int i = 0; i < bankNames.length(); i++) {
                            bankNameAdapter.add(bankNames.getString(i));
                        }
                        bankNameInput.setAdapter(bankNameAdapter);

                        // Account Types
                        JSONArray accountTypes = response.getJSONArray("account_types");
                        ArrayAdapter<String> accountTypeAdapter = new ArrayAdapter<>(CBOAddEmpActivity.this, android.R.layout.simple_dropdown_item_1line);
                        for (int i = 0; i < accountTypes.length(); i++) {
                            accountTypeAdapter.add(accountTypes.getString(i));
                        }
                        accountTypeInput.setAdapter(accountTypeAdapter);

                        // Reporting To
                        JSONArray reportingTo = response.getJSONArray("reporting_to");
                        ArrayAdapter<String> reportingToAdapter = new ArrayAdapter<>(CBOAddEmpActivity.this, android.R.layout.simple_dropdown_item_1line);
                        for (int i = 0; i < reportingTo.length(); i++) {
                            JSONObject user = reportingTo.getJSONObject(i);
                            String name = user.getString("firstName") + " " + user.getString("lastName") + " (" + user.getString("designation_id") + ")";
                            reportingToAdapter.add(name);
                        }
                        reportingToInput.setAdapter(reportingToAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CBOAddEmpActivity.this, "Error parsing dropdown data", Toast.LENGTH_SHORT).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(CBOAddEmpActivity.this, "Failed to fetch dropdown options", Toast.LENGTH_SHORT).show();
                }
            }
        );
        queue.add(jsonObjectRequest);
    }

    private void submitEmployeeForm() {
        String url = "https://emp.kfinone.com/mobile/api/cboAddEmployeeSubmit.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
            response -> {
                Toast.makeText(this, "Employee added successfully!", Toast.LENGTH_SHORT).show();
                // Optionally clear form or finish()
            },
            error -> {
                Toast.makeText(this, "Failed to add employee", Toast.LENGTH_SHORT).show();
            }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", usernameInput.getText().toString());
                params.put("firstName", firstNameInput.getText().toString());
                params.put("lastName", lastNameInput.getText().toString());
                params.put("mobile", phoneInput.getText().toString());
                params.put("email_id", emailInput.getText().toString());
                params.put("password", passwordInput.getText().toString());
                params.put("employee_no", employeeIdInput.getText().toString());
                params.put("alias_name", aliasNameInput.getText().toString());
                params.put("residential_address", residentialAddressInput.getText().toString());
                params.put("office_address", officeAddressInput.getText().toString());
                params.put("pan_number", panNumberInput.getText().toString());
                params.put("aadhaar_number", aadhaarNumberInput.getText().toString());
                params.put("alternative_mobile_number", altPhoneInput.getText().toString());
                params.put("company_name", companyNameInput.getText().toString());
                params.put("account_number", accountNumberInput.getText().toString());
                params.put("bank_name", bankNameInput.getText().toString());
                params.put("ifsc_code", ifscCodeInput.getText().toString());
                params.put("account_type", accountTypeInput.getText().toString());
                params.put("branch_state_name_id", branchStateInput.getText().toString());
                params.put("branch_location_id", branchLocationInput.getText().toString());
                params.put("reportingTo", reportingToInput.getText().toString());
                params.put("createdBy", "CBO"); // Or use actual user/session info
                return params;
            }
        };

        queue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
} 