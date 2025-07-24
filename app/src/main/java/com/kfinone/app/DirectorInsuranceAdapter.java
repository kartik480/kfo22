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

public class DirectorInsuranceAdapter extends RecyclerView.Adapter<DirectorInsuranceAdapter.DirectorInsuranceViewHolder> {
    private List<DirectorInsuranceItem> insuranceList;
    private Context context;

    public DirectorInsuranceAdapter(List<DirectorInsuranceItem> insuranceList, Context context) {
        this.insuranceList = insuranceList;
        this.context = context;
    }

    @NonNull
    @Override
    public DirectorInsuranceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_director_insurance, parent, false);
        return new DirectorInsuranceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DirectorInsuranceViewHolder holder, int position) {
        DirectorInsuranceItem item = insuranceList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return insuranceList.size();
    }

    public static class DirectorInsuranceViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText, phoneText, emailText, passwordText;
        private Button actionButton;

        public DirectorInsuranceViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.directorInsuranceName);
            phoneText = itemView.findViewById(R.id.directorInsurancePhone);
            emailText = itemView.findViewById(R.id.directorInsuranceEmail);
            passwordText = itemView.findViewById(R.id.directorInsurancePassword);
            actionButton = itemView.findViewById(R.id.directorInsuranceActionButton);
        }

        public void bind(DirectorInsuranceItem item) {
            nameText.setText("Name: " + item.getName());
            phoneText.setText("Phone: " + item.getPhone());
            emailText.setText("Email: " + item.getEmail());
            passwordText.setText("Password: " + item.getMaskedPassword());
            actionButton.setOnClickListener(v -> {
                // TODO: Implement action (e.g., view, edit, etc.)
            });
        }
    }
} 