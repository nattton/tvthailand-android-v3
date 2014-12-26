package com.makathon.tvthailand.ads;

import mobi.vserv.android.ads.AdLoadCallback;
import mobi.vserv.android.ads.AdOrientation;
import mobi.vserv.android.ads.ViewNotEmptyException;
import mobi.vserv.android.ads.VservAd;
import mobi.vserv.android.ads.VservController;
import mobi.vserv.android.ads.VservManager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.makathon.tvthailand.dao.advertise.AdCollectionDao;
import com.makathon.tvthailand.dao.advertise.AdItemDao;
import com.makathon.tvthailand.manager.http.HTTPEngine;

public class InHouseAdView implements OnTouchListener, Handler.Callback {
	private final String PREF_NAME = "com.makathon.tvthailand.ads.InHouseAdView";
	private static final int CLICK_ON_WEBVIEW = 1;
	private static final int CLICK_ON_URL = 2;
	
	private Context mContext;
	private WebView mWebView;
	private Handler handler = new Handler(this);
	
	private FrameLayout adView;
	private VservAd adObject;
	private VservController controller;
	private VservManager manager;
	private static String BANNER_ZONE = "ceb27b33";
	
	private static final String UserAgentDesktop = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36";
	private static final String UserAgentMobile = System.getProperty("http.agent");

	public InHouseAdView(Context context) {
		mContext = context;
	}
	
	public void loadRequest() {
		mWebView = new WebView(mContext);
		mWebView.setOnTouchListener(this);
		requestAds();
	}

	public void loadAd(WebView webView, FrameLayout adView) {
		this.adView = adView;
		setUpAd();
		
		mWebView = webView;
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setOnTouchListener(this);
		requestAds();
	}

    private void requestAds() {
        HTTPEngine.getInstance().getAdvertiseData(new FutureCallback<AdCollectionDao>() {
            @Override
            public void onCompleted(Exception e, AdCollectionDao result) {
                if (e == null) {
                    startAd(result);
                } else {

                }
            }
        });
    }
	
	private void setUpAd() {
		try {
			if (null != controller) {
				controller.stopRefresh();
				controller = null;
			}
			if (null != adView) {
				adView.removeAllViews();
			}
			
			manager = VservManager.getInstance(mContext);
			
			manager.getAd(BANNER_ZONE, AdOrientation.PORTRAIT, new AdLoadCallback() {
				
				@Override
				public void onNoFill() {
					
				}
				
				@Override
				public void onLoadSuccess(VservAd adObj) {
					adObject = adObj;
					if(null != adView) {
						adView.removeAllViews();
					}
					/***** APPLICATION IF USE RENDER AD FUNCTIONALITY ******/
					if (null != controller) {
						controller = null;
					}
					
					if (null != adObject) {
						try {
							adObject.show(mContext, adView);
						} catch (ViewNotEmptyException e) {
							Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				}
				
				@Override
				public void onLoadFailure() {
				}
			});
		} catch (Exception e) {
		}
	}

    private void startAd(AdCollectionDao adItemList) {
        AdItemDao adItem = adItemList.getShuffleAd();
        String adUrl = adItem.getUrl();
        if (adUrl.contains("kapook.com")) {
            mWebView.getSettings().setUserAgentString(UserAgentDesktop);
        } else {
            mWebView.getSettings().setUserAgentString(UserAgentMobile);
        }
        mWebView.loadUrl(adItem.getUrl());
        mWebView.setVisibility(View.VISIBLE);
        if (adView != null) {
            adView.setVisibility(View.GONE);
        }
    }

//	private boolean startRotateAd(final AdsItemDao adItem) {
//		SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//		long lasttime = prefs.getLong(adItem.getName(), 0);
//		Date nextTime = new Date(lasttime + adItem.getInterval());
//		Date currentTime = new Date();
//		if (currentTime.after(nextTime)) {
//			String adUrl = adItem.getUrl();
//			if (adUrl.contains("kapook.com")) {
//				mWebView.getSettings().setUserAgentString(UserAgentDesktop);
//			} else {
//				mWebView.getSettings().setUserAgentString(UserAgentMobile);
//			}
//			mWebView.loadUrl(adItem.getUrl());
//			mWebView.setVisibility(View.VISIBLE);
//			if (adView != null) {
//				adView.setVisibility(View.GONE);
//			}
//
//			SharedPreferences.Editor editor = prefs.edit();
//			editor.putLong(adItem.getName(), currentTime.getTime());
//			editor.commit();
//
//			return true;
//		} else {
//			mWebView.setVisibility(View.GONE);
//			if (adView != null) {
//				adView.setVisibility(View.VISIBLE);
//			}
//		}
//
//		return false;
//	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v == mWebView && event.getAction() == MotionEvent.ACTION_DOWN){
			mWebView.stopLoading();
			mContext.startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse(mWebView.getUrl())));
	    }
		return false;
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == CLICK_ON_URL){
			Log.e("InHouse Ads", "CLICK_ON_URL");
	        handler.removeMessages(CLICK_ON_WEBVIEW);
	        return true;
	    }
	    if (msg.what == CLICK_ON_WEBVIEW){
	        Log.e("InHouse Ads", "CLICK_ON_WEBVIEW");
	        return true;
	    }
	    return false;
	}
}
