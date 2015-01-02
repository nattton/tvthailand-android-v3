package com.makathon.tvthailand.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.makathon.tvthailand.MainApplication;
import com.makathon.tvthailand.MainApplication.TrackerName;
import com.makathon.tvthailand.MyVolley;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.adapter.RadioCustomAdapter;
import com.makathon.tvthailand.dao.section.RadioItemDao;
import com.makathon.tvthailand.manager.SectionManager;
import com.makathon.tvthailand.manager.bus.MainBus;
import com.makathon.tvthailand.player.RadioPlayerActivity;
import com.squareup.otto.Subscribe;

public class RadioFragment extends Fragment implements OnItemClickListener {
	
	private GridView mGridView;
	private RadioCustomAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.radio_fragment_grid, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);


		mGridView = (GridView) view.findViewById(R.id.asset_grid);
		mGridView.setOnItemClickListener(this);


        mAdapter = new RadioCustomAdapter(getActivity().getApplicationContext(),
			   	SectionManager.getInstance().getData().getRadios(),
			   	R.layout.radio_grid_header,
			   	R.layout.radio_grid_item, MyVolley.getImageLoader());
		
		mGridView.setAdapter(mAdapter);
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
                RadioItemDao radio = SectionManager.getInstance().getData().getRadios().get(position);
				 Tracker t = ((MainApplication) getActivity().getApplication()).getTracker(
				            TrackerName.APP_TRACKER);
				 t.setScreenName("Radio");
				 t.send(new HitBuilders.AppViewBuilder().setCustomDimension(6, radio.getTitle()).build());
				
				Uri radioUrl = Uri.parse(radio.getUrl());
				Intent intentVideo = new Intent(getActivity(), RadioPlayerActivity.class);
				intentVideo.putExtra(Intent.EXTRA_TITLE, radio.getTitle());
				
				intentVideo.putExtra(RadioPlayerActivity.EXTRAS_MEDIA_TYPE, "radio");
				intentVideo.putExtra(RadioPlayerActivity.EXTRAS_THUMBNAIL_URL, radio.getThumbnail());
	
				intentVideo.setDataAndType(radioUrl, "video/*");
				startActivity(intentVideo);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
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
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
    }
}
