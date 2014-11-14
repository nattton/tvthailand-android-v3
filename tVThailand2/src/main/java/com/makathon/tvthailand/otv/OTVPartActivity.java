package com.makathon.tvthailand.otv;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.makathon.tvthailand.Application;
import com.makathon.tvthailand.MyVolley;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.Application.TrackerName;
import com.makathon.tvthailand.datasource.AppUtility;
import com.makathon.tvthailand.otv.datasoruce.OTVEpisode;
import com.makathon.tvthailand.otv.datasoruce.OTVPart;
import com.makathon.tvthailand.otv.datasoruce.OTVPartAdapter;
import com.makathon.tvthailand.player.VastPlayerActivity;
import com.makathon.tvthailand.player.VitamioVastPlayerActivity;

public class OTVPartActivity extends SherlockActivity implements OnItemClickListener {
	public static String EXTRAS_OTV_EPISODE = "EXTRAS_OTV_EPISODE";

    private ImageLoader mImageLoader;
	private OTVEpisode episode;
	private OTVPartAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.part_list_view);
		
		mImageLoader = MyVolley.getImageLoader();

        Intent i = getIntent();
        episode = i.getParcelableExtra(EXTRAS_OTV_EPISODE);
        setTitle(episode.getNameTh() + "  " + episode.getDate());

        mAdapter = new OTVPartAdapter(this, episode.getParts(), R.layout.part_list_item, mImageLoader);

        ListView listview = (ListView) findViewById(R.id.listView);
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(this);

        sendTracker(episode);
	}
	
	private void sendTracker(OTVEpisode episode) {
		if (episode != null) {
			Tracker t = ((Application) getApplication())
					.getTracker(TrackerName.APP_TRACKER);
			t.setScreenName("OTVPart");
			t.send(new HitBuilders.AppViewBuilder().build());

			Tracker t2 = ((Application) getApplication())
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

        intentVastPlayer.putExtra(VastPlayerActivity.EXTRAS_OTV_EPISODE, episode);
        intentVastPlayer.putExtra(VastPlayerActivity.EXTRAS_OTV_PART_POSITION, position);
        startActivity(intentVastPlayer);
	}
}
