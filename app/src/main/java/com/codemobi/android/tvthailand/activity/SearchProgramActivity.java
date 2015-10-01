package com.codemobi.android.tvthailand.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.adapter.ProgramAdapter;
import com.codemobi.android.tvthailand.contentprovider.ProgramSuggestionProvider;
import com.codemobi.android.tvthailand.datasource.OnLoadDataListener;
import com.codemobi.android.tvthailand.datasource.Program;
import com.codemobi.android.tvthailand.datasource.Programs;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class SearchProgramActivity extends AppCompatActivity implements OnLoadDataListener {

	Toolbar toolbar;
	private Programs mPrograms;
	private ProgramAdapter mAdapter;

	private MenuItem refreshMenu;
	
	private String query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_program);

		initToolbar();

		mPrograms = new Programs();
		mAdapter = new ProgramAdapter(mPrograms);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(mAdapter);

		mPrograms.setOnProgramChangeListener(new Programs.OnProgramChangeListener() {

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

					Tracker t = ((MainApplication) getApplication()).getDefaultTracker();
					t.setScreenName("Program");
					t.send(new HitBuilders.AppViewBuilder().setCustomDimension(2, program.getTitle()).build());

					if (program.isOTV() == 1) {
						t.setScreenName("Program");
						t.send(new HitBuilders.AppViewBuilder().setCustomDimension(2, program.getTitle()).build());

					}
					Intent intent = new Intent(SearchProgramActivity.this, EpisodeActivity.class);
					intent.putExtra(EpisodeActivity.EXTRAS_PROGRAM, program);
					startActivity(intent);
				}
			}
		});
		

		Intent intent = getIntent();
		handleIntent(intent);
	}

	private void initToolbar() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
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
			toolbar.setTitle("Search : " + query);
			mPrograms.loadProgramBySearch(query, 0);
			
			Tracker t = ((MainApplication)getApplication()).getDefaultTracker();
			t.setScreenName("Search");
			t.send(new HitBuilders.AppViewBuilder().setCustomDimension(4, query).build());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search_program, menu);
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
//		if (getSherlock() != null) {
//			if(refreshMenu != null) {
//				startLoadingProgressBar(refreshMenu);
//			}
//		}
	}

	@Override
	public void onLoadFinished() {
//		if (getSherlock() != null) {
//			if(refreshMenu != null) {
//				stopLoadingProgressBar(refreshMenu);
//			}
//		}
	}

}
