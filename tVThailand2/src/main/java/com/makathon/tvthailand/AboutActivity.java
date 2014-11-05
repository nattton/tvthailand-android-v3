package com.makathon.tvthailand;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.makathon.tvthailand.Application.TrackerName;

public class AboutActivity extends SherlockActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.about);
		
		TextView textViewVersion = (TextView)findViewById(R.id.textViewVersion);
		
		try {
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			textViewVersion.setText("Version : " + pInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		Tracker t = ((Application)getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("About");
		t.send(new HitBuilders.AppViewBuilder().build());
		
	}
}
