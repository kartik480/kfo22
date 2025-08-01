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

public class ManagingDirectorTeamEmpLinksActivity extends AppCompatActivity {
    
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
    private UserItem selectedUser = null;

    // Model for reporting user
    public static class ReportingUser {
        public final String username;
        public final String fullname;
        public final String mobile;
        public final String email;
        public final String reportingTo;
        public ReportingUser(String username, String fullname, String mobile, String email, String reportingTo) {
            this.username = username;
            this.fullname = fullname;
            this.mobile = mobile;
            this.email = email;
            this.reportingTo = reportingTo;
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
            holder.usernameText.setText(user.username);
            holder.fullnameText.setText(user.fullname);
            holder.mobileText.setText(user.mobile);
            holder.emailText.setText(user.email);
            holder.reportingToText.setText(user.reportingTo);
            holder.actionButton.setOnClickListener(v -> {
                // Placeholder for action
                Toast.makeText(holder.itemView.getContext(), "Action for " + user.fullname, Toast.LENGTH_SHORT).show();
            });
        }
        @Override
        public int getItemCount() { return users.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView usernameText, fullnameText, mobileText, emailText, reportingToText;
            Button actionButton;
            ViewHolder(View itemView) {
                super(itemView);
                usernameText = itemView.findViewById(R.id.usernameText);
                fullnameText = itemView.findViewById(R.id.fullnameText);
                mobileText = itemView.findViewById(R.id.mobileText);
                emailText = itemView.findViewById(R.id.emailText);
                reportingToText = itemView.findViewById(R.id.reportingToText);
                actionButton = itemView.findViewById(R.id.actionButton);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managing_director_team_emp_links);

        // Get user data from intent
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            userName = intent.getStringExtra("USERNAME");
            firstName = intent.getStringExtra("FIRST_NAME");
            lastName = intent.getStringExtra("LAST_NAME");
        }

        setupToolbar();
        initializeViews();
        setupSpinner();
        fetchUsers();
    }

    private void initializeViews() {
        userSpinner = findViewById(R.id.userSpinner);
        reportingUsersRecyclerView = findViewById(R.id.reportingUsersRecyclerView);
        reportingUserList = new ArrayList<>();
        reportingUserAdapter = new ReportingUserAdapter(reportingUserList);
        reportingUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reportingUsersRecyclerView.setAdapter(reportingUserAdapter);
    }

    private void setupSpinner() {
        userAdapter = new ArrayAdapter<UserItem>(this, android.R.layout.simple_spinner_item, userList) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView text = (TextView) super.getView(position, convertView, parent);
                text.setText(userList.get(position).getDisplayName());
                return text;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView text = (TextView) super.getDropDownView(position, convertView, parent);
                text.setText(userList.get(position).getDisplayName());
                return text;
            }
        };
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(userAdapter);
        userSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedUser = userList.get(position);
                fetchReportingUsersForUser(selectedUser);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Managing Director Team Employee Links");
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

    private void fetchUsers() {
        String url = "https://emp.kfinone.com/mobile/api/get_managing_director_users.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if ("success".equals(response.getString("status"))) {
                            JSONArray data = response.getJSONArray("data");
                            userList.clear();
                            if (data.length() == 0) {
                                Toast.makeText(ManagingDirectorTeamEmpLinksActivity.this, "No users found.", Toast.LENGTH_LONG).show();
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
                            Toast.makeText(ManagingDirectorTeamEmpLinksActivity.this, "API error: " + response.optString("message", "Unknown error"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(ManagingDirectorTeamEmpLinksActivity.this, "Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ManagingDirectorTeamEmpLinksActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void fetchReportingUsersForUser(UserItem user) {
        if (user == null) return;
        String userId = user.id;
        String url = "https://emp.kfinone.com/mobile/api/get_users_reporting_to_managing_director.php?user_id=" + userId;
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
                                reportingUserList.add(new ReportingUser(
                                    user.optString("username", ""),
                                    user.optString("fullname", ""),
                                    user.optString("mobile", ""),
                                    user.optString("email", ""),
                                    user.optString("reportingTo", "")
                                ));
                            }
                            reportingUserAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ManagingDirectorTeamEmpLinksActivity.this, "API error: " + response.optString("message", "Unknown error"), Toast.LENGTH_LONG).show();
                            reportingUserList.clear();
                            reportingUserAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        Toast.makeText(ManagingDirectorTeamEmpLinksActivity.this, "Parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        reportingUserList.clear();
                        reportingUserAdapter.notifyDataSetChanged();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ManagingDirectorTeamEmpLinksActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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