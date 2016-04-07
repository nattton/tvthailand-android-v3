package com.vmax.android.ads.mediation.partners;


import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import com.vmax.android.ads.mediation.partners.VmaxCustomAd;
import com.vmax.android.ads.mediation.partners.VmaxCustomAdListener;

/*
 * Tested with facebook SDK 4.8.2.
 */

public class FaceBookInterstitial extends VmaxCustomAd implements
        InterstitialAdListener {
    private static final String PLACEMENT_ID_KEY = "placementid";

    private InterstitialAd mFacebookInterstitial;
    private VmaxCustomAdListener mInterstitialListener;
    public boolean LOGS_ENABLED = true;

    /**
     * CustomEventInterstitial implementation
     */

    @Override
    public void loadAd(final Context context,
                       final VmaxCustomAdListener vmaxCustomAdListener,
                       final Map<String, Object> localExtras,
                       final Map<String, Object> serverExtras) {
        try {
            // if (LOGS_ENABLED) {
            // Log.d("vmax", "Facebook loadad inter.");
            // }

            mInterstitialListener = vmaxCustomAdListener;

            final String placementId;
            if (extrasAreValid(serverExtras)) {
                placementId = serverExtras.get(PLACEMENT_ID_KEY).toString();
            } else {
                mInterstitialListener.onAdFailed(0);
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

            mFacebookInterstitial = new InterstitialAd(context, placementId);

            mFacebookInterstitial.setAdListener(this);

            mFacebookInterstitial.loadAd();
        } catch (Exception e) {
            if (mInterstitialListener != null) {
                mInterstitialListener.onAdFailed(0);
            }
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void showAd() {
        try {
            // if (LOGS_ENABLED) {
            // Log.d("vmax", "Facebook showad inter.");
            // }
            if (mFacebookInterstitial != null
                    && mFacebookInterstitial.isAdLoaded()) {

                mFacebookInterstitial.show();
            } else {
                if (LOGS_ENABLED) {
                    Log.d("vmax",
                            "Tried to show a Facebook interstitial ad before it finished loading. Please try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mInterstitialListener.onAdFailed(0);
        }
    }

    @Override
    public void onInvalidate() {
        try {
            if (mFacebookInterstitial != null) {
                if (LOGS_ENABLED) {
                    Log.d("vmax", "Facebook Interstitial ad onInvalidate.");
                }
                mFacebookInterstitial.setAdListener(null);
                mFacebookInterstitial.destroy();
                mFacebookInterstitial = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * InterstitialAdListener implementation
     */

    @Override
    public void onAdLoaded(final Ad ad) {
        if (LOGS_ENABLED) {
            Log.d("vmax", "Facebook interstitial ad loaded successfully.");

        }
        mInterstitialListener.onAdLoaded();
    }

    @Override
    public void onError(final Ad ad, final AdError error) {
        if (LOGS_ENABLED) {
            Log.d("vmax", "Facebook interstitial ad failed to load. error: "
                    + error.getErrorCode());
        }
        if (error == AdError.NO_FILL) {
            mInterstitialListener.onAdFailed(1); // 1 No fill
        } else if (error == AdError.INTERNAL_ERROR) {
            mInterstitialListener.onAdFailed(2); // 2 Internal Error
        } else {
            mInterstitialListener.onAdFailed(0);
        }
    }

    @Override
    public void onInterstitialDisplayed(final Ad ad) {
        if (LOGS_ENABLED) {
            Log.d("vmax", "Showing Facebook interstitial ad.");
        }
        mInterstitialListener.onAdShown();
    }

    @Override
    public void onAdClicked(final Ad ad) {
        if (LOGS_ENABLED) {
            Log.d("vmax", "Facebook interstitial ad clicked.");
        }
        mInterstitialListener.onAdClicked();
    }

    @Override
    public void onInterstitialDismissed(final Ad ad) {
        if (LOGS_ENABLED) {
            Log.d("vmax", "Facebook interstitial ad dismissed.");
        }
        mInterstitialListener.onAdDismissed();
        onDestroy();
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

        if (mFacebookInterstitial != null) {
            if (LOGS_ENABLED) {
                Log.d("vmax", "Facebook Interstitial ad onDestroy.");
            }
            mFacebookInterstitial.destroy();

            mFacebookInterstitial = null;
        }
    }
}
