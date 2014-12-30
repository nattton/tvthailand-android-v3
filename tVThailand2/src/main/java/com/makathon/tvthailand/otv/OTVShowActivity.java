package com.makathon.tvthailand.otv;

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
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.makathon.tvthailand.EpisodeActivity;
import com.makathon.tvthailand.MainApplication;
import com.makathon.tvthailand.MainApplication.TrackerName;
import com.makathon.tvthailand.MoreDetailActivity;
import com.makathon.tvthailand.MyVolley;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.contentprovider.MyProgramContentProvider;
import com.makathon.tvthailand.database.Dao;
import com.makathon.tvthailand.database.MyProgramModel;
import com.makathon.tvthailand.database.ProgramTable;
import com.makathon.tvthailand.datasource.OnLoadDataListener;
import com.makathon.tvthailand.datasource.Program;
import com.makathon.tvthailand.otv.model.OTVEpisode;
import com.makathon.tvthailand.otv.model.OTVEpisodeAdapter;
import com.makathon.tvthailand.otv.model.OTVEpisodes;
import com.makathon.tvthailand.otv.model.OTVEpisodes.OnOTVEpisodesChangeListener;

public class OTVShowActivity extends SherlockActivity implements
		OnLoadDataListener, OnClickListener, OnItemClickListener, OnLongClickListener {
	
	public static final String EXTRAS_PROGRAM = "EXTRAS_PROGRAM";

	private ProgressBar progressBar;
	private MenuItem refreshMenu;
	private MenuItem playMenu;

	/*
	 * Header View
	 */
	private TextView tv_title;
	private TextView tvDescription;
	private NetworkImageView imgThumbnail;
	private ImageButton imb_fav;
//	private LinearLayout header_buttom_ll;
	// private TextView vote_now_tv;
	// private TextView my_vote_tv;
	// private TextView avg_rating_tv;

	private Program program;

	private View header;
	private ListView epList;
	private OTVEpisodeAdapter mAdapter;
	private OTVEpisodes mOTVEpisodes;

	private Handler mHandler = new Handler();
	private Dao<MyProgramModel> mDaoMyProgram;
	private MyProgramModel mMyProgram;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.episode_view);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

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
			mMyProgram.setRating(program.getRating());
			MyProgamInsertTask myProgramInsertTask = new MyProgamInsertTask(
					mDaoMyProgram, mMyProgram) {

			};
			myProgramInsertTask.execute();
			Log.e("mDaoMyProgram", "Insert");
		} else {
			mMyProgram = mDaoMyProgram.get(0);
			mMyProgram.setTitle(program.getTitle());
			mMyProgram.setThumbnail(program.getThumbnail());
			mMyProgram.setDescription(program.getDescription());
			mMyProgram.setRating(program.getRating());
			MyProgamUpdateTask myProgamUpdateTask = new MyProgamUpdateTask(
					mDaoMyProgram, mMyProgram) {

			};
			myProgamUpdateTask.execute();
		}

		epList = (ListView) findViewById(R.id.ep_list);
		epList.addHeaderView(header, null, false);

		mOTVEpisodes = new OTVEpisodes();

		mAdapter = new OTVEpisodeAdapter(this, mOTVEpisodes,
				R.layout.episode_list_item);
		epList.setAdapter(mAdapter);
		epList.setOnItemClickListener(this);

		mOTVEpisodes.setOnLoadListener(this);
		mOTVEpisodes
				.setOnOTVEpisodesChangeListener(new OnOTVEpisodesChangeListener() {

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
		
		sendTracker(program);
	}

	private void initiazeUI() {
		header = ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.episode_header, null, false);

		// vote_now_tv = (TextView) header.findViewById(R.id.vote_now);
		// my_vote_tv = (TextView) header.findViewById(R.id.my_vote);

		tv_title = (TextView) header.findViewById(R.id.tv_title);
		imgThumbnail = (NetworkImageView) header.findViewById(R.id.thumbnail);
		imgThumbnail.setOnClickListener(this);
		tvDescription = (TextView) header.findViewById(R.id.tv_description);
		imb_fav = (ImageButton) header.findViewById(R.id.imb_add_to_fav);
//		imb_fav.setVisibility(View.GONE);

		// header.findViewById(R.id.vote_llayout).setOnClickListener(this);
		header.findViewById(R.id.btn_more_detail).setOnClickListener(this);
        header.findViewById(R.id.btn_more_detail).setOnLongClickListener(this);
		 imb_fav.setOnClickListener(this);

		// avg_rating_tv = (TextView) header
		// .findViewById(R.id.avg_rating);
	}
	
	private void sendTracker(Program show) {
		if (show != null) {
			Tracker t = ((MainApplication) getApplication())
					.getTracker(TrackerName.APP_TRACKER);
			t.setScreenName("OTVShow");
			t.send(new HitBuilders.AppViewBuilder().setCustomDimension(2,
					show.getTitle()).build());

			Tracker t2 = ((MainApplication) getApplication())
					.getTracker(TrackerName.OTV_TRACKER);
			t2.setScreenName("OTVShow");
			t2.send(new HitBuilders.AppViewBuilder().setCustomDimension(1,
					show.getTitle()).build());
		}
	}

	private void setUpHeaderView() {
		setTitle(program.getTitle());
		tv_title.setText(program.getTitle());
		tvDescription.setText(program.getDetail());
		imgThumbnail.setImageUrl(program.getThumbnail(), MyVolley.getImageLoader());
	}

	private void refreshView() {
		if (mDaoMyProgram.size() > 0) {
			mMyProgram = mDaoMyProgram.get(0);
			if (mMyProgram.isFav()) {
				imb_fav.setBackgroundResource(R.drawable.button_fav_pressed);
			} else {
				imb_fav.setBackgroundResource(R.drawable.button_fav);
			}

			// if (mMyProgram.getMyVote() > 0) {
			// my_vote_tv.setText(String.valueOf(mMyProgram.getMyVote()));
			// vote_now_tv.setText("You");
			// } else {
			// vote_now_tv.setText("Rate this");
			// }
		}
	}

	protected class MyProgamInsertTask extends
			AsyncTask<Integer, Integer, Void> {

		private Dao<MyProgramModel> mDaoMyProgram;
		private MyProgramModel mMyProgram;

		public MyProgamInsertTask(Dao<MyProgramModel> mDaoMyProgram,
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

	protected class MyProgamUpdateTask extends
			AsyncTask<Integer, Integer, Void> {

		private Dao<MyProgramModel> mDaoMyProgram;
		private MyProgramModel mMyProgram;

		public MyProgamUpdateTask(Dao<MyProgramModel> mDaoMyProgram,
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
		inflater.inflate(R.menu.program, menu);
		refreshMenu = menu.findItem(R.id.refresh);
		playMenu = menu.findItem(R.id.play);
		playMenu.setVisible(false);

		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			mOTVEpisodes.loadOTVEpisodes(program);
			break;

		default:
			break;
		}
		return true;
	}

	public void startLoadingProgressBar(MenuItem menuItem) {
		menuItem.setActionView(R.layout.refresh_menuitem);
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
			// // case R.id.vote_llayout:
			// // Intent intentVote = new Intent(EpisodeActivity.this,
			// RatingActivity.class);
			// // intentVote.putExtra(RatingActivity.EXTRAS_ID,
			// mMyProgram.getId());
			// // startActivity(intentVote);
			// // break;
		case R.id.btn_more_detail:
			openMoreDetail();
			break;
		// case R.id.thumbnail:
		// openLargeImage();
		// break;
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
