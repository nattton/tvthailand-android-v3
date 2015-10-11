package com.codemobi.android.tvthailand.activity;

import android.app.Activity;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.codemobi.android.tvthailand.contentprovider.MyProgramContentProvider;
import com.codemobi.android.tvthailand.database.Dao;
import com.codemobi.android.tvthailand.database.MyProgramModel;
import com.codemobi.android.tvthailand.database.ProgramTable;

public class LargeThumbnailActivity extends Activity implements
		OnClickListener {
	public static final String EXTRAS_TITLE = "EXTRAS_TITLE";
	public static final String EXTRAS_LARGE_THUMBNAIL = "EXTRAS_LARGE_THUMBNAIL";
	public static final String EXTRAS_PROGRAM_ID = "EXTRAS_PROGRAM_ID";
	public static final String EXTRAS_DETAIL = "EXTRAS_DETAIL";

	private Handler mHandler = new Handler();
	
	private String title_str;
	private String larg_thumbnail_url;

	private String programId;
	private String detail;
	
	private Dao<MyProgramModel> mDaoMyProgram;
	private MyProgramModel mMyProgram;
	
	private ImageButton buttonFav;
	private TextView textDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.large_thumbnail);
		textDescription = (TextView)findViewById(R.id.description);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			title_str = extras.getString(EXTRAS_TITLE);
			larg_thumbnail_url = extras.getString(EXTRAS_LARGE_THUMBNAIL);
			programId = extras.getString(EXTRAS_PROGRAM_ID);
			detail = extras.getString(EXTRAS_DETAIL);
			if (programId != null) {
				buttonFav = (ImageButton) findViewById(R.id.imb_add_to_fav);
				buttonFav.setOnClickListener(this);
				buttonFav.setVisibility(View.VISIBLE);
				
				loadProgramDB();
			}
			else {
				textDescription.setText(title_str);
			}

		} else {
			finish();
		}

		ImageView img = (ImageView) findViewById(R.id.iv_largThumbnail);
		Glide.with(this)
				.load(larg_thumbnail_url)
				.crossFade()
				.into(img);
		
		Tracker t = ((MainApplication)getApplication()).getDefaultTracker();
		t.setScreenName("LargeThumbnail");
		t.send(new HitBuilders.AppViewBuilder().build());

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imb_add_to_fav:
			 tapFavorite();
			break;
		case R.id.btn_more_detail:
			break;

		default:
			break;
		}

	}
	
	private void loadProgramDB() {
		String where = ProgramTable.ProgramColumns.PROGRAM_ID + " = " + programId;
		mDaoMyProgram = new Dao<>(MyProgramModel.class, this,
				MyProgramContentProvider.CONTENT_URI, where);
		mMyProgram = mDaoMyProgram.get(0);
		
		if("".equals(detail) || detail == null) {
			textDescription.setText(mMyProgram.getTitle());
		} else {
			String description = String.format("%s\n%s", mMyProgram.getTitle(), detail);
			textDescription.setText(description);
		}
		refreshView();
		
		ContentObserver observer = new ContentObserver(mHandler) {
			@Override
			public void onChange(boolean selfChange) {
				refreshView();
			}
		};
		
		getContentResolver().registerContentObserver(MyProgramContentProvider.CONTENT_URI, true, observer);
		
	}
	
	private void refreshView() {
		if (mMyProgram != null) {
			if (mMyProgram.isFav()) {
				buttonFav.setBackgroundResource(R.drawable.button_fav_pressed);
			} else {
				buttonFav.setBackgroundResource(R.drawable.button_fav);
			}	
		}
	}
	
	private void tapFavorite() {
		mMyProgram.setFav(!mMyProgram.isFav());
		mDaoMyProgram.update(mMyProgram);
	}

}
