package com.codemobi.android.tvthailand.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.adapter.OnTapListener;
import com.codemobi.android.tvthailand.adapter.ProgramListAdapter;
import com.codemobi.android.tvthailand.contentprovider.ProgramSuggestionProvider;
import com.codemobi.android.tvthailand.datasource.OnLoadDataListener;
import com.codemobi.android.tvthailand.datasource.Program;
import com.codemobi.android.tvthailand.datasource.Programs;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.rey.material.widget.ProgressView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchProgramActivity extends AppCompatActivity implements OnLoadDataListener, OnTapListener, SwipeRefreshLayout.OnRefreshListener {

	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.swipe_container) SwipeRefreshLayout swipeLayout;
	@BindView(R.id.progress_view) ProgressView progressView;
	@BindView(R.id.text_view_no_content) TextView textViewNoContent;
	@BindView(R.id.rv_program) RecyclerView mRecyclerView;

	private Programs mPrograms;
	private String query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_program_new);

		ButterKnife.bind(this);

		setSupportActionBar(toolbar);
		initInstances();
		Intent intent = getIntent();
		handleIntent(intent);
	}

	private void initInstances() {
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorSchemeResources(R.color.holo_blue_bright,
				R.color.holo_green_light,
				R.color.holo_orange_light,
				R.color.holo_red_light);

		mPrograms = new Programs();
		mPrograms.setOnLoadListener(this);
		mPrograms.setOnProgramChangeListener(new Programs.OnProgramChangeListener() {
			@Override
			public void onProgramChange(Programs programs) {
				if (programs.size() > 0) {
					textViewNoContent.setVisibility(View.GONE);
				} else {
					textViewNoContent.setVisibility(View.VISIBLE);
				}
			}
		});

		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		GridLayoutManager mLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.category_column_num));
		mRecyclerView.setLayoutManager(mLayoutManager);
		ProgramListAdapter programAdapter = new ProgramListAdapter(this, mPrograms);
		mRecyclerView.setAdapter(programAdapter);
		programAdapter.setOnTapListener(this);
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
			SearchRecentSuggestions suggestionProvider = new SearchRecentSuggestions(this, ProgramSuggestionProvider.AUTHORITY, ProgramSuggestionProvider.MODE);
			suggestionProvider.saveRecentQuery(query, null);
			toolbar.setTitle("Search : " + query);
			mPrograms.loadProgramBySearch(query, 0);
			
			Tracker t = ((MainApplication)getApplication()).getDefaultTracker();
			t.setScreenName("Search");
			t.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(4, query).build());
		} else if (intent.getAction().equals(Intent.ACTION_VIEW)) {
			query = intent.getStringExtra(SearchManager.QUERY);
			mPrograms.loadProgramBySearch(query, 0);
		}
	}

	private void clearHistory() {
		SearchRecentSuggestions suggestionProvider = new SearchRecentSuggestions(this, ProgramSuggestionProvider.AUTHORITY, ProgramSuggestionProvider.MODE);
		suggestionProvider.clearHistory();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search_program, menu);

		SearchManager searchManager =
				(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			mPrograms.loadProgramBySearch(query, 0);			
			break;
		case R.id.search:
//			onSearchRequested();
		case R.id.clear_history:
			clearHistory();
			break;
		default:
			break;
		}
		
		return true;
	}

	@Override
	public void onTapView(int position) {
		if (mPrograms.size() > 0) {
			Program program = mPrograms.get(position);

			Tracker t = ((MainApplication) getApplication()).getDefaultTracker();
			t.setScreenName("Program");
			t.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(2, program.getTitle()).build());

			if (program.isOTV() == 1) {
				t.setScreenName("Program");
				t.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(2, program.getTitle()).build());
			}

			Intent intent = new Intent(SearchProgramActivity.this, EpisodeActivity.class);
			intent.putExtra(EpisodeActivity.EXTRAS_PROGRAM, program);
			startActivity(intent);
		}
	}

	@Override
	public void onRefresh() {
		mPrograms.loadProgramBySearch(query, 0);
	}

	@Override
	public void onLoadStart() {
		swipeLayout.setRefreshing(true);
	}

	@Override
	public void onLoadFinished() {
		progressView.setVisibility(View.GONE);
		swipeLayout.setRefreshing(false);
	}

	@Override
	public void onLoadError(String error) {

	}
}
