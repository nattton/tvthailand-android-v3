package com.makathon.tvthailand;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.contentprovider.MyProgramContentProvider;
import com.makathon.tvthailand.database.Dao;
import com.makathon.tvthailand.database.MyProgramModel;

public class RatingActivity extends Activity {

	private static final String TAG = "RatingActivity";
	static final String EXTRAS_ID = "EXTRAS_ID";
	private Dao<MyProgramModel> mDaoMyProgram;
	private MyProgramModel mMyProgram;

	private RatingBar ratingbar;
	private TextView rate_number_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.rating_view);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int id = extras.getInt(EXTRAS_ID);
			mDaoMyProgram = new Dao<>(MyProgramModel.class, this,
					MyProgramContentProvider.CONTENT_URI);
			mMyProgram = mDaoMyProgram.getById(id);

		} else {
			finish();
		}

        TextView title_tv = (TextView) findViewById(R.id.tv_title_ep);
		title_tv.setText(mMyProgram.getTitle());

        NetworkImageView thumbnail_imv = (NetworkImageView) findViewById(R.id.iv_tri_point_ep);
		thumbnail_imv.setImageUrl(mMyProgram.getThumbnail(), MyVolley.getImageLoader());

		ratingbar = (RatingBar) findViewById(R.id.ratingBar);
		ratingbar.setRating((float) mMyProgram.getMyVote());

		rate_number_tv = (TextView) findViewById(R.id.my_vote);
		rate_number_tv.setText(String.valueOf(mMyProgram.getMyVote()));

        LinearLayout save_btn = (LinearLayout) findViewById(R.id.vote_llayout);

		save_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMyProgram.setMyVote((int) ratingbar.getRating());
				mDaoMyProgram.update(mMyProgram);
				finish();
			}
		});

		addListenerOnRatingBar();

	}

	public void addListenerOnRatingBar() {

		// if rating value is changed,
		// display the current rating value in the result (textview)
		// automatically
		ratingbar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				rate_number_tv.setText(String.valueOf((int) rating));
			}
		});

	}

}
