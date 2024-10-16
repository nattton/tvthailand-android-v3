package com.codemobi.android.tvthailand.player;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.MediaController.OnHiddenListener;
import io.vov.vitamio.widget.MediaController.OnShownListener;
import io.vov.vitamio.widget.VideoView;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codemobi.android.tvthailand.R;

@SuppressLint("NewApi")
public class VideoPlayerActivity extends Activity implements OnInfoListener,
		OnBufferingUpdateListener {
	
	private Uri uri;
	private String title;
	private VideoView mVideoView;
	
	private boolean isStart = true;
	
	private ProgressBar pb;
	private TextView downloadRateView, loadRateView;
	private int start_buffer_rate;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

        start_buffer_rate = 80;

		if (!LibsChecker.checkVitamioLibs(this)) {
			return;
		}

		setContentView(R.layout.videoview);
		mVideoView = (VideoView) findViewById(R.id.buffer);
		pb = (ProgressBar) findViewById(R.id.probar);
		downloadRateView = (TextView) findViewById(R.id.download_rate);
		loadRateView = (TextView) findViewById(R.id.load_rate);
		
		uri = getIntent().getData();

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
		if (title != null) {
			setTitle(title);
		}
		

		if (uri == null) {
			// Tell the user to provide a media file URL/path.
			Toast.makeText(VideoPlayerActivity.this, "path is empty",
					Toast.LENGTH_LONG).show();
			return;
		} else {
			/*
			 * Alternatively,for streaming media you can use
			 * mVideoView.setVideoURI(Uri.parse(URLstring));
			 */
			mVideoView.setVideoURI(uri);
			final MediaController mediaController = new MediaController(this);
			mVideoView.setMediaController(mediaController);
			mVideoView.requestFocus();
			mVideoView.setOnInfoListener(this);
			mVideoView.setOnBufferingUpdateListener(this);
			mVideoView
					.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
						@SuppressLint("NewApi")
						@Override
						public void onPrepared(MediaPlayer mediaPlayer) {
							// optional need Vitamio 4.0
							mediaPlayer.setPlaybackSpeed(1.0f);
							mediaController.setFileName(title);
							if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
								mVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
							}
						}
					});
			
			mediaController.setOnShownListener(new OnShownListener() {
				
				@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				@Override
				public void onShown() {
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
						mVideoView.setSystemUiVisibility(View.VISIBLE);
					}
				}
			});
			mediaController.setOnHiddenListener(new OnHiddenListener() {
				
				@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				@Override
				public void onHidden() {
					if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
						mVideoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
					}
				}
			});
		}
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			
			if (mVideoView.isPlaying()) {
				mVideoView.pause();
				pb.setVisibility(View.VISIBLE);
				downloadRateView.setVisibility(View.VISIBLE);
				loadRateView.setVisibility(View.VISIBLE);

			}
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
				mVideoView.start();
				pb.setVisibility(View.GONE);
				downloadRateView.setVisibility(View.GONE);
				loadRateView.setVisibility(View.GONE);
			break;
		case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
			downloadRateView.setText(" " + extra + "kb/s" + "  ");
			break;
		}
		return true;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		loadRateView.setText(" " + percent + "%");
		if (isStart && percent > start_buffer_rate) {
			mVideoView.start();
			isStart = false;
			pb.setVisibility(View.GONE);
			downloadRateView.setVisibility(View.GONE);
			loadRateView.setVisibility(View.GONE);
		} 
	}
	
}
