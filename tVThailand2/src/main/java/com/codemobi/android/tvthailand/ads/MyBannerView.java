package com.codemobi.android.tvthailand.ads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.dao.advertise.AdCollectionDao;
import com.codemobi.android.tvthailand.dao.advertise.AdItemDao;
import com.codemobi.android.tvthailand.manager.http.HTTPEngine;
import com.codemobi.android.tvthailand.utils.Constant;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.vserv.android.ads.api.VservAdView;
import com.vserv.android.ads.common.VservAdListener;

import java.util.Locale;


@SuppressLint({ "DefaultLocale", "SetJavaScriptEnabled" })
public class MyBannerView extends LinearLayout {
	private boolean autoLoad = false;

	private LinearLayout parentView;
	private WebView webViewShow;

	private VservAdView vservAdView;
	private RelativeLayout adViewContainer;
	private AdView adView;
	private VservAdListener mAdListener;

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
		vservAdView = (VservAdView) convertView.findViewById(R.id.vservAdView);
		adViewContainer = (RelativeLayout) findViewById(R.id.adViewContainer);
		webViewShow = (WebView) convertView.findViewById(R.id.webViewShow);
		setUpView();
		
		if(autoLoad) requestAds();
	}
	
	private void setUpView() {
		webViewShow.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					Log.d("webViewShow", "load complete");
					webViewShow.setVisibility(View.VISIBLE);
					parentView.setVisibility(View.VISIBLE);
				}
				super.onProgressChanged(view, newProgress);
			}
		});

		webViewShow.setVerticalScrollBarEnabled(false);
		webViewShow.setHorizontalScrollBarEnabled(false);
		webViewShow.getSettings().setJavaScriptEnabled(true);
		webViewShow.getSettings().setAppCacheEnabled(true);
	}

    private void requestAds() {
        HTTPEngine.getInstance().getAdvertiseData(new Response.Listener<AdCollectionDao>() {
            @Override
            public void onResponse(AdCollectionDao response) {
                displayAds(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestVservAd();
            }
        });
    }
	
	private void displayAds (AdCollectionDao adItemList)  {
		try {
			AdItemDao adItem = adItemList.getShuffleAd();
			String nameLower = adItem.getName().toLowerCase(Locale.getDefault());
			if (nameLower.contains("vserv")) {
				requestVservAd();
			} else {
				if (adItem.getUrl().length() > 0) {
					webViewShow.loadUrl(adItem.getUrl());
				}
			}
		} catch (AdCollectionDao.EmptyException err) {
			requestFacebook();
		}
	}

	public class AdWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
	
	private void requestVservAd() {
		adListenerInitialization();
		vservAdView.setAdListener(mAdListener);
		vservAdView.setZoneId(Constant.VSERV_BANNER);
		vservAdView.setRefresh(true);
		vservAdView.setRefreshRate(30);
		vservAdView.loadAd();
	}

	private void requestFacebook() {

		adView = new AdView(getContext(), Constant.FACEBOOK_BANNER, AdSize.BANNER_320_50);
		adView.setAdListener(new AdListener() {
			@Override
			public void onError(Ad ad, AdError adError) {

			}

			@Override
			public void onAdLoaded(Ad ad) {
				parentView.setVisibility(VISIBLE);
				adViewContainer.setVisibility(VISIBLE);
			}

			@Override
			public void onAdClicked(Ad ad) {

			}
		});
		adViewContainer.addView(adView);
		adView.loadAd();
	}

	private void adListenerInitialization() {
		mAdListener = new VservAdListener() {

			@Override
			public void didInteractWithAd(VservAdView adView) {
//				Toast.makeText(getContext(), "didInteractWithAd",
//						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void adViewDidLoadAd(VservAdView adView) {
				parentView.setVisibility(VISIBLE);
				vservAdView.setVisibility(VISIBLE);
//				Toast.makeText(getContext(), "adViewDidLoadAd",
//						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void willPresentOverlay(VservAdView adView) {

//				Toast.makeText(getContext(), "willPresentOverlay",
//						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void willDismissOverlay(VservAdView adView) {

//				Toast.makeText(getContext(), "willDismissOverlay",
//						Toast.LENGTH_SHORT).show();

			}

			@Override
			public void adViewDidCacheAd(VservAdView adView) {

//				Toast.makeText(getContext(), "adViewDidCacheAd",
//						Toast.LENGTH_SHORT).show();
				if (vservAdView != null) {

					if (vservAdView.getUxType() == VservAdView.UX_INTERSTITIAL) {
//						isAppInBackgorund = true;
					}
					vservAdView.showAd();
				}

			}

			@Override
			public VservAdView didFailedToLoadAd(String arg0) {
				requestFacebook();
//				Toast.makeText(getContext(), "didFailedToLoadAd",
//						Toast.LENGTH_SHORT).show();

				return null;
			}

			@Override
			public VservAdView didFailedToCacheAd(String Error) {

//				Toast.makeText(getContext(), "didFailedToCacheAd",
//						Toast.LENGTH_SHORT).show();

				return null;
			}

			@Override
			public void willLeaveApp(VservAdView adView) {
//				Toast.makeText(getContext(), "willLeaveApp",
//						Toast.LENGTH_SHORT).show();
			}
		};
	}
}
