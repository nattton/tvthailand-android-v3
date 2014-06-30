package com.makathon.tvthailand.utils;

import android.os.Handler;
import android.util.Log;

public class CountDownTimer {
	   private long millisInFuture;
	    private long countDownInterval;
	    private boolean status;
	    public CountDownTimer(long pMillisInFuture, long pCountDownInterval) {
	            this.millisInFuture = pMillisInFuture;
	            this.countDownInterval = pCountDownInterval;
	            status = false;
	            Initialize();
	    }

	    public void Stop() {
	        status = false;
	    }

	    public long getCurrentTime() {
	        return millisInFuture;
	    }

	    public void Start() {
	        status = true;
	    }
	    public void Initialize() 
	    {
	        final Handler handler = new Handler();
	        final Runnable counter = new Runnable(){

	            public void run(){
	                long sec = millisInFuture/1000;
	                if(status) {
	                    if(millisInFuture <= 0) {
	                    } else {
	                        millisInFuture -= countDownInterval;
	                        handler.postDelayed(this, countDownInterval);
	                    }
	                } else {
	                    handler.postDelayed(this, countDownInterval);
	                }
	            }
	        };

	        handler.postDelayed(counter, countDownInterval);
	    }
	

}
