package com.codemobi.android.tvthailand.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class MoreDetailActivity extends Activity{
	public static final String EXTRAS_ID = "EXTRAS_ID";
	public static final String EXTRAS_TITLE = "EXTRAS_TITLE";
	public static final String EXTRAS_DESCRIPTION = "EXTRAS_DESCRIPTION";
	public static final String EXTRAS_DETAIL = "EXTRAS_DETAIL";
	public static final String EXTRAS_THUMBNAIL = "EXTRAS_THUMBNAIL";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_detail);
		
		Bundle bundle = getIntent().getExtras();
		String title_name = bundle.getString(EXTRAS_TITLE);
//		String program_id = bundle.getString(EXTRAS_ID);
		String descriptionStr = bundle.getString(EXTRAS_DESCRIPTION);
		String thumbStr = bundle.getString(EXTRAS_THUMBNAIL);
		String detail = bundle.getString(EXTRAS_DETAIL);
		
		setTitle(title_name);
		((TextView)findViewById(R.id.more_detail_full_description)).setText(detail);
		((TextView)findViewById(R.id.more_detail_time)).setText(descriptionStr);
		ImageView thumbnail_imv = (ImageView)findViewById(R.id.more_detail_thumbnail);
		Tracker t = ((MainApplication)getApplication()).getDefaultTracker();
		t.setScreenName("MoreDetail");
		t.send(new HitBuilders.AppViewBuilder().build());
	}
	
}
