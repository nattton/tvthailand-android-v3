package com.vmax.android.ads.mediation.partners;


import java.util.Map;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AdListener;

import com.vmax.android.ads.mediation.partners.VmaxCustomAd;
import com.vmax.android.ads.mediation.partners.VmaxCustomAdListener;

/*
 * Tested with facebook SDK 4.8.2.
 */
public class FaceBookBanner extends VmaxCustomAd implements AdListener {
    private static final String PLACEMENT_ID_KEY = "placementid";

    private AdView mFacebookBanner;
    private VmaxCustomAdListener mBannerListener;
    public boolean LOGS_ENABLED = true;
    private boolean isFirstAd = false;

    /**
     * CustomEventBanner implementation
     */

    @Override
    public void loadAd(final Context context,
                       final VmaxCustomAdListener customEventBannerListener,
                       final Map<String, Object> localExtras,
                       final Map<String, Object> serverExtras) {
        try {
            // if (LOGS_ENABLED) {
            // Log.d("vmax", "Facebook showad banner.");
            // }
            mBannerListener = customEventBannerListener;

            final String placementId;
            if (extrasAreValid(serverExtras)) {
                placementId = serverExtras.get(PLACEMENT_ID_KEY).toString();
            } else {
                mBannerListener.onAdFailed(0);
                return;
            }
            if (localExtras != null) {
                if (localExtras.containsKey("test")) {

                    String[] mTestAvdIds = (String[]) localExtras
                            .get("test");
                    if (mTestAvdIds != null) {
                        for (int i = 0; i < mTestAvdIds.length; i++) {
                            if (LOGS_ENABLED) {
                                Log.i("vmax",
                                        "test devices: "
                                                + mTestAvdIds[i]);
                            }
                            AdSettings.addTestDevice(mTestAvdIds[i]);
                            if (LOGS_ENABLED) {
                                Log.i("vmax",
                                        "Test mode: "
                                                + AdSettings.isTestMode(context));
                            }
                        }
                    }
                }
            }

            if (isTablet(context)) {
                mFacebookBanner = new AdView(context, placementId,
                        AdSize.BANNER_HEIGHT_90);
            } else {
                mFacebookBanner = new AdView(context, placementId,
                        AdSize.BANNER_HEIGHT_50);
            }

            mFacebookBanner.setAdListener(this);
            mFacebookBanner.loadAd();
        } catch (Exception e) {
            if (mBannerListener != null) {
                mBannerListener.onAdFailed(0);
            }
            e.printStackTrace();
            return;
        }
    }

    public boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onInvalidate() {
        try {
            if (LOGS_ENABLED) {
                Log.d("vmax", "onInvalidate.");
            }
            if (mFacebookBanner != null) {
                if (LOGS_ENABLED) {
                    Log.d("vmax", "Facebook banner ad onInvalidate.");
                }
                mFacebookBanner.removeAllViews();
                mFacebookBanner.setAdListener(null);
                mFacebookBanner.destroy();
                mFacebookBanner = null;
                isFirstAd = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showAd() {
        // TODO Auto-generated method stub

    }

    /**
     * AdListener implementation
     */

    @Override
    public void onAdLoaded(Ad ad) {

        if (!isFirstAd) {
            isFirstAd = true;
            if (LOGS_ENABLED) {
                Log.d("vmax",
                        "Facebook banner ad loaded successfully. Showing ad...");

            }
            mBannerListener.onAdLoaded(mFacebookBanner);
        } else {
            if (LOGS_ENABLED) {
                Log.i("vmax",
                        "Recommended: Make sure you switch OFF refresh of Facebook from thier dashboard.");

            }
            mFacebookBanner.removeAllViews();
            try {
                if (mFacebookBanner != null) {
                    mFacebookBanner.setAdListener(null);
                    mFacebookBanner.destroy();
                    mFacebookBanner = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(final Ad ad, final AdError error) {
        if (LOGS_ENABLED) {
            Log.d("vmax",
                    "Facebook banner ad failed to load. error: "
                            + error.getErrorCode());
        }
        if (error == AdError.NO_FILL) {
            mBannerListener.onAdFailed(1);
        } else if (error == AdError.INTERNAL_ERROR) {
            mBannerListener.onAdFailed(2);
        } else {
            mBannerListener.onAdFailed(0);
        }
    }

    @Override
    public void onAdClicked(Ad ad) {
        if (LOGS_ENABLED) {
            Log.d("vmax", "Facebook banner ad clicked.");
        }
        mBannerListener.onAdClicked();
    }

    private boolean extrasAreValid(final Map<String, Object> serverExtras) {
        final String placementId = serverExtras.get(PLACEMENT_ID_KEY)
                .toString();
        return (placementId != null && placementId.length() > 0);
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onDestroy() {

        if (mFacebookBanner != null) {
            if (LOGS_ENABLED) {
                Log.d("vmax", "Facebook banner ad onDestroy.");
            }
            mFacebookBanner.removeAllViews();
            mFacebookBanner.destroy();

            mFacebookBanner = null;
        }
    }

}
