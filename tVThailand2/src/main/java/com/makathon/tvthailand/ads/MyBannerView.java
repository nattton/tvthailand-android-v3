package com.makathon.tvthailand.ads;

import java.util.Locale;

import mobi.vserv.android.ads.AdLoadCallback;
import mobi.vserv.android.ads.AdOrientation;
import mobi.vserv.android.ads.ViewNotEmptyException;
import mobi.vserv.android.ads.VservAd;
import mobi.vserv.android.ads.VservController;
import mobi.vserv.android.ads.VservManager;

import com.koushikdutta.async.future.FutureCallback;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.dao.advertise.AdCollectionDao;
import com.makathon.tvthailand.dao.advertise.AdItemDao;
import com.makathon.tvthailand.dao.advertise.KapookItemDao;
import com.makathon.tvthailand.manager.http.HTTPEngine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint({ "DefaultLocale", "SetJavaScriptEnabled" })
public class MyBannerView extends LinearLayout {
	private final String PREF_NAME = "com.makathon.tvthailand.ads.KapookBannerView";

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
			autoLoad = a.getBoolean(R.styleable.MyBannerView_autoLoad, true);
		} finally {
			a.recycle();
		}
		
		initView(context);
	}

	private void initView(Context context) {
		parentView = this;
		parentView.setVisibility(View.GONE);
		
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
				if (newProgress == 100) {
                    Log.d("webViewShow", "load complete");
					parentView.setVisibility(View.VISIBLE);
				}
				super.onProgressChanged(view, newProgress);
			}
		});

		webViewShow.setVerticalScrollBarEnabled(false);
		webViewShow.setHorizontalScrollBarEnabled(false);
		webViewShow.getSettings().setJavaScriptEnabled(true);
		webViewShow.getSettings().setAppCacheEnabled(true);

        WebSettings webSettings1px = webView1px.getSettings();
        webSettings1px.setJavaScriptEnabled(true);
        webSettings1px.setAppCacheEnabled(false);
        webSettings1px.setUserAgentString(System.getProperty("http.agent"));
        webView1px.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    Log.d("webView1px", "load complete");
                }
                super.onProgressChanged(view, newProgress);
            }
        });
	}

    private void requestAds() {
        HTTPEngine.getInstance().getAdvertiseData(new FutureCallback<AdCollectionDao>() {
            @Override
            public void onCompleted(Exception e, AdCollectionDao result) {
                if (e == null) {
                    displayAds(result);
                } else {
                    requestVservAd();
                }
            }
        });
    }
	
	private void displayAds (AdCollectionDao adItemList)  {
        AdItemDao adItem = adItemList.getShuffleAd();
		String nameLower = adItem.getName().toLowerCase(Locale.getDefault());
		if (nameLower.contains("kapook")) {
			requestKapookAds();
		} else if (nameLower.contains("vserv")) {
			requestVservAd();
		} else {
			if (adItem.getUrl().length() > 0) {
				webViewShow.loadUrl(adItem.getUrl());
			}
		}
	}

    public void requestKapookAds() {
        HTTPEngine.getInstance().getAdKapookData(new FutureCallback<KapookItemDao>() {
            @Override
            public void onCompleted(Exception e, KapookItemDao result) {
                if (e == null && result != null) {
                    webView1px.loadUrl(result.getUrl1x1());
                    webViewShow.loadUrl(result.getUrlShow());
                } else {
                    requestVservAd();
                }
            }
        });
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
