package com.codemobi.android.tvthailand;

import com.codemobi.android.tvthailand.utils.Constant;
import com.codemobi.android.tvthailand.utils.Contextor;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.multidex.MultiDexApplication;
import android.webkit.WebView;

import java.util.HashMap;

public class MainApplication extends MultiDexApplication {
    private Tracker mTracker;

    public MainApplication() {
        super();
    }
    
    @Override
    public void onCreate() {
    	super.onCreate();
        Contextor.getInstance().init(getApplicationContext());
    	Fabric.with(this, new Crashlytics());
        init();
    }
    
    private void init() {
        Constant.UserAgentChrome = new WebView(this).getSettings().getUserAgentString();
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    synchronized public Tracker getOTVTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.otv_tracker);
        }
        return mTracker;
    }
}
