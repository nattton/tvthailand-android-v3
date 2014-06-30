package com.makathon.tvthailand.ads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import mobi.vserv.android.ads.AdLoadCallback;
import mobi.vserv.android.ads.AdOrientation;
import mobi.vserv.android.ads.ViewNotEmptyException;
import mobi.vserv.android.ads.VservAd;
import mobi.vserv.android.ads.VservController;
import mobi.vserv.android.ads.VservManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.makathon.tvthailand.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint({ "DefaultLocale", "SetJavaScriptEnabled" })
public class MyBannerView extends LinearLayout {
	private final String PREF_NAME = "com.makathon.tvthailand2.ads.KapookBannerView";
//	private static final String UserAgentDesktop = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36";
	
	private final String adsApiUrl = "http://tv.makathon.com/api2/advertise?device=android";
	private final String kpookApiUrl = "http://kapi.kapook.com/partner/url";
	
	private RequestQueue mRequestQueue;
	private boolean autoLoad = false;

	private LinearLayout parentView;
	private WebView webViewShow;
	private WebView webView1px;
	
	private FrameLayout vservView;
	private VservAd adObject;
	private VservController controller;
	private VservManager manager;
	private static String BANNER_ZONE = "ceb27b33";

	public MyBannerView(Context context) {
		super(context);
		
		initView(context);
	}

	public MyBannerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyBannerView, 0, 0);
		try {
			autoLoad = a.getBoolean(R.styleable.MyBannerView_autoLoad, false);
		} finally {
			a.recycle();
		}
		
		initView(context);
	}

	private void initView(Context context) {
		parentView = this;
		parentView.setVisibility(View.GONE);
		
		mRequestQueue = Volley.newRequestQueue(context);
		
		View convertView = LayoutInflater.from(context).inflate(R.layout.my_banner_view, this);
		vservView = (FrameLayout) convertView.findViewById(R.id.vservView);
		webViewShow = (WebView) convertView.findViewById(R.id.webViewShow);
		webView1px = (WebView) convertView.findViewById(R.id.webView1px);
		setUpView();
		
		if(autoLoad) requestAds();
	}
	
	private void setUpView() {
		webViewShow.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				System.out.println(newProgress);
				if (newProgress == 100) {
					parentView.setVisibility(View.VISIBLE);
				}
				super.onProgressChanged(view, newProgress);
			}
		});

		webViewShow.setVerticalScrollBarEnabled(false);
		webViewShow.setHorizontalScrollBarEnabled(false);
		webViewShow.getSettings().setJavaScriptEnabled(true);
		webViewShow.getSettings().setAppCacheEnabled(true);
		
		webView1px.getSettings().setJavaScriptEnabled(true);
		webView1px.getSettings().setUserAgentString(System.getProperty("http.agent"));
	}
	
	public void requestAds() {
		JsonObjectRequest loadAdsApi = new JsonObjectRequest(Method.GET, adsApiUrl, null, loadAdsSuccessListener(), loadAdsErrorListener());
		loadAdsApi.setShouldCache(false);
		mRequestQueue.add(loadAdsApi);
	}
	
	private Response.Listener<JSONObject> loadAdsSuccessListener() {
		return new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONArray jAds = response.getJSONArray("ads");
					int adLength = jAds.length();
					ArrayList<AdRotate> adRotates = new ArrayList<AdRotate>();
					for (int i = 0; i < adLength; i++) {
						JSONObject adObj = jAds.getJSONObject(i);
						String name = adObj.getString("name");
						String url = adObj.getString("url");
						int time = adObj.getInt("time");
						int interval = adObj.getInt("interval");
						AdRotate adsR = new AdRotate(name, url, time, interval);
						adRotates.add(adsR);
					}
					
					// Shuffle Ads
					Collections.shuffle(adRotates);
					
					if (adRotates.size() > 0) {
						displayAds(adRotates.get(0));
					} else {
						requestVservAd();
					}
					
				} catch (JSONException e) {
					Log.e("InHouseAd", "JSONException");
				}
			}
		};
	}
	
	private Response.ErrorListener loadAdsErrorListener () {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				requestVservAd();
			}
		};
	}
	
	private void displayAds (AdRotate adRotate)  {
		String nameLower = adRotate.getName().toLowerCase(Locale.getDefault());
		if (nameLower.contains("kapook")) {
			requestKapookAds();
		} else if (nameLower.contains("vserv")) {
			requestVservAd();
		} else {
			if (adRotate.getUrl().length() > 0) {
				webViewShow.loadUrl(adRotate.getUrl());
			}
		}
		
		Handler handle = new Handler();
		handle.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				requestVservAd();
			}
		}, 30 * 1000);
	}
	
	private boolean startRotateAd (AdRotate adRotate) {
		SharedPreferences prefs = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		long lasttime = prefs.getLong(adRotate.getName(), 0);
		Date nextTime = new Date(lasttime + adRotate.getInterval());
		Date currentTime = new Date();
		if (currentTime.after(nextTime)) {
			
			SharedPreferences.Editor editor = prefs.edit();
			editor.putLong(adRotate.getName(), currentTime.getTime());
			editor.commit();
			
			String nameLower = adRotate.getName().toLowerCase(Locale.getDefault());
			if (nameLower.contains("kapook")) {
				requestKapookAds();
			} else if (nameLower.contains("vserv")) {
				requestVservAd();
			} else {
				if (adRotate.getUrl().length() > 0) {
					webViewShow.loadUrl(adRotate.getUrl());
				} else {
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}

	public void requestKapookAds() {
		JsonObjectRequest loadKpApi = new JsonObjectRequest(Method.GET, kpookApiUrl,
				null, loadKpSuccessListener(), loadKpErrorListener());
		loadKpApi.setShouldCache(true);
		mRequestQueue.add(loadKpApi);
	}

	private Response.Listener<JSONObject> loadKpSuccessListener() {
		return new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					String url1x1 = response.getString("url_1x1");
					String urlShow = response.getString("url_show");

					webView1px.loadUrl(url1x1);
					webViewShow.loadUrl(urlShow);
				} catch (Exception e) {

				}
			}
		};
	}

	private Response.ErrorListener loadKpErrorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				requestVservAd();
			}
		};
	}

	public class AdWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
	
	private void requestVservAd() {
		vservView.setVisibility(View.GONE);
		try {
			if (null != controller) {
				controller.stopRefresh();
				controller = null;
			}
			if (null != vservView) {
				vservView.removeAllViews();
			}
			
			manager = VservManager.getInstance(getContext());
			
			manager.getAd(BANNER_ZONE, AdOrientation.PORTRAIT, new AdLoadCallback() {
				
				@Override
				public void onNoFill() {
					
				}
				
				@Override
				public void onLoadSuccess(VservAd adObj) {
					adObject = adObj;
					if(null != vservView) {
						vservView.removeAllViews();
					}
					/***** APPLICATION IF USE RENDER AD FUNCTIONALITY ******/
					if (null != controller) {
						controller = null;
					}
					
					if (null != adObject) {
						try {
							vservView.setVisibility(View.VISIBLE);
							parentView.setVisibility(View.VISIBLE);
							adObject.show(getContext(), vservView);
						} catch (ViewNotEmptyException e) {
							Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				}
				
				@Override
				public void onLoadFailure() {
					vservView.setVisibility(View.GONE);
				}
			});
		} catch (Exception e) {
		}
	}
}
