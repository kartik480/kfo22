package com.kfinone.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

public class PartnerTeamActivity extends AppCompatActivity {
    private static final String TAG = "PartnerTeamActivity";
    private static final String BASE_URL = "https://emp.kfinone.com/mobile/api/";

    private RecyclerView teamMembersRecyclerView;
    private RecyclerView.Adapter<?> teamAdapter;
    private List<PartnerTeamMember> teamList;
    private List<PartnerTeamMember> allTeamList;
    private RequestQueue requestQueue;
    private List<User> userList;

    // UI Elements
    private ImageButton backButton;
    private Spinner userSpinner;
    private MaterialButton showDataButton;
    private MaterialButton resetButton;
    private MaterialButton filterButton;
    private LinearLayout noTeamMembersLayout;
    private LinearLayout loadingLayout;

    // Remove all references to selectedUserText, searchTeamInput, and TeamMemberCardAdapter.OnTeamMemberActionListener
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_team);
        // DIRECTOR: Initialize dropdown, buttons, and list
        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        setupVolley();
        loadUsersForDropdown();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        userSpinner = findViewById(R.id.userSpinner);
        showDataButton = findViewById(R.id.showDataButton);
        resetButton = findViewById(R.id.resetButton);
        // filterButton = findViewById(R.id.filterButton); // Not present in layout, comment out
        noTeamMembersLayout = findViewById(R.id.noTeamMembersLayout);
        loadingLayout = findViewById(R.id.loadingLayout);
        teamMembersRecyclerView = findViewById(R.id.teamMembersRecyclerView);

        if (showDataButton == null || resetButton == null) {
            android.util.Log.e("PartnerTeamActivity", "ShowDataButton or ResetButton is null! Check layout IDs.");
            throw new RuntimeException("ShowDataButton or ResetButton is null! Check layout IDs.");
        }
        if (teamMembersRecyclerView == null) {
            android.util.Log.e("PartnerTeamActivity", "teamMembersRecyclerView is null! Check layout IDs.");
            throw new RuntimeException("teamMembersRecyclerView is null! Check layout IDs.");
        }

        backButton.setOnClickListener(v -> onBackPressed());
        // filterButton.setOnClickListener(v -> showFilterDialog()); // Not present in layout
    }

    private void setupRecyclerView() {
        teamList = new ArrayList<>();
        allTeamList = new ArrayList<>();
        teamAdapter = new DirectorTeamAdapter(teamList);
        teamMembersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamMembersRecyclerView.setAdapter(teamAdapter);
    }

    private void setupSearchFunctionality() {
        // searchTeamInput.addTextChangedListener(new TextWatcher() {
        //     @Override
        //     public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        //     @Override
        //     public void onTextChanged(CharSequence s, int start, int before, int count) {
        //         filterTeamMembers(s.toString());
        //     }

        //     @Override
        //     public void afterTextChanged(Editable s) {}
        // });
    }

    private void setupVolley() {
        requestQueue = Volley.newRequestQueue(this);
    }

    private void setupClickListeners() {
        showDataButton.setOnClickListener(v -> {
            int selectedPosition = userSpinner.getSelectedItemPosition();
            if (selectedPosition == 0) {
                // Show all users (fetch all from backend)
                fetchPartnerTeamForDirector("");
            } else if (selectedPosition > 0 && selectedPosition <= userList.size()) {
                User selectedUser = userList.get(selectedPosition - 1); // -1 because first is 'Select User'
                // Fetch from backend for users created by this user
                fetchPartnerTeamForDirector(selectedUser.getId());
            } else {
                Toast.makeText(this, "Please select a user first", Toast.LENGTH_SHORT).show();
            }
        });

        resetButton.setOnClickListener(v -> {
            userSpinner.setSelection(0);
            fetchPartnerTeamForDirector("");
        });
    }

    // Fetch users from tbl_partner_users where createdBy = selected user id
    private void fetchPartnerTeamForDirector(String createdById) {
        showLoading(true);
        String url = "https://emp.kfinone.com/mobile/api/director_fetch_users_with_creator.php";
        // No createdBy parameter needed, backend handles filtering by CBO/RBH
        Log.d("PartnerTeamActivity", "Fetching users with creator (CBO/RBH only) URL: " + url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                showLoading(false);
                try {
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
                        Log.d("PartnerTeamActivity", "API returned " + data.length() + " users");
                        allTeamList = new ArrayList<>();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject userObj = data.getJSONObject(i);
                            String fullName = userObj.optString("first_name", "") + " " + userObj.optString("last_name", "");
                            String phone = userObj.optString("Phone_number", "");
                            String email = userObj.optString("email_id", "");
                            String creatorName = userObj.optString("creator_username", "");
                            if (creatorName == null || creatorName.trim().isEmpty() || creatorName.equalsIgnoreCase("null")) {
                                creatorName = "Unknown Creator";
                            }
                            allTeamList.add(new PartnerTeamMember(
                                userObj.optString("id", ""),
                                fullName,
                                phone,
                                email,
                                creatorName
                            ));
                        }
                        teamList.clear();
                        teamList.addAll(allTeamList);
                        teamAdapter.notifyDataSetChanged();
                        updateUI();
                    } else {
                        String message = response.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        showNoTeamMembersMessage();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    showNoTeamMembersMessage();
                }
            },
            error -> {
                Log.e(TAG, "Error fetching users: " + error.getMessage());
                Toast.makeText(this, "Error fetching users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        );
        requestQueue.add(request);
    }

    // getSelectedUserId is no longer needed with Spinner usage

    private void loadUsersForDropdown() {
        // Fetch all users with designation for the dropdown
        String url = "https://emp.kfinone.com/mobile/api/director_fetch_all_users_with_designation.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    String status = response.getString("status");
                    if ("success".equals(status)) {
                        JSONArray data = response.getJSONArray("data");
                        userList = new ArrayList<>();
                        List<String> userNames = new ArrayList<>();
                        userNames.add("Select User");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject userObj = data.getJSONObject(i);
                            String fullName = userObj.getString("fullName");
                            String designation = userObj.optString("designation_name", "");
                            String display = fullName;
                            if (designation != null && !designation.isEmpty() && !"null".equalsIgnoreCase(designation)) {
                                display += " (" + designation + ")";
                            }
                            userNames.add(display);
                            userList.add(new User(userObj.getString("id"), fullName));
                        }
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                            this, android.R.layout.simple_spinner_item, userNames
                        );
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        userSpinner.setAdapter(spinnerAdapter);
                    } else {
                        String message = response.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                    Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            },
            error -> {
                Log.e(TAG, "Error fetching users: " + error.getMessage());
                Toast.makeText(this, "Error fetching users: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        );
        requestQueue.add(request);
    }

    // Old loadPartnerTeamData methods removed; only fetchPartnerTeamForDirector is used for the director panel

    @Override
    protected void onResume() {
        super.onResume();
        // No-op: do not auto-load team data; only load when user selects and presses Show Data
    }

    private void filterTeamMembers(String query) {
        if (query.isEmpty()) {
            teamList.clear();
            teamList.addAll(allTeamList);
        } else {
            List<PartnerTeamMember> filteredList = new ArrayList<>();
            for (PartnerTeamMember member : allTeamList) {
                if (member.getFullName().toLowerCase().contains(query.toLowerCase()) ||
                    member.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                    member.getMobile().contains(query)) {
                    filteredList.add(member); // Just add the existing member, do not create a new one
                }
            }
            teamList.clear();
            teamList.addAll(filteredList);
        }
        teamAdapter.notifyDataSetChanged();
        updateUI();
    }

    private void showFilterDialog() {
        String[] filterOptions = {"All Members", "By Name", "By Email", "By Mobile"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Team Members")
               .setItems(filterOptions, (dialog, which) -> {
                   // For now, just show a message. In a real app, you'd implement specific filters
                   Toast.makeText(this, "Filter: " + filterOptions[which], Toast.LENGTH_SHORT).show();
               })
               .show();
    }

    // updateStats removed (no longer needed)

    private void updateUI() {
        if (teamList.isEmpty()) {
            showNoTeamMembersMessage();
        } else {
            hideNoTeamMembersMessage();
        }
    }

    private void showLoading(boolean show) {
        loadingLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        teamMembersRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        noTeamMembersLayout.setVisibility(View.GONE);
    }

    private void showNoTeamMembersMessage() {
        noTeamMembersLayout.setVisibility(View.VISIBLE);
        teamMembersRecyclerView.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
    }

    private void hideNoTeamMembersMessage() {
        noTeamMembersLayout.setVisibility(View.GONE);
        teamMembersRecyclerView.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
    }

    // User data class
    public static class User {
        private String id;
        private String name;

        public User(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public String getName() { return name; }

        @Override
        public String toString() {
            return name;
        }
    }

    // Update PartnerTeamMember to match new API: id, fullName, mobile, email, creatorName
    public static class PartnerTeamMember {
        private String id;
        private String fullName;
        private String mobile;
        private String email;
        private String creatorName;
        public PartnerTeamMember(String id, String fullName, String mobile, String email, String creatorName) {
            this.id = id;
            this.fullName = fullName;
            this.mobile = mobile;
            this.email = email;
            this.creatorName = creatorName;
        }
        public String getId() { return id; }
        public String getFullName() { return fullName; }
        public String getMobile() { return mobile; }
        public String getEmail() { return email; }
        public String getCreatorName() { return creatorName; }
    }

    // Replace TeamMemberCardAdapter with a simple RecyclerView.Adapter for the new box layout
    private class DirectorTeamAdapter extends RecyclerView.Adapter<DirectorTeamAdapter.ViewHolder> {
        private List<PartnerTeamMember> members;
        DirectorTeamAdapter(List<PartnerTeamMember> members) { this.members = members; }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.partner_team_list_item, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            PartnerTeamMember member = members.get(position);
            holder.nameText.setText("Name: " + member.getFullName());
            holder.phoneText.setText("Phone: " + member.getMobile());
            holder.emailText.setText("Email: " + member.getEmail());
            holder.createdByText.setText("Created By: " + member.getCreatorName());
            holder.actionButton.setOnClickListener(v -> {
                Toast.makeText(PartnerTeamActivity.this, "Action for " + member.getFullName(), Toast.LENGTH_SHORT).show();
            });
        }
        @Override
        public int getItemCount() { return members.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText, phoneText, emailText, createdByText;
            Button actionButton;
            ViewHolder(View view) {
                super(view);
                nameText = view.findViewById(R.id.nameText);
                phoneText = view.findViewById(R.id.phoneText);
                emailText = view.findViewById(R.id.emailText);
                createdByText = view.findViewById(R.id.createdByText);
                actionButton = view.findViewById(R.id.actionButton);
            }
        }
    }
} 