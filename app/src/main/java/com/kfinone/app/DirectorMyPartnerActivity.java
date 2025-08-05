package com.kfinone.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DirectorMyPartnerActivity extends AppCompatActivity {
    private static final String TAG = "DirectorMyPartner";
    private RecyclerView partnerRecyclerView;
    private PartnerAdapter adapter;
    private List<PartnerItem> partnerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_director_my_partner);
        partnerRecyclerView = findViewById(R.id.partnerRecyclerView);
        partnerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        partnerList = new ArrayList<>();
        adapter = new PartnerAdapter(partnerList);
        partnerRecyclerView.setAdapter(adapter);
        TextView headingText = findViewById(R.id.headingText);
        boolean showList = getIntent().getBooleanExtra("SHOW_LIST", false);
        if (showList) {
            loadPartnerData();
            partnerRecyclerView.setVisibility(View.VISIBLE);
            headingText.setText("Directors My Partner Users");
        } else {
            partnerRecyclerView.setVisibility(View.GONE);
            headingText.setText("No partner users found reporting to ID 11.");
        }
    }

    private void loadPartnerData() {
        new Thread(() -> {
            try {
                URL url = new URL("https://emp.kfinone.com/mobile/api/get_director_partner_users_reporting_to_11.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();
                    
                    Log.d(TAG, "API Response: " + response.toString());
                    
                    JSONObject json = new JSONObject(response.toString());
                    JSONArray data = json.optJSONArray("data");
                    if (data != null) {
                        partnerList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject partner = data.getJSONObject(i);
                            partnerList.add(new PartnerItem(
                                partner.optString("id", ""),
                                partner.optString("username", ""),
                                partner.optString("alias_name", ""),
                                partner.optString("first_name", ""),
                                partner.optString("last_name", ""),
                                partner.optString("full_name", ""),
                                partner.optString("password", ""),
                                partner.optString("phone_number", ""),
                                partner.optString("email_id", ""),
                                partner.optString("alternative_mobile_number", ""),
                                partner.optString("company_name", ""),
                                partner.optString("branch_state_name_id", ""),
                                partner.optString("branch_location_id", ""),
                                partner.optString("bank_id", ""),
                                partner.optString("account_type_id", ""),
                                partner.optString("office_address", ""),
                                partner.optString("residential_address", ""),
                                partner.optString("aadhaar_number", ""),
                                partner.optString("pan_number", ""),
                                partner.optString("account_number", ""),
                                partner.optString("ifsc_code", ""),
                                partner.optString("rank", ""),
                                partner.optString("status", ""),
                                partner.optString("reportingTo", ""),
                                partner.optString("employee_no", ""),
                                partner.optString("department", ""),
                                partner.optString("designation", ""),
                                partner.optString("branchstate", ""),
                                partner.optString("branchloaction", ""),
                                partner.optString("bank_name", ""),
                                partner.optString("account_type", ""),
                                partner.optString("partner_type_id", ""),
                                partner.optString("pan_img", ""),
                                partner.optString("aadhaar_img", ""),
                                partner.optString("photo_img", ""),
                                partner.optString("bankproof_img", ""),
                                partner.optString("created_at", ""),
                                partner.optString("createdBy", ""),
                                partner.optString("updated_at", "")
                            ));
                        }
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Loaded " + partnerList.size() + " partner users");
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(DirectorMyPartnerActivity.this, "No partner users found reporting to ID 11.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(DirectorMyPartnerActivity.this, "Failed to load partner users.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading partner users", e);
                runOnUiThread(() -> Toast.makeText(DirectorMyPartnerActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private static class PartnerItem {
        String id, username, aliasName, firstName, lastName, fullName, password, phoneNumber, emailId, 
               alternativeMobileNumber, companyName, branchStateNameId, branchLocationId, bankId, 
               accountTypeId, officeAddress, residentialAddress, aadhaarNumber, panNumber, 
               accountNumber, ifscCode, rank, status, reportingTo, employeeNo, department, 
               designation, branchState, branchLocation, bankName, accountType, partnerTypeId, 
               panImg, aadhaarImg, photoImg, bankproofImg, createdAt, createdBy, updatedAt;
        
        PartnerItem(String id, String username, String aliasName, String firstName, String lastName, 
                   String fullName, String password, String phoneNumber, String emailId, 
                   String alternativeMobileNumber, String companyName, String branchStateNameId, 
                   String branchLocationId, String bankId, String accountTypeId, String officeAddress, 
                   String residentialAddress, String aadhaarNumber, String panNumber, String accountNumber, 
                   String ifscCode, String rank, String status, String reportingTo, String employeeNo, 
                   String department, String designation, String branchState, String branchLocation, 
                   String bankName, String accountType, String partnerTypeId, String panImg, 
                   String aadhaarImg, String photoImg, String bankproofImg, String createdAt, 
                   String createdBy, String updatedAt) {
            this.id = id;
            this.username = username;
            this.aliasName = aliasName;
            this.firstName = firstName;
            this.lastName = lastName;
            this.fullName = fullName;
            this.password = password;
            this.phoneNumber = phoneNumber;
            this.emailId = emailId;
            this.alternativeMobileNumber = alternativeMobileNumber;
            this.companyName = companyName;
            this.branchStateNameId = branchStateNameId;
            this.branchLocationId = branchLocationId;
            this.bankId = bankId;
            this.accountTypeId = accountTypeId;
            this.officeAddress = officeAddress;
            this.residentialAddress = residentialAddress;
            this.aadhaarNumber = aadhaarNumber;
            this.panNumber = panNumber;
            this.accountNumber = accountNumber;
            this.ifscCode = ifscCode;
            this.rank = rank;
            this.status = status;
            this.reportingTo = reportingTo;
            this.employeeNo = employeeNo;
            this.department = department;
            this.designation = designation;
            this.branchState = branchState;
            this.branchLocation = branchLocation;
            this.bankName = bankName;
            this.accountType = accountType;
            this.partnerTypeId = partnerTypeId;
            this.panImg = panImg;
            this.aadhaarImg = aadhaarImg;
            this.photoImg = photoImg;
            this.bankproofImg = bankproofImg;
            this.createdAt = createdAt;
            this.createdBy = createdBy;
            this.updatedAt = updatedAt;
        }
    }

    private class PartnerAdapter extends RecyclerView.Adapter<PartnerAdapter.ViewHolder> {
        private List<PartnerItem> partnerItems;
        PartnerAdapter(List<PartnerItem> partnerItems) {
            this.partnerItems = partnerItems;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_director_partner, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            PartnerItem item = partnerItems.get(position);
            
            // Format the display text with comprehensive information
            String displayText = "Name: " + item.fullName + "\n" +
                               "Employee ID: " + item.employeeNo + "\n" +
                               "Phone: " + item.phoneNumber + "\n" +
                               "Email: " + item.emailId + "\n" +
                               "Company: " + item.companyName + "\n" +
                               "Department: " + item.department + "\n" +
                               "Designation: " + item.designation + "\n" +
                               "Branch State: " + item.branchState + "\n" +
                               "Branch Location: " + item.branchLocation + "\n" +
                               "Bank: " + item.bankName + "\n" +
                               "Account Type: " + item.accountType + "\n" +
                               "Status: " + item.status + "\n" +
                               "Reporting To: " + item.reportingTo;
            
            holder.nameText.setText(displayText);
            holder.phoneText.setVisibility(View.GONE);
            holder.emailText.setVisibility(View.GONE);
            holder.passwordText.setVisibility(View.GONE);
            
            holder.actionButton.setText("VIEW DETAILS");
            holder.actionButton.setOnClickListener(v -> showPartnerDetailsDialog(item));
        }
        @Override
        public int getItemCount() {
            return partnerItems.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText, phoneText, emailText, passwordText;
            Button actionButton;
            ViewHolder(View view) {
                super(view);
                nameText = view.findViewById(R.id.nameText);
                phoneText = view.findViewById(R.id.phoneText);
                emailText = view.findViewById(R.id.emailText);
                passwordText = view.findViewById(R.id.passwordText);
                actionButton = view.findViewById(R.id.actionButton);
            }
        }
    }
    
    private void showPartnerDetailsDialog(PartnerItem partner) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Partner User Details");
        
        String details = "ID: " + partner.id + "\n\n" +
                        "Username: " + partner.username + "\n" +
                        "Alias Name: " + partner.aliasName + "\n" +
                        "Full Name: " + partner.fullName + "\n" +
                        "Employee ID: " + partner.employeeNo + "\n\n" +
                        "Contact Information:\n" +
                        "Phone: " + partner.phoneNumber + "\n" +
                        "Alternative Phone: " + partner.alternativeMobileNumber + "\n" +
                        "Email: " + partner.emailId + "\n\n" +
                        "Company Information:\n" +
                        "Company: " + partner.companyName + "\n" +
                        "Department: " + partner.department + "\n" +
                        "Designation: " + partner.designation + "\n" +
                        "Rank: " + partner.rank + "\n\n" +
                        "Location Information:\n" +
                        "Branch State: " + partner.branchState + "\n" +
                        "Branch Location: " + partner.branchLocation + "\n" +
                        "Office Address: " + partner.officeAddress + "\n" +
                        "Residential Address: " + partner.residentialAddress + "\n\n" +
                        "Banking Information:\n" +
                        "Bank: " + partner.bankName + "\n" +
                        "Account Type: " + partner.accountType + "\n" +
                        "Account Number: " + partner.accountNumber + "\n" +
                        "IFSC Code: " + partner.ifscCode + "\n\n" +
                        "Document Information:\n" +
                        "Aadhaar Number: " + partner.aadhaarNumber + "\n" +
                        "PAN Number: " + partner.panNumber + "\n\n" +
                        "System Information:\n" +
                        "Status: " + partner.status + "\n" +
                        "Reporting To: " + partner.reportingTo + "\n" +
                        "Partner Type ID: " + partner.partnerTypeId + "\n" +
                        "Created By: " + partner.createdBy + "\n" +
                        "Created At: " + partner.createdAt + "\n" +
                        "Updated At: " + partner.updatedAt;
        
        builder.setMessage(details);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
} 