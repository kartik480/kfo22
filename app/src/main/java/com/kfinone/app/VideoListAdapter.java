package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class VideoListAdapter extends BaseAdapter {

    private Context context;
    private List<DirectorTypeOfLoanActivity.VideoItem> videoList;
    private LayoutInflater inflater;

    public VideoListAdapter(Context context, List<DirectorTypeOfLoanActivity.VideoItem> videoList) {
        this.context = context;
        this.videoList = videoList;
        this.inflater = LayoutInflater.from(context);
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
            convertView = inflater.inflate(R.layout.item_video_list, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = convertView.findViewById(R.id.nameTextView);
            holder.vendorBankTextView = convertView.findViewById(R.id.vendorBankTextView);
            holder.loanTypeTextView = convertView.findViewById(R.id.loanTypeTextView);
            holder.videoTextView = convertView.findViewById(R.id.videoTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DirectorTypeOfLoanActivity.VideoItem video = videoList.get(position);
        
        holder.nameTextView.setText(video.getName());
        holder.vendorBankTextView.setText(video.getVendorBank());
        holder.loanTypeTextView.setText(video.getLoanType());
        holder.videoTextView.setText("Video");

        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView vendorBankTextView;
        TextView loanTypeTextView;
        TextView videoTextView;
    }
}
