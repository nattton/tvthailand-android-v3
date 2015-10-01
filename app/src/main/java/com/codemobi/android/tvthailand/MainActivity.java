package com.codemobi.android.tvthailand;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codemobi.android.tvthailand.activity.AboutActivity;
import com.codemobi.android.tvthailand.activity.ProgramLoaderActivity;
import com.codemobi.android.tvthailand.fragment.CategoryFragment;
import com.codemobi.android.tvthailand.fragment.ChannelFragment;
import com.codemobi.android.tvthailand.fragment.RadioFragment;
import com.codemobi.android.tvthailand.utils.Constant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.codemobi.android.tvthailand.dao.section.SectionCollectionDao;
import com.codemobi.android.tvthailand.datasource.OnLoadDataListener;
import com.codemobi.android.tvthailand.manager.http.HTTPEngine;
import com.codemobi.android.tvthailand.manager.SectionManager;
import com.vserv.android.ads.api.VservAdView;
import com.vserv.android.ads.common.VservAdListener;

public class MainActivity extends AppCompatActivity implements
		OnLoadDataListener {

	Toolbar toolbar;
	TabLayout tabLayout;
	ViewPager viewPager;

	private static final String TAG = "MainActivity";
	private static final String KEY_IS_ADS_DISPLAYED = "KEY_IS_ADS_DISPLAYED";
//	private boolean doubleBackToExitPressedOnce = false;
	private static boolean isAdsEnabled = true;
	private boolean isAdsDisplayed = false;
	private int current_pos = 0;

	private MenuItem refreshMenu;
	
	private VservAdView vservAdView;
	private VservAdListener mAdListener;
	
	public void setTest() {
		isAdsEnabled = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		initToolbar();
		initTabLayout();
        initInstances();

		if (savedInstanceState != null){
		    isAdsDisplayed = savedInstanceState.getBoolean(KEY_IS_ADS_DISPLAYED);
		}
	}

	private void initToolbar() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
	}

	private void initTabLayout() {
		tabLayout = (TabLayout) findViewById(R.id.tabLayout);
		tabLayout.addTab(tabLayout.newTab().setText("Categories"));
		tabLayout.addTab(tabLayout.newTab().setText("Channel"));
		tabLayout.addTab(tabLayout.newTab().setText("Radio"));

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override
			public Fragment getItem(int position) {
				switch (position) {
					case 0:
						return CategoryFragment.newInstance();
					case 1:
						return ChannelFragment.newInstance();
					case 2:
						return RadioFragment.newInstance();
					default:
						return RadioFragment.newInstance();
				}
			}

			@Override
			public int getCount() {
				return 3;
			}
		});

		viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		viewPager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				current_pos = position;
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				viewPager.setCurrentItem(tab.getPosition(), true);
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
	}

    private void initInstances()
	{
        loadSection();
    }

    private void loadSection()
	{
        SectionManager.getInstance().loadData();
        HTTPEngine.getInstance().getSectionData(new Response.Listener<SectionCollectionDao>() {
			@Override
			public void onResponse(SectionCollectionDao response) {
				SectionManager.getInstance().setData(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(MainActivity.this, "Cannot connect to the internet.", Toast.LENGTH_LONG).show();
			}
		});
    }
	
	@Override
	protected void onStart() {
		super.onStart();
		if (!isAdsDisplayed) {
//			loadInterstitialAd();
//
			adListenerInitialization();
			vservAdView = new VservAdView(this, "", VservAdView.UX_INTERSTITIAL);
			vservAdView.setAdListener(mAdListener);
			vservAdView.setZoneId(Constant.VSERV_BILLBOARD);
			vservAdView.setUxType(VservAdView.UX_INTERSTITIAL);
//			if (!Constant.VSERV_TEST_DEVICE.equals("")){
//				vservAdView.setTestDevices(Constant.VSERV_TEST_DEVICE);
//			}
			vservAdView.cacheAd();
			isAdsDisplayed = true;
		}
        sendTracker();
	}

	private void sendTracker() {
		Tracker t = ((MainApplication)getApplication()).getDefaultTracker();
        t.setScreenName("Main");
        t.send(new HitBuilders.AppViewBuilder().build());
        
		Tracker otvTracker = ((MainApplication)getApplication()).getOTVTracker();
		otvTracker.setScreenName("Main");
		otvTracker.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
		protected void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			outState.putBoolean(KEY_IS_ADS_DISPLAYED, isAdsDisplayed);
		}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		refreshMenu = menu.findItem(R.id.refresh);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			refreshMenuClick(current_pos);
			break;
		case R.id.search:
			onSearchRequested();
			break;
		case R.id.account:
//			goToAccount();
			break;
		case R.id.favorite:
			Intent intent_fave = new Intent(MainActivity.this,
					ProgramLoaderActivity.class);
			Bundle bd_fave = new Bundle();
			bd_fave.putString(ProgramLoaderActivity.KEY,
					ProgramLoaderActivity.FAVORITE);
			intent_fave.putExtras(bd_fave);
			startActivity(intent_fave);
			break;
		case R.id.recently:
			Intent intent_recent = new Intent(
					MainActivity.this,
					ProgramLoaderActivity.class);
			Bundle bd_recent = new Bundle();
			bd_recent.putString(ProgramLoaderActivity.KEY,
					ProgramLoaderActivity.RECENTLY);
			intent_recent.putExtras(bd_recent);
			startActivity(intent_recent);
			break;
		case R.id.about:
			startActivity(new Intent(MainActivity.this, AboutActivity.class));
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void refreshMenuClick(int position) {
		switch (current_pos) {
		case 0:
		case 1:
		case 2:
		default:
			loadSection();
			break;
		}
	}

	private void goToAccount() {

	}

	@Override
	public void onLowMemory() {
		Log.w(TAG, "onLowMemory");
		super.onLowMemory();
	}

	@Override
	public void onLoadStart() {

	}

	@Override
	public void onLoadFinished() {

	}
	
//	@Override
//	public void onBackPressed() {
//		if (doubleBackToExitPressedOnce) {
//			super.onBackPressed();
//			return;
//		}
//
//		this.doubleBackToExitPressedOnce = true;
//		Toast.makeText(this, "Press BACK again to exit",
//				Toast.LENGTH_SHORT).show();
//
//		new Handler().postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				doubleBackToExitPressedOnce = false;
//			}
//		}, 2000);
//	}
	
	@Override
	public void finish() {
//		if (isAdsEnabled) {
//			manager = VservManager.getInstance(MainActivity.this);
//			if (!Constant.VSERV_TEST_DEVICE.equals("")){
//				manager.addTestDevice(Constant.VSERV_TEST_DEVICE);
//			}
//			manager.setShowAt(AdPosition.END);
//			manager.displayAd(Constant.VSERV_BILLBOARD);
//		} else {
			super.finish();
//		}
	}

	private void adListenerInitialization() {
		mAdListener = new VservAdListener() {

			@Override
			public void didInteractWithAd(VservAdView adView) {
//				Toast.makeText(MainActivity.this, "didInteractWithAd",
//						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void adViewDidLoadAd(VservAdView adView) {

//				Toast.makeText(MainActivity.this, "adViewDidLoadAd",
//						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void willPresentOverlay(VservAdView adView) {

//				Toast.makeText(MainActivity.this, "willPresentOverlay",
//						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void willDismissOverlay(VservAdView adView) {

//				Toast.makeText(MainActivity.this, "willDismissOverlay",
//						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void adViewDidCacheAd(VservAdView adView) {

//				Toast.makeText(MainActivity.this, "adViewDidCacheAd",
//						Toast.LENGTH_SHORT).show();
				if (vservAdView != null) {

					if (vservAdView.getUxType() == VservAdView.UX_INTERSTITIAL) {
//						isAppInBackgorund = true;
					}
					vservAdView.showAd();
				}
			}

			@Override
			public VservAdView didFailedToLoadAd(String arg0) {
//				loadInterstitialAd();
				Log.d("VservAdView", "didFailedToLoadAd");

				return null;
			}

			@Override
			public VservAdView didFailedToCacheAd(String Error) {

				Log.d("VservAdView", "didFailedToCacheAd");

				return null;
			}

			@Override
			public void willLeaveApp(VservAdView adView) {
//				Toast.makeText(MainActivity.this, "willLeaveApp",
//						Toast.LENGTH_SHORT).show();
			}
		};
	}
}
