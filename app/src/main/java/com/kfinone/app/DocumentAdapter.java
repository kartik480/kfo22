package com.kfinone.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private List<DocCheckListActivity.DocumentItem> documentList;
    private Context context;

    public DocumentAdapter(List<DocCheckListActivity.DocumentItem> documentList, Context context) {
        this.documentList = documentList;
        this.context = context;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        DocCheckListActivity.DocumentItem document = documentList.get(position);
        
        // Display document_name as Name
        holder.nameText.setText(document.getName());
        
        // Display uploaded_file as Document
        holder.documentText.setText(document.getDocumentName());
        
        holder.downloadButton.setOnClickListener(v -> {
            downloadDocument(document);
        });
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    private void downloadDocument(DocCheckListActivity.DocumentItem document) {
        try {
            String downloadUrl = document.getDownloadUrl();
            if (downloadUrl != null && !downloadUrl.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(downloadUrl));
                context.startActivity(intent);
                Toast.makeText(context, "Opening document: " + document.getDocumentName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Download URL not available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error opening document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView documentText;
        Button downloadButton;

        DocumentViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            documentText = itemView.findViewById(R.id.documentText);
            downloadButton = itemView.findViewById(R.id.downloadButton);
        }
    }
} 