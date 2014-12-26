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

import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.makathon.tvthailand.MainApplication;
import com.makathon.tvthailand.MainApplication.TrackerName;
import com.makathon.tvthailand.MyVolley;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.adapter.RadioCustomAdapter;
import com.makathon.tvthailand.dao.section.RadioItemDao;
import com.makathon.tvthailand.dao.section.SectionCollectionDao;
import com.makathon.tvthailand.manager.SectionManager;
import com.makathon.tvthailand.manager.bus.BusProvider;
import com.makathon.tvthailand.player.RadioPlayerActivity;
import com.squareup.otto.Subscribe;

public class RadioFragment extends Fragment implements OnItemClickListener {
	
	private GridView mGridView;
	private RadioCustomAdapter mAdapter;
	private ImageLoader mImageLoader;
	
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
		
		mImageLoader = MyVolley.getImageLoader();

        mAdapter = new RadioCustomAdapter(getActivity().getApplicationContext(),
			   	SectionManager.getInstance().getData().getRadios(),
			   	R.layout.radio_grid_header,
			   	R.layout.radio_grid_item, mImageLoader);
		
		mGridView.setAdapter(mAdapter);
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
                RadioItemDao radio = SectionManager.getInstance().getData().getRadios()[position];
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
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void onSectionLoaded(SectionCollectionDao data) {
        mAdapter.notifyDataSetChanged();
    }
}
