package com.makathon.tvthailand;

import mobi.vserv.android.ads.AdPosition;
import mobi.vserv.android.ads.AdType;
import mobi.vserv.android.ads.VservManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.TVThailandApp.TrackerName;
import com.makathon.tvthailand.account.AccountActivity;
import com.makathon.tvthailand.datasource.AppUtility;
import com.makathon.tvthailand.datasource.OnLoadDataListener;
import com.makathon.tvthailand.datasource.Section;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends SherlockFragmentActivity implements
		OnLoadDataListener {

	private static final String TAG = "MainActivity";
	private static final String KEY_IS_ADS_DISPLAYED = "KEY_IS_ADS_DISPLAYED";
//	private boolean doubleBackToExitPressedOnce = false; 
	private static boolean isAdsEnabled = true;
	private boolean isAdsDisplayed = false;
	private static final String BILLBOARD_ZONE = "c84927ed";
	
	
	private static String[] CONTENT = new String[4];

	private FragmentStatePagerAdapter adapter;
	private TabPageIndicator indicator;
	private Section mSection;
	private CateFragment catFragment;
	private ChannelFragment chFragment;
	private RadioFragment radioFragment;
	private int current_pos = 0;

	private MenuItem refreshMenu;
	UiLifecycleHelper uiHelper = null;
	
	VservManager manager;

	public static boolean isActive() {
		Session session = Session.getActiveSession();
		if (session == null) {
			return false;
		}

		return session.isOpened();
	}
	
	public void setTest() {
		isAdsEnabled = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null){
		    isAdsDisplayed = savedInstanceState.getBoolean(KEY_IS_ADS_DISPLAYED);
		  }
		
		CONTENT = getResources().getStringArray(R.array.sections);

		setContentView(R.layout.simple_tabs);

		adapter = new TVThailandAdapter(getSupportFragmentManager());

		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		indicator.setCurrentItem(current_pos);
		indicator.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int position) {
				current_pos = position;
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			public void onPageScrollStateChanged(int position) {
			}
		});

		catFragment = new CateFragment();
		chFragment = new ChannelFragment();
		radioFragment = new RadioFragment();

		mSection = AppUtility.getInstance()
				.getSections(getApplicationContext());
		mSection.setOnLoadListener(this);
		mSection.load(false);
        
        super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
        if (!isAdsDisplayed) {
        	manager = VservManager.getInstance(MainActivity.this);
			manager.setShowAt(AdPosition.START);
			manager.setCacheNextAd(false);
			manager.displayAd(BILLBOARD_ZONE, AdType.OVERLAY);
			isAdsDisplayed = true;
		}
        
        sendTracker();
	}
	
	private void sendTracker() {
		Tracker t = ((TVThailandApp)getApplication()).getTracker(
                TrackerName.APP_TRACKER);
        t.setScreenName("Main");
        t.send(new HitBuilders.AppViewBuilder().build());
        
		Tracker otvTracker = ((TVThailandApp)getApplication()).getTracker(
                TrackerName.OTV_TRACKER);
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
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		refreshMenu = menu.findItem(R.id.refresh);

		return super.onCreateOptionsMenu(menu);
	}

	public void startLoadingProgressBar(MenuItem menuItem) {
		menuItem.setActionView(R.layout.refresh_menuitem);
	}

	public void stopLoadingProgressBar(MenuItem menuItem) {
		menuItem.setActionView(null);
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
			goToAccount();
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
					com.makathon.tvthailand.MainActivity.this,
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
			mSection.load(true);
			break;
		default:

			break;
		}
	}

	class TVThailandAdapter extends FragmentStatePagerAdapter {

		public TVThailandAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				// Categories
				return catFragment;
			case 1:
				// Channel
				return chFragment;
			case 2:
				// Radio
				return radioFragment;

			default:
				return null;
			}

		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length];
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}

	}

	private void goToAccount() {
		Intent intent = new Intent(MainActivity.this, AccountActivity.class);
		startActivity(intent);
	}

	@Override
	public void onLowMemory() {
		Log.w(TAG, "onLowMemory");
		super.onLowMemory();
	}

	@Override
	public void onLoadStart() {
		if (refreshMenu != null) {
			startLoadingProgressBar(refreshMenu);
		}
	}

	@Override
	public void onLoadFinished() {
		if (refreshMenu != null) {
			stopLoadingProgressBar(refreshMenu);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VservManager.REQUEST_CODE) {
		       if (data != null) {
		           if (data.hasExtra("showAt") && data.getStringExtra("showAt").equalsIgnoreCase("end")) {
		        	   VservManager.getInstance(this).release(this);
		               super.finish();
		           }
		       }
		       else {
		           super.finish();
		       }
		   }
		
		super.onActivityResult(requestCode, resultCode, data);

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
		if (isAdsEnabled) {
			manager = VservManager.getInstance(MainActivity.this);
			manager.setShowAt(AdPosition.END);
			manager.displayAd(BILLBOARD_ZONE);
		} else {
			super.finish();
		}
	}
	
	
}
