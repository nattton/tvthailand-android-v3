package com.codemobi.android.tvthailand.player;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.MediaController.OnHiddenListener;
import io.vov.vitamio.widget.MediaController.OnShownListener;

import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.player.VitamioTrackingVideoView.CompleteCallback;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * An example video player that implements VideoAdPlayer.
 */
public class VitamioVastPlayer extends RelativeLayout implements VideoAdPlayer,
		OnInfoListener, OnBufferingUpdateListener {
	private VitamioTrackingVideoView video;
	private FrameLayout adUiContainer;
	private MediaController mediaController;

	private String savedContentUrl;
	private long savedContentPosition = 0l;
	private long savedPosition = 0l;
	private boolean contentPlaying;

	private boolean isStart;
	private ProgressBar pb;
	private TextView downloadRateView, loadRateView;

	public VitamioVastPlayer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public VitamioVastPlayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VitamioVastPlayer(Context context) {
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
//		mediaController = new MediaController(getContext());
		mediaController = new CustomMediaController(getContext());
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
		video = new VitamioTrackingVideoView(getContext());
		video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mediaPlayer) {
				// optional need Vitamio 4.0
				mediaPlayer.setPlaybackSpeed(1.0f);
			}
		});

		mediaController.setOnShownListener(new OnShownListener() {

			@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			@Override
			public void onShown() {
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					video.setSystemUiVisibility(View.VISIBLE);
				}
				if (onTitleBarListener != null) {
					onTitleBarListener.onTitleBar(true);
				}
			}
		});
		mediaController.setOnHiddenListener(new OnHiddenListener() {

			@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			@Override
			public void onHidden() {
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					video.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
				}
				if (onTitleBarListener != null) {
					onTitleBarListener.onTitleBar(false);
				}
			}
		});
		
		
		
		video.setOnInfoListener(this);
		video.setOnBufferingUpdateListener(this);

		View playerView = ((LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.video_progress, null, false);

		pb = (ProgressBar) playerView.findViewById(R.id.probar);
		
		
		downloadRateView = (TextView) playerView
				.findViewById(R.id.download_rate);
		loadRateView = (TextView) playerView.findViewById(R.id.load_rate);

		addView(video, videoLayouyParams);
		addView(playerView, videoLayouyParams);

		adUiContainer = new FrameLayout(getContext());
		addView(adUiContainer, LayoutParams.MATCH_PARENT);
		
	}
	
	public void setTitle(String title) {
		mediaController.setFileName(title);
	}

	public ViewGroup getUiContainer() {
		return adUiContainer;
	}

	public void setCompletionCallback(CompleteCallback callback) {
		video.setCompleteCallback(callback);
	}

	/**
	 * Play whatever is already in the video view.
	 */
	public void play() {
		video.start();
	}

	public void playContent(String contentUrl) {
		pb.setVisibility(View.VISIBLE);
		loadRateView.setVisibility(View.VISIBLE);
		loadRateView.setText(" Loading...");
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
				pb.setVisibility(View.VISIBLE);
				downloadRateView.setVisibility(View.VISIBLE);
				loadRateView.setVisibility(View.VISIBLE);
			}
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			if (isStart) {
				video.start();
				pb.setVisibility(View.GONE);
				downloadRateView.setVisibility(View.GONE);
				loadRateView.setVisibility(View.GONE);
			}
			break;
		case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
			downloadRateView.setText("" + extra + "kb/s" + "  ");
			break;
			
			
		}
		
		return true;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		loadRateView.setText(percent + "%");
	}
	

}