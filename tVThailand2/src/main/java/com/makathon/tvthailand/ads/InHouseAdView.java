package com.makathon.tvthailand.ads;

import java.util.Date;

import mobi.vserv.android.ads.AdLoadCallback;
import mobi.vserv.android.ads.AdOrientation;
import mobi.vserv.android.ads.ViewNotEmptyException;
import mobi.vserv.android.ads.VservAd;
import mobi.vserv.android.ads.VservController;
import mobi.vserv.android.ads.VservManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.makathon.tvthailand.MyVolley;
import com.makathon.tvthailand.datasource.AppUtility;

public class InHouseAdView implements OnTouchListener, Handler.Callback {
	private final String PREF_NAME = "com.makathon.tvthailand.ads.InHouseAdView";
	private static final int CLICK_ON_WEBVIEW = 1;
	private static final int CLICK_ON_URL = 2;
	
	private Context mContext;
	private RequestQueue mReqestQueue;
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
		mReqestQueue = MyVolley.getRequestQueue();
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
	
	private String getCurrentTime() {
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		return today.format("%Y%m%d%H%M");
	}
	
	private void requestAds() {
		String url = String.format("%s/advertise?device=android&time=%s", AppUtility.BASE_URL, getCurrentTime());
		JsonObjectRequest loadAdsRequest = new JsonObjectRequest(Method.GET, url, null, reqSuccessListener(), reqErrorListener());
		mReqestQueue.add(loadAdsRequest);
	}
	
	private Response.Listener<JSONObject> reqSuccessListener() {
		return new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONArray jAds = response.getJSONArray("ads");
					int adLength = jAds.length();
					for (int i = 0; i < adLength; i++) {
						JSONObject adObj = jAds.getJSONObject(i);
						String name = adObj.getString("name");
						String url = adObj.getString("url");
						int time = adObj.getInt("time");
						int interval = adObj.getInt("interval");
						AdRotate adR = new AdRotate(name, url, time, interval);
						if (startRotateAd(adR)) break;
					}
				} catch (JSONException e) {
					Log.e("InHouseAd", "JSONException");
				}
			}
		};
	}
	
	private Response.ErrorListener reqErrorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				
			}
		};
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

	private boolean startRotateAd(final AdRotate adRotate) {
		SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		long lasttime = prefs.getLong(adRotate.getName(), 0);
		Date nextTime = new Date(lasttime + adRotate.getInterval());
		Date currentTime = new Date();
		if (currentTime.after(nextTime)) {
			String adUrl = adRotate.getUrl();
			if (adUrl.contains("kapook.com")) {
				mWebView.getSettings().setUserAgentString(UserAgentDesktop);
			} else {
				mWebView.getSettings().setUserAgentString(UserAgentMobile);
			}
			mWebView.loadUrl(adRotate.getUrl());
			mWebView.setVisibility(View.VISIBLE);
			if (adView != null) {
				adView.setVisibility(View.GONE);	
			}
			
			SharedPreferences.Editor editor = prefs.edit();
			editor.putLong(adRotate.getName(), currentTime.getTime());
			editor.commit();

			return true;
		} else {
			mWebView.setVisibility(View.GONE);
			if (adView != null) {
				adView.setVisibility(View.VISIBLE);
			}
		}
		
		return false;
	}

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
