/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.makathon.tvthailand.player;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.utils.Constant;

/**
 * A simple YouTube Android API demo application which shows how to create a
 * simple application that displays a YouTube Video in a
 * {@link YouTubePlayerView}.
 * <p>
 * Note, to use a {@link YouTubePlayerView}, your activity must extend
 * {@link YouTubeBaseActivity}.
 */
public class YoutubePlayerViewActivity extends YouTubeFailureRecoveryActivity {

	public static final String EXTRAS_TITLE = "EXTRAS_TITLE";
	public static final String EXTRAS_VIDEO_ID = "EXTRAS_VIDEO_ID";
	
	private String videoId = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.youtube_playerview);

		Bundle bundle = getIntent().getExtras();

		setTitle(bundle.getString(EXTRAS_TITLE));
		videoId = bundle.getString(EXTRAS_VIDEO_ID);

		YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		youTubeView.initialize(Constant.DEVELOPER_KEY, this);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//			Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//	    	Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
	    }
	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		if (!wasRestored) {
			player.cueVideo(videoId);
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				player.setFullscreen(true);
				player.play();
			}
		}
	}

	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}

	@Override
	public void onInitializationFailure(Provider provider,
			YouTubeInitializationResult errorReason) {
		super.onInitializationFailure(provider, errorReason);

		playWithIntent();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.player_menu, menu);
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
		if(YouTubeIntents.isYouTubeInstalled(this)) {
			Intent intent = YouTubeIntents.createPlayVideoIntent(this, videoId);
			startActivity(intent);
		} else {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(
				"http://www.youtube.com/watch?v=%s", videoId))));
		}
	}

}
