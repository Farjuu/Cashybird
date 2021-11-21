package com.kingleader.cashybird;

import android.app.Application;

import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.MobileAds;

public class myApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this);
        AppLovinSdk.getInstance( this ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( this, configuration -> {
            // AppLovin SDK is initialized, start loading ads
        });
    }
}
