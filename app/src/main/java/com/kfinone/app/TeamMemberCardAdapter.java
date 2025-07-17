package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import java.util.List;

public class TeamMemberCardAdapter extends RecyclerView.Adapter<TeamMemberCardAdapter.TeamMemberViewHolder> {

    private Context context;
    private List<PartnerTeamActivity.PartnerTeamMember> teamList;
    private OnTeamMemberActionListener actionListener;

    public interface OnTeamMemberActionListener {
        void onEditTeamMember(PartnerTeamActivity.PartnerTeamMember member);
        void onDeleteTeamMember(PartnerTeamActivity.PartnerTeamMember member);
    }

    public TeamMemberCardAdapter(Context context, List<PartnerTeamActivity.PartnerTeamMember> teamList) {
        this.context = context;
        this.teamList = teamList;
    }

    public void setActionListener(OnTeamMemberActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public TeamMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.team_member_card_item, parent, false);
        return new TeamMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamMemberViewHolder holder, int position) {
        PartnerTeamActivity.PartnerTeamMember member = teamList.get(position);
        holder.bind(member);
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    public void updateData(List<PartnerTeamActivity.PartnerTeamMember> newData) {
        this.teamList.clear();
        this.teamList.addAll(newData);
        notifyDataSetChanged();
    }

    public void filterData(List<PartnerTeamActivity.PartnerTeamMember> filteredData) {
        this.teamList.clear();
        this.teamList.addAll(filteredData);
        notifyDataSetChanged();
    }

    class TeamMemberViewHolder extends RecyclerView.ViewHolder {
        private TextView memberName;
        private TextView memberRole;
        private TextView memberMobile;
        private TextView memberEmail;
        private TextView memberCreatedBy;
        private Chip memberStatusChip;
        private MaterialButton editMemberButton;
        private MaterialButton deleteMemberButton;

        public TeamMemberViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.memberName);
            memberRole = itemView.findViewById(R.id.memberRole);
            memberMobile = itemView.findViewById(R.id.memberMobile);
            memberEmail = itemView.findViewById(R.id.memberEmail);
            memberCreatedBy = itemView.findViewById(R.id.memberCreatedBy);
            memberStatusChip = itemView.findViewById(R.id.memberStatusChip);
            editMemberButton = itemView.findViewById(R.id.editMemberButton);
            deleteMemberButton = itemView.findViewById(R.id.deleteMemberButton);
        }

        public void bind(PartnerTeamActivity.PartnerTeamMember member) {
            memberName.setText(member.getFullName());
            memberMobile.setText(member.getMobile());
            memberEmail.setText(member.getEmail());
            memberCreatedBy.setText(member.getCreatedBy());
            memberRole.setText("Team Member");

            // Set status chip (assuming all team members are active)
            memberStatusChip.setText("Active");
            memberStatusChip.setChipBackgroundColorResource(android.R.color.holo_green_light);
            memberStatusChip.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));

            // Set up action buttons
            editMemberButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onEditTeamMember(member);
                } else {
                    Toast.makeText(context, "Edit team member: " + member.getFullName(), Toast.LENGTH_SHORT).show();
                }
            });

            deleteMemberButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onDeleteTeamMember(member);
                } else {
                    Toast.makeText(context, "Remove team member: " + member.getFullName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
} 