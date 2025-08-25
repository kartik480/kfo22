package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class UserProfileActivity extends AppCompatActivity {
    private static final String TAG = "UserProfile";
    
    private String username;
    private String firstName;
    private String lastName;
    private String userId;
    private String userDesignation;
    private String sourcePanel;
    
    // Header Views
    private ImageView backButton;
    private TextView headerTitle;
    private ImageView profileImage;
    private TextView userNameText;
    private TextView userRoleText;
    
    // Profile Information Cards
    private CardView cardPersonalInfo;
    private CardView cardContactInfo;
    private CardView cardRoleInfo;
    private CardView cardAccountInfo;
    private CardView cardPermissions;
    private CardView cardActivityLog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        
        // Get user data from intent
        Intent intent = getIntent();
        username = intent.getStringExtra("USERNAME");
        firstName = intent.getStringExtra("FIRST_NAME");
        lastName = intent.getStringExtra("LAST_NAME");
        userId = intent.getStringExtra("USER_ID");
        userDesignation = intent.getStringExtra("USER_DESIGNATION");
        sourcePanel = intent.getStringExtra("SOURCE_PANEL");
        
        // Debug logging
        Log.d(TAG, "UserProfileActivity received:");
        Log.d(TAG, "USER_ID: " + userId);
        Log.d(TAG, "USERNAME: " + username);
        Log.d(TAG, "FIRST_NAME: " + firstName);
        Log.d(TAG, "LAST_NAME: " + lastName);
        Log.d(TAG, "USER_DESIGNATION: " + userDesignation);
        Log.d(TAG, "SOURCE_PANEL: " + sourcePanel);
        
        initializeViews();
        setupClickListeners();
        updateUserInfo();
    }
    
    private void initializeViews() {
        // Header Views
        backButton = findViewById(R.id.backButton);
        headerTitle = findViewById(R.id.headerTitle);
        profileImage = findViewById(R.id.profileImage);
        userNameText = findViewById(R.id.userNameText);
        userRoleText = findViewById(R.id.userRoleText);
        
        // Profile Section Cards
        cardPersonalInfo = findViewById(R.id.cardPersonalInfo);
        cardContactInfo = findViewById(R.id.cardContactInfo);
        cardRoleInfo = findViewById(R.id.cardRoleInfo);
        cardAccountInfo = findViewById(R.id.cardAccountInfo);
        cardPermissions = findViewById(R.id.cardPermissions);
        cardActivityLog = findViewById(R.id.cardActivityLog);
    }
    
    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> {
            finish();
        });
        
        // Personal Information
        cardPersonalInfo.setOnClickListener(v -> {
            showPersonalInfoDialog();
        });
        
        // Contact Information
        cardContactInfo.setOnClickListener(v -> {
            showContactInfoDialog();
        });
        
        // Role Information
        cardRoleInfo.setOnClickListener(v -> {
            showRoleInfoDialog();
        });
        
        // Account Information
        cardAccountInfo.setOnClickListener(v -> {
            showAccountInfoDialog();
        });
        
        // Permissions
        cardPermissions.setOnClickListener(v -> {
            showPermissionsDialog();
        });
        
        // Activity Log
        cardActivityLog.setOnClickListener(v -> {
            showActivityLogDialog();
        });
    }
    
    private void updateUserInfo() {
        // Set user name
        if (firstName != null && lastName != null && !firstName.isEmpty() && !lastName.isEmpty()) {
            userNameText.setText(firstName + " " + lastName);
        } else if (firstName != null && !firstName.isEmpty()) {
            userNameText.setText(firstName);
        } else if (username != null && !username.isEmpty()) {
            userNameText.setText(username);
        } else {
            userNameText.setText("User");
        }
        
        // Set user role based on designation or source panel
        String roleText = "User";
        if (userDesignation != null && !userDesignation.isEmpty()) {
            roleText = userDesignation;
        } else if (sourcePanel != null && !sourcePanel.isEmpty()) {
            switch (sourcePanel) {
                case "CBO_PANEL":
                    roleText = "Chief Business Officer";
                    break;
                case "RBH_PANEL":
                    roleText = "Regional Business Head";
                    break;
                case "BH_PANEL":
                    roleText = "Business Head";
                    break;
                case "MD_PANEL":
                    roleText = "Managing Director";
                    break;
                case "DIRECTOR_PANEL":
                    roleText = "Director";
                    break;
                default:
                    roleText = sourcePanel.replace("_", " ");
                    break;
            }
        }
        userRoleText.setText(roleText);
    }
    
    private void showPersonalInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Personal Information");
        
        StringBuilder message = new StringBuilder();
        message.append("Full Name: ").append(firstName != null ? firstName : "N/A")
               .append(" ").append(lastName != null ? lastName : "N/A").append("\n\n");
        message.append("Username: ").append(username != null ? username : "N/A").append("\n\n");
        message.append("User ID: ").append(userId != null ? userId : "N/A").append("\n\n");
        message.append("Role: ").append(userRoleText.getText()).append("\n\n");
        message.append("Source Panel: ").append(sourcePanel != null ? sourcePanel.replace("_", " ") : "N/A");
        
        builder.setMessage(message.toString());
        builder.setPositiveButton("Edit", (dialog, which) -> {
            showToast("Edit Personal Info - Coming Soon!");
        });
        builder.setNegativeButton("Close", null);
        builder.show();
    }
    
    private void showContactInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contact Information");
        builder.setMessage("Email: " + (username != null ? username + "@kfinone.com" : "N/A") + "\n\n" +
                "Phone: Contact HR Department\n\n" +
                "Address: Kurakulas Partners Office\n\n" +
                "Emergency Contact: HR Department");
        builder.setPositiveButton("Update", (dialog, which) -> {
            showToast("Update Contact Info - Coming Soon!");
        });
        builder.setNegativeButton("Close", null);
        builder.show();
    }
    
    private void showRoleInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Role Information");
        
        String roleDescription = "";
        if (sourcePanel != null) {
            switch (sourcePanel) {
                case "CBO_PANEL":
                    roleDescription = "Chief Business Officer (CBO)\n\n" +
                            "• Strategic leadership and business oversight\n" +
                            "• High-level decision making\n" +
                            "• Team and performance management\n" +
                            "• Business analytics and reporting";
                    break;
                case "RBH_PANEL":
                    roleDescription = "Regional Business Head (RBH)\n\n" +
                            "• Regional business operations management\n" +
                            "• Team leadership and coordination\n" +
                            "• Performance monitoring and reporting\n" +
                            "• Strategic planning for region";
                    break;
                case "BH_PANEL":
                    roleDescription = "Business Head (BH)\n\n" +
                            "• Business unit management\n" +
                            "• Team performance oversight\n" +
                            "• Strategic planning and execution\n" +
                            "• Business analytics and insights";
                    break;
                case "MD_PANEL":
                    roleDescription = "Managing Director (MD)\n\n" +
                            "• Executive leadership and management\n" +
                            "• Strategic direction and planning\n" +
                            "• High-level business decisions\n" +
                            "• Company-wide oversight";
                    break;
                case "DIRECTOR_PANEL":
                    roleDescription = "Director\n\n" +
                            "• Departmental leadership\n" +
                            "• Strategic planning and execution\n" +
                            "• Team management and development\n" +
                            "• Performance monitoring";
                    break;
                default:
                    roleDescription = "User Role\n\n" +
                            "• Role details available\n" +
                            "• Contact HR for specific information";
                    break;
            }
        }
        
        builder.setMessage(roleDescription);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private void showAccountInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Account Information");
        builder.setMessage("Account Status: Active\n\n" +
                "Account Type: Employee\n\n" +
                "Department: Business Operations\n\n" +
                "Location: Kurakulas Partners\n\n" +
                "Access Level: " + (sourcePanel != null ? sourcePanel.replace("_", " ") : "Standard"));
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private void showPermissionsDialog() {
        String[] permissions = {"View Dashboard", "Manage Team", "View Reports", "Access Analytics", "User Management", "System Settings"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Permissions");
        builder.setItems(permissions, (dialog, which) -> {
            String permission = permissions[which];
            String status = "✓ Granted";
            showToast(permission + ": " + status);
        });
        builder.setNegativeButton("Close", null);
        builder.show();
    }
    
    private void showActivityLogDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recent Activity Log");
        builder.setMessage("• Last Login: Today\n" +
                "• Panel Access: " + (sourcePanel != null ? sourcePanel.replace("_", " ") : "Main Panel") + "\n" +
                "• Session Duration: Active\n" +
                "• Actions Performed: Viewing profile\n\n" +
                "Detailed logs available through HR department.");
        builder.setPositiveButton("OK", null);
        builder.show();
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onBackPressed() {
        finish();
    }
}

