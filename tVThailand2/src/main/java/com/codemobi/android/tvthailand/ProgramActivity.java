package com.codemobi.android.tvthailand;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codemobi.android.tvthailand.adapter.ProgramAdapter;
import com.codemobi.android.tvthailand.datasource.OnLoadDataListener;
import com.codemobi.android.tvthailand.datasource.PreRollAd;
import com.codemobi.android.tvthailand.datasource.PreRollAdFactory;
import com.codemobi.android.tvthailand.datasource.Program;
import com.codemobi.android.tvthailand.datasource.Programs;
import com.codemobi.android.tvthailand.otv.activity.OTVShowActivity;
import com.codemobi.android.tvthailand.player.VastContentPlayerActivity;

public class ProgramActivity extends AppCompatActivity implements
		OnLoadDataListener, OnItemClickListener, OnScrollListener {

	Toolbar toolbar;

	public static final String EXTRAS_MODE = "EXTRAS_MODE";
    public static final String EXTRAS_ID = "EXTRAS_ID";
    public static final String EXTRAS_TITLE = "EXTRAS_TITLE";
    public static final String EXTRAS_ICON = "EXTRAS_ICON";
    public static final String EXTRAS_URL = "EXTRAS_URL";

	/** MODE **/
    public static final int BY_CATEGORY = 1;
    public static final int BY_CHANNEL = 2;

	private String id;
	private int mode;
	private String title;
	private String icon;
	private String url;

	private Programs mPrograms;
	private ProgramAdapter mAdapter;
	private MenuItem refreshMenu;

	private ProgressBar progressBar;
	private TextView textViewNoContent;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_program);

		initExtras();
		initToolbar();
		setTitle(title);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		mPrograms = new Programs();
		mAdapter = new ProgramAdapter(mPrograms);

		textViewNoContent = (TextView) findViewById(R.id.textViewNoContent);
		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(mAdapter);
		
		FrameLayout live_frame_ll = (FrameLayout) findViewById(R.id.live_frame_ll);
		if (mode == BY_CHANNEL && url != null && !url.equals("")) {
			live_frame_ll.setVisibility(View.VISIBLE);
		} else {
			live_frame_ll.setVisibility(View.GONE);
		}
		ImageButton watch_live_btn = (ImageButton) findViewById(R.id.watch_live_btn);
		watch_live_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				playVideo(url);
			}
		});
		
		TextView watch_live_text = (TextView) findViewById(R.id.watch_live_txt);
		watch_live_text.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				playVideo(url);
			}
		});
		
		
		mPrograms.setOnProgramChangeListener(new Programs.OnProgramChangeListener() {

			@Override
			public void onProgramChange(Programs programs) {
				mAdapter.notifyDataSetChanged();
				if (programs.size() > 0) {
					textViewNoContent.setVisibility(View.GONE);
				} else {
					textViewNoContent.setVisibility(View.VISIBLE);
				}
			}
		});

		mPrograms.setOnLoadListener(this);

		gridview.setOnItemClickListener(this);
		gridview.setOnScrollListener(this);
	}

	private void initExtras() {
		Bundle bundle = getIntent().getExtras();
		id = bundle.getString(EXTRAS_ID);
		mode = bundle.getInt(EXTRAS_MODE);
		title = bundle.getString(EXTRAS_TITLE);
		icon = bundle.getString(EXTRAS_ICON);
		url = bundle.getString(EXTRAS_URL);
	}

	private void initToolbar() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
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
	
	@Override
	protected void onResume() {
		loadProgram(0);
		
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
		refreshMenu = menu.findItem(R.id.refresh);
        MenuItem playMenu = menu.findItem(R.id.play);
		if (mode == BY_CHANNEL && url != null && !url.equals("")) {
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
			playVideo(url);
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
//		if (getSherlock() != null) {
//			if (progressBar != null) {
//				progressBar.setVisibility(View.VISIBLE);
//			}
//
//			if (refreshMenu != null) {
//				startLoadingProgressBar(refreshMenu);
//			}
//		}
	}

	@Override
	public void onLoadFinished() {
//		if (getSherlock() != null) {
//			if (progressBar != null) {
//				progressBar.setVisibility(View.GONE);
//			}
//			if (refreshMenu != null) {
//				stopLoadingProgressBar(refreshMenu);
//			}
//		}
	}

	private void playVideo(final String videoUrl) {
        final PreRollAdFactory preRollAdFactory = new PreRollAdFactory(this.getApplicationContext());
        preRollAdFactory.setOnLoadListener(new PreRollAdFactory.OnLoadListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                Intent intentVideo = new Intent(ProgramActivity.this, VastContentPlayerActivity.class);
                intentVideo.putExtra(VastContentPlayerActivity.EXTRAS_CONTENT_URL, videoUrl);

                PreRollAd ad = preRollAdFactory.getPreRollAd();
                if (ad != null) {
                    intentVideo.putExtra(VastContentPlayerActivity.EXTRAS_TAG_URL, ad.getUrl());
                    intentVideo.putExtra(VastContentPlayerActivity.EXTRAS_SKIP_TIME, ad.getSkipTime());
                }

                startActivity(intentVideo);
            }
        });
        preRollAdFactory.load();

//		Intent intentVideo = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
//		intentVideo.putExtra(Intent.EXTRA_TITLE, title);
//		intentVideo.setDataAndType(Uri.parse(videoUrl), "video/*");
//		startActivity(intentVideo);
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {		
		int size = mAdapter.getCount();
		if (size > 0
				&& ((firstVisibleItem + visibleItemCount + 10) > totalItemCount)) {
			loadProgram(size);
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view,
			int position, long id) {
        if (mPrograms.size() > 0) {
            Program program = mPrograms.get(position);

            if (program.isOTV() == 1) {
                Intent intent = new Intent(ProgramActivity.this, OTVShowActivity.class);
                intent.putExtra(EpisodeActivity.EXTRAS_PROGRAM, program);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ProgramActivity.this, EpisodeActivity.class);
                intent.putExtra(EpisodeActivity.EXTRAS_PROGRAM, program);
                startActivity(intent);
            }
        }
	}


}
