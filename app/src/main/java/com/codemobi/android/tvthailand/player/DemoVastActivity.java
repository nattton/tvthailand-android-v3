package com.codemobi.android.tvthailand.player;

import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent.AdErrorListener;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent.AdEventListener;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsLoader.AdsLoadedListener;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
import com.codemobi.android.tvthailand.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

public class DemoVastActivity extends Activity implements
	AdErrorListener, AdsLoadedListener, AdEventListener, TrackingVideoView.CompleteCallback {

//	private String tagUrl = "http://bs.serving-sys.com/BurstingPipe/adServer.bs?cn=is&c=23&pl=VAST&pli=8334141&PluID=0&pos=3802&ord=13946878711755&cim=1";
	private String tagUrl = "http://61.19.18.239/vod/_definst_/OTV/Mobile/elec_ver4_23sec.mp4/playlist.m3u8";
	private static final String CONTENT_URL = 
			 "http://61.19.18.239/vod/_definst_/OTV/Mobile/Variety/09_broadcast/01_sudfatalok/ep1/01_sudfatalok_part1.mp4/playlist.m3u8";
			//"http://rmcdn.2mdn.net/MotifFiles/html/1248596/" + "android_1330378998288.mp4";
	
	protected AdDisplayContainer container;
	private ImaSdkFactory sdkFactory;
	private FrameLayout videoHolder;
	private VastPlayer videoPlayer;
	private AdsLoader adsLoader;
	private ImaSdkSettings sdkSettings;
	private boolean contentStarted = false;
	private AdsManager adsManager;
	protected boolean isAdStarted;
	protected boolean isAdPlaying;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vast_player);
		initUi();
		
		sdkFactory = ImaSdkFactory.getInstance();
		creatAdsLoader();
		
		adsLoader.requestAds(buildAdsRequest());
	}

	private void initUi() {
		videoHolder = (FrameLayout) findViewById(R.id.videoHolder);
		
		if (videoPlayer == null) {
			videoPlayer = new VastPlayer(this);
			videoPlayer.setCompletionCallback(this);
		}
		videoHolder.addView(videoPlayer);
		
		
	}

	private void creatAdsLoader() {
		adsLoader = sdkFactory.createAdsLoader(this, getImgSdkSettings());
		adsLoader.addAdErrorListener(this);
		adsLoader.addAdsLoadedListener(this);
	}

	private ImaSdkSettings getImgSdkSettings() {
		if (sdkSettings == null) {
			sdkSettings = sdkFactory.createImaSdkSettings();
		}
		return sdkSettings;
	}
	
	protected void playVideo() {
		Log.e("VAST Activity", "Playing video");
		videoPlayer.playContent(CONTENT_URL);
		contentStarted = true;
	}
	
	private AdsRequest buildAdsRequest() {
		container = sdkFactory.createAdDisplayContainer();
		container.setPlayer(videoPlayer);
		container.setAdContainer(videoPlayer.getUiContainer());
		
		AdsRequest request = sdkFactory.createAdsRequest();
		request.setAdTagUrl(tagUrl);

		request.setAdDisplayContainer(container);
		return request;
	}

	@Override
	public void onComplete() {
		if (videoPlayer.isContentPlaying()) {
			adsLoader.contentComplete();
		}
	}

	@Override
	public void onAdEvent(AdEvent event) {
		Log.e("VAST Activity", "Event:" + event.getType());

	    switch (event.getType()) {
	      case LOADED:
	    	  Log.e("VAST Activity", "Calling start.");
	        adsManager.start();
	        break;
	      case CONTENT_PAUSE_REQUESTED:
	        if (contentStarted) {
	          videoPlayer.pauseContent();
	        }
	        break;
	      case CONTENT_RESUME_REQUESTED:
	        if (contentStarted) {
	          videoPlayer.resumeContent();
	        } else {
	          playVideo();
	        }
	        break;
	      case STARTED:
	        isAdStarted = true;
	        isAdPlaying = true;
	        break;
	      case COMPLETED:
	        isAdStarted = false;
	        isAdPlaying = false;
	        break;
	      case ALL_ADS_COMPLETED:
	        isAdStarted = false;
	        isAdPlaying = false;
	        adsManager.destroy();
	        break;
	      case PAUSED:
	        isAdPlaying = false;
	        break;
	      case RESUMED:
	        isAdPlaying = true;
	        break;
	      default:
	        break;
	    }
	}

	@Override
	public void onAdsManagerLoaded(AdsManagerLoadedEvent event) {
		Log.e("VAST Activity", "Ads loaded!");
		adsManager = event.getAdsManager();
	    adsManager.addAdErrorListener(this);
	    adsManager.addAdEventListener(this);
	    Log.e("VAST Activity", "Calling init.");
	    adsManager.init();
	}

	@Override
	public void onAdError(AdErrorEvent event) {
		Log.e("VAST Activity", event.getError().getMessage());
	}
}
