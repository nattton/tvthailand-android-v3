package com.codemobi.android.tvthailand;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codemobi.android.tvthailand.MainApplication.TrackerName;
import com.codemobi.android.tvthailand.adapter.PartAdapter;
import com.codemobi.android.tvthailand.datasource.Parts;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class PartActivity extends AppCompatActivity{
	
	public static final String EXTRAS_TITLE = "EXTRAS_TITLE";
	public static final String EXTRAS_VIDEOS = "EXTRAS_VIDEOS";
	public static final String EXTRAS_SRC_TYPE = "EXTRAS_SRC_TYPE";
	public static final String EXTRAS_PASSWORD = "EXTRAS_PASSWORD";
	public static final String EXTRAS_ICON = "EXTRAS_ICON";

	Toolbar toolbar;
	private Parts mParts;
    private ProgressDialog progressDialog;

	private String title;
	private String[] videos;
	private String srcType;
	private String password;
	private String icon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_part);

		initExtras();
		initToolbar();
		initInstances();
	}

	private void initExtras() {
		Bundle bundle = getIntent().getExtras();
		title = bundle.getString(EXTRAS_TITLE);
		videos = bundle.getStringArray(EXTRAS_VIDEOS);
		srcType = bundle.getString(EXTRAS_SRC_TYPE);
		password = bundle.getString(EXTRAS_PASSWORD);
		icon = bundle.getString(EXTRAS_ICON);
	}

	private void initToolbar() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(title);
		setSupportActionBar(toolbar);
	}

	private void initInstances() {
		mParts = new Parts(this, title, icon, videos, srcType, password);
		PartAdapter mAdapter = new PartAdapter(mParts);

		mParts.setOnLoadListener(new Parts.OnLoadListener() {

			@Override
			public void onStart() {
				if (progressDialog == null)
					progressDialog = ProgressDialog.show(PartActivity.this, "", "Loading, Please wait...", true);
				else
					progressDialog.show();
			}

			@Override
			public void onFinish() {
				progressDialog.dismiss();
			}

			@Override
			public void onError(String message) {
				Toast.makeText(PartActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});

		ListView listview = (ListView) findViewById(R.id.listView);
		listview.setAdapter(mAdapter);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
									long id) {
				mParts.playVideoPart(position);
			}
		});

		Tracker t = ((MainApplication)getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Part");
		t.send(new HitBuilders.AppViewBuilder().build());
	}
}
