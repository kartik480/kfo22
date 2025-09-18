package com.kfinone.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class RbhUserAdapter extends BaseAdapter {
    
    private Context context;
    private List<RbhUserItem> userList;
    private LayoutInflater inflater;
    private OnViewDetailsClickListener onViewDetailsClickListener;

    public interface OnViewDetailsClickListener {
        void onViewDetailsClick(RbhUserItem userItem);
    }
    
    public RbhUserAdapter(Context context, List<RbhUserItem> userList) {
        this.context = context;
        this.userList = userList;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnViewDetailsClickListener(OnViewDetailsClickListener listener) {
        this.onViewDetailsClickListener = listener;
    }
    
    @Override
    public int getCount() {
        return userList.size();
    }
    
    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_rbh_user, parent, false);
            holder = new ViewHolder();
            holder.userNameText = convertView.findViewById(R.id.userNameText);
            holder.userIdText = convertView.findViewById(R.id.userIdText);
            holder.creatorNameText = convertView.findViewById(R.id.creatorNameText);
            holder.viewDetailsButton = convertView.findViewById(R.id.viewDetailsButton);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        RbhUserItem user = userList.get(position);
        holder.userNameText.setText(user.getFullName());
        holder.userIdText.setText("(" + user.getUsername() + ")");
        holder.creatorNameText.setText(user.getCreatorName());
        
        // Set up View Details button click listener
        holder.viewDetailsButton.setOnClickListener(v -> {
            if (onViewDetailsClickListener != null) {
                onViewDetailsClickListener.onViewDetailsClick(user);
            }
        });
        
        return convertView;
    }
    
    static class ViewHolder {
        TextView userNameText;
        TextView userIdText;
        TextView creatorNameText;
        Button viewDetailsButton;
    }
    
    public void updateData(List<RbhUserItem> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }
}
