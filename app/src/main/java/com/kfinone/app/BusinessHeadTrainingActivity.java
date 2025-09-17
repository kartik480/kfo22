package com.kfinone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class BusinessHeadTrainingActivity extends AppCompatActivity {

    private static final String TAG = "BusinessHeadTrainingActivity";

    // Top navigation elements
    private View backButton;
    private TextView titleText;

    // Training boxes
    private MaterialCardView typeOfLoanBox;
    private MaterialCardView trainingVideoBox;
    private MaterialCardView profileBox;
    private MaterialCardView seminarsBox;
    private MaterialCardView policyBox;
    private MaterialCardView offersBox;
    private MaterialCardView newsBox;
    private MaterialCardView policyImagesBox;

    // User data
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide status bar and make fullscreen
        getWindow().setFlags(
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        
        // Additional flags to ensure complete fullscreen
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        
        setContentView(R.layout.activity_business_head_training);

        initializeViews();
        setupUserData();
        setupClickListeners();
        setupAnimations();
    }

    private void initializeViews() {
        // Top navigation
        backButton = findViewById(R.id.backButton);
        titleText = findViewById(R.id.titleText);
        if (titleText != null) {
            titleText.setText("Business Head Training Center");
        }

        // Training boxes
        typeOfLoanBox = findViewById(R.id.typeOfLoanBox);
        trainingVideoBox = findViewById(R.id.trainingVideoBox);
        profileBox = findViewById(R.id.profileBox);
        seminarsBox = findViewById(R.id.seminarsBox);
        policyBox = findViewById(R.id.policyBox);
        offersBox = findViewById(R.id.offersBox);
        newsBox = findViewById(R.id.newsBox);
        policyImagesBox = findViewById(R.id.policyImagesBox);
    }

    private void setupUserData() {
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("USER_ID");
            userName = intent.getStringExtra("USERNAME");
            firstName = intent.getStringExtra("FIRST_NAME");
            lastName = intent.getStringExtra("LAST_NAME");
        }
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> onBackPressed());

        // Training boxes click listeners
        typeOfLoanBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessHeadTypeOfLoanActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        trainingVideoBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessHeadTrainingVideoActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        profileBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessHeadProfileActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        seminarsBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessHeadSeminarActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        policyBox.setOnClickListener(v -> {
            Toast.makeText(this, "Policy - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Policy activity when implemented
        });

        offersBox.setOnClickListener(v -> {
            Intent intent = new Intent(this, BusinessHeadOffersActivity.class);
            passUserDataToIntent(intent);
            startActivity(intent);
        });

        newsBox.setOnClickListener(v -> {
            Toast.makeText(this, "News - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to News activity when implemented
        });

        policyImagesBox.setOnClickListener(v -> {
            Toast.makeText(this, "Policy Images - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to Policy Images activity when implemented
        });
    }

    private void setupAnimations() {
        // Add entrance animations for training boxes
        android.view.animation.Animation fadeIn = android.view.animation.AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeIn.setDuration(800);

        typeOfLoanBox.startAnimation(fadeIn);
        trainingVideoBox.startAnimation(fadeIn);
        profileBox.startAnimation(fadeIn);
        seminarsBox.startAnimation(fadeIn);
        policyBox.startAnimation(fadeIn);
        offersBox.startAnimation(fadeIn);
        newsBox.startAnimation(fadeIn);
        policyImagesBox.startAnimation(fadeIn);
    }

    private void passUserDataToIntent(Intent intent) {
        if (userId != null) intent.putExtra("USER_ID", userId);
        if (userName != null) intent.putExtra("USERNAME", userName);
        if (firstName != null) intent.putExtra("FIRST_NAME", firstName);
        if (lastName != null) intent.putExtra("LAST_NAME", lastName);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
