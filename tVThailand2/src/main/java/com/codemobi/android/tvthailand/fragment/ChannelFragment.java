package com.codemobi.android.tvthailand.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.codemobi.android.tvthailand.MyVolley;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.MainApplication.TrackerName;
import com.codemobi.android.tvthailand.ProgramActivity;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.adapter.ChannelAdapter;
import com.codemobi.android.tvthailand.dao.section.ChannelItemDao;
import com.codemobi.android.tvthailand.manager.SectionManager;
import com.codemobi.android.tvthailand.manager.bus.MainBus;
import com.squareup.otto.Subscribe;

public class ChannelFragment extends Fragment {
	private ChannelAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.channel_grid_view, container,
				false);
		GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
		gridview.setAdapter(mAdapter = new ChannelAdapter(MyVolley.getImageLoader()));
		
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
                ChannelItemDao channel = SectionManager.getInstance().getData().getChannels().get(position);
				
				 Tracker t = ((MainApplication) getActivity().getApplication()).getTracker(
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

    @Override
    public void onResume() {
        super.onResume();
        MainBus.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainBus.getInstance().unregister(this);
    }

    @Subscribe
    public void onSectionLoaded(SectionManager.EventType eventType) {
        if (eventType == SectionManager.EventType.Loaded)
            mAdapter.notifyDataSetChanged();
    }
}
