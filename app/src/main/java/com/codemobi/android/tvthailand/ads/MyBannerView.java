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
import android.widget.RelativeLayout;

import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.dao.advertise.AdCollectionDao;
import com.codemobi.android.tvthailand.dao.advertise.AdItemDao;
import com.codemobi.android.tvthailand.manager.http.APIClient;
import com.codemobi.android.tvthailand.utils.Constant;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.ads.*;
import com.vmax.android.ads.api.VmaxAdView;
import com.vmax.android.ads.common.VmaxAdListener;

import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.ads.AdSize.*;


@SuppressLint({ "DefaultLocale", "SetJavaScriptEnabled" })
public class MyBannerView extends LinearLayout {
	private boolean autoLoad = false;

	@BindView(R.id.vmaxAdView) VmaxAdView vmaxAdView;
	@BindView(R.id.adViewContainer) RelativeLayout adViewContainer;
	@BindView(R.id.webViewShow) WebView webViewShow;

	private LinearLayout parentView;
	private AdView adView;
	private VmaxAdListener mAdListener;

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
		ButterKnife.bind(this, convertView);
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
			public void onResponse(Call<AdCollectionDao> call, Response<AdCollectionDao> response) {
				if (response.isSuccessful())
					displayAds(response.body());
			}

			@Override
			public void onFailure(Call<AdCollectionDao> call, Throwable t) {
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
			} else if (nameLower.contains("facebook")) {
				requestFacebookAds();
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
		try {
			vmaxAdView.setAdListener(mAdListener);
			vmaxAdView.setAdSpotId(getResources().getString(R.string.vmax_interstitial_ad_unit_id));
			vmaxAdView.setRefresh(true);
			vmaxAdView.setRefreshRate(60);
			vmaxAdView.loadAd();
		}
		catch (Exception e) {
			requestFacebookAds();
			CrashlyticsCore.getInstance().logException(e);
		}
	}

	private void adListenerInitialization() {
		mAdListener = new VmaxAdListener() {

			@Override
			public void didInteractWithAd(VmaxAdView adView) {
				Log.d("Vmax", "adViewDidLoadAd");
			}

			@Override
			public void willPresentAd(VmaxAdView vmaxAdView) {
				Log.d("Vmax", "willPresentAd");
			}

			@Override
			public void willDismissAd(VmaxAdView vmaxAdView) {
				Log.d("Vmax", "willPresentAd");
			}

			@Override
			public void adViewDidLoadAd(VmaxAdView adView) {
				Log.d("Vmax", "adViewDidLoadAd");
				parentView.setVisibility(VISIBLE);
				vmaxAdView.setVisibility(VISIBLE);
			}

			@Override
			public void adViewDidCacheAd(VmaxAdView adView) {
				Log.d("Vmax", "adViewDidCacheAd");
				if (vmaxAdView != null) {
					vmaxAdView.showAd();
				}
			}

			@Override
			public VmaxAdView didFailedToLoadAd(String arg0) {
				Log.d("VmaxAdView", "didFailedToLoadAd");
				requestFacebookAds();
				return null;
			}

			@Override
			public VmaxAdView didFailedToCacheAd(String Error) {
				Log.d("VmaxAdView", "didFailedToCacheAd");
				requestFacebookAds();
				return null;
			}

			@Override
			public void willLeaveApp(VmaxAdView adView) {
				Log.d("Vmax", "willLeaveApp");
			}

			@Override
			public void onVideoView(boolean b, int i, int i1) {

			}

			@Override
			public void onAdExpand() {

			}

			@Override
			public void onAdCollapsed() {

			}

		};
	}

	private void requestFacebookAds () {
		adView = new AdView(getContext(), getResources().getString(R.string.facebook_banner_ad_unit_id), AdSize.BANNER_HEIGHT_50);
		adViewContainer.addView(adView);
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
		adView.loadAd();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (adView != null)
			adView.destroy();
	}
}
