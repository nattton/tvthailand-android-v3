/**
 * Project      : Advert
 * Filename     : FaceBookNative.java
 * Author       : narendrap
 * Comments     :
 * Copyright    : Copyright  2014, VSERV
 */

package com.vmax.android.ads.mediation.partners;


import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAd.Rating;

import com.vmax.android.ads.mediation.partners.VmaxCustomAd;
import com.vmax.android.ads.mediation.partners.VmaxCustomAdListener;
import com.vmax.android.ads.mediation.partners.VmaxCustomNativeAdListener;

/**
 * @author narendrap
 */

/*
 * Tested with facebook SDK 4.8.2.
 */
public class FaceBookNative extends VmaxCustomAd implements AdListener {

    private NativeAd nativeAd;
    private static final String PLACEMENT_ID_KEY = "placementid";
    private VmaxCustomNativeAdListener mNativeAdListener;
    public boolean LOGS_ENABLED = true;
    private VmaxCustomAdListener vmaxCustomAdListener;
    private Context context;


    @Override
    public void loadAd(Context context,
                       VmaxCustomAdListener vmaxCustomAdListener,
                       Map<String, Object> localExtras, Map<String, Object> serverExtras) {

        try {
            if (LOGS_ENABLED) {
                Log.d("vmax", "Facebook loadAd .");
            }
            this.context = context;
            this.vmaxCustomAdListener = vmaxCustomAdListener;
            final String placementId;


            if (localExtras != null) {
                if (localExtras.containsKey("nativeListener")) {
                    if (LOGS_ENABLED) {
                        Log.i("Log", "nativeListener in localextras ");
                    }
                    mNativeAdListener = (VmaxCustomNativeAdListener) localExtras.get("nativeListener");


                }
            }
            if (extrasAreValid(serverExtras)) {
                placementId = serverExtras.get(PLACEMENT_ID_KEY).toString();
            } else {
                mNativeAdListener.onAdFailed("Placement id missing");
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


            nativeAd = new NativeAd(context, placementId);
            AdSettings.addTestDevice("a33fc28edcc10fa20ce0454b7a9a204a"); // put your test device id printed in logs when you make first request to facebook.
            nativeAd.setAdListener(this);
            nativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
        } catch (Exception e) {
            if (mNativeAdListener != null) {
                mNativeAdListener.onAdFailed(e.getMessage());
            }
            e.printStackTrace();
            return;
        }
    }


    /* (non-Javadoc)
     * @see com.facebook.com.vmax.android.ads.AdListener#onAdClicked(com.facebook.com.vmax.android.ads.Ad)
     */
    @Override
    public void onAdClicked(Ad arg0) {
        Log.i("vmax", "fb onAdClicked");
        if (vmaxCustomAdListener != null) {

            vmaxCustomAdListener.onAdClicked();
        }
        if (vmaxCustomAdListener != null) {

            vmaxCustomAdListener.onLeaveApplication();
        }
    }

    /* (non-Javadoc)
     * @see com.facebook.com.vmax.android.ads.AdListener#onAdLoaded(com.facebook.com.vmax.android.ads.Ad)
     */
    @Override
    public void onAdLoaded(Ad ad) {
        try {
            String adChoiceIcon = null, adChoiceURl = null;
            String coverImageURL = null;
            int coverImageHeight = 0;
            int coverImageWidth = 0;
            String iconForAd = null;
            int iconAdWidth = 0;
            int iconAdHeight = 0;

            if (ad != nativeAd) {
                return;
            }
            nativeAd.unregisterView();
            String titleForAd = nativeAd.getAdTitle();
            if (nativeAd.getAdCoverImage() != null) {
                coverImageURL = nativeAd.getAdCoverImage().getUrl();
                coverImageHeight = nativeAd.getAdCoverImage().getHeight();
                coverImageWidth = nativeAd.getAdCoverImage().getWidth();
            }
            if (nativeAd.getAdIcon() != null) {
                iconForAd = nativeAd.getAdIcon().getUrl();
                iconAdHeight = nativeAd.getAdIcon().getHeight();
                iconAdWidth = nativeAd.getAdIcon().getWidth();
            }
            String socialContextForAd = nativeAd.getAdSocialContext();
            String titleForAdButton = nativeAd.getAdCallToAction();
            String textForAdBody = nativeAd.getAdBody();
            if (nativeAd.getAdChoicesIcon() != null) {
                adChoiceIcon = nativeAd.getAdChoicesIcon().getUrl();
            }
            if (nativeAd.getAdChoicesLinkUrl() != null) {
                adChoiceURl = nativeAd.getAdChoicesLinkUrl();
            }


            MediaView nativeMediaView = new MediaView(context);
            nativeAd.setMediaViewAutoplay(true);
            nativeMediaView.setAutoplay(true);
            nativeMediaView.setNativeAd(nativeAd);

            String appRatingForAd = "";
            Double rating = getDoubleRating(nativeAd.getAdStarRating());
            if (rating != null) {
                appRatingForAd = Double.toString(rating);
            }
            if (LOGS_ENABLED) {
                Log.d("vmax", "Title for Ad : " + titleForAd);
                Log.d("vmax", "coverImage URL : " + coverImageURL);
                Log.d("vmax", "socialContextForAd : " + socialContextForAd);
                Log.d("vmax", "titleForAdButton : " + titleForAdButton);
                Log.d("vmax", "textForAdBody : " + textForAdBody);
                Log.d("vmax", "appRatingForAd : " + appRatingForAd);
                Log.d("vmax", "iconForAd : " + iconForAd);
            }

            JSONObject fbJSON = new JSONObject();
            try {
                fbJSON.put("TITLE_TEXT", titleForAd);
                fbJSON.put("SOCIAL_CONTEXT", socialContextForAd);
                fbJSON.put("CALL_TO_ACTION", titleForAdButton);
                fbJSON.put("RATING", appRatingForAd);
                fbJSON.put("DESCRIPTION", textForAdBody);
                fbJSON.put("ICON", iconForAd);
                fbJSON.put("ICON_WIDTH", iconAdWidth);
                fbJSON.put("ICON_HEIGHT", iconAdHeight);
                fbJSON.put("COVER_IMAGE", coverImageURL);
                fbJSON.put("COVER_IMAGE_WIDTH", coverImageWidth);
                fbJSON.put("COVER_IMAGE_HEIGHT", coverImageHeight);
                fbJSON.put("MEDIA_VIEW", nativeMediaView);
                fbJSON.put("SCREENSHOT", coverImageURL);
                fbJSON.put("SCREENSHOT_WIDTH", coverImageWidth);
                fbJSON.put("SCREENSHOT_HEIGHT", coverImageHeight);
                fbJSON.put("AD_CHOICE_ICON", adChoiceIcon);
                fbJSON.put("AD_CHOICE_URL", adChoiceURl);
                fbJSON.put("TYPE", "Media");
                if (nativeAd.getAdIcon() != null) {
                    fbJSON.put("ICON_DRAWABLE", nativeAd.getAdIcon());
                }
                if (nativeAd.getAdCoverImage() != null) {
                    fbJSON.put("COVER_IMAGE_DRAWABLE", nativeAd.getAdCoverImage());
                    fbJSON.put("SCREENSHOT_DRAWABLE", nativeAd.getAdCoverImage());
                }
                if (nativeAd.getAdChoicesIcon() != null) {
                    fbJSON.put("AD_CHOICE_ICON_DRAWABLE", nativeAd.getAdChoicesIcon());
                }

                Object[] objArray = new Object[]{fbJSON};
                mNativeAdListener.onAdLoaded(objArray);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (Exception e) {
        }
    }

    /* (non-Javadoc)
     * @see com.facebook.com.vmax.android.ads.AdListener#onError(com.facebook.com.vmax.android.ads.Ad, com.facebook.com.vmax.android.ads.AdError)
     */
    @Override
    public void onError(final Ad ad, final AdError error) {
        if (error != null) {
            if (LOGS_ENABLED) {
                Log.d("vmax", "Facebook native ad failed to load. error: " + error.getErrorCode());
            }
            mNativeAdListener.onAdFailed(error.getErrorMessage());
        } else {
            mNativeAdListener.onAdFailed("No ad in inventory");
        }
    }


    /* (non-Javadoc)
     * @see com.vmax.android.ads.mediation.partners.VmaxCustomAd#showAd()
     */
    @Override
    public void showAd() {
    }

    /* (non-Javadoc)
     * @see com.vmax.android.ads.mediation.partners.VmaxCustomAd#onInvalidate()
     */
    @Override
    public void onInvalidate() {
        if (LOGS_ENABLED) {
            Log.i("vmax", "onInvalidate fb native : ");
        }
        try {
            if (nativeAd != null) {
                nativeAd.unregisterView();
                nativeAd.setAdListener(null);
                nativeAd.destroy();
                nativeAd = null;
                if (LOGS_ENABLED) {
                    Log.i("vmax", "onInvalidate fb native clear : ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean extrasAreValid(final Map<String, Object> serverExtras) {
        final String placementId = serverExtras.get(PLACEMENT_ID_KEY)
                .toString();
        return (placementId != null && placementId.length() > 0);
    }


    public void handleImpression(ViewGroup viewgroup, View view, List<View> listOfView) {
        if (LOGS_ENABLED) {
            Log.i("vmax", "handleImpressions fb: ");
        }
        if (nativeAd != null) {
            if (listOfView != null) {
                if (LOGS_ENABLED) {
                    Log.i("vmax", " registerViewForInteraction with list of views: " + listOfView.size());
                }
                nativeAd.registerViewForInteraction(view, listOfView);
            } else if (view != null) {
                if (LOGS_ENABLED) {
                    Log.i("vmax", " registerViewForInteraction with only view: ");
                }
                nativeAd.registerViewForInteraction(view);
            }
        }
    }


    private Double getDoubleRating(final Rating rating) {
        if (rating == null) {
            return null;
        }
        return rating.getValue() / rating.getScale();
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onDestroy() {

    }

}
