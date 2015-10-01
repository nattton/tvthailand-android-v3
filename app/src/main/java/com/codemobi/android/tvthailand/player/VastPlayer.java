package com.codemobi.android.tvthailand.player;

import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.codemobi.android.tvthailand.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * An example video player that implements VideoAdPlayer.
 */
public class VastPlayer extends RelativeLayout implements VideoAdPlayer,
		OnInfoListener, OnBufferingUpdateListener {
	private TrackingVideoView video;
	private FrameLayout adUiContainer;
	private MediaController mediaController;

	private String savedContentUrl;
	private int savedContentPosition = 0;
	private int savedPosition = 0;
	private boolean contentPlaying;

	private boolean isStart;
	private ProgressBar pb;
	private TextView downloadRateView, loadRateView;

	public VastPlayer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public VastPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VastPlayer(Context context) {
		super(context);
		init();
	}
	
	
	private OnTitleBarListener onTitleBarListener;
	
	public void setOnTitleBarListener (OnTitleBarListener listener) {
		onTitleBarListener = listener;
	}
	
	public interface OnTitleBarListener {
		
		boolean onTitleBar(boolean isTitleBarVisible);
	}
	
	
	private void init() {
		View playerView = ((LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.video_progress, null, false);

		pb = (ProgressBar) playerView.findViewById(R.id.probar);
		
		
		downloadRateView = (TextView) playerView
				.findViewById(R.id.download_rate);
		loadRateView = (TextView) playerView.findViewById(R.id.load_rate);
		
		
		mediaController = new MediaController(getContext());
//		mediaController = new CustomMediaController(getContext());
		mediaController.setAnchorView(this);
		
		// Center the video in the parent layout (when video ratio doesn't match
		// the
		// layout size it will by default position to the left).
		LayoutParams videoLayouyParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		videoLayouyParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		videoLayouyParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		videoLayouyParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		videoLayouyParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		videoLayouyParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		videoLayouyParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		video = new TrackingVideoView(getContext());

		video.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				hiddenLoading();
			}
		});

		setVideoInfo();
		
		addView(video, videoLayouyParams);
		addView(playerView, videoLayouyParams);

		adUiContainer = new FrameLayout(getContext());
		addView(adUiContainer, LayoutParams.MATCH_PARENT);
		
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void setVideoInfo()
	{
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1)
		{
			video.setOnInfoListener(this);
		}
	}
	
	public void setTitle(String title) {
//		mediaController.setFileName(title);
	}

	public ViewGroup getUiContainer() {
		return adUiContainer;
	}

	public void setCompletionCallback(TrackingVideoView.CompleteCallback callback) {
		video.setCompleteCallback(callback);
	}

	/**
	 * Play whatever is already in the video view.
	 */
	public void play() {
		video.start();
	}

	public void playContent(String contentUrl) {
		visibleLoading();
		contentPlaying = true;
		savedContentUrl = contentUrl;
		video.setVideoPath(contentUrl);
		video.setMediaController(mediaController);
		video.requestFocus();
		video.postInvalidateDelayed(800);
		play();
	}

	public void pauseContent() {
		savedContentPosition = video.getCurrentPosition();
		video.stopPlayback();
		video.setMediaController(null); // Disables seeking during ad playback.
	}

	public void resumeContent() {
		contentPlaying = true;
		video.setVideoPath(savedContentUrl);
		video.seekTo(savedContentPosition);
		video.setMediaController(mediaController);
		play();
	}

	public boolean isContentPlaying() {
		return contentPlaying;
	}

	public void savePosition() {
		savedPosition = video.getCurrentPosition();
	}

	public void restorePosition() {
		video.seekTo(savedPosition);
	}

	// Methods implementing VideoAdPlayer interface.

	@Override
	public void playAd() {
		contentPlaying = false;
		video.start();
	}

	@Override
	public void stopAd() {
		video.stopPlayback();
	}

	@Override
	public void loadAd(String url) {
		video.setVideoPath(url);
	}

	@Override
	public void pauseAd() {
		video.pause();
	}

	@Override
	public void resumeAd() {
		video.start();
	}

	@Override
	public void addCallback(VideoAdPlayerCallback callback) {
		video.addCallback(callback);
	}

	@Override
	public void removeCallback(VideoAdPlayerCallback callback) {
		video.removeCallback(callback);
	}

    @Override
    public VideoProgressUpdate getAdProgress() {
        long durationMs = video.getDuration();

        if (durationMs <= 0) {
            return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
        }
        VideoProgressUpdate vpu = new VideoProgressUpdate(
                video.getCurrentPosition(), durationMs);
        // Log.i("PLAYER", vpu.toString());
        return vpu;
    }

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			if (video.isPlaying()) {
				video.pause();
				isStart = true;
				visibleLoading();
			}
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			if (isStart) {
				video.start();
				hiddenLoading();
			}
			break;
//		case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
//			downloadRateView.setText("" + extra + "kb/s" + "  ");
//			break;
			
		}
		
		return true;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		loadRateView.setText(percent + "%");
	}
	
	public void visibleLoading()
	{
		pb.setVisibility(View.VISIBLE);
		downloadRateView.setVisibility(View.VISIBLE);
		loadRateView.setVisibility(View.VISIBLE);
	}
	
	public void hiddenLoading()
	{
		pb.setVisibility(View.GONE);
		downloadRateView.setVisibility(View.GONE);
		loadRateView.setVisibility(View.GONE);
	}

}