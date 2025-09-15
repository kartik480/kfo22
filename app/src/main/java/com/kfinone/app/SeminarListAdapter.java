package com.kfinone.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class SeminarListAdapter extends BaseAdapter {

    private Context context;
    private List<DirectorSeminarActivity.SeminarItem> seminarList;

    public SeminarListAdapter(Context context, List<DirectorSeminarActivity.SeminarItem> seminarList) {
        this.context = context;
        this.seminarList = seminarList;
    }

    @Override
    public int getCount() {
        return seminarList.size();
    }

    @Override
    public Object getItem(int position) {
        return seminarList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_seminar_list, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = convertView.findViewById(R.id.nameTextView);
            holder.vendorBankTextView = convertView.findViewById(R.id.vendorBankTextView);
            holder.loanTypeTextView = convertView.findViewById(R.id.loanTypeTextView);
            holder.videoTextView = convertView.findViewById(R.id.videoTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DirectorSeminarActivity.SeminarItem seminar = seminarList.get(position);
        holder.nameTextView.setText(seminar.getName());
        holder.vendorBankTextView.setText(seminar.getVendorBank());
        holder.loanTypeTextView.setText(seminar.getLoanType());
        
        // Set clickable link for Video
        if (seminar.getVideoUrl() != null && !seminar.getVideoUrl().isEmpty()) {
            holder.videoTextView.setText("🎥 View Video");
            holder.videoTextView.setTextColor(context.getResources().getColor(android.R.color.holo_blue_dark));
            holder.videoTextView.setPaintFlags(holder.videoTextView.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
        } else {
            holder.videoTextView.setText("No Video");
            holder.videoTextView.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.videoTextView.setPaintFlags(holder.videoTextView.getPaintFlags() & (~android.graphics.Paint.UNDERLINE_TEXT_FLAG));
        }

        // Set click listener for Video
        holder.videoTextView.setOnClickListener(v -> {
            if (seminar.getVideoUrl() != null && !seminar.getVideoUrl().isEmpty()) {
                android.util.Log.d("SeminarAdapter", "Video URL: " + seminar.getVideoUrl());
                openVideo(seminar.getVideoUrl());
            }
            // No else case - if no video, clicking does nothing
        });

        return convertView;
    }

    private void openVideo(String videoUrl) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(videoUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Cannot open video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static class ViewHolder {
        TextView nameTextView;
        TextView vendorBankTextView;
        TextView loanTypeTextView;
        TextView videoTextView;
    }
}
