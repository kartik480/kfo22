package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DocumentUploadAdapter extends RecyclerView.Adapter<DocumentUploadAdapter.ViewHolder> {

    private Context context;
    private List<DocumentUploadItem> documentList;
    private List<DocumentUploadItem> filteredList;
    private OnDocumentActionListener actionListener;

    public interface OnDocumentActionListener {
        void onDownloadClick(DocumentUploadItem document);
        void onDeleteClick(DocumentUploadItem document);
    }

    public DocumentUploadAdapter(Context context, List<DocumentUploadItem> documentList, OnDocumentActionListener actionListener) {
        this.context = context;
        this.documentList = documentList;
        this.filteredList = new ArrayList<>(documentList);
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_document_upload, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentUploadItem document = filteredList.get(position);
        
        holder.documentNameText.setText(document.getDocumentName());
        holder.documentFileText.setText(document.getUploadedFile());

        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionListener != null) {
                    actionListener.onDownloadClick(document);
                }
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionListener != null) {
                    actionListener.onDeleteClick(document);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filterDocuments(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(documentList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (DocumentUploadItem document : documentList) {
                if (document.getDocumentName().toLowerCase().contains(lowerCaseQuery) ||
                    document.getUploadedFile().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(document);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void resetFilter() {
        filteredList.clear();
        filteredList.addAll(documentList);
        notifyDataSetChanged();
    }

    public void updateDocumentList(List<DocumentUploadItem> newList) {
        this.documentList = newList;
        this.filteredList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView documentNameText;
        TextView documentFileText;
        ImageButton downloadButton;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            documentNameText = itemView.findViewById(R.id.documentNameText);
            documentFileText = itemView.findViewById(R.id.documentFileText);
            downloadButton = itemView.findViewById(R.id.downloadButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
} 
