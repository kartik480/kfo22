package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class ProfileListAdapter extends BaseAdapter {

    private Context context;
    private List<DirectorProfileActivity.ProfileItem> profileList;

    public ProfileListAdapter(Context context, List<DirectorProfileActivity.ProfileItem> profileList) {
        this.context = context;
        this.profileList = profileList;
    }

    @Override
    public int getCount() {
        return profileList.size();
    }

    @Override
    public Object getItem(int position) {
        return profileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_profile_list, parent, false);
            holder = new ViewHolder();
            holder.vendorBankTextView = convertView.findViewById(R.id.vendorBankTextView);
            holder.loanTypeTextView = convertView.findViewById(R.id.loanTypeTextView);
            holder.imagesTextView = convertView.findViewById(R.id.imagesTextView);
            holder.fileTextView = convertView.findViewById(R.id.fileTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DirectorProfileActivity.ProfileItem profile = profileList.get(position);
        holder.vendorBankTextView.setText(profile.getVendorBank());
        holder.loanTypeTextView.setText(profile.getLoanType());
        holder.imagesTextView.setText("Images"); // Placeholder for images
        holder.fileTextView.setText("File"); // Placeholder for file

        return convertView;
    }

    private static class ViewHolder {
        TextView vendorBankTextView;
        TextView loanTypeTextView;
        TextView imagesTextView;
        TextView fileTextView;
    }
}
