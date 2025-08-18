package com.kfinone.app;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class TeamMemberAdapter extends RecyclerView.Adapter<TeamMemberAdapter.ViewHolder> {
    private static final String TAG = "TeamMemberAdapter";
    private List<TeamMember> teamMembers;
    private Context context;

    public TeamMemberAdapter(List<TeamMember> teamMembers, Context context) {
        this.teamMembers = teamMembers;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_team_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TeamMember member = teamMembers.get(position);
        holder.employeeName.setText(member.getFullName());
        holder.employeeId.setText(member.getEmployeeNo());
        holder.designation.setText(member.getDesignation());
        holder.mobile.setText(member.getMobile());
        holder.email.setText(member.getEmail());
        
        // Set click listener for view button
        holder.viewButton.setOnClickListener(v -> showUserDetails(member));
    }

    @Override
    public int getItemCount() {
        return teamMembers.size();
    }

    private void showUserDetails(TeamMember member) {
        // Fetch detailed user information from server
        fetchUserDetails(member.getId());
    }

    private void fetchUserDetails(String userId) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Fetching user details for ID: " + userId);
                URL url = new URL("https://emp.kfinone.com/mobile/api/GetUserDetails.php?user_id=" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "GetUserDetails response code: " + responseCode);
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    String responseString = response.toString();
                    Log.d(TAG, "GetUserDetails response: " + responseString);

                    JSONObject json = new JSONObject(responseString);
                    if (json.getString("status").equals("success")) {
                        JSONObject userData = json.getJSONObject("data");
                        
                        new Handler(Looper.getMainLooper()).post(() -> {
                            showUserDetailsDialog(userId, userData);
                        });
                    } else {
                        String errorMsg = json.optString("message");
                        Log.e(TAG, "Server returned error: " + errorMsg);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            showErrorDialog("Error fetching user details: " + errorMsg);
                        });
                    }
                } else {
                    Log.e(TAG, "Server error with response code: " + responseCode);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        showErrorDialog("Server error: " + responseCode);
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception fetching user details: " + e.getMessage(), e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    showErrorDialog("Error: " + e.getMessage());
                });
            }
        }).start();
    }

    private void showUserDetailsDialog(String userId, JSONObject userData) {
        try {
            StringBuilder details = new StringBuilder();
            
            // Get available columns from the response
            JSONArray availableColumns = userData.optJSONArray("available_columns");
            if (availableColumns != null) {
                details.append("📋 AVAILABLE COLUMNS IN DATABASE\n");
                details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
                for (int i = 0; i < availableColumns.length(); i++) {
                    details.append("• ").append(availableColumns.getString(i)).append("\n");
                }
                details.append("\n");
            }
            
            // Personal Information - Basic fields that are likely to exist
            details.append("👤 BASIC INFORMATION\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            addFieldIfExists(details, "ID", userData, "id");
            addFieldIfExists(details, "Username", userData, "username");
            addFieldIfExists(details, "First Name", userData, "firstName");
            addFieldIfExists(details, "Last Name", userData, "lastName");
            addFieldIfExists(details, "Employee No", userData, "employee_no");
            addFieldIfExists(details, "Status", userData, "status");
            addFieldIfExists(details, "Rank", userData, "rank");
            details.append("\n");
            
            // Contact Information
            details.append("📞 CONTACT INFORMATION\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            addFieldIfExists(details, "Mobile", userData, "mobile");
            addFieldIfExists(details, "Email", userData, "email_id");
            addFieldIfExists(details, "Alternative Mobile", userData, "alternative_mobile_number");
            addFieldIfExists(details, "Official Phone", userData, "official_phone");
            addFieldIfExists(details, "Official Email", userData, "official_email");
            details.append("\n");
            
            // Employment Information
            details.append("💼 EMPLOYMENT INFORMATION\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            addFieldIfExists(details, "Joining Date", userData, "joining_date");
            addFieldIfExists(details, "Department ID", userData, "department_id");
            addFieldIfExists(details, "Designation ID", userData, "designation_id");
            addFieldIfExists(details, "Branch State", userData, "branch_state_name_id");
            addFieldIfExists(details, "Branch Location", userData, "branch_location_id");
            addFieldIfExists(details, "Reporting To", userData, "reportingTo");
            addFieldIfExists(details, "Work State", userData, "work_state");
            addFieldIfExists(details, "Work Location", userData, "work_location");
            addFieldIfExists(details, "Company Name", userData, "company_name");
            details.append("\n");
            
            // Address Information
            details.append("🏠 ADDRESS INFORMATION\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            addFieldIfExists(details, "Present Address", userData, "present_address");
            addFieldIfExists(details, "Permanent Address", userData, "permanent_address");
            addFieldIfExists(details, "Residential Address", userData, "residential_address");
            addFieldIfExists(details, "Office Address", userData, "office_address");
            details.append("\n");
            
            // Additional Personal Information
            details.append("🔍 ADDITIONAL PERSONAL INFO\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            addFieldIfExists(details, "Father's Name", userData, "father_name");
            addFieldIfExists(details, "Blood Group", userData, "blood_group");
            addFieldIfExists(details, "Height", userData, "height");
            addFieldIfExists(details, "Weight", userData, "weight");
            addFieldIfExists(details, "Alias Name", userData, "alias_name");
            addFieldIfExists(details, "Languages", userData, "languages");
            addFieldIfExists(details, "Hobbies", userData, "hobbies");
            addFieldIfExists(details, "PAN Number", userData, "pan_number");
            addFieldIfExists(details, "Aadhaar Number", userData, "aadhaar_number");
            details.append("\n");
            
            // Emergency & Reference Information
            details.append("🚨 EMERGENCY & REFERENCES\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            addFieldIfExists(details, "Emergency Number", userData, "emergency_no");
            addFieldIfExists(details, "Emergency Address", userData, "emergency_address");
            addFieldIfExists(details, "Reference 1 Name", userData, "reference_name");
            addFieldIfExists(details, "Reference 1 Relation", userData, "reference_relation");
            addFieldIfExists(details, "Reference 1 Mobile", userData, "reference_mobile");
            addFieldIfExists(details, "Reference 1 Address", userData, "reference_address");
            addFieldIfExists(details, "Reference 2 Name", userData, "reference_name2");
            addFieldIfExists(details, "Reference 2 Relation", userData, "reference_relation2");
            addFieldIfExists(details, "Reference 2 Mobile", userData, "reference_mobile2");
            addFieldIfExists(details, "Reference 2 Address", userData, "reference_address2");
            details.append("\n");
            
            // Banking Information
            details.append("🏦 BANKING INFORMATION\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            addFieldIfExists(details, "Account Holder", userData, "acc_holder_name");
            addFieldIfExists(details, "Bank Name", userData, "bank_name");
            addFieldIfExists(details, "Branch Name", userData, "branch_name");
            addFieldIfExists(details, "Account Number", userData, "account_number");
            addFieldIfExists(details, "IFSC Code", userData, "ifsc_code");
            details.append("\n");
            
            // Documents & Certificates
            details.append("📄 DOCUMENTS & CERTIFICATES\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            addFieldIfExists(details, "School Marks Card", userData, "school_marksCard");
            addFieldIfExists(details, "Intermediate Marks", userData, "intermediate_marksCard");
            addFieldIfExists(details, "Degree Certificate", userData, "degree_certificate");
            addFieldIfExists(details, "PG Certificate", userData, "pg_certificate");
            addFieldIfExists(details, "Experience Letter", userData, "experience_letter");
            addFieldIfExists(details, "Relieving Letter", userData, "relieving_letter");
            addFieldIfExists(details, "Bank Passbook", userData, "bank_passbook");
            addFieldIfExists(details, "Passport Document", userData, "passport_document");
            addFieldIfExists(details, "Aadhar Document", userData, "aadhar_document");
            addFieldIfExists(details, "PAN Card Document", userData, "pancard_document");
            addFieldIfExists(details, "Resume Document", userData, "resume_document");
            addFieldIfExists(details, "Joining Kit Document", userData, "joiningKit_document");
            details.append("\n");
            
            // Passport & Travel Information
            details.append("🛂 PASSPORT & TRAVEL\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            addFieldIfExists(details, "Passport Number", userData, "passport_no");
            addFieldIfExists(details, "Passport Valid", userData, "passport_valid");
            details.append("\n");
            
            // System & Permission Information
            details.append("⚙️ SYSTEM & PERMISSIONS\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            addFieldIfExists(details, "Avatar", userData, "avatar");
            addFieldIfExists(details, "Manage Icons", userData, "manage_icons");
            addFieldIfExists(details, "Data Icons", userData, "data_icons");
            addFieldIfExists(details, "Work Icons", userData, "work_icons");
            addFieldIfExists(details, "Payout Icons", userData, "payout_icons");
            details.append("\n");
            
            // Work History
            details.append("📅 WORK HISTORY\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            addFieldIfExists(details, "Last Working Date", userData, "last_working_date");
            addFieldIfExists(details, "Leaving Reason", userData, "leaving_reason");
            addFieldIfExists(details, "Re-joining Date", userData, "re_joining_date");
            details.append("\n");
            
            // System Timestamps
            details.append("🕒 SYSTEM TIMESTAMPS\n");
            details.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            addFieldIfExists(details, "Created By", userData, "createdBy");
            addFieldIfExists(details, "Created At", userData, "created_at");
            addFieldIfExists(details, "Updated At", userData, "updated_at");

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("📋 Complete User Details - " + userData.optString("firstName", "") + " " + userData.optString("lastName", ""));
            builder.setMessage(details.toString());
            builder.setPositiveButton("Close", null);
            
            AlertDialog dialog = builder.create();
            dialog.show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating user details dialog: " + e.getMessage(), e);
            showErrorDialog("Error displaying user details: " + e.getMessage());
        }
    }

    private void addFieldIfExists(StringBuilder details, String label, JSONObject userData, String fieldName) {
        try {
            if (userData.has(fieldName) && !userData.isNull(fieldName)) {
                String value = userData.optString(fieldName, "");
                if (!value.isEmpty() && !value.equals("null")) {
                    details.append(label).append(": ").append(value).append("\n");
                }
            }
        } catch (Exception e) {
            // Skip this field if there's an error
        }
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Error");
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView employeeName, employeeId, designation, mobile, email;
        Button viewButton;

        ViewHolder(View itemView) {
            super(itemView);
            employeeName = itemView.findViewById(R.id.employeeName);
            employeeId = itemView.findViewById(R.id.employeeId);
            designation = itemView.findViewById(R.id.designation);
            mobile = itemView.findViewById(R.id.mobile);
            email = itemView.findViewById(R.id.email);
            viewButton = itemView.findViewById(R.id.viewButton);
        }
    }
} 