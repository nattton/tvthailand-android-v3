package com.makathon.tvthailand;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.TVThailandApp.TrackerName;
import com.makathon.tvthailand.adapter.PartAdapter;
import com.makathon.tvthailand.datasource.AppUtility;
import com.makathon.tvthailand.datasource.Parts;

public class PartActivity extends SherlockActivity{
	
	public static final String EXTRAS_TITLE = "EXTRAS_TITLE";
	public static final String EXTRAS_VIDEOS = "EXTRAS_VIDEOS";
	public static final String EXTRAS_SRC_TYPE = "EXTRAS_SRC_TYPE";
	public static final String EXTRAS_PASSWORD = "EXTRAS_PASSWORD";
	static final String EXTRAS_ICON = "EXTRAS_ICON";
	
	private String title;
	private String[] videos;
	private String srcType;
	private String password;
	private String icon;
	
	private ImageLoader mImageLoader;
	private Parts mParts;
	private PartAdapter mAdapter;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.part_list_view);
		
		Bundle bundle = getIntent().getExtras();
		title = bundle.getString(EXTRAS_TITLE);
		videos = bundle.getStringArray(EXTRAS_VIDEOS);
		srcType = bundle.getString(EXTRAS_SRC_TYPE);
		password = bundle.getString(EXTRAS_PASSWORD);
		icon = bundle.getString(EXTRAS_ICON);
		
		setTitle(title);
		
		mImageLoader = MyVolley.getImageLoader();
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
		
		mAdapter = new PartAdapter(this, mParts,
				R.layout.part_list_item, mImageLoader);

		ListView listview = (ListView) findViewById(R.id.listView);
		listview.setAdapter(mAdapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				mParts.playVideoPart(position);
			}
		});
		
//		setUpAd();
		
		Tracker t = ((TVThailandApp)getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Part");
		t.send(new HitBuilders.AppViewBuilder().build());
	}
	
//	private void setUpAd() {
//		adView = (FrameLayout) findViewById(R.id.ad_view);
//		if (null != controller) {
//			controller.stopRefresh();
//			controller = null;
//		}
//		if (null != adView) {
//			adView.removeAllViews();
//		}
//		
//		manager = VservManager.getInstance(context);
//		
//		manager.getAd(BANNER_ZONE, AdOrientation.PORTRAIT, new AdLoadCallback() {
//			
//			@Override
//			public void onNoFill() {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onLoadSuccess(VservAd adObj) {
////				Toast.makeText(YoutubePlayerViewActivity.this, "Success in getting Ad", Toast.LENGTH_SHORT).show();
//				adObject = adObj;
//				if(null != adView) {
//					adView.removeAllViews();
//				}
//				/***** APPLICATION IF USE RENDER AD FUNCTIONALITY ******/
//				if (null != controller) {
//					controller = null;
//				}
//				
//				if (null != adObject) {
//					try {
//						adObject.show(context, adView);
//					} catch (ViewNotEmptyException e) {
//						Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//						e.printStackTrace();
//					}
//				}
//			}
//			
//			@Override
//			public void onLoadFailure() {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//	}
//	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode,
//			Intent intent) {
//		super.onActivityResult(requestCode, resultCode, intent);
//
//		if (requestCode == VservManager.REQUEST_CODE) {
//			if (intent != null) {
//
//				if (intent.hasExtra("showAt")
//						&& intent.getStringExtra("showAt").equalsIgnoreCase(
//								"end")) {
//
//					VservManager.release(this);
//					super.finish();
//				}
//			} else {
//
//				super.finish();
//			}
//		}
//
//	}
//	
//	@Override
//	protected void onStart() {
//
//		if (null != controller) {
//			controller.resumeRefresh();
//		}
//		super.onStart();
//	}
//
//	@Override
//	protected void onStop() {
//
//		if (null != controller) {
//			controller.stopRefresh();
//		}
//		super.onStop();
//	}

}
