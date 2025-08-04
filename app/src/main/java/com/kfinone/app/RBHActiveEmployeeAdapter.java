package com.kfinone.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RBHActiveEmployeeAdapter extends RecyclerView.Adapter<RBHActiveEmployeeAdapter.EmployeeViewHolder> {

    private List<RBHEmployee> employeeList;
    private OnEmployeeClickListener listener;

    public interface OnEmployeeClickListener {
        void onEmployeeClick(RBHEmployee employee);
    }

    public RBHActiveEmployeeAdapter(List<RBHEmployee> employeeList, OnEmployeeClickListener listener) {
        this.employeeList = employeeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rbh_active_employee, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        RBHEmployee employee = employeeList.get(position);
        holder.bind(employee);
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {
        private TextView avatarText, employeeNameText, employeeIdText, statusBadge;
        private TextView designationText, departmentText, mobileText, emailText;
        private TextView workStateText, workLocationText, joiningDateText;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            
            avatarText = itemView.findViewById(R.id.avatarText);
            employeeNameText = itemView.findViewById(R.id.employeeNameText);
            employeeIdText = itemView.findViewById(R.id.employeeIdText);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            designationText = itemView.findViewById(R.id.designationText);
            departmentText = itemView.findViewById(R.id.departmentText);
            mobileText = itemView.findViewById(R.id.mobileText);
            emailText = itemView.findViewById(R.id.emailText);
            workStateText = itemView.findViewById(R.id.workStateText);
            workLocationText = itemView.findViewById(R.id.workLocationText);
            joiningDateText = itemView.findViewById(R.id.joiningDateText);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEmployeeClick(employeeList.get(position));
                }
            });
        }

        public void bind(RBHEmployee employee) {
            // Set avatar initials
            avatarText.setText(employee.getInitials());

            // Set name
            employeeNameText.setText(employee.getName());

            // Set employee ID
            String empId = employee.getEmployeeId();
            if (empId != null && !empId.isEmpty()) {
                employeeIdText.setText("EMP" + empId);
            } else {
                employeeIdText.setText("N/A");
            }

            // Set status badge
            if (employee.isActive()) {
                statusBadge.setText("Active");
                statusBadge.setBackgroundResource(R.drawable.status_active_background);
            } else {
                statusBadge.setText("Inactive");
                statusBadge.setBackgroundResource(R.drawable.status_inactive_background);
            }

            // Set designation
            String designation = employee.getDesignation();
            designationText.setText(designation != null && !designation.isEmpty() ? designation : "N/A");

            // Set department
            String department = employee.getDepartment();
            departmentText.setText(department != null && !department.isEmpty() ? department : "N/A");

            // Set mobile
            String mobile = employee.getMobile();
            mobileText.setText(mobile != null && !mobile.isEmpty() ? mobile : "N/A");

            // Set email
            String email = employee.getEmail();
            emailText.setText(email != null && !email.isEmpty() ? email : "N/A");

            // Set work state
            String workState = employee.getWorkState();
            workStateText.setText(workState != null && !workState.isEmpty() ? workState : "N/A");

            // Set work location
            String workLocation = employee.getWorkLocation();
            workLocationText.setText(workLocation != null && !workLocation.isEmpty() ? workLocation : "N/A");

            // Set joining date
            String joiningDate = employee.getJoiningDate();
            if (joiningDate != null && !joiningDate.isEmpty()) {
                try {
                    // Try to parse and format the date
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    Date date = inputFormat.parse(joiningDate);
                    joiningDateText.setText(outputFormat.format(date));
                } catch (Exception e) {
                    // If parsing fails, show the original date
                    joiningDateText.setText(joiningDate);
                }
            } else {
                joiningDateText.setText("N/A");
            }
        }
    }
} 