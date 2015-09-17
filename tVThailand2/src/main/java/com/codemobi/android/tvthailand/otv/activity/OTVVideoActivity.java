package com.codemobi.android.tvthailand.otv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.MainApplication.TrackerName;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.otv.adapter.OTVPartAdapter;
import com.codemobi.android.tvthailand.otv.model.OTVEpisode;
import com.codemobi.android.tvthailand.player.VastPlayerActivity;
import com.codemobi.android.tvthailand.player.VitamioVastPlayerActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class OTVVideoActivity extends AppCompatActivity implements OnItemClickListener {
	public static String EXTRAS_OTV_EPISODE = "EXTRAS_OTV_EPISODE";

	private OTVEpisode episode;
	private OTVPartAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_part);

        Intent i = getIntent();
        episode = i.getParcelableExtra(EXTRAS_OTV_EPISODE);
        setTitle(episode.getNameTh() + "  " + episode.getDate());

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
			intentVastPlayer = new Intent(OTVVideoActivity.this, VastPlayerActivity.class);

		} else {
			intentVastPlayer = new Intent(OTVVideoActivity.this, VitamioVastPlayerActivity.class);
		}

        Log.d("Play Video", episode.getParts().get(position).getStreamUrl());
        intentVastPlayer.putExtra(VastPlayerActivity.EXTRAS_OTV_EPISODE, episode);
        intentVastPlayer.putExtra(VastPlayerActivity.EXTRAS_OTV_PART_POSITION, position);
        startActivity(intentVastPlayer);
	}
}
