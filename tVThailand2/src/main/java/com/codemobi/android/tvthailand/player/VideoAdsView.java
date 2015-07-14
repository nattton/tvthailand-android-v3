// Copyright 2011 Google Inc. All Rights Reserved.

package com.codemobi.android.tvthailand.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.widget.VideoView;

/**
 * A VideoView that intercepts various methods and reports them back to a set of
 * VideoAdPlayerCallbacks.
 */
public class VideoAdsView extends VideoView implements OnCompletionListener, OnErrorListener {
  /** Interface for alerting caller of video completion. */
  public interface CompleteCallback {
    public void onComplete();
  }

  private enum PlaybackState {
    STOPPED, PAUSED, PLAYING
  }

  private CompleteCallback completeCallback;
  private PlaybackState state = PlaybackState.STOPPED;

  public VideoAdsView(Context context) {
    super(context);
//    super.setOnCompletionListener(this);
    super.setOnErrorListener(this);
  }

  public void setCompleteCallback(CompleteCallback callback) {
    this.completeCallback = callback;
  }

  public void togglePlayback() {
    switch(state) {
      case STOPPED:
      case PAUSED:
        start();
        break;
      case PLAYING:
        pause();
        break;
    }
  }

  @Override
  public void start() {
    super.start();
    PlaybackState oldState = state;
    state = PlaybackState.PLAYING;

    switch (oldState) {
      case STOPPED:
    	  
        break;
      case PAUSED:
    	  
        break;
      default:
        // Already playing; do nothing.
    }
  }

  @Override
  public void pause() {
    super.pause();
    state = PlaybackState.PAUSED;
    
  }

  @Override
  public void stopPlayback() {
    super.stopPlayback();
    onStop();
  }

  private void onStop() {
    if (state == PlaybackState.STOPPED) {
      return; // Already stopped; do nothing.
    }

    state = PlaybackState.STOPPED;
  }

  @Override
  public void onCompletion(MediaPlayer mp) {
    onStop();
    completeCallback.onComplete();
  }

  @Override
  public boolean onError(MediaPlayer mp, int what, int extra) {
    onStop();
    // Returning true signals to MediaPlayer that we handled the error. This will prevent the
    // completion handler from being called.
    return true;
  }

//  @Override
//  public void setOnCompletionListener(OnCompletionListener l) {
//	  
//    throw new UnsupportedOperationException();
//  }
}
