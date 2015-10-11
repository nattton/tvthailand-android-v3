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
import android.widget.LinearLayout;

import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.dao.advertise.AdCollectionDao;
import com.codemobi.android.tvthailand.dao.advertise.AdItemDao;
import com.codemobi.android.tvthailand.manager.http.APIClient;
import com.codemobi.android.tvthailand.utils.Constant;
import com.vserv.android.ads.api.VservAdView;
import com.vserv.android.ads.common.VservAdListener;

import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


@SuppressLint({ "DefaultLocale", "SetJavaScriptEnabled" })
public class MyBannerView extends LinearLayout {
	private boolean autoLoad = false;

	private LinearLayout parentView;
	private WebView webViewShow;

	private VservAdView vservAdView;
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
		parentView.setVisibility(GONE);
		View convertView = LayoutInflater.from(context).inflate(R.layout.my_banner_view, this);
		vservAdView = (VservAdView) convertView.findViewById(R.id.vservAdView);
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
		APIClient.APIService service = APIClient.getClient();
		Call<AdCollectionDao> call =  service.loadAd(Constant.defaultParams);
		call.enqueue(new Callback<AdCollectionDao>() {
			@Override
			public void onResponse(Response<AdCollectionDao> response, Retrofit retrofit) {
				if (response.isSuccess())
					displayAds(response.body());
			}

			@Override
			public void onFailure(Throwable t) {
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
			requestVservAd();
		}
	}
	
	private void requestVservAd() {
		adListenerInitialization();
		vservAdView.setAdListener(mAdListener);
		vservAdView.setZoneId(getResources().getString(R.string.vserv_banner_ad_unit_id));
		vservAdView.setRefresh(true);
		vservAdView.setRefreshRate(60);
		vservAdView.loadAd();
	}

	private void adListenerInitialization() {
		mAdListener = new VservAdListener() {

			@Override
			public void didInteractWithAd(VservAdView adView) {
				Log.d("Vserv", "adViewDidLoadAd");
			}

			@Override
			public void adViewDidLoadAd(VservAdView adView) {
				Log.d("Vserv", "adViewDidLoadAd");
				parentView.setVisibility(VISIBLE);
				vservAdView.setVisibility(VISIBLE);
			}

			@Override
			public void willPresentOverlay(VservAdView adView) {
				Log.d("Vserv", "willPresentOverlay");
			}

			@Override
			public void willDismissOverlay(VservAdView adView) {
				Log.d("Vserv", "willDismissOverlay");
			}

			@Override
			public void adViewDidCacheAd(VservAdView adView) {
				Log.d("Vserv", "adViewDidCacheAd");
				if (vservAdView != null) {
					vservAdView.showAd();
				}
			}

			@Override
			public VservAdView didFailedToLoadAd(String arg0) {
				Log.d("VservAdView", "didFailedToLoadAd");
				return null;
			}

			@Override
			public VservAdView didFailedToCacheAd(String Error) {
				Log.d("VservAdView", "didFailedToCacheAd");
				return null;
			}

			@Override
			public void willLeaveApp(VservAdView adView) {
				Log.d("Vserv", "willLeaveApp");
			}
		};
	}
}
