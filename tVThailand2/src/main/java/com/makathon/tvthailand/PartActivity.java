package com.makathon.tvthailand;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.makathon.tvthailand.MainApplication.TrackerName;
import com.makathon.tvthailand.adapter.PartAdapter;
import com.makathon.tvthailand.datasource.Parts;

public class PartActivity extends SherlockActivity{
	
	public static final String EXTRAS_TITLE = "EXTRAS_TITLE";
	public static final String EXTRAS_VIDEOS = "EXTRAS_VIDEOS";
	public static final String EXTRAS_SRC_TYPE = "EXTRAS_SRC_TYPE";
	public static final String EXTRAS_PASSWORD = "EXTRAS_PASSWORD";
	static final String EXTRAS_ICON = "EXTRAS_ICON";

	private Parts mParts;
    private ProgressDialog progressDialog;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.part_list_view);
		
		Bundle bundle = getIntent().getExtras();
        String title = bundle.getString(EXTRAS_TITLE);
        String[] videos = bundle.getStringArray(EXTRAS_VIDEOS);
        String srcType = bundle.getString(EXTRAS_SRC_TYPE);
        String password = bundle.getString(EXTRAS_PASSWORD);
        String icon = bundle.getString(EXTRAS_ICON);
		
		setTitle(title);

        ImageLoader mImageLoader = MyVolley.getImageLoader();
        mImageLoader.get(icon, new ImageListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {

			}
			
			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if (response.getBitmap() != null)
					getSupportActionBar().setIcon(new BitmapDrawable(getResources(), response.getBitmap()));
			}
		});

		mParts = new Parts(this, title, icon, videos, srcType, password);

        PartAdapter mAdapter = new PartAdapter(this, mParts,
				R.layout.part_list_item, mImageLoader);

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
