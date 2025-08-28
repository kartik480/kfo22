package com.kfinone.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MarketingHeadActiveEmpListAdapter extends RecyclerView.Adapter<MarketingHeadActiveEmpListAdapter.ViewHolder> {
    private Context context;
    private List<MarketingHeadUser> userList;

    public MarketingHeadActiveEmpListAdapter(Context context, List<MarketingHeadUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_marketing_head_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MarketingHeadUser user = userList.get(position);
        
        // Set user information
        holder.nameText.setText(user.getDisplayName());
        holder.usernameText.setText("Username: " + (user.getUsername() != null ? user.getUsername() : "N/A"));
        holder.emailText.setText("Email: " + (user.getEmailId() != null ? user.getEmailId() : "N/A"));
        holder.phoneText.setText("Phone: " + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A"));
        holder.statusText.setText("Status: " + (user.getStatus() != null ? user.getStatus() : "N/A"));
        holder.employeeNoText.setText("Employee No: " + (user.getEmployeeNo() != null ? user.getEmployeeNo() : "N/A"));
        holder.designationText.setText("Designation: " + (user.getDesignation() != null ? user.getDesignation() : "N/A"));
        holder.departmentText.setText("Department: " + (user.getDepartment() != null ? user.getDepartment() : "N/A"));
        // Note: joiningDate field was removed from MarketingHeadUser class
        holder.joiningDateText.setText("Joining Date: N/A");
        holder.rankText.setText("Rank: " + (user.getRank() != null ? user.getRank() : "N/A"));
                      holder.workStateText.setText("Work State: " + (user.getWorkState() != null ? user.getWorkState() : "N/A"));
              holder.workLocationText.setText("Work Location: " + (user.getWorkLocation() != null ? user.getWorkLocation() : "N/A"));
              holder.birthDateText.setText("Birth Date: " + (user.getBirthDate() != null ? user.getBirthDate() : "N/A"));

        // Set View button click listener
        holder.viewButton.setOnClickListener(v -> {
            // Launch user detail activity
            android.content.Intent intent = new android.content.Intent(context, MarketingHeadUserDetailActivity.class);
            
            // Pass all user data to the detail activity
            intent.putExtra("USER_ID", user.getId());
            intent.putExtra("USERNAME", user.getUsername());
            intent.putExtra("FIRST_NAME", user.getFirstName());
            intent.putExtra("LAST_NAME", user.getLastName());
            intent.putExtra("EMAIL", user.getEmailId());
            intent.putExtra("PHONE", user.getPhoneNumber());
            intent.putExtra("STATUS", user.getStatus());
            intent.putExtra("EMPLOYEE_NO", user.getEmployeeNo());
            intent.putExtra("DESIGNATION", user.getDesignation());
            intent.putExtra("DEPARTMENT", user.getDepartment());
            intent.putExtra("BIRTH_DATE", user.getBirthDate());
            intent.putExtra("RANK", user.getRank());
            intent.putExtra("WORK_STATE", user.getWorkState());
            intent.putExtra("WORK_LOCATION", user.getWorkLocation());
            intent.putExtra("RESIDENTIAL_ADDRESS", user.getResidentialAddress());
            intent.putExtra("OFFICE_ADDRESS", user.getOfficeAddress());
            intent.putExtra("PAN_NUMBER", user.getPanNumber());
            intent.putExtra("AADHAAR_NUMBER", user.getAadhaarNumber());
            intent.putExtra("ACC_HOLDER_NAME", user.getAccHolderName());
            intent.putExtra("BANK_NAME", user.getBankName());
            intent.putExtra("ACCOUNT_TYPE", user.getAccountType());
            intent.putExtra("BRANCH_NAME", user.getBranchName());
            intent.putExtra("ACCOUNT_NUMBER", user.getAccountNumber());
            intent.putExtra("IFSC_CODE", user.getIfscCode());
            intent.putExtra("REF_NAME_1", user.getRefName1());
            intent.putExtra("REF_RELATION_1", user.getRefRelation1());
            intent.putExtra("REF_MOBILE_1", user.getRefMobile1());
            intent.putExtra("REF_ADDRESS_1", user.getRefAddress1());
            intent.putExtra("REF_NAME_2", user.getRefName2());
            intent.putExtra("REF_RELATION_2", user.getRefRelation2());
            intent.putExtra("REF_MOBILE_2", user.getRefMobile2());
            intent.putExtra("REF_ADDRESS_2", user.getRefAddress2());
            intent.putExtra("REPORTING_TO", user.getReportingTo());
            intent.putExtra("OFFICIAL_PHONE", user.getOfficialPhone());
            intent.putExtra("OFFICIAL_EMAIL", user.getOfficialEmail());
            intent.putExtra("MANAGE_ICONS", user.getManageIcons());
            intent.putExtra("DATA_ICONS", user.getDataIcons());
            intent.putExtra("CREATED_BY", user.getCreatedBy());
            intent.putExtra("CREATED_AT", user.getCreatedAt());
            intent.putExtra("UPDATED_AT", user.getUpdatedAt());
            
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

              public static class ViewHolder extends RecyclerView.ViewHolder {
              TextView nameText, usernameText, emailText, phoneText, statusText, employeeNoText, 
                       designationText, departmentText, joiningDateText, rankText, workStateText, 
                       workLocationText, birthDateText;
              Button viewButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            usernameText = itemView.findViewById(R.id.usernameText);
            emailText = itemView.findViewById(R.id.emailText);
            phoneText = itemView.findViewById(R.id.phoneText);
            statusText = itemView.findViewById(R.id.statusText);
            employeeNoText = itemView.findViewById(R.id.employeeNoText);
            designationText = itemView.findViewById(R.id.designationText);
            departmentText = itemView.findViewById(R.id.departmentText);
            joiningDateText = itemView.findViewById(R.id.joiningDateText);
            rankText = itemView.findViewById(R.id.rankText);
            workStateText = itemView.findViewById(R.id.workStateText);
                              workLocationText = itemView.findViewById(R.id.workLocationText);
                  birthDateText = itemView.findViewById(R.id.birthDateText);
                  viewButton = itemView.findViewById(R.id.viewButton);
        }
    }
}
