package com.makathon.tvthailand;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.makathon.tvthailand.adapter.ProgramAdapter;
import com.makathon.tvthailand.datasource.OnLoadDataListener;
import com.makathon.tvthailand.datasource.PreRollAd;
import com.makathon.tvthailand.datasource.PreRollAdFactory;
import com.makathon.tvthailand.datasource.Program;
import com.makathon.tvthailand.datasource.Programs;
import com.makathon.tvthailand.datasource.Programs.OnProgramChangeListener;
import com.makathon.tvthailand.otv.OTVShowActivity;
import com.makathon.tvthailand.player.VastContentPlayerActivity;

public class ProgramActivity extends SherlockActivity implements
		OnLoadDataListener, OnItemClickListener, OnScrollListener {

	public static final String EXTRAS_MODE = "EXTRAS_MODE";
    public static final String EXTRAS_ID = "EXTRAS_ID";
    public static final String EXTRAS_TITLE = "EXTRAS_TITLE";
    public static final String EXTRAS_ICON = "EXTRAS_ICON";
    public static final String EXTRAS_URL = "EXTRAS_URL";

	/** MODE **/
    public static final int BY_CATEGORY = 1;
    public static final int BY_CHANNEL = 2;

	private int mode;
	private String id;
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
		setContentView(R.layout.simple_grid_view);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		Bundle bundle = getIntent().getExtras();
		String title = bundle.getString(EXTRAS_TITLE);
		setTitle(title);
		String icon = bundle.getString(EXTRAS_ICON);
		url = bundle.getString(EXTRAS_URL);

        MyVolley.getImageLoader().get(icon, new ImageListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {

			}
			
			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				if (response.getBitmap() != null)
					getSupportActionBar().setIcon(new BitmapDrawable(getResources(), response.getBitmap()));
			}
		});
        
		id = bundle.getString(EXTRAS_ID);
		mode = bundle.getInt(EXTRAS_MODE);

		mPrograms = new Programs();
		mAdapter = new ProgramAdapter(this, mPrograms, R.layout.whatnew_grid_item, MyVolley.getImageLoader());

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
		
		
		mPrograms.setOnProgramChangeListener(new OnProgramChangeListener() {

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
		MenuInflater inflater = getSherlock().getMenuInflater();
		inflater.inflate(R.menu.program, menu);
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
		// TODO Auto-generated method stub
		
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
