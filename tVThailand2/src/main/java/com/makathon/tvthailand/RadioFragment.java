package com.makathon.tvthailand;

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
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.TVThailandApp.TrackerName;
import com.makathon.tvthailand.adapter.RadioCustomAdapter;
import com.makathon.tvthailand.datasource.AppUtility;
import com.makathon.tvthailand.datasource.Radio;
import com.makathon.tvthailand.datasource.Radios;
import com.makathon.tvthailand.datasource.Radios.OnRadioChangeListener;
import com.makathon.tvthailand.player.RadioPlayerActivity;

public class RadioFragment extends Fragment implements OnItemClickListener {
	
	private GridView mGridView;
	private Radios mRadios;
	private RadioCustomAdapter radioAdapter;
	
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
		
		mRadios = AppUtility.getInstance().getRadios(getActivity());
		
		radioAdapter = new RadioCustomAdapter(getActivity().getApplicationContext(), 
			   	mRadios.getArray(), 
			   	R.layout.radio_grid_header,
			   	R.layout.radio_grid_item, mImageLoader);
        /*
         * Currently set in the XML layout, but this is how you would do it in
         * your code.
         */
//         mGridView.setColumnWidth((int) calculatePixelsFromDips(100));
//         mGridView.setNumColumns(StickyGridHeadersGridView.AUTO_FIT);
		
		mGridView.setAdapter(radioAdapter);
		mRadios.setRadioChangeListener(new OnRadioChangeListener() {
			
			@Override
			public void onRadioChange(Radios radios) {
				radioAdapter.notifyDataSetChanged();
			}
		});
		
		
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Radio radio = mRadios.get(position);
				
				 Tracker t = ((TVThailandApp) getActivity().getApplication()).getTracker(
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
	
}
