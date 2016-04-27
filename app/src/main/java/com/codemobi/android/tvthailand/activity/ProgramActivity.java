package com.codemobi.android.tvthailand.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.adapter.OnTapListener;
import com.codemobi.android.tvthailand.adapter.ProgramListAdapter;
import com.codemobi.android.tvthailand.dao.advertise.PreRollAdDao;
import com.codemobi.android.tvthailand.datasource.OnLoadDataListener;
import com.codemobi.android.tvthailand.dao.advertise.PreRollAdFactory;
import com.codemobi.android.tvthailand.datasource.Program;
import com.codemobi.android.tvthailand.datasource.Programs;
import com.codemobi.android.tvthailand.player.VastContentPlayerActivity;
import com.rey.material.widget.ProgressView;
import com.vmax.android.ads.api.VmaxAdView;
import com.vmax.android.ads.common.VmaxAdListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgramActivity extends AppCompatActivity implements
		OnLoadDataListener, OnTapListener, SwipeRefreshLayout.OnRefreshListener {

	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.swipe_container) SwipeRefreshLayout swipeLayout;
	@BindView(R.id.root_layout) CoordinatorLayout rootLayout;
	@BindView(R.id.progress_view) ProgressView progressView;
	@BindView(R.id.text_view_no_content) TextView textViewNoContent;
	@BindView(R.id.rv_program) RecyclerView mRecyclerView;
	@BindView(R.id.live_frame_ll) FrameLayout liveFrameLL;
	@BindView(R.id.watch_live_btn) ImageButton watchLiveBtn;
	@BindView(R.id.watch_live_txt) TextView watchLiveTxt;

	public static final String EXTRAS_MODE = "EXTRAS_MODE";
    public static final String EXTRAS_ID = "EXTRAS_ID";
    public static final String EXTRAS_TITLE = "EXTRAS_TITLE";
    public static final String EXTRAS_ICON = "EXTRAS_ICON";
    public static final String EXTRAS_LIVE_URL = "EXTRAS_LIVE_URL";

	/** MODE **/
    public static final int BY_CATEGORY = 1;
    public static final int BY_CHANNEL = 2;

	private String id;
	private int mode;
	private String title;
	private String icon;
	private String liveURL;

	private Programs mPrograms;
	private GridLayoutManager mLayoutManager;
	private ProgramListAdapter programAdapter;

	private VmaxAdView vmaxAdView;
	private VmaxAdListener mAdListener;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_program_new);

		ButterKnife.bind(this);

		initExtras();
		initToolbar();
		initInstances();
	}

	private void initExtras() {
		Bundle bundle = getIntent().getExtras();
		id = bundle.getString(EXTRAS_ID);
		mode = bundle.getInt(EXTRAS_MODE);
		title = bundle.getString(EXTRAS_TITLE);
		icon = bundle.getString(EXTRAS_ICON);
		liveURL = bundle.getString(EXTRAS_LIVE_URL);
	}

	private void initToolbar() {
		toolbar.setTitle(title);
		Glide.with(this).load(icon)
				.asBitmap()
				.into(new SimpleTarget<Bitmap>() {
					@Override
					public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
						toolbar.setLogo(new BitmapDrawable(getResources(), resource));
					}
				});
		setSupportActionBar(toolbar);
	}

	private void initInstances() {
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorSchemeResources(R.color.holo_blue_bright,
				R.color.holo_green_light,
				R.color.holo_orange_light,
				R.color.holo_red_light);

		mPrograms = new Programs();
		mPrograms.setOnLoadListener(this);

		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.category_column_num));
		mRecyclerView.setLayoutManager(mLayoutManager);
		programAdapter = new ProgramListAdapter(this, mPrograms);
		mRecyclerView.setAdapter(programAdapter);
		programAdapter.setOnTapListener(this);
		mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int visibleItemCount = mLayoutManager.getChildCount();
				int totalItemCount = mLayoutManager.getItemCount();
				int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
				int size = mPrograms.size();
				if (size > 0 && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
					loadProgram(size);
				}
			}
		});

		if (mode == BY_CHANNEL && liveURL != null && !liveURL.equals("")) {
			liveFrameLL.setVisibility(View.VISIBLE);
		} else {
			liveFrameLL.setVisibility(View.GONE);
		}

		OnClickListener clickLive = new OnClickListener() {
			@Override
			public void onClick(View v) {
				playVideo(liveURL);
			}
		};
		watchLiveBtn.setOnClickListener(clickLive);
		watchLiveTxt.setOnClickListener(clickLive);


		mPrograms.setOnProgramChangeListener(new Programs.OnProgramChangeListener() {

			@Override
			public void onProgramChange(Programs programs) {
				programAdapter.notifyDataSetChanged();
				if (programs.size() > 0) {
					textViewNoContent.setVisibility(View.GONE);
				} else {
					textViewNoContent.setVisibility(View.VISIBLE);
				}
			}
		});

		loadProgram(0);
	}

	@Override
	public void onRefresh() {
		loadProgram(0);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void loadProgram(int start) {
		switch (mode) {
		case BY_CATEGORY:
			mPrograms.loadProgramByCategory(id, start);
			break;
		case BY_CHANNEL:
			mPrograms.loadProgramByChannel(id, start);
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.program, menu);
        MenuItem playMenu = menu.findItem(R.id.play);
		if (mode == BY_CHANNEL && liveURL != null && !liveURL.equals("")) {
			playMenu.setVisible(true);
		} else {
			playMenu.setVisible(false);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			loadProgram(0);
			break;
		case R.id.play:
			playVideo(liveURL);
			break;
		default:
			break;
		}
		return true;
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
		Snackbar.make(rootLayout, error, Snackbar.LENGTH_LONG)
				.setAction("Open Settings", new OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(new Intent(Settings.ACTION_SETTINGS));
					}
				}).show();
	}

	private void playVideo(final String videoUrl) {

        final PreRollAdFactory preRollAdFactory = new PreRollAdFactory();
        preRollAdFactory.setOnLoadListener(new PreRollAdFactory.OnLoadListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                Intent intentVideo = new Intent(ProgramActivity.this, VastContentPlayerActivity.class);
                intentVideo.putExtra(VastContentPlayerActivity.EXTRAS_CONTENT_URL, videoUrl);

                PreRollAdDao ad = preRollAdFactory.getPreRollAd();
                if (ad != null) {
                    intentVideo.putExtra(VastContentPlayerActivity.EXTRAS_TAG_URL, ad.getUrl());
                    intentVideo.putExtra(VastContentPlayerActivity.EXTRAS_SKIP_TIME, ad.getSkipTime());
                }

                startActivityForResult(intentVideo, VastContentPlayerActivity.LIVE_PLAYER_CODE);
            }
        });
        preRollAdFactory.load();

//		Intent intentVideo = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
//		intentVideo.putExtra(Intent.EXTRA_TITLE, title);
//		intentVideo.setDataAndType(Uri.parse(videoUrl), "video/*");
//		startActivity(intentVideo);
	}

	@Override
	public void onTapView(int position) {
        if (mPrograms.size() > 0) {
			Program program = mPrograms.get(position);
			Intent intent = new Intent(ProgramActivity.this, EpisodeActivity.class);
			intent.putExtra(EpisodeActivity.EXTRAS_PROGRAM, program);
			startActivity(intent);
		}
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VastContentPlayerActivity.LIVE_PLAYER_CODE)
			startAds();
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void startAds() {
		adListenerInitialization();
		vmaxAdView = new VmaxAdView(this, getResources().getString(R.string.vmax_interstitial_ad_unit_id), VmaxAdView.UX_INTERSTITIAL);
		vmaxAdView.setAdListener(mAdListener);
		vmaxAdView.setUxType(VmaxAdView.UX_INTERSTITIAL);
		vmaxAdView.loadAd();
	}

	private void adListenerInitialization() {
		mAdListener = new VmaxAdListener() {

			@Override
			public void didInteractWithAd(VmaxAdView adView) {
				Log.d("Vmax", "adViewDidLoadAd");
			}

			@Override
			public void adViewDidLoadAd(VmaxAdView adView) {
				Log.d("Vmax", "adViewDidLoadAd");
			}

			@Override
			public void willPresentOverlay(VmaxAdView adView) {
				Log.d("Vmax", "willPresentOverlay");
			}

			@Override
			public void willDismissOverlay(VmaxAdView adView) {
				Log.d("Vmax", "willDismissOverlay");
			}

			@Override
			public void adViewDidCacheAd(VmaxAdView adView) {
				Log.d("Vmax", "adViewDidCacheAd");
				if (adView != null) {
					adView.showAd();
				}
			}

			@Override
			public VmaxAdView didFailedToLoadAd(String arg0) {
				Log.d("VmaxAdView", "didFailedToLoadAd");
				return null;
			}

			@Override
			public VmaxAdView didFailedToCacheAd(String Error) {
				Log.d("VmaxAdView", "didFailedToCacheAd");
				return null;
			}

			@Override
			public void willLeaveApp(VmaxAdView adView) {
				Log.d("Vmax", "willLeaveApp");
			}

			@Override
			public void onVideoCompleted() {

			}
		};
	}


}
