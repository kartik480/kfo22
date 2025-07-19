package com.kfinone.app;

import android.app.Application;
import android.view.WindowManager;

public class KfinOneApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Set global fullscreen theme for the entire app
        setTheme(R.style.FullscreenTheme);
    }
} 