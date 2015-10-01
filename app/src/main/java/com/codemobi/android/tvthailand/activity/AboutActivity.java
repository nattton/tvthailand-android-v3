package com.codemobi.android.tvthailand.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class AboutActivity extends AppCompatActivity {
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
		
		Tracker t = ((MainApplication)getApplication()).getDefaultTracker();
		t.setScreenName("About");
		t.send(new HitBuilders.AppViewBuilder().build());
		
	}
}
