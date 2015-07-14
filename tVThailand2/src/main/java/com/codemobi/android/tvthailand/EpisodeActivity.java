package com.codemobi.android.tvthailand;

import java.util.Date;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bumptech.glide.Glide;
import com.codemobi.android.tvthailand.datasource.Program;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.codemobi.android.tvthailand.MainApplication.TrackerName;
import com.codemobi.android.tvthailand.adapter.EpisodeAdapter;
import com.codemobi.android.tvthailand.contentprovider.MyProgramContentProvider;
import com.codemobi.android.tvthailand.database.Dao;
import com.codemobi.android.tvthailand.database.MyProgramModel;
import com.codemobi.android.tvthailand.database.ProgramTable;
import com.codemobi.android.tvthailand.datasource.Episode;
import com.codemobi.android.tvthailand.datasource.Episodes;
import com.codemobi.android.tvthailand.datasource.Parts;
import com.codemobi.android.tvthailand.datasource.Episodes.OnLoadListener;
import com.codemobi.android.tvthailand.datasource.Episodes.OnProgramChangeListener;
import com.codemobi.android.tvthailand.otv.OTVShowActivity;

public class EpisodeActivity extends SherlockFragmentActivity implements OnClickListener,
		OnItemClickListener, OnLoadListener, OnScrollListener {
	public static final String EXTRAS_PROGRAM = "EXTRAS_PROGRAM";
	public static final String EXTRAS_DISABLE_OTV = "EXTRAS_DISABLE_OTV";
	
	private View header;
	private Program program;
	
	private ProgressBar progressBar;
    private ProgressDialog progressDialog;

	private EpisodeAdapter mAdapter;
	private Episodes mEpisodes;
	
	/*
	 * Header View 
	 */
	private TextView tv_title;
	private TextView tvDescription;
	private ImageView imgThumbnail;
	private ImageButton imb_fav;
	
	//////
	
	private Handler mHandler = new Handler();
	private Dao<MyProgramModel> mDaoMyProgram;
	private MyProgramModel mMyProgram;
	private MenuItem refreshMenu;

    private Boolean isDisableOTV = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.episode_view);

        isDisableOTV = getIntent().getBooleanExtra(EXTRAS_DISABLE_OTV, false);

		initProgram();
		
		initUI();
		
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		
		setUpHeaderView();

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

        ListView epList = (ListView) findViewById(R.id.ep_list);
		epList.addHeaderView(header, null, false);

		mEpisodes = new Episodes(this);
		mAdapter = new EpisodeAdapter(this, mEpisodes);
		epList.setAdapter(mAdapter);

		mEpisodes.setOnLoadListener(this);
		mEpisodes.setOnProgramChangeListener(new OnProgramChangeListener() {
			
			@Override
			public void onProgramChange(Program program) {
				setUpHeaderView(program);
				mAdapter.notifyDataSetChanged();

				if (!isDisableOTV && program.isOTV() == 1) {
					Intent intent = new Intent(EpisodeActivity.this, OTVShowActivity.class);
					intent.putExtra(EpisodeActivity.EXTRAS_PROGRAM, program);
					startActivity(intent);
				}
			}
		});

		epList.setOnScrollListener(this);
		epList.setOnItemClickListener(this);

		mEpisodes.loadEpisodes(program.getId(), 0);
		refreshView();

		ContentObserver observer = new ContentObserver(mHandler) {
			@Override
			public void onChange(boolean selfChange) {
				refreshView();
			}
		};
		getContentResolver().registerContentObserver(
				MyProgramContentProvider.CONTENT_URI, true, observer);
		
		Tracker t = ((MainApplication) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Episode");
		t.send(new HitBuilders.AppViewBuilder().setCustomDimension(2, program.getTitle()).build());
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initUI() {
		header = ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.episode_header, null, false);

		tv_title = (TextView) header.findViewById(R.id.tv_title);
		imgThumbnail = (ImageView) header.findViewById(R.id.thumbnail);
		imgThumbnail.setOnClickListener(this);
		tvDescription = (TextView) header.findViewById(R.id.tv_description);
		imb_fav = (ImageButton) header.findViewById(R.id.imb_add_to_fav);
		header.findViewById(R.id.btn_more_detail).setOnClickListener(this);
		imb_fav.setOnClickListener(this);
	}

	private void initProgram() {
		Intent i = getIntent();
		program = i.getParcelableExtra(EXTRAS_PROGRAM);
	}

	private void setUpHeaderView(Program program) {
		this.program = program;
		setUpHeaderView();
	}
	
	private void setUpHeaderView() {
		setTitle(program.getTitle());
		tv_title.setText(program.getTitle());
		tvDescription.setText(program.getDescription());
		Glide.with(this)
				.load(program.getThumbnail())
				.fitCenter()
				.crossFade()
				.into(imgThumbnail);
	}

	private void refreshView() {
		if (mDaoMyProgram.size() > 0) {
			mMyProgram = mDaoMyProgram.get(0);
			if (mMyProgram.isFav()) {
				imb_fav.setBackgroundResource(R.drawable.button_fav_pressed);
			} else {
				imb_fav.setBackgroundResource(R.drawable.button_fav);
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
	
	public void startLoadingProgressBar(MenuItem menuItem){
		menuItem.setActionView(R.layout.refresh_menuitem);
	}
	public void stopLoadingProgressBar(MenuItem menuItem){
		menuItem.setActionView(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.episode, menu);
		refreshMenu = menu.findItem(R.id.refresh);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			mEpisodes.loadEpisodes(program.getId(), 0, false);
			break;
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
		case R.id.imb_add_to_fav:
			mMyProgram.setFav(!mMyProgram.isFav());
			mDaoMyProgram.update(mMyProgram);
			break;
		case R.id.btn_more_detail:
			openMoreDetail();
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		Episode episode = mEpisodes.get(position - 1);

		Tracker t = ((MainApplication) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("Episode");
		t.send(new HitBuilders.AppViewBuilder().setCustomDimension(3,
				episode.getTitle()).build());

		if (episode != null) {
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
		
		
		long lastUpdate = new Date().getTime();
		mMyProgram.setTimeViewed(lastUpdate);
		MyProgramUpdateTask myProgramUpdateTask = new MyProgramUpdateTask(
				mDaoMyProgram, mMyProgram) {

		};
		myProgramUpdateTask.execute();
	}
	
	@Override
	public void onLoadStart() {
		progressBar.setVisibility(View.VISIBLE);
		if(refreshMenu != null) {
			startLoadingProgressBar(refreshMenu);
		}
	}

	@Override
	public void onLoadFinished() {
		progressBar.setVisibility(View.GONE);
		if(refreshMenu != null) {
			stopLoadingProgressBar(refreshMenu);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int lastInScreen = firstVisibleItem + visibleItemCount + 5;
		int size = mEpisodes.size();
		if (size > 0 && (lastInScreen > totalItemCount)) {
			mEpisodes.loadEpisodes(program.getId(), size);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int state) {
	}
}
