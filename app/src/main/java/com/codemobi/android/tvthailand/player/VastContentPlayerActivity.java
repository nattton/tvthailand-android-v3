package com.codemobi.android.tvthailand.player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent.AdErrorListener;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent.AdEventListener;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsLoader.AdsLoadedListener;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.utils.CountDownTimer;


public class VastContentPlayerActivity extends Activity implements AdErrorListener, AdsLoadedListener, AdEventListener, TrackingVideoView.CompleteCallback, OnClickListener {

    public static final String EXTRAS_TAG_URL = "EXTRAS_TAG_URL";
    public static final String EXTRAS_SKIP_TIME = "EXTRAS_SKIP_TIME";
    public static final String EXTRAS_CONTENT_URL = "EXTRAS_CONTENT_URL";
    public static final int LIVE_PLAYER_CODE = 2;

    private String tagUrl;
    private int skipTime;
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

    /** Title Bar **/
    private RelativeLayout titleBarRL;
    private ImageButton openWithImb;

    private WakeLock wakeLock;
    private CountDownTimer skipAdCounter;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.vast_player);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "WakeLockPlayer");
        wakeLock.acquire();


        Intent i = getIntent();

        contentUrl = i.getStringExtra(EXTRAS_CONTENT_URL);
        tagUrl = i.getStringExtra(EXTRAS_TAG_URL);
        skipTime = i.getIntExtra(EXTRAS_SKIP_TIME, 7000);

        initUi();

        sdkFactory = ImaSdkFactory.getInstance();
        createAdsLoader();
        if (tagUrl != null)
            adsLoader.requestAds(buildAdsRequest());
        else
            playVideo();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            videoPlayer
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

        sendTracker();
    }

    private void sendTracker()
    {
        Tracker t = ((MainApplication) getApplication()).getDefaultTracker();
        t.setScreenName("VasContentPlayer");
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
    }

    private void initUi() {
        videoHolder = (FrameLayout) findViewById(R.id.videoHolder);
        buttonSkip = (Button) findViewById(R.id.buttonSkip);
        txtSkipCount = (TextView) findViewById(R.id.txtSkipCounter);

        /** Title Bar **/
        titleBarRL = (RelativeLayout) findViewById(R.id.title_bar_rl);
        titleBarRL.setVisibility(View.GONE);
        openWithImb = (ImageButton) findViewById(R.id.open_with_imb);
        openWithImb.setVisibility(View.GONE);
        openWithImb.setOnClickListener(this);

        if (videoPlayer == null) {
            videoPlayer = new VastPlayer(this);
            videoPlayer.setCompletionCallback(this);
        }

        videoHolder.addView(videoPlayer);

        LayoutParams videoLayoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        videoLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        videoLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        videoLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        videoLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        videoLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        videoLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        buttonSkip.setOnClickListener(this);
        txtSkipCount.setOnClickListener(this);
    }

    private void createAdsLoader() {
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
        videoPlayer.playContent(contentUrl);
    }

    private AdsRequest buildAdsRequest() {
        videoPlayer.visibleLoading();
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
                skipAdCounter = new CountDownTimer(skipTime, 1000);
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
                }, skipTime);

                break;
            case COMPLETED:
                isAdStarted = false;
                isAdPlaying = false;
                break;
            case ALL_ADS_COMPLETED:
                Tracker t2 = ((MainApplication) getApplication()).getOTVTracker();
                t2.setScreenName("VastPlayer");
                t2.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(5, tagUrl).build());

            case SKIPPED:
                isAdStarted = false;
                isAdPlaying = false;
                adsManager.destroy();
                if (!contentStarted) {
                    buttonSkip.setVisibility(View.VISIBLE);
                }
                playVideo();
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
//		Log.e("VAST Activity", event.getError().getMessage());

        contentStarted = false;

        playVideo();

        sendTracker();

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

            default:
                break;
        }

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
