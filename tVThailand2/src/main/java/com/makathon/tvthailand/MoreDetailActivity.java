package com.makathon.tvthailand;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.TVThailandApp.TrackerName;

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
		NetworkImageView thumbnail_imv = (NetworkImageView)findViewById(R.id.more_detail_thumbnail);
		thumbnail_imv.setImageUrl(thumbStr, MyVolley.getImageLoader());
		
		Tracker t = ((TVThailandApp)getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("MoreDetail");
		t.send(new HitBuilders.AppViewBuilder().build());
	}
	
}
