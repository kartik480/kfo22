package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class TrainingVideoListAdapter extends BaseAdapter {

    private Context context;
    private List<DirectorTrainingVideoActivity.TrainingVideoItem> videoList;

    public TrainingVideoListAdapter(Context context, List<DirectorTrainingVideoActivity.TrainingVideoItem> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_training_video_list, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = convertView.findViewById(R.id.nameTextView);
            holder.categoryTextView = convertView.findViewById(R.id.categoryTextView);
            holder.videoTextView = convertView.findViewById(R.id.videoTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DirectorTrainingVideoActivity.TrainingVideoItem video = videoList.get(position);
        holder.nameTextView.setText(video.getName());
        holder.categoryTextView.setText(video.getCategory());
        holder.videoTextView.setText("Video"); // Placeholder for video

        return convertView;
    }

    private static class ViewHolder {
        TextView nameTextView;
        TextView categoryTextView;
        TextView videoTextView;
    }
}
