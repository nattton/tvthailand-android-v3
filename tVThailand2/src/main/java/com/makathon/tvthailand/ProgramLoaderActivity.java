package com.makathon.tvthailand;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.makathon.tvthailand.Application.TrackerName;
import com.makathon.tvthailand.adapter.ProgramCursorAdapter;
import com.makathon.tvthailand.contentprovider.MyProgramContentProvider;
import com.makathon.tvthailand.database.MyProgramModel;
import com.makathon.tvthailand.database.MyProgramTable.MyProgramColumns;
import com.makathon.tvthailand.database.ProgramTable.ProgramColumns;
import com.makathon.tvthailand.datasource.Episodes;
import com.makathon.tvthailand.datasource.Episodes.OnProgramChangeListener;
import com.makathon.tvthailand.datasource.Program;
import com.makathon.tvthailand.otv.OTVShowActivity;

public class ProgramLoaderActivity extends SherlockFragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener {
	static final String FAVORITE = "Favorite";
	static final String RECENTLY = "Recently";
	static final String KEY = "ACTION_KEY";
	
	private TextView textViewNoContent;
	private GridView gridView;
	private ProgramCursorAdapter mAdapter;
	String string_key = "";
	
//	private User mUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_grid_view);
		Bundle bundle = getIntent().getExtras();
		string_key = bundle.getString(KEY);
		
		ActionBar ab = getSupportActionBar();
		if(string_key.equals(FAVORITE)) {
			setTitle(FAVORITE);
			ab.setIcon(R.drawable.ic_favorite);
		}
		else if(string_key.equals(RECENTLY)) {
			setTitle(RECENTLY);
			ab.setIcon(R.drawable.ic_recently);
		}

		textViewNoContent = (TextView)findViewById(R.id.textViewNoContent);
		gridView = (GridView) findViewById(R.id.gridview);
		
		mAdapter = new ProgramCursorAdapter(this, MyVolley.getImageLoader());
		gridView.setAdapter(mAdapter);

		getSupportLoaderManager().initLoader(0, null, this);

		gridView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Cursor cursor = mAdapter.getCursor();
		if (cursor.moveToPosition(position)) {
			
			MyProgramModel programLoader = MyProgramModel.newInstance(cursor, this);
			
			Program program = programLoader.toProgram();
			
			 Tracker t = ((Application)getApplication()).getTracker(
			            TrackerName.APP_TRACKER);
			 t.setScreenName("ProgramLoader");
			 t.send(new HitBuilders.AppViewBuilder().setCustomDimension(2, program.getTitle()).build());
			 
			 Episodes mEpisodes = new Episodes(this);
			 
			 mEpisodes.setOnProgramChangeListener(new OnProgramChangeListener() {
				
				@Override
				public void onProgramChange(Program program) {
					if (program.isOTV() == 1) {
						Intent intent = new Intent(ProgramLoaderActivity.this, OTVShowActivity.class);
						intent.putExtra(EpisodeActivity.EXTRAS_PROGRAM, program);
						startActivity(intent);
					}
					else {
						Intent intent = new Intent(ProgramLoaderActivity.this, EpisodeActivity.class);
						intent.putExtra(EpisodeActivity.EXTRAS_PROGRAM, program);
						startActivity(intent);
					}
				}
			});
			mEpisodes.loadEpisodes(program.getId(), 0);
		}

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg) {
		
		Loader<Cursor> cursor = null;

		if(string_key.equals(FAVORITE)){
			String selection = MyProgramColumns.IS_FAV + " = " + "1";
			String sortOrder = ProgramColumns.TITLE + " COLLATE LOCALIZED ASC";
			cursor = new CursorLoader(this,
					MyProgramContentProvider.CONTENT_URI, null, selection, null,
					sortOrder);
		}
		if(string_key.equals(RECENTLY)){
			String selection = MyProgramColumns.TIME_VIEWED + " != " + "0";
			String sortOrder = MyProgramColumns.TIME_VIEWED + " COLLATE LOCALIZED DESC";
			cursor = new CursorLoader(this,
					MyProgramContentProvider.CONTENT_URI, null, selection, null,
					sortOrder);
		}

		return cursor;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
		
		if (mAdapter.getCount() > 0) {
			textViewNoContent.setVisibility(View.GONE);
		} else {
			textViewNoContent.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);

	}

}
