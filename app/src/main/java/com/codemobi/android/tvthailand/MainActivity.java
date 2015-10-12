package com.codemobi.android.tvthailand;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;

import com.codemobi.android.tvthailand.activity.AboutActivity;
import com.codemobi.android.tvthailand.activity.ProgramLoaderActivity;
import com.codemobi.android.tvthailand.fragment.CategoryFragment;
import com.codemobi.android.tvthailand.fragment.ChannelFragment;
import com.codemobi.android.tvthailand.fragment.RadioFragment;
import com.codemobi.android.tvthailand.manager.http.APIClient;
import com.codemobi.android.tvthailand.player.RadioPlayerActivity;
import com.codemobi.android.tvthailand.utils.Constant;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.codemobi.android.tvthailand.dao.section.SectionCollectionDao;
import com.codemobi.android.tvthailand.manager.SectionManager;
import com.vserv.android.ads.api.VservAdView;
import com.vserv.android.ads.common.VservAdListener;

import java.io.IOException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

	Toolbar toolbar;
	TabLayout tabLayout;
	ViewPager viewPager;
	CoordinatorLayout rootLayout;
	SearchView searchView;

	private static final String TAG = "MainActivity";
	private int current_pos = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		initToolbar();
		initTabLayout();
        initInstances();
		sendTracker();
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
		rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);
        loadSection();
    }

    private void loadSection()
	{
		APIClient.APIService service = APIClient.getClient();
		Call<SectionCollectionDao> call = service.loadSection(Constant.defaultParams);
		call.enqueue(new Callback<SectionCollectionDao>() {
			@Override
			public void onResponse(retrofit.Response<SectionCollectionDao> response, Retrofit retrofit) {
				if (response.isSuccess())
					SectionManager.getInstance().setData(response.body());
				else {
					String errorString;
					try {
						errorString = response.errorBody().string();
					} catch (IOException e) {
						errorString = "Cannot load data.";
					}
					Snackbar.make(rootLayout, errorString, Snackbar.LENGTH_LONG)
							.setAction("Refresh", new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									loadSection();
								}
							}).show();
				}
			}

			@Override
			public void onFailure(Throwable t) {
				Snackbar.make(rootLayout, "Cannot load data.", Snackbar.LENGTH_LONG)
						.setAction("Refresh", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								loadSection();
							}
						}).show();
			}
		});
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		SearchManager searchManager =
				(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			refreshMenuClick(current_pos);
			break;
		case R.id.search:
//			onSearchRequested();
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
}
