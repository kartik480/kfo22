package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class PartnerTeamAdapter extends ArrayAdapter<PartnerTeamActivity.PartnerTeamMember> {

    private Context context;
    private List<PartnerTeamActivity.PartnerTeamMember> teamList;

    public PartnerTeamAdapter(Context context, List<PartnerTeamActivity.PartnerTeamMember> teamList) {
        super(context, 0, teamList);
        this.context = context;
        this.teamList = teamList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.partner_team_list_item, parent, false);
            holder = new ViewHolder();
            holder.fullName = convertView.findViewById(R.id.fullName);
            holder.mobile = convertView.findViewById(R.id.mobile);
            holder.email = convertView.findViewById(R.id.email);
            holder.createdBy = convertView.findViewById(R.id.createdBy);
            holder.editButton = convertView.findViewById(R.id.editButton);
            holder.deleteButton = convertView.findViewById(R.id.deleteButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PartnerTeamActivity.PartnerTeamMember member = getItem(position);
        if (member != null) {
            holder.fullName.setText(member.getFullName());
            holder.mobile.setText(member.getMobile());
            holder.email.setText(member.getEmail());
            holder.createdBy.setText(member.getCreatedBy());

            // Set up action buttons
            holder.editButton.setOnClickListener(v -> {
                Toast.makeText(context, "Edit team member: " + member.getFullName(), Toast.LENGTH_SHORT).show();
                // TODO: Navigate to edit team member activity
            });

            holder.deleteButton.setOnClickListener(v -> {
                Toast.makeText(context, "Delete team member: " + member.getFullName(), Toast.LENGTH_SHORT).show();
                // TODO: Show confirmation dialog and delete team member
            });
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView fullName;
        TextView mobile;
        TextView email;
        TextView createdBy;
        Button editButton;
        Button deleteButton;
    }
} 