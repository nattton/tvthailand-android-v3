package com.codemobi.android.tvthailand.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.R;
import com.google.android.gms.analytics.HitBuilders;
import com.codemobi.android.tvthailand.adapter.ProgramCursorAdapter;
import com.codemobi.android.tvthailand.contentprovider.MyProgramContentProvider;
import com.codemobi.android.tvthailand.database.MyProgramModel;
import com.codemobi.android.tvthailand.database.MyProgramTable;
import com.codemobi.android.tvthailand.database.ProgramTable;
import com.codemobi.android.tvthailand.datasource.Program;
import com.codemobi.android.tvthailand.datasource.Episodes;
import com.codemobi.android.tvthailand.datasource.Episodes.OnProgramChangeListener;
import com.google.android.gms.analytics.Tracker;

public class ProgramLoaderActivity extends AppCompatActivity implements
		LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener {
	public static final String FAVORITE = "Favorite";
	public static final String RECENTLY = "Recently";
	public static final String KEY = "ACTION_KEY";

	Toolbar toolbar;
	
	private TextView textViewNoContent;
	private ProgramCursorAdapter mAdapter;
	private String actionKey = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_program);

		initExtras();
		initToolbar();
		initInstance();
	}

	private void initExtras() {
		Bundle bundle = getIntent().getExtras();
		actionKey = bundle.getString(KEY);
	}

	private void initToolbar() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		if(actionKey.equals(FAVORITE)) {
			toolbar.setTitle(FAVORITE);
			toolbar.setLogo(ContextCompat.getDrawable(this, R.drawable.ic_favorite));
		}
		else if(actionKey.equals(RECENTLY)) {
			toolbar.setTitle(RECENTLY);
			toolbar.setLogo(ContextCompat.getDrawable(this, R.drawable.ic_recently));
		}
		setSupportActionBar(toolbar);
	}

	private void initInstance() {
		textViewNoContent = (TextView)findViewById(R.id.textViewNoContent);
		GridView gridView = (GridView) findViewById(R.id.gridview);

		mAdapter = new ProgramCursorAdapter(this);
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
			
			 Tracker t = ((MainApplication)getApplication()).getDefaultTracker();
			 t.setScreenName("ProgramLoader");
			 t.send(new HitBuilders.AppViewBuilder().setCustomDimension(2, program.getTitle()).build());
			 
			 Episodes mEpisodes = new Episodes(this);
			 
			 mEpisodes.setOnProgramChangeListener(new OnProgramChangeListener() {
				
				@Override
				public void onProgramChange(Program program) {
						Intent intent = new Intent(ProgramLoaderActivity.this, EpisodeActivity.class);
						intent.putExtra(EpisodeActivity.EXTRAS_PROGRAM, program);
						startActivity(intent);
				}
			});
			mEpisodes.loadEpisodes(program.getId(), 0);
		}

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg) {
		
		Loader<Cursor> cursor = null;

		if(actionKey.equals(FAVORITE)){
			String selection = MyProgramTable.MyProgramColumns.IS_FAV + " = " + "1";
			String sortOrder = ProgramTable.ProgramColumns.TITLE + " COLLATE LOCALIZED ASC";
			cursor = new CursorLoader(this,
					MyProgramContentProvider.CONTENT_URI, null, selection, null,
					sortOrder);
		}
		if(actionKey.equals(RECENTLY)){
			String selection = MyProgramTable.MyProgramColumns.TIME_VIEWED + " != " + "0";
			String sortOrder = MyProgramTable.MyProgramColumns.TIME_VIEWED + " COLLATE LOCALIZED DESC";
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
