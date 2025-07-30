package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BusinessHeadEmployeeAdapter extends RecyclerView.Adapter<BusinessHeadEmployeeAdapter.ViewHolder> {

    private List<BusinessHeadEmployee> employeeList;
    private Context context;
    private OnEmployeeClickListener listener;

    public interface OnEmployeeClickListener {
        void onEmployeeClick(BusinessHeadEmployee employee);
    }

    public BusinessHeadEmployeeAdapter(Context context, List<BusinessHeadEmployee> employeeList, OnEmployeeClickListener listener) {
        this.context = context;
        this.employeeList = employeeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_business_head_employee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusinessHeadEmployee employee = employeeList.get(position);
        
        // Set employee data
        holder.employeeNameText.setText("Name: " + employee.getFullName());
        holder.employeePhoneText.setText("Phone: " + (employee.getMobile() != null ? employee.getMobile() : "N/A"));
        holder.employeeEmailText.setText("Email: " + (employee.getEmailId() != null ? employee.getEmailId() : "N/A"));
        holder.employeePasswordText.setText("Password: " + (employee.getPassword() != null ? "******" : "N/A"));
        
        // Set click listener for action button
        holder.actionButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEmployeeClick(employee);
            }
        });
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public void updateEmployeeList(List<BusinessHeadEmployee> newEmployeeList) {
        this.employeeList = newEmployeeList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView employeeNameText;
        TextView employeePhoneText;
        TextView employeeEmailText;
        TextView employeePasswordText;
        Button actionButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            employeeNameText = itemView.findViewById(R.id.employeeNameText);
            employeePhoneText = itemView.findViewById(R.id.employeePhoneText);
            employeeEmailText = itemView.findViewById(R.id.employeeEmailText);
            employeePasswordText = itemView.findViewById(R.id.employeePasswordText);
            actionButton = itemView.findViewById(R.id.actionButton);
        }
    }
} 