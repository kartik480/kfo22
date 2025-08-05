package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import org.json.JSONArray;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;

public class DirectorTeamEmpLinksActivity extends AppCompatActivity {
    
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;
    private Spinner userSpinner;
    public static class UserItem {
        public final String id;
        public final String firstName;
        public final String lastName;
        public final String designationName;
        public UserItem(String id, String firstName, String lastName, String designationName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.designationName = designationName;
        }
        public String getDisplayName() {
            return firstName + " " + lastName + " (" + designationName + ")";
        }
        @Override
        public String toString() { return getDisplayName(); }
    }
    private List<UserItem> userList = new ArrayList<>();
    private ArrayAdapter<UserItem> userAdapter;
    private RecyclerView reportingUsersRecyclerView;
    private ReportingUserAdapter reportingUserAdapter;
    private List<ReportingUser> reportingUserList = new ArrayList<>();
    private UserItem selectedCboUser = null;

    // Model for reporting user
    public static class ReportingUser {
        public final String username;
        public final String fullname;
        public final String mobile;
        public final String email;
        public final String reportingTo;
        public final String employeeNo;
        public final List<String> manageIcons;
        public ReportingUser(String username, String fullname, String mobile, String email, String reportingTo, String employeeNo, List<String> manageIcons) {
            this.username = username;
            this.fullname = fullname;
            this.mobile = mobile;
            this.email = email;
            this.reportingTo = reportingTo;
            this.employeeNo = employeeNo;
            this.manageIcons = manageIcons;
        }
    }

    // Adapter for reporting users
    public class ReportingUserAdapter extends RecyclerView.Adapter<ReportingUserAdapter.ViewHolder> {
        private final List<ReportingUser> users;
        public ReportingUserAdapter(List<ReportingUser> users) { this.users = users; }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_reporting_user, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ReportingUser user = users.get(position);
            holder.employeeName.setText("Name: " + user.fullname);
            holder.employeeId.setText("Employee ID: " + (user.employeeNo != null ? user.employeeNo : "N/A"));
            holder.mobile.setText("Phone: " + (user.mobile != null ? user.mobile : "N/A"));
            holder.email.setText("Email: " + (user.email != null ? user.email : "N/A"));
            
            // Format manage icons
            if (user.manageIcons != null && !user.manageIcons.isEmpty()) {
                holder.manageIcons.setText("Manage Icons: " + String.join(", ", user.manageIcons));
            } else {
                holder.manageIcons.setText("Manage Icons: None");
            }
        }
        @Override
        public int getItemCount() { return users.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView employeeName, employeeId, mobile, email, manageIcons;
            ViewHolder(View itemView) {
                super(itemView);
                employeeName = itemView.findViewById(R.id.employeeName);
                employeeId = itemView.findViewById(R.id.employeeId);
                mobile = itemView.findViewById(R.id.mobile);
                email = itemView.findViewById(R.id.email);
                manageIcons = itemView.findViewById(R.id.manageIcons);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_team_emp_links);

        // Get user data from intent
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            userName = intent.getStringExtra("USERNAME");
            firstName = intent.getStringExtra("FIRST_NAME");
            lastName = intent.getStringExtra("LAST_NAME");
        }

        setupToolbar();
        userSpinner = findViewById(R.id.userSpinner);
        userAdapter = new ArrayAdapter<UserItem>(
                this,
                R.layout.item_user_spinner,
                R.id.userSpinnerText,
                userList
        ) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView tv = v.findViewById(R.id.userSpinnerText);
                tv.setText(getItem(position).getDisplayName());
                return v;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                TextView tv = v.findViewById(R.id.userSpinnerText);
                tv.setText(getItem(position).getDisplayName());
                return v;
            }
        };
        userSpinner.setAdapter(userAdapter);
        fetchChiefBusinessOfficers();
        reportingUsersRecyclerView = findViewById(R.id.reportingUsersRecyclerView);
        reportingUserAdapter = new ReportingUserAdapter(reportingUserList);
        reportingUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportingUsersRecyclerView.setAdapter(reportingUserAdapter);
        userSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedCboUser = userAdapter.getItem(position);
                fetchReportingUsersForCbo(selectedCboUser);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                reportingUserList.clear();
                reportingUserAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Director Team Employee Links");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }

    private void fetchChiefBusinessOfficers() {
        String url = "https://emp.kfinone.com/mobile/api/get_chief_business_officers.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if ("success".equals(response.getString("status"))) {
                            JSONArray data = response.getJSONArray("data");
                            userList.clear();
                            if (data.length() == 0) {
                                Toast.makeText(DirectorTeamEmpLinksActivity.this, "No Chief Business Officer found.", Toast.LENGTH_LONG).show();
                            }
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject user = data.getJSONObject(i);
                                String id = user.optString("id", "");
                                String firstName = user.optString("firstName", "");
                                String lastName = user.optString("lastName", "");
                                String designationName = user.optString("designation_name", "");
                                userList.add(new UserItem(id, firstName, lastName, designationName));
                            }
                            userAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(DirectorTeamEmpLinksActivity.this, "API error: " + response.optString("message", "Unknown error"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(DirectorTeamEmpLinksActivity.this, "Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DirectorTeamEmpLinksActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void fetchReportingUsersForCbo(UserItem cboUser) {
        if (cboUser == null) return;
        String cboId = cboUser.id;
        String url = "https://emp.kfinone.com/mobile/api/get_users_reporting_to.php?cbo_id=" + cboId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if ("success".equals(response.getString("status"))) {
                            JSONArray data = response.getJSONArray("data");
                            reportingUserList.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject user = data.getJSONObject(i);
                                
                                // Parse manage icons
                                List<String> manageIcons = new ArrayList<>();
                                if (user.has("manage_icons")) {
                                    JSONArray iconsArray = user.getJSONArray("manage_icons");
                                    for (int j = 0; j < iconsArray.length(); j++) {
                                        manageIcons.add(iconsArray.getString(j));
                                    }
                                }
                                
                                reportingUserList.add(new ReportingUser(
                                    user.optString("username", ""),
                                    user.optString("fullName", ""),
                                    user.optString("mobile", ""),
                                    user.optString("email_id", ""),
                                    user.optString("reportingTo", ""),
                                    user.optString("employee_no", ""),
                                    manageIcons
                                ));
                            }
                            reportingUserAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(DirectorTeamEmpLinksActivity.this, "API error: " + response.optString("message", "Unknown error"), Toast.LENGTH_LONG).show();
                            reportingUserList.clear();
                            reportingUserAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        Toast.makeText(DirectorTeamEmpLinksActivity.this, "Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        reportingUserList.clear();
                        reportingUserAdapter.notifyDataSetChanged();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(DirectorTeamEmpLinksActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    reportingUserList.clear();
                    reportingUserAdapter.notifyDataSetChanged();
                }
            });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
} 