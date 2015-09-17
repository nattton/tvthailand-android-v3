package com.codemobi.android.tvthailand.otv.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codemobi.android.tvthailand.otv.adapter.OTVPartAdapter;
import com.codemobi.android.tvthailand.player.VitamioVastPlayerActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.MainApplication.TrackerName;
import com.codemobi.android.tvthailand.otv.model.OTVEpisode;
import com.codemobi.android.tvthailand.player.VastPlayerActivity;

public class OTVPartActivity extends AppCompatActivity implements OnItemClickListener {
	public static String EXTRAS_OTV_EPISODE = "EXTRAS_OTV_EPISODE";

	Toolbar toolbar;
	private OTVEpisode episode;
	private OTVPartAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_part);

		initExtras();
		initToolbar();
		initInstances();
	}

	private void initExtras() {
		Intent i = getIntent();
		episode = i.getParcelableExtra(EXTRAS_OTV_EPISODE);
	}

	private void initToolbar() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(episode.getNameTh() + "  " + episode.getDate());
		setSupportActionBar(toolbar);
	}

	private void initInstances() {
		mAdapter = new OTVPartAdapter(episode.getParts());

		ListView listview = (ListView) findViewById(R.id.listView);
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(this);

		sendTracker(episode);
	}
	
	private void sendTracker(OTVEpisode episode) {
		if (episode != null) {
			Tracker t = ((MainApplication) getApplication())
					.getTracker(TrackerName.APP_TRACKER);
			t.setScreenName("OTVPart");
			t.send(new HitBuilders.AppViewBuilder().build());

			Tracker t2 = ((MainApplication) getApplication())
					.getTracker(TrackerName.OTV_TRACKER);
			t2.setScreenName("OTVPart");
			t2.send(new HitBuilders.AppViewBuilder().setCustomDimension(2, episode.getNameTh()).build());
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position,
			long id) {

        Intent intentVastPlayer;

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			intentVastPlayer = new Intent(OTVPartActivity.this, VastPlayerActivity.class);

		} else {
			intentVastPlayer = new Intent(OTVPartActivity.this, VitamioVastPlayerActivity.class);
		}

        Log.d("Play Video", episode.getParts().get(position).getStreamUrl());
        intentVastPlayer.putExtra(VastPlayerActivity.EXTRAS_OTV_EPISODE, episode);
        intentVastPlayer.putExtra(VastPlayerActivity.EXTRAS_OTV_PART_POSITION, position);
        startActivity(intentVastPlayer);
	}
}
