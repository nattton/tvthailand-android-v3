package com.codemobi.android.tvthailand.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.codemobi.android.tvthailand.manager.bus.MainBus;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.adapter.RadioCustomAdapter;
import com.codemobi.android.tvthailand.dao.section.RadioItemDao;
import com.codemobi.android.tvthailand.manager.SectionManager;
import com.codemobi.android.tvthailand.player.RadioPlayerActivity;
import com.squareup.otto.Subscribe;
import com.vmax.android.ads.api.VmaxAdView;
import com.vmax.android.ads.common.VmaxAdListener;

public class RadioFragment extends Fragment implements OnItemClickListener {

	private GridView mGridView;
	private RadioCustomAdapter mAdapter;

	RadioItemDao radio;

	private VmaxAdView vmaxAdView;
	private VmaxAdListener mAdListener;

	public static RadioFragment newInstance() {
		RadioFragment fragment = new RadioFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

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
				R.layout.radio_grid_item);

		mGridView.setAdapter(mAdapter);

		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
									long id) {
				radio = SectionManager.getInstance().getData().getRadios().get(position);
				Tracker t = ((MainApplication) getActivity().getApplication()).getDefaultTracker();
				t.setScreenName("Radio");
				t.send(new HitBuilders.AppViewBuilder().setCustomDimension(6, radio.getTitle()).build());
				playRadio();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {

	}

	private void playRadio() {
		if (radio != null) {
			Uri radioUrl = Uri.parse(radio.getUrl());
			Intent intentVideo = new Intent(getActivity(), RadioPlayerActivity.class);
			intentVideo.putExtra(Intent.EXTRA_TITLE, radio.getTitle());

			intentVideo.putExtra(RadioPlayerActivity.EXTRAS_MEDIA_TYPE, "radio");
			intentVideo.putExtra(RadioPlayerActivity.EXTRAS_THUMBNAIL_URL, radio.getThumbnail());

			intentVideo.setDataAndType(radioUrl, "video/*");
			startActivityForResult(intentVideo, RadioPlayerActivity.RADIO_PLAYER_CODE);
		}
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RadioPlayerActivity.RADIO_PLAYER_CODE)
			startAds();
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void startAds() {
		adListenerInitialization();
		vmaxAdView = new VmaxAdView(getContext(), getResources().getString(R.string.vmax_interstitial_ad_unit_id), VmaxAdView.UX_INTERSTITIAL);
		vmaxAdView.setAdListener(mAdListener);
		vmaxAdView.setUxType(VmaxAdView.UX_INTERSTITIAL);
		vmaxAdView.cacheAd();
	}

	private void adListenerInitialization() {
		mAdListener = new VmaxAdListener() {

			@Override
			public void didInteractWithAd(VmaxAdView adView) {
				Log.d("Vmax", "adViewDidLoadAd");
			}

			@Override
			public void adViewDidLoadAd(VmaxAdView adView) {
				Log.d("Vmax", "adViewDidLoadAd");
			}

			@Override
			public void willPresentOverlay(VmaxAdView adView) {
				Log.d("Vmax", "willPresentOverlay");
			}

			@Override
			public void willDismissOverlay(VmaxAdView adView) {
				Log.d("Vmax", "willDismissOverlay");
			}

			@Override
			public void adViewDidCacheAd(VmaxAdView adView) {
				Log.d("Vmax", "adViewDidCacheAd");
				if (adView != null) {
					adView.showAd();
				}
			}

			@Override
			public VmaxAdView didFailedToLoadAd(String arg0) {
				Log.d("VmaxAdView", "didFailedToLoadAd");
				return null;
			}

			@Override
			public VmaxAdView didFailedToCacheAd(String Error) {
				Log.d("VmaxAdView", "didFailedToCacheAd");
				return null;
			}

			@Override
			public void willLeaveApp(VmaxAdView adView) {
				Log.d("Vmax", "willLeaveApp");
			}

			@Override
			public void onVideoCompleted() {

			}
		};
	}
}
