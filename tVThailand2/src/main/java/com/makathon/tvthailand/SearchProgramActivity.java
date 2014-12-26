package com.makathon.tvthailand;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.makathon.tvthailand.MainApplication.TrackerName;
import com.makathon.tvthailand.adapter.ProgramAdapter;
import com.makathon.tvthailand.contentprovider.ProgramSuggestionProvider;
import com.makathon.tvthailand.datasource.OnLoadDataListener;
import com.makathon.tvthailand.datasource.Program;
import com.makathon.tvthailand.datasource.Programs;
import com.makathon.tvthailand.datasource.Programs.OnProgramChangeListener;
import com.makathon.tvthailand.otv.OTVShowActivity;
import com.makathon.tvthailand.toolbox.BitmapLruCache;

import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchProgramActivity extends SherlockActivity implements OnLoadDataListener {
	
	private Programs mPrograms;
	private ProgramAdapter mAdapter;

	private MenuItem refreshMenu;
	
	private String query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/** Enabling Progress bar for this activity */
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		
		setContentView(R.layout.simple_grid_view);

        RequestQueue mRequestQueue = Volley.newRequestQueue(this);

        int memClass = ((ActivityManager)getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        // Use 1/8th of the available memory for this memory cache.
        int cacheSize = 1024 * 1024 * memClass / 8;
        ImageLoader mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(cacheSize));

		mPrograms = new Programs();
		mAdapter = new ProgramAdapter(this, mPrograms,
				R.layout.whatnew_grid_item, mImageLoader);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(mAdapter);

		mPrograms.setOnProgramChangeListener(new OnProgramChangeListener() {

			@Override
			public void onProgramChange(Programs Programs) {
				mAdapter.notifyDataSetChanged();
			}
		});
		
		mPrograms.setOnLoadListener(this);

		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
                if (mPrograms.size() > 0) {
                    Program program = mPrograms.get(position);

                    Tracker t = ((MainApplication) getApplication()).getTracker(
                            TrackerName.APP_TRACKER);
                    t.setScreenName("Program");
                    t.send(new HitBuilders.AppViewBuilder().setCustomDimension(2, program.getTitle()).build());

                    if (program.isOTV() == 1) {
                        t.setScreenName("Program");
                        t.send(new HitBuilders.AppViewBuilder().setCustomDimension(2, program.getTitle()).build());

                        Intent intent = new Intent(SearchProgramActivity.this, OTVShowActivity.class);
                        intent.putExtra(EpisodeActivity.EXTRAS_PROGRAM, program);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SearchProgramActivity.this, EpisodeActivity.class);
                        intent.putExtra(EpisodeActivity.EXTRAS_PROGRAM, program);
                        startActivity(intent);
                    }
                }
			}
		});
		

		Intent intent = getIntent();
		handleIntent(intent);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}
	
	private void handleIntent(Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
			query = intent.getStringExtra(SearchManager.QUERY);

			ProgramSuggestionProvider.getBridge(this).saveRecentQuery(query,
					null);
			setTitle("Search : " + query);
			mPrograms.loadProgramBySearch(query, 0);
			
			Tracker t = ((MainApplication)getApplication()).getTracker(
		            TrackerName.APP_TRACKER);
			t.setScreenName("Search");
			t.send(new HitBuilders.AppViewBuilder().setCustomDimension(4, query).build());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.search_program, menu);
		refreshMenu = menu.findItem(R.id.refresh);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			mPrograms.loadProgramBySearch(query, 0);			
			break;
		case R.id.search:
			onSearchRequested();
		default:
			break;
		}
		
		return true;
	}

	public void startLoadingProgressBar(MenuItem menuItem){
		menuItem.setActionView(R.layout.refresh_menuitem);
	}
	public void stopLoadingProgressBar(MenuItem menuItem){
		menuItem.setActionView(null);
	}
	
	@Override
	public void onLoadStart() {
		if (getSherlock() != null) {
			if(refreshMenu != null) {
				startLoadingProgressBar(refreshMenu);
			}
		}		
	}

	@Override
	public void onLoadFinished() {
		if (getSherlock() != null) {
			if(refreshMenu != null) {
				stopLoadingProgressBar(refreshMenu);
			}
		}
	}

}
