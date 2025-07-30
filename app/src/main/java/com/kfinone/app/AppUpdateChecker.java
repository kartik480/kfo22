package com.kfinone.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppUpdateChecker {
    
    private static final String TAG = "AppUpdateChecker";
    private static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.kfinone.app";
    
    // You can update this URL to point to your own server API that returns version info
    private static final String VERSION_CHECK_URL = "https://emp.kfinone.com/mobile/api/check_app_version.php";
    
    private Context context;
    private Activity activity;
    private boolean isDialogShowing = false;
    
    public AppUpdateChecker(Context context) {
        this.context = context;
        if (context instanceof Activity) {
            this.activity = (Activity) context;
        }
    }
    
    public void checkForUpdates() {
        new CheckUpdateTask().execute();
    }
    
    private class CheckUpdateTask extends AsyncTask<Void, Void, UpdateInfo> {
        
        @Override
        protected UpdateInfo doInBackground(Void... voids) {
            try {
                // Get current app version
                PackageInfo packageInfo = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0);
                int currentVersionCode = packageInfo.versionCode;
                String currentVersionName = packageInfo.versionName;
                
                Log.d(TAG, "Current version: " + currentVersionName + " (" + currentVersionCode + ")");
                
                // Check for updates from server
                UpdateInfo serverInfo = checkServerForUpdates();
                
                if (serverInfo != null && serverInfo.versionCode > currentVersionCode) {
                    Log.d(TAG, "Update available: " + serverInfo.versionName + " (" + serverInfo.versionCode + ")");
                    return serverInfo;
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error checking for updates", e);
            }
            
            return null;
        }
        
        @Override
        protected void onPostExecute(UpdateInfo updateInfo) {
            if (updateInfo != null && !isDialogShowing) {
                showUpdateDialog(updateInfo);
            }
        }
    }
    
    private UpdateInfo checkServerForUpdates() {
        try {
            URL url = new URL(VERSION_CHECK_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JSONObject jsonResponse = new JSONObject(response.toString());
                
                if (jsonResponse.getBoolean("status")) {
                    JSONObject data = jsonResponse.getJSONObject("data");
                    UpdateInfo updateInfo = new UpdateInfo();
                    updateInfo.versionCode = data.getInt("version_code");
                    updateInfo.versionName = data.getString("version_name");
                    updateInfo.updateMessage = data.getString("update_message");
                    updateInfo.isForceUpdate = data.optBoolean("force_update", false);
                    updateInfo.downloadUrl = data.optString("download_url", PLAY_STORE_URL);
                    
                    return updateInfo;
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking server for updates", e);
        }
        
        return null;
    }
    
    private void showUpdateDialog(final UpdateInfo updateInfo) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        
        isDialogShowing = true;
        
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                
                // Inflate custom layout
                LayoutInflater inflater = activity.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_app_update, null);
                
                TextView titleText = dialogView.findViewById(R.id.updateTitle);
                TextView messageText = dialogView.findViewById(R.id.updateMessage);
                Button updateButton = dialogView.findViewById(R.id.updateButton);
                Button laterButton = dialogView.findViewById(R.id.laterButton);
                
                titleText.setText("Update Available");
                messageText.setText("A new version of Kurakulas Partners is available!\n\n" +
                        "Version: " + updateInfo.versionName + "\n\n" +
                        (updateInfo.updateMessage != null ? updateInfo.updateMessage : 
                        "This update includes bug fixes and performance improvements."));
                
                final AlertDialog dialog = builder.create();
                
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openPlayStore();
                        dialog.dismiss();
                        isDialogShowing = false;
                    }
                });
                
                if (updateInfo.isForceUpdate) {
                    // Force update - hide later button
                    laterButton.setVisibility(View.GONE);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                } else {
                    laterButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            isDialogShowing = false;
                        }
                    });
                }
                
                dialog.show();
            }
        });
    }
    
    private void openPlayStore() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(PLAY_STORE_URL));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error opening Play Store", e);
            Toast.makeText(context, "Unable to open Play Store", Toast.LENGTH_SHORT).show();
        }
    }
    
    public static class UpdateInfo {
        public int versionCode;
        public String versionName;
        public String updateMessage;
        public boolean isForceUpdate;
        public String downloadUrl;
    }
} 