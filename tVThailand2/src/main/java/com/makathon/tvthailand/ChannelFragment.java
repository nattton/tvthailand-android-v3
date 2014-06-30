package com.makathon.tvthailand;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.TVThailandApp.TrackerName;
import com.makathon.tvthailand.adapter.ChannelAdapter;
import com.makathon.tvthailand.datasource.AppUtility;
import com.makathon.tvthailand.datasource.Channel;
import com.makathon.tvthailand.datasource.Channels;
import com.makathon.tvthailand.datasource.Channels.OnChannelChangeListener;

public class ChannelFragment extends Fragment {
	private Channels mChannels;
	private ChannelAdapter mAdapter;
	private static ImageLoader mImageLoader;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.channel_grid_view, container,
				false);
		GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
		
		mImageLoader = MyVolley.getImageLoader();

		mChannels = AppUtility.getInstance().getChannels(getActivity());
		mAdapter = new ChannelAdapter(getActivity(), mChannels,
				R.layout.channel_grid_item, mImageLoader);

		gridview.setAdapter(mAdapter);

		mChannels.setChannelChangeListener(new OnChannelChangeListener() {

			@Override
			public void onChannelChange(Channels channels) {
				mAdapter.notifyDataSetChanged();
			}
		});
		
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Channel channel = mChannels.get(position);
				
				 Tracker t = ((TVThailandApp) getActivity().getApplication()).getTracker(
				            TrackerName.APP_TRACKER);
				 t.setScreenName("Channel");
				 t.send(new HitBuilders.AppViewBuilder().setCustomDimension(5, channel.getTitle()).build());
				
				Intent intent = new Intent(getActivity(), ProgramActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(ProgramActivity.EXTRAS_MODE, ProgramActivity.BY_CHANNEL);
				bundle.putString(ProgramActivity.EXTRAS_TITLE, channel.getTitle());
				bundle.putString(ProgramActivity.EXTRAS_ID, channel.getId());
				bundle.putString(ProgramActivity.EXTRAS_ICON, channel.getThumbnail());
				bundle.putString(ProgramActivity.EXTRAS_URL, channel.getUrl());
				intent.putExtras(bundle);
				getActivity().startActivity(intent);
			}
		});

		return rootView;

	}
}
