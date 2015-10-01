package com.codemobi.android.tvthailand.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.adapter.OnTapListener;
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

		RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rvPart);
		mRecyclerView.setHasFixedSize(true);
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		PartAdapter mAdapter = new PartAdapter(this, mParts);
		mRecyclerView.setAdapter(mAdapter);
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
		mAdapter.setOnTapListener(new OnTapListener() {
			@Override
			public void onTapView(int position) {
				mParts.playVideoPart(position);
			}
		});

		Tracker t = ((MainApplication)getApplication()).getDefaultTracker();
		t.setScreenName("Part");
		t.send(new HitBuilders.AppViewBuilder().build());
	}
}
