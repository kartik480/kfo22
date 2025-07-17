package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PolicyAdapter extends RecyclerView.Adapter<PolicyAdapter.PolicyViewHolder> {

    private List<PolicyActivity.PolicyItem> policyList;
    private Context context;

    public PolicyAdapter(List<PolicyActivity.PolicyItem> policyList, Context context) {
        this.policyList = policyList;
        this.context = context;
    }

    @NonNull
    @Override
    public PolicyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_policy, parent, false);
        return new PolicyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PolicyViewHolder holder, int position) {
        PolicyActivity.PolicyItem policy = policyList.get(position);
        
        holder.vendorBankText.setText(policy.getVendorBank());
        holder.loanTypeText.setText(policy.getLoanType());
        holder.contentText.setText(policy.getContent());
        
        // Set default image for policy
        holder.policyImage.setImageResource(R.drawable.ic_file);
    }

    @Override
    public int getItemCount() {
        return policyList.size();
    }

    static class PolicyViewHolder extends RecyclerView.ViewHolder {
        TextView vendorBankText;
        TextView loanTypeText;
        TextView contentText;
        ImageView policyImage;

        PolicyViewHolder(View itemView) {
            super(itemView);
            vendorBankText = itemView.findViewById(R.id.vendorBankText);
            loanTypeText = itemView.findViewById(R.id.loanTypeText);
            contentText = itemView.findViewById(R.id.contentText);
            policyImage = itemView.findViewById(R.id.policyImage);
        }
    }
} 
