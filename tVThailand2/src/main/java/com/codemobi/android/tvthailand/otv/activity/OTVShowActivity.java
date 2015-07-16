package com.codemobi.android.tvthailand.otv.activity;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codemobi.android.tvthailand.EpisodeActivity;
import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.MoreDetailActivity;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.contentprovider.MyProgramContentProvider;
import com.codemobi.android.tvthailand.database.Dao;
import com.codemobi.android.tvthailand.database.MyProgramModel;
import com.codemobi.android.tvthailand.database.ProgramTable;
import com.codemobi.android.tvthailand.datasource.OnLoadDataListener;
import com.codemobi.android.tvthailand.datasource.Program;
import com.codemobi.android.tvthailand.otv.model.OTVEpisode;
import com.codemobi.android.tvthailand.otv.adapter.OTVEpisodeAdapter;
import com.codemobi.android.tvthailand.otv.model.OTVEpisodes;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class OTVShowActivity extends SherlockActivity implements
		OnLoadDataListener, OnClickListener, OnItemClickListener, OnLongClickListener {
	
	public static final String EXTRAS_PROGRAM = "EXTRAS_PROGRAM";

	private ProgressBar progressBar;
	private MenuItem refreshMenu;

	/*
	 * Header View
	 */
	private TextView tv_title;
	private TextView tvDescription;
	private ImageView imgThumbnail;
	private ImageButton imb_fav;

	private Program program;

	private View header;
	private OTVEpisodeAdapter mAdapter;
	private OTVEpisodes mOTVEpisodes;

	private Handler mHandler = new Handler();
	private Dao<MyProgramModel> mDaoMyProgram;
	private MyProgramModel mMyProgram;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.codemobi.android.tvthailand.R.layout.episode_view);
		
		progressBar = (ProgressBar) findViewById(com.codemobi.android.tvthailand.R.id.progressBar);

		initEpisode();
		initiazeUI();
		setUpHeaderView();

		String where = ProgramTable.ProgramColumns.PROGRAM_ID + " = "
				+ program.getId();
		mDaoMyProgram = new Dao<MyProgramModel>(MyProgramModel.class, this,
				MyProgramContentProvider.CONTENT_URI, where);
		if (mDaoMyProgram.size() == 0) {
			mMyProgram = new MyProgramModel();
			mMyProgram.setProgramId(program.getId());
			mMyProgram.setTitle(program.getTitle());
			mMyProgram.setThumbnail(program.getThumbnail());
			mMyProgram.setDescription(program.getDescription());
			MyProgramInsertTask myProgramInsertTask = new MyProgramInsertTask(
					mDaoMyProgram, mMyProgram) {

			};
			myProgramInsertTask.execute();
			Log.e("mDaoMyProgram", "Insert");
		} else {
			mMyProgram = mDaoMyProgram.get(0);
			mMyProgram.setTitle(program.getTitle());
			mMyProgram.setThumbnail(program.getThumbnail());
			mMyProgram.setDescription(program.getDescription());
			MyProgramUpdateTask myProgramUpdateTask = new MyProgramUpdateTask(
					mDaoMyProgram, mMyProgram) {

			};
			myProgramUpdateTask.execute();
		}

		ListView epList = (ListView) findViewById(com.codemobi.android.tvthailand.R.id.ep_list);
		epList.addHeaderView(header, null, false);

		mOTVEpisodes = new OTVEpisodes();

		mAdapter = new OTVEpisodeAdapter(this, mOTVEpisodes,
				com.codemobi.android.tvthailand.R.layout.episode_list_item, program.getOtvLogo());
		epList.setAdapter(mAdapter);
		epList.setOnItemClickListener(this);

		mOTVEpisodes.setOnLoadListener(this);
		mOTVEpisodes
				.setOnOTVEpisodesChangeListener(new OTVEpisodes.OnOTVEpisodesChangeListener() {

					@Override
					public void OnOTVEpisodesChange(OTVEpisodes otvEpisodes) {
						mAdapter.notifyDataSetChanged();
					}
				});

		mOTVEpisodes.loadOTVEpisodes(program);
		
		refreshView();

		ContentObserver observer = new ContentObserver(mHandler) {
			@Override
			public void onChange(boolean selfChange) {
				refreshView();
			}
		};
		getContentResolver().registerContentObserver(
				MyProgramContentProvider.CONTENT_URI, true, observer);

	}

	private void initEpisode() {
		Intent i = getIntent();
		program = (Program) i.getParcelableExtra(EXTRAS_PROGRAM);

		Glide.with(this)
				.load(program.getOtvLogo())
				.asBitmap()
				.into(new SimpleTarget<Bitmap>() {
					@Override
					public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
							getSupportActionBar().setIcon(new BitmapDrawable(getResources(), resource));
					}
				});

		sendTracker(program);
	}

	private void initiazeUI() {
		header = ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				com.codemobi.android.tvthailand.R.layout.episode_header, null, false);

		tv_title = (TextView) header.findViewById(com.codemobi.android.tvthailand.R.id.tv_title);
		imgThumbnail = (ImageView) header.findViewById(com.codemobi.android.tvthailand.R.id.thumbnail);
		imgThumbnail.setOnClickListener(this);
		tvDescription = (TextView) header.findViewById(com.codemobi.android.tvthailand.R.id.tv_description);
		imb_fav = (ImageButton) header.findViewById(com.codemobi.android.tvthailand.R.id.imb_add_to_fav);

		header.findViewById(com.codemobi.android.tvthailand.R.id.btn_more_detail).setOnClickListener(this);
        header.findViewById(com.codemobi.android.tvthailand.R.id.btn_more_detail).setOnLongClickListener(this);
		imb_fav.setOnClickListener(this);
	}
	
	private void sendTracker(Program show) {
		if (show != null) {
			Tracker t = ((MainApplication) getApplication())
					.getTracker(MainApplication.TrackerName.APP_TRACKER);
			t.setScreenName("OTVShow");
			t.send(new HitBuilders.AppViewBuilder().setCustomDimension(2,
					show.getTitle()).build());

			Tracker t2 = ((MainApplication) getApplication())
					.getTracker(MainApplication.TrackerName.OTV_TRACKER);
			t2.setScreenName("OTVShow");
			t2.send(new HitBuilders.AppViewBuilder().setCustomDimension(1,
					show.getTitle()).build());
		}
	}

	private void setUpHeaderView() {
		setTitle(program.getTitle());
		tv_title.setText(program.getTitle());
		tvDescription.setText(program.getDetail());
		Glide.with(this)
				.load(program.getThumbnail())
				.crossFade()
				.into(imgThumbnail);
	}

	private void refreshView() {
		if (mDaoMyProgram.size() > 0) {
			mMyProgram = mDaoMyProgram.get(0);
			if (mMyProgram.isFav()) {
				imb_fav.setBackgroundResource(com.codemobi.android.tvthailand.R.drawable.button_fav_pressed);
			} else {
				imb_fav.setBackgroundResource(com.codemobi.android.tvthailand.R.drawable.button_fav);
			}
		}
	}

	protected class MyProgramInsertTask extends
			AsyncTask<Integer, Integer, Void> {

		private Dao<MyProgramModel> mDaoMyProgram;
		private MyProgramModel mMyProgram;

		public MyProgramInsertTask(Dao<MyProgramModel> mDaoMyProgram,
								   MyProgramModel mMyProgram) {
			this.mDaoMyProgram = mDaoMyProgram;
			this.mMyProgram = mMyProgram;
		}

		@Override
		protected Void doInBackground(Integer... params) {
			mDaoMyProgram.insert(mMyProgram);
			return null;
		}

	}

	protected class MyProgramUpdateTask extends
			AsyncTask<Integer, Integer, Void> {

		private Dao<MyProgramModel> mDaoMyProgram;
		private MyProgramModel mMyProgram;

		public MyProgramUpdateTask(Dao<MyProgramModel> mDaoMyProgram,
								   MyProgramModel mMyProgram) {
			this.mDaoMyProgram = mDaoMyProgram;
			this.mMyProgram = mMyProgram;
		}

		@Override
		protected Void doInBackground(Integer... params) {
			mDaoMyProgram.update(mMyProgram);
			return null;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(com.codemobi.android.tvthailand.R.menu.program, menu);
		refreshMenu = menu.findItem(com.codemobi.android.tvthailand.R.id.refresh);
		MenuItem playMenu = menu.findItem(com.codemobi.android.tvthailand.R.id.play);
		playMenu.setVisible(false);

		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case com.codemobi.android.tvthailand.R.id.refresh:
			mOTVEpisodes.loadOTVEpisodes(program);
			break;

		default:
			break;
		}
		return true;
	}

	public void startLoadingProgressBar(MenuItem menuItem) {
		menuItem.setActionView(com.codemobi.android.tvthailand.R.layout.refresh_menuitem);
	}

	public void stopLoadingProgressBar(MenuItem menuItem) {
		menuItem.setActionView(null);
	}

	@Override
	public void onLoadStart() {
		if (getSherlock() != null) {
			if (progressBar != null) {
				progressBar.setVisibility(View.VISIBLE);
			}

			if (refreshMenu != null) {
				startLoadingProgressBar(refreshMenu);
			}
		}
	}

	@Override
	public void onLoadFinished() {
		if (getSherlock() != null) {
			if (progressBar != null) {
				progressBar.setVisibility(View.GONE);
			}
			if (refreshMenu != null) {
				stopLoadingProgressBar(refreshMenu);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imb_add_to_fav:
			 mMyProgram.setFav(!mMyProgram.isFav());
			 mDaoMyProgram.update(mMyProgram);
			 break;
		case R.id.btn_more_detail:
			openMoreDetail();
			break;
		default:
			break;
		}
	}

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btn_more_detail:
                Intent intent = new Intent(OTVShowActivity.this, EpisodeActivity.class);
                intent.putExtra(EpisodeActivity.EXTRAS_PROGRAM, program);
                intent.putExtra(EpisodeActivity.EXTRAS_DISABLE_OTV, true);
                startActivity(intent);
                break;
        }
        return false;
    }

    private void openMoreDetail() {
		Intent intentMoreDetail = new Intent(OTVShowActivity.this,
				MoreDetailActivity.class);
		intentMoreDetail.putExtra(MoreDetailActivity.EXTRAS_TITLE,
				program.getTitle());
		intentMoreDetail
				.putExtra(MoreDetailActivity.EXTRAS_ID, program.getId());
		intentMoreDetail.putExtra(MoreDetailActivity.EXTRAS_THUMBNAIL,
				program.getThumbnail());
		intentMoreDetail.putExtra(MoreDetailActivity.EXTRAS_DESCRIPTION,
				program.getDescription());
		intentMoreDetail.putExtra(MoreDetailActivity.EXTRAS_DETAIL,
				program.getDetail());
		startActivity(intentMoreDetail);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		OTVEpisode ep = mOTVEpisodes.get(position - 1);
		Intent intent = new Intent(this, OTVPartActivity.class);
        intent.putExtra(OTVPartActivity.EXTRAS_OTV_EPISODE, ep);
		startActivity(intent);

	}

}
