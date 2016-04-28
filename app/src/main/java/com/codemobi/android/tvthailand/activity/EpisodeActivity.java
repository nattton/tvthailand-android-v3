package com.codemobi.android.tvthailand.activity;

import java.util.Date;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.adapter.EpisodeAdapter;
import com.codemobi.android.tvthailand.datasource.OnLoadDataListener;
import com.codemobi.android.tvthailand.adapter.OnTapListener;
import com.codemobi.android.tvthailand.datasource.Program;
import com.codemobi.android.tvthailand.otv.activity.OTVPartActivity;
import com.codemobi.android.tvthailand.otv.adapter.OTVEpisodeAdapter;
import com.codemobi.android.tvthailand.otv.model.OTVEpisode;
import com.codemobi.android.tvthailand.otv.model.OTVEpisodes;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.codemobi.android.tvthailand.contentprovider.MyProgramContentProvider;
import com.codemobi.android.tvthailand.database.Dao;
import com.codemobi.android.tvthailand.database.MyProgramModel;
import com.codemobi.android.tvthailand.database.ProgramTable;
import com.codemobi.android.tvthailand.datasource.Episode;
import com.codemobi.android.tvthailand.datasource.Episodes;
import com.codemobi.android.tvthailand.datasource.Parts;
import com.codemobi.android.tvthailand.datasource.Episodes.OnProgramChangeListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EpisodeActivity extends AppCompatActivity implements OnClickListener, OnLoadDataListener, OnTapListener {
	public static final String EXTRAS_PROGRAM = "EXTRAS_PROGRAM";
	public static final String EXTRAS_DISABLE_OTV = "EXTRAS_DISABLE_OTV";

	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.image_header) ImageView imageHeader;
	@BindView(R.id.progress_bar) ProgressBar progressBar;
	@BindView(R.id.favorite_button) FloatingActionButton favoriteButton;
	@BindView(R.id.rv_episode) RecyclerView mRecyclerView;

	ProgressDialog progressDialog;
	EpisodeAdapter episodeAdapter;
	OTVEpisodeAdapter mOTVAdapter;
	LinearLayoutManager mLayoutManager;


	private Boolean isOTV;
	private Program program;
	private Episodes mEpisodes;
	private OTVEpisodes mOTVEpisodes;
	
	private Handler mHandler = new Handler();
	private Dao<MyProgramModel> mDaoMyProgram;
	private MyProgramModel mMyProgram;
    private Boolean isDisableOTV = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_episode_new);

		ButterKnife.bind(this);

        isDisableOTV = getIntent().getBooleanExtra(EXTRAS_DISABLE_OTV, false);

		initProgram();
		initToolbar();
		initUI();
		initInstances();
		refreshView();
		registerContentObserver();
		sendTracker();
	}

	private void initProgram() {
		Intent i = getIntent();
		program = i.getParcelableExtra(EXTRAS_PROGRAM);
		isOTV = (program.isOTV() == 1) && !isDisableOTV;
	}

	private void initToolbar() {
		toolbar.setTitle(program.getTitle());
		setSupportActionBar(toolbar);
		Glide.with(this).load(program.getThumbnail()).into(imageHeader);
	}

	private void initUI() {
		favoriteButton.setOnClickListener(this);
	}

	private void initInstances() {
		String where = ProgramTable.ProgramColumns.PROGRAM_ID + " = " + program.getId();
		mDaoMyProgram = new Dao<>(MyProgramModel.class, this,
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

		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);

		if (isOTV) {
			initOTVAdapter();
		}
		else {
			initAdapter();
		}
	}

	private void initAdapter() {
		mEpisodes = new Episodes(this);
		episodeAdapter = new EpisodeAdapter(mEpisodes);
		mRecyclerView.setAdapter(episodeAdapter);
		episodeAdapter.setOnTapListener(this);

		mEpisodes.setOnLoadDataListener(this);
		mEpisodes.setOnProgramChangeListener(new OnProgramChangeListener() {
			@Override
			public void onProgramChange(Program program) {
				setUpHeaderView(program);
				episodeAdapter.notifyDataSetChanged();
				if (!isDisableOTV && program.isOTV() == 1) {
					initInstances();
				}
			}
		});
		mEpisodes.setOnEpisodeChangeListener(new Episodes.OnEpisodeChangeListener() {
			@Override
			public void onEpisodeChange(Episodes episodes) {
				episodeAdapter.notifyDataSetChanged();
			}
		});

		mEpisodes.loadEpisodes(program.getId(), 0);
		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int visibleItemCount = mLayoutManager.getChildCount();
				int totalItemCount = mLayoutManager.getItemCount();
				int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
				int size = mEpisodes.size();
				if (size > 0 && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
					mEpisodes.loadEpisodes(program.getId(), size);
				}
			}
		});
	}

	private void initOTVAdapter() {
		mOTVEpisodes = new OTVEpisodes();
		mOTVAdapter = new OTVEpisodeAdapter(this, mOTVEpisodes, program.getOtvLogo());
		mRecyclerView.setAdapter(mOTVAdapter);
		mOTVAdapter.setOnTapListener(this);
		mOTVEpisodes.setOnLoadDataListener(this);
		mOTVEpisodes.setOnOTVEpisodesChangeListener(new OTVEpisodes.OnOTVEpisodesChangeListener() {

			@Override
			public void OnOTVEpisodesChange(OTVEpisodes otvEpisodes) {
				mOTVAdapter.notifyDataSetChanged();
			}
		});
		mOTVEpisodes.loadOTVEpisodes(program);
	}


	private void registerContentObserver() {
		ContentObserver observer = new ContentObserver(mHandler) {
			@Override
			public void onChange(boolean selfChange) {
				refreshView();
			}
		};
		getContentResolver().registerContentObserver(
				MyProgramContentProvider.CONTENT_URI, true, observer);
	}

	private void sendTracker() {
		Tracker t = ((MainApplication) getApplication()).getDefaultTracker();
		t.setScreenName("Episode");
		t.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(2, program.getTitle()).build());

		if (isOTV) {
			Tracker t2 = ((MainApplication) getApplication()).getOTVTracker();
			t2.setScreenName("OTVShow");
			t2.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(1,
					program.getTitle()).build());
		}
	}

	private void setUpHeaderView(Program program) {
		this.program = program;
		setTitle(program.getTitle());
	}

	private void refreshView() {
		if (mDaoMyProgram.size() > 0) {
			mMyProgram = mDaoMyProgram.get(0);
			if (mMyProgram.isFav()) {
				favoriteButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_white_48dp));
			} else {
				favoriteButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_border_white_48dp));
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
		getMenuInflater().inflate(R.menu.episode, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			if (isOTV)
				mOTVEpisodes.loadOTVEpisodes(program);
			else
				mEpisodes.loadEpisodes(program.getId(), 0, false);
			break;
		case R.id.info:
			openLargeImage();
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.favorite_button:
			mMyProgram.setFav(!mMyProgram.isFav());
			mDaoMyProgram.update(mMyProgram);
			break;
		case R.id.thumbnail:
			openLargeImage();
			break;
		default:
			break;
		}
	}

	private void openMoreDetail() {
		Intent intentMoreDetail = new Intent(EpisodeActivity.this,
				MoreDetailActivity.class);
		intentMoreDetail.putExtra(MoreDetailActivity.EXTRAS_TITLE, program.getTitle());
		intentMoreDetail.putExtra(MoreDetailActivity.EXTRAS_ID, program.getId());
		intentMoreDetail.putExtra(MoreDetailActivity.EXTRAS_THUMBNAIL, program.getThumbnail());
		intentMoreDetail.putExtra(MoreDetailActivity.EXTRAS_DESCRIPTION, program.getDescription());
		intentMoreDetail.putExtra(MoreDetailActivity.EXTRAS_DETAIL, program.getDetail());
		startActivity(intentMoreDetail);
	}

	private void openLargeImage() {
		Intent intent = new Intent(EpisodeActivity.this,
				LargeThumbnailActivity.class);
		intent.putExtra(LargeThumbnailActivity.EXTRAS_TITLE,
				program.getTitle());
		intent.putExtra(LargeThumbnailActivity.EXTRAS_LARGE_THUMBNAIL, program.getPoster());
		intent.putExtra(LargeThumbnailActivity.EXTRAS_PROGRAM_ID, program.getId());
		intent.putExtra(LargeThumbnailActivity.EXTRAS_DETAIL, program.getDetail());
		startActivity(intent);
	}

	@Override
	public void onTapView(int position) {
		if (isOTV)
			onSelectedOTV(position);
		else
			onSelected(position);
		
		long lastUpdate = new Date().getTime();
		mMyProgram.setTimeViewed(lastUpdate);
		MyProgramUpdateTask myProgramUpdateTask = new MyProgramUpdateTask(
				mDaoMyProgram, mMyProgram) {

		};
		myProgramUpdateTask.execute();
	}

	private void onSelected(int position) {
			Episode episode = mEpisodes.get(position);
			Tracker t = ((MainApplication) getApplication()).getDefaultTracker();
			t.setScreenName("Episode");
			t.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(3,
					episode.getTitle()).build());
			episode.sendView();
			if (episode.size() == 1) {
				Parts parts = new Parts(this, episode.getTitle(), program.getThumbnail(),
						episode.getVideos(), episode.getSrcType(),
						episode.getPassword());
				parts.setOnLoadListener(new Parts.OnLoadListener() {

					@Override
					public void onStart() {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								if (progressDialog == null)
									progressDialog = ProgressDialog.show(EpisodeActivity.this, "",
											"Loading, Please wait...", true);
								else progressDialog.show();
							}
						});
					}

					@Override
					public void onFinish() {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								progressDialog.dismiss();
							}
						});
					}

					@Override
					public void onError(final String message) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(EpisodeActivity.this, message, Toast.LENGTH_LONG).show();
							}
						});
					}
				});
				parts.playVideoPart(0);

			} else {
				Intent intent = new Intent(EpisodeActivity.this,
						PartActivity.class);
				intent.putExtra(PartActivity.EXTRAS_TITLE, episode.getTitle());
				intent.putExtra(PartActivity.EXTRAS_VIDEOS, episode.getVideos());
				intent.putExtra(PartActivity.EXTRAS_SRC_TYPE,
						episode.getSrcType());
				intent.putExtra(PartActivity.EXTRAS_PASSWORD,
						episode.getPassword());
				intent.putExtra(PartActivity.EXTRAS_ICON, program.getThumbnail());
				startActivity(intent);
			}
	}

	private void onSelectedOTV(int position) {
		OTVEpisode ep = mOTVEpisodes.get(position);
		Intent intent = new Intent(this, OTVPartActivity.class);
		intent.putExtra(OTVPartActivity.EXTRAS_OTV_EPISODE, ep);
		startActivity(intent);
	}
	
	@Override
	public void onLoadStart() {
		progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void onLoadFinished() {
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onLoadError(String error) {

	}
}
