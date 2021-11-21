package com.kingleader.cashybird;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.vungle.warren.AdConfig;
import com.vungle.warren.Banners;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.VungleBanner;
import com.vungle.warren.error.VungleException;

import java.util.Timer;

public class GameActivity extends Activity implements MaxAdViewAdListener {

    GameView gameView;
    //    private AdView adView;
    AdsAuthorization adsAuthorization;
    Timer timer;
    LinearLayout adContainer;
    //    AdView adView;
    com.google.android.gms.ads.AdView adView;
    private com.facebook.ads.AdView fbAdView;
    private VungleBanner vungleBanner;
    private MaxAdView maxAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        AppConstants.gameActivityContext = this;

        /*
        mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        // Replace this with your ad unit Id
        mAdView.setAdUnitId(getResources().getString(R.string.admob_banner2));
        // Create an ad request.
        AdRequest adRequest = new AdRequest.Builder().build();
        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
        */

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);



        loadFbBannerAd();

        //loadAdmobBanner();


        RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        gameView = new GameView(this);
        RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(gameView);

        if (Ads.adNetwork.equals("fb")) {
            AudienceNetworkAds.initialize(this);
            loadFbBannerAd();
            layout.addView(fbAdView, adParams);
        } else if (Ads.adNetwork.equals("admob")) {
            loadAdmobBanner();
            layout.addView(maxAdView);
        }else if(Ads.adNetwork.equals("vungle")){
            loadVungleBanner();
            if(Banners.canPlayAd(Ads.VungleBannerId,AdConfig.AdSize.BANNER)){
                vungleBanner = Banners.getBanner(Ads.VungleBannerId, AdConfig.AdSize.BANNER,null);
                //bungleBannerContainer.addView(vungleBanner);
                layout.addView(vungleBanner, adParams);
            }
        }


        setContentView(layout);
    }

    private void loadVungleBanner() {
        //LinearLayout bungleBannerContainer = findViewById(R.id.vungle_banner_container);

        Banners.loadBanner(Ads.VungleBannerId, AdConfig.AdSize.BANNER, new LoadAdCallback() {
            @Override
            public void onAdLoad(String id) {

            }

            @Override
            public void onError(String id, VungleException exception) {

            }
        });

    }


    private void loadAdmobBanner() {

        maxAdView = new MaxAdView( Ads.admobBannerId, this );
        maxAdView.setListener(this);

        // Stretch to the width of the screen for banners to be fully functional
        int width = LinearLayout.LayoutParams.MATCH_PARENT;

        // Banner height on phones and tablets is 50 and 90, respectively
        int heightPx = getResources().getDimensionPixelSize( R.dimen.banner_height );

        maxAdView.setLayoutParams(new LinearLayout.LayoutParams( width, heightPx ) );

        // Set background or background color for banners to be fully functional
        maxAdView.setBackgroundColor(getResources().getColor(R.color.colorWhite));

//        LinearLayout bannerContainer = findViewById(R.id.adViewMain);
//        bannerContainer.addView(maxAdView);

        // Load the ad
        maxAdView.loadAd();

//        com.google.android.gms.ads.MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
//
//            }
//        });
//
//        adView = new AdView(this);
//        adView.setAdSize(AdSize.BANNER);
//        adView.setAdUnitId(Ads.admobBannerId);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);
//
//        adView.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
//                AdRequest adRequest = new AdRequest.Builder().build();
//                adView.loadAd(adRequest);
//                super.onAdFailedToLoad(loadAdError);
//            }
//
//            @Override
//            public void onAdOpened() {
//                super.onAdOpened();
//            }
//
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//            }
//        });

    }


    private void loadFbBannerAd() {
        fbAdView = new com.facebook.ads.AdView(this, Ads.fbBannerId, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
       /* // Find the Ad Container
        adContainer = findViewById(R.id.banner_container);
        // Add the ad view to your activity layout
        adContainer.addView(adView);*/
        // Request an ad
        fbAdView.loadAd();
        fbAdView.buildLoadAdConfig().withAdListener(new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                try {
                    fbAdView.loadAd();
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        }).build();

    }


    @Override
    protected void onDestroy()
    {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onAdExpanded(MaxAd ad) {

    }

    @Override
    public void onAdCollapsed(MaxAd ad) {

    }

    @Override
    public void onAdLoaded(MaxAd ad) {

    }

    @Override
    public void onAdDisplayed(MaxAd ad) {

    }

    @Override
    public void onAdHidden(MaxAd ad) {

    }

    @Override
    public void onAdClicked(MaxAd ad) {

    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {

    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {

    }
}
