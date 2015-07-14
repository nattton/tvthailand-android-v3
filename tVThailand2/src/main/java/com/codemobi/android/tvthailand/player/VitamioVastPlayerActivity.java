package com.codemobi.android.tvthailand.player;

import io.vov.vitamio.LibsChecker;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.MainApplication.TrackerName;
import com.codemobi.android.tvthailand.otv.model.OTVEpisode;
import com.codemobi.android.tvthailand.otv.model.OTVPart;
import com.codemobi.android.tvthailand.player.TrackingVideoView.CompleteCallback;
import com.codemobi.android.tvthailand.player.VastPlayer.OnTitleBarListener;
import com.codemobi.android.tvthailand.utils.CountDownTimer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class VitamioVastPlayerActivity extends Activity implements AdErrorListener,
		AdsLoadedListener, AdEventListener, CompleteCallback, OnClickListener,
		OnLongClickListener, OnTitleBarListener {

    public static String EXTRAS_OTV_EPISODE = "EXTRAS_OTV_EPISODE";
	public static final String EXTRAS_OTV_PART_POSITION = "EXTRAS_OTV_PART_POSITION";

	private String mediaCode;
	private String tagUrl;
	private String contentUrl;

	protected AdDisplayContainer container;
	private ImaSdkFactory sdkFactory;
	private FrameLayout videoHolder;
	private Button buttonSkip;
	private TextView txtSkipCount;
	private VastPlayer videoPlayer;
	private AdsLoader adsLoader;
	private ImaSdkSettings sdkSettings;
	private boolean contentStarted = false;
	private AdsManager adsManager;
	protected boolean isAdStarted;
	protected boolean isAdPlaying;

	private WebViewPlayer webViewPlayer;
	private OTVPart part;

	/** Title Bar **/
	private RelativeLayout titleBarRL;
	private TextView titleBarTV;
	private ImageButton openWithImb;
	private String titleString;

	private ArrayList<OTVPart> mParts;
	private OTVEpisode mEpisode;
	private int position;

	private CountDownTimer skipAdCounter;
	

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!LibsChecker.checkVitamioLibs(this)) {
			return;
		}

		setContentView(R.layout.vast_player);

		Intent i = getIntent();
        mEpisode = i.getParcelableExtra(EXTRAS_OTV_EPISODE);
        mParts = mEpisode.getParts();
		position = i.getIntExtra(EXTRAS_OTV_PART_POSITION, 0);
		updateValue(position);
		initUi();

		sdkFactory = ImaSdkFactory.getInstance();
		creatAdsLoader();

		adsLoader.requestAds(buildAdsRequest());

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			videoPlayer
					.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		}

		videoPlayer.setOnLongClickListener(this);
		
		sendTracker();
	}
	
	private void sendTracker()
	{
		Tracker t = ((MainApplication) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		t.setScreenName("VastPlayer");
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void updateValue(int position) {
		part = mParts.get(position);
		titleString = mEpisode.getNameTh() + " " + mEpisode.getDate();
		mediaCode = part.getMediaCode();
		tagUrl = part.getVastUrl();
		// Log.e("tagUrl", tagUrl);
		contentUrl = part.getStreamUrl();
	}

	private void initUi() {
		videoHolder = (FrameLayout) findViewById(R.id.videoHolder);
		buttonSkip = (Button) findViewById(R.id.buttonSkip);
		txtSkipCount = (TextView) findViewById(R.id.txtSkipCounter);
		
		/** Title Bar **/
		titleBarRL = (RelativeLayout) findViewById(R.id.title_bar_rl);
		titleBarTV = (TextView) findViewById(R.id.title_bar_tv);
		openWithImb = (ImageButton) findViewById(R.id.open_with_imb);
		openWithImb.setVisibility(View.GONE);
		titleBarTV.setText(titleString + " - " + part.getNameTh());
		openWithImb.setOnClickListener(this);

		if (videoPlayer == null) {
			videoPlayer = new VastPlayer(this);
			videoPlayer.setCompletionCallback(this);
			videoPlayer.setOnTitleBarListener(this);
		}

		if (webViewPlayer == null) {
			webViewPlayer = new WebViewPlayer(getApplicationContext());
			webViewPlayer.setVisibility(View.GONE);
		}

		videoHolder.addView(videoPlayer);

		LayoutParams videoLayouyParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		videoLayouyParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		videoLayouyParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		videoLayouyParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		videoLayouyParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		videoLayouyParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		videoLayouyParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		videoHolder.addView(webViewPlayer, videoLayouyParams);

		buttonSkip.setOnClickListener(this);
		txtSkipCount.setOnClickListener(this);
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
        titleBarRL.setVisibility(View.GONE);
        buttonSkip.setVisibility(View.GONE);
        txtSkipCount.setVisibility(View.GONE);

		Tracker t2 = ((MainApplication) getApplication())
				.getTracker(TrackerName.OTV_TRACKER);
		t2.send(new HitBuilders.AppViewBuilder().setCustomDimension(3, part.getNameTh()).build());
		
		if (mediaCode.equals("1000")) {
			Log.e("VAST Activity", "Playing video");
			buttonSkip.setVisibility(View.GONE);
			videoPlayer.playContent(contentUrl);
			videoPlayer.setTitle(part.getNameTh());
			contentStarted = true;
			
			openWithImb.setVisibility(View.VISIBLE);
			
			t2.send(new HitBuilders.AppViewBuilder().setCustomDimension(6, part.getStreamUrl()).build());
		} else if (mediaCode.equals("1002")) {
			Log.e("VAST Activity", "Playing Iframe");
			String iframeData = Html.fromHtml(part.getStreamUrl()).toString();

			webViewPlayer.loadDataWithIFrame(iframeData);
			videoPlayer.setVisibility(View.GONE);
			webViewPlayer.setVisibility(View.VISIBLE);
			buttonSkip.setVisibility(View.GONE);
			
			contentStarted = true;
			Document doc = Jsoup.parse(iframeData);
			Elements iframes = doc.getElementsByTag("iframe");
			for (Element iframe : iframes) {
				String src = iframe.attr("src");
				if (src != null) {
					Log.e("VatPlayer", src);
					t2.send(new HitBuilders.AppViewBuilder().setCustomDimension(6, iframe.attr("src")).build());	
				}
			}
			
		} else {
			Toast.makeText(VitamioVastPlayerActivity.this,
					"Video is not support." + mediaCode, Toast.LENGTH_LONG)
					.show();
		}
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
			if (adsLoader != null) {
				adsLoader.contentComplete();
			}

			position++;
			if (position < mParts.size()) {
				contentStarted = false;
				updateValue(position);
				titleBarTV.setText(titleString + " - " + part.getNameTh());
				adsLoader.requestAds(buildAdsRequest());

				sendTracker();
			}
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
			
	        //** Start countdown counter to skip ad **//
	        skipAdCounter = new CountDownTimer(7000, 1000);
	        skipAdCounter.Start();
	        RefreshTimer();
	        txtSkipCount.setVisibility(View.VISIBLE);
	        
	        final Handler handler = new Handler();
	        handler.postDelayed(new Runnable() {
	            @Override
	            public void run() {
	            	if (!contentStarted) {
	            		txtSkipCount.setVisibility(View.GONE);
	            		buttonSkip.setVisibility(View.VISIBLE);
					}
	            }
	        }, 7000);
			
			titleBarRL.setVisibility(View.GONE);

			Tracker t = ((MainApplication) getApplication())
					.getTracker(TrackerName.OTV_TRACKER);
			t.setScreenName("VastPlayer");
			t.send(new HitBuilders.AppViewBuilder().setCustomDimension(4, tagUrl).build());

			break;
		case COMPLETED:
			isAdStarted = false;
			isAdPlaying = false;
			break;
		case ALL_ADS_COMPLETED:
			Tracker t2 = ((MainApplication) getApplication())
					.getTracker(TrackerName.OTV_TRACKER);
			t2.setScreenName("VastPlayer");
			t2.send(new HitBuilders.AppViewBuilder().setCustomDimension(5, tagUrl).build());
			
		case SKIPPED:
			isAdStarted = false;
			isAdPlaying = false;
			adsManager.destroy();
			if (!contentStarted) {
				buttonSkip.setVisibility(View.VISIBLE);
			}

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
        playVideo();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonSkip:
			Log.e("VAST Activity", "Ads Skip!");
			adsManager.skip();
			isAdStarted = false;
			isAdPlaying = false;
			adsManager.destroy();
			break;
		case R.id.open_with_imb:
			playWithIntent();
			break;

		default:
			break;
		}

	}

	@Override
	public void onPause() {
		super.onPause();

		try {
			Class.forName("android.webkit.WebView")
					.getMethod("onPause", (Class[]) null)
					.invoke(webViewPlayer, (Object[]) null);

		} catch (ClassNotFoundException cnfe) {
		} catch (NoSuchMethodException nsme) {
		} catch (InvocationTargetException ite) {
		} catch (IllegalAccessException iae) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mediaCode.equals("1000")) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.player_menu, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_play:
			playWithIntent();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void playWithIntent() {
		if (mediaCode.equals("1000")) {
			Intent intentPlayer = new Intent(Intent.ACTION_VIEW, Uri.parse(part
					.getStreamUrl()));
			intentPlayer.putExtra(Intent.EXTRA_TITLE, part.getNameTh());
			intentPlayer.setDataAndType(Uri.parse(part.getStreamUrl()),
					"video/*");
			startActivity(intentPlayer);
		}
	}

	@Override
	public boolean onLongClick(View v) {
		playWithIntent();
		return false;
	}

	@Override
	public boolean onTitleBar(boolean isTitleBarVisible) {

		if (isTitleBarVisible) {
			titleBarRL.setVisibility(View.VISIBLE);
		}

		if (!isTitleBarVisible) {
			titleBarRL.setVisibility(View.GONE);
		}

		return true;
	}
	
	//** RefreshTimmer of skip Ad counter **//
	public void RefreshTimer() {
		final Handler handler = new Handler();
		final Runnable counter = new Runnable() {

			public void run() {
				txtSkipCount.setText("Skip in "+Long.toString(skipAdCounter.getCurrentTime()/1000) + " second");
				handler.postDelayed(this, 1);
			}
		};

		handler.postDelayed(counter, 1);
	}

}
