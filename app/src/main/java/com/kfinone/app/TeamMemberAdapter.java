package com.kfinone.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TeamMemberAdapter extends RecyclerView.Adapter<TeamMemberAdapter.ViewHolder> {
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
    }

    @Override
    public int getItemCount() {
        return teamMembers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView employeeName, employeeId, designation, mobile, email;

        ViewHolder(View itemView) {
            super(itemView);
            employeeName = itemView.findViewById(R.id.employeeName);
            employeeId = itemView.findViewById(R.id.employeeId);
            designation = itemView.findViewById(R.id.designation);
            mobile = itemView.findViewById(R.id.mobile);
            email = itemView.findViewById(R.id.email);
        }
    }


} 