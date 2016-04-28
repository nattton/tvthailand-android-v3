package com.codemobi.android.tvthailand.otv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.codemobi.android.tvthailand.adapter.OnTapListener;
import com.codemobi.android.tvthailand.otv.adapter.OTVPartAdapter;
import com.codemobi.android.tvthailand.player.VitamioVastPlayerActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.otv.model.OTVEpisode;
import com.codemobi.android.tvthailand.player.VastPlayerActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OTVPartActivity extends AppCompatActivity implements OnTapListener {
	public static String EXTRAS_OTV_EPISODE = "EXTRAS_OTV_EPISODE";

	@BindView(R.id.toolbar) Toolbar toolbar;
	private OTVEpisode episode;
	private OTVPartAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_part);

		ButterKnife.bind(this);

		initExtras();
		initToolbar();
		initInstances();
	}

	private void initExtras() {
		Intent i = getIntent();
		episode = i.getParcelableExtra(EXTRAS_OTV_EPISODE);
	}

	private void initToolbar() {
		toolbar.setTitle(episode.getNameTh() + "  " + episode.getDate());
		setSupportActionBar(toolbar);
	}

	private void initInstances() {
		mAdapter = new OTVPartAdapter(this, episode.getParts());
		RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_part);
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mRecyclerView.setAdapter(mAdapter);
		mAdapter.setOnTapListener(this);

		sendTracker(episode);
	}
	
	private void sendTracker(OTVEpisode episode) {
		if (episode != null) {
			Tracker t = ((MainApplication) getApplication()).getDefaultTracker();
			t.setScreenName("OTVPart");
			t.send(new HitBuilders.ScreenViewBuilder().build());

			Tracker t2 = ((MainApplication) getApplication()).getOTVTracker();
			t2.setScreenName("OTVPart");
			t2.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(2, episode.getNameTh()).build());
		}
	}
	
	@Override
	public void onTapView (int position) {

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
