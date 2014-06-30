package com.makathon.tvthailand;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.adapter.ProgramAdapter;
import com.makathon.tvthailand.ads.InHouseAdView;
import com.makathon.tvthailand.datasource.OnLoadDataListener;
import com.makathon.tvthailand.datasource.Program;
import com.makathon.tvthailand.datasource.Programs;
import com.makathon.tvthailand.datasource.Programs.OnProgramChangeListener;
import com.makathon.tvthailand.otv.OTVShowActivity;

public class ProgramActivity extends SherlockActivity implements
		OnLoadDataListener, OnItemClickListener, OnScrollListener {

	static final String EXTRAS_MODE = "EXTRAS_MODE";
	static final String EXTRAS_ID = "EXTRAS_ID";
	static final String EXTRAS_TITLE = "EXTRAS_TITLE";
	static final String EXTRAS_ICON = "EXTRAS_ICON";
	static final String EXTRAS_URL = "EXTRAS_URL";

	/** MODE **/
	static final int BY_CATEGORY = 1;
	static final int BY_CHANNEL = 2;

	private static ImageLoader mImageLoader;

	private int mode;
	private String id;
	private String title;
	private String icon;
	private String url;

	private Programs mPrograms;
	private ProgramAdapter mAdapter;
	private MenuItem refreshMenu;
	private MenuItem playMenu;

	private ProgressBar progressBar;
	private TextView textViewNoContent;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_grid_view);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		Bundle bundle = getIntent().getExtras();
		title = bundle.getString(EXTRAS_TITLE);
		setTitle(title);
		icon = bundle.getString(EXTRAS_ICON);
		url = bundle.getString(EXTRAS_URL);

        mImageLoader = MyVolley.getImageLoader();        
        mImageLoader.get(icon, new ImageListener() {
			
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

		mPrograms = new Programs(this);
		mAdapter = new ProgramAdapter(this, mPrograms, R.layout.whatnew_grid_item, mImageLoader);

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
		playMenu = menu.findItem(R.id.play);
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

	private void playVideo(String videoUrl) {
		Intent intentVideo = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
		intentVideo.putExtra(Intent.EXTRA_TITLE, title);
		intentVideo.setDataAndType(Uri.parse(videoUrl), "video/*");
		startActivity(intentVideo);
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
		Program program = mPrograms.get(position);
		
		if (program.isOTV() == 1) {
			Intent intent = new Intent(ProgramActivity.this, OTVShowActivity.class);
			intent.putExtra(EpisodeActivity.EXTRAS_PROGRAM, program);
			startActivity(intent);
		}
		else {
			Intent intent = new Intent(ProgramActivity.this, EpisodeActivity.class);
			intent.putExtra(EpisodeActivity.EXTRAS_PROGRAM, program);
			startActivity(intent);
		}
	}


}
