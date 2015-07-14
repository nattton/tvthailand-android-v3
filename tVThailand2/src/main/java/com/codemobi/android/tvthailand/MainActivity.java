package com.codemobi.android.tvthailand;

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
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codemobi.android.tvthailand.account.AccountActivity;
import com.codemobi.android.tvthailand.fragment.CategoryFragment;
import com.codemobi.android.tvthailand.fragment.ChannelFragment;
import com.codemobi.android.tvthailand.fragment.RadioFragment;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.codemobi.android.tvthailand.MainApplication.TrackerName;
import com.codemobi.android.tvthailand.dao.section.SectionCollectionDao;
import com.codemobi.android.tvthailand.datasource.OnLoadDataListener;
import com.codemobi.android.tvthailand.manager.http.HTTPEngine;
import com.codemobi.android.tvthailand.manager.SectionManager;
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

	private CategoryFragment catFragment;
	private ChannelFragment chFragment;
	private RadioFragment radioFragment;
	private int current_pos = 0;

	private MenuItem refreshMenu;
	
	VservManager manager;
	
	public void setTest() {
		isAdsEnabled = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_tabs);

        initInstances();

		if (savedInstanceState != null){
		    isAdsDisplayed = savedInstanceState.getBoolean(KEY_IS_ADS_DISPLAYED);
		  }
	}

    private void initInstances() {
        CONTENT = getResources().getStringArray(R.array.sections);

        FragmentStatePagerAdapter adapter = new TVThailandAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
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

        catFragment = new CategoryFragment();
        chFragment = new ChannelFragment();
        radioFragment = new RadioFragment();

        loadSection();
    }

    private void loadSection() {
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
        	manager = VservManager.getInstance(MainActivity.this);
			manager.setShowAt(AdPosition.START);
			manager.setCacheNextAd(false);
			manager.displayAd(BILLBOARD_ZONE, AdType.OVERLAY);
			isAdsDisplayed = true;
		}
        
        sendTracker();
	}
	
	private void sendTracker() {
		Tracker t = ((MainApplication)getApplication()).getTracker(
                TrackerName.APP_TRACKER);
        t.setScreenName("Main");
        t.send(new HitBuilders.AppViewBuilder().build());
        
		Tracker otvTracker = ((MainApplication)getApplication()).getTracker(
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
            loadSection();
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
				return catFragment;
			case 1:
				return chFragment;
			case 2:
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
