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
import com.makathon.tvthailand.MyVolley;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.TVThailandApp;
import com.makathon.tvthailand.TVThailandApp.TrackerName;
import com.makathon.tvthailand.datasource.AppUtility;
import com.makathon.tvthailand.otv.datasoruce.OTVEpisode;
import com.makathon.tvthailand.otv.datasoruce.OTVPart;
import com.makathon.tvthailand.otv.datasoruce.OTVPartAdapter;
import com.makathon.tvthailand.player.VastPlayerActivity;
import com.makathon.tvthailand.player.VitamioVastPlayerActivity;

public class OTVPartActivity extends SherlockActivity implements OnItemClickListener {
	private ImageLoader mImageLoader;
	private OTVEpisode episode;
	private ArrayList<OTVPart> mParts;
	private OTVPartAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.part_list_view);
		
		mImageLoader = MyVolley.getImageLoader();

		episode = AppUtility.getEpisodeSelected();
		mParts = episode.getParts();
		
		setTitle(episode.getNameTh() + "  " + episode.getDate());

		mAdapter = new OTVPartAdapter(this, mParts, R.layout.part_list_item, mImageLoader);
		
		ListView listview = (ListView) findViewById(R.id.listView);
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(this);
		
		sendTracker(episode);
	}
	
	private void sendTracker(OTVEpisode episode) {
		if (episode != null) {
			Tracker t = ((TVThailandApp) getApplication())
					.getTracker(TrackerName.APP_TRACKER);
			t.setScreenName("OTVPart");
			t.send(new HitBuilders.AppViewBuilder().build());

			Tracker t2 = ((TVThailandApp) getApplication())
					.getTracker(TrackerName.OTV_TRACKER);
			t2.setScreenName("OTVPart");
			t2.send(new HitBuilders.AppViewBuilder().setCustomDimension(2, episode.getNameTh()).build());
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position,
			long id) {
		

		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
		{
			Intent intentVastPlayer = new Intent(OTVPartActivity.this, VastPlayerActivity.class);
			intentVastPlayer.putExtra(VastPlayerActivity.EXTRAS_OTV_PART_POSITION, position);
			intentVastPlayer.putExtra(VastPlayerActivity.EXTRAS_OTV_PARTS, mParts);
			startActivity(intentVastPlayer);
		}
		else
		{
			Intent intentVastPlayer = new Intent(OTVPartActivity.this, VitamioVastPlayerActivity.class);
			intentVastPlayer.putExtra(VitamioVastPlayerActivity.EXTRAS_OTV_PART_POSITION, position);
			intentVastPlayer.putExtra(VitamioVastPlayerActivity.EXTRAS_OTV_PARTS, mParts);
			startActivity(intentVastPlayer);
		}
		
	}
}
