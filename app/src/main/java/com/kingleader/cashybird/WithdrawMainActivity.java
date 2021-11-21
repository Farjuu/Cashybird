package com.kingleader.cashybird;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class WithdrawMainActivity extends Activity implements MaxAdViewAdListener, MaxAdListener
{

    private static int ELIGIBLE_LIMIT = 1000;
    CardView cvPaypal,cvPaytm,cv_bitcoin,cv_litacoin;
    TextView totalCoinsTextView;
    static double totalEarnedCoins;
    private Timer timer;
    AdsAuthorization adsAuthorization;

    ProgressDialog dialog;
    Dialog noCoinsDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    //NumberFormat numberFormat;
    TextView withdrawOption1;
    TextView withdrawOption2;
    TextView withdrawOption3;
    TextView withdrawOption4;
    TextView withdrawOption5;

    TextView withdrawOption1_value;
    TextView withdrawOption2_value;
    TextView withdrawOption3_value;
    TextView withdrawOption4_value;
    TextView withdrawOption5_value;
    private InterstitialAd interstitialAd;
    private com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    private MaxInterstitialAd maxInterstitialAd;
    private int retryAttempt;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        totalCoinsTextView= findViewById(R.id.totalCoinsTextView);
        cvPaypal = findViewById(R.id.cv_paypal);
        cvPaytm = findViewById(R.id.cv_paytm);
        cv_bitcoin = findViewById(R.id.cv_bitcoin);
        cv_litacoin = findViewById(R.id.cv_litacoin);
        noCoinsDialog = new Dialog(this);
        adsAuthorization= new AdsAuthorization(this);

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        dialog = new ProgressDialog(WithdrawMainActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        cvPaypal.setOnClickListener(v -> {
            Intent intent = new Intent(this, WithdrawPayPalActivity.class);
            startActivity(intent);
        });
        cvPaytm.setOnClickListener(v -> {
            Intent intent = new Intent(this, WithdrawPaytmActivity.class);
            startActivity(intent);
        });
        //load interstitial ad here
//        interstitialAd = new InterstitialAd(this, getResources().getString(R.string.facebook_InterstitialWithdraw));
//
//        interstitialAd.buildLoadAdConfig().withAdListener(new InterstitialAdListener() {
//            @Override
//            public void onInterstitialDisplayed(Ad ad) {
//                // Interstitial ad displayed callback
//                Log.e(TAG, "Interstitial ad displayed.");
//            }
//
//            @Override
//            public void onInterstitialDismissed(Ad ad) {
//                // Interstitial dismissed callback
//                Log.e(TAG, "Interstitial ad dismissed.");
//            }
//
//            @Override
//            public void onError(Ad ad, AdError adError) {
//                // Ad error callback
//                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//                // Interstitial ad is loaded and ready to be displayed
//                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
//                // Show the ad
//                // interstitialAd.show();
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//                // Ad clicked callback
//                Log.d(TAG, "Interstitial ad clicked!");
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//                // Ad impression logged callback
//                Log.d(TAG, "Interstitial ad impression logged!");
//            }
//        }).build();
//
//        // For auto play video ads, it's recommended to load the ad
//        // at least 30 seconds before it is shown
//        interstitialAd.loadAd();
        admobInterstital();
        AdsAuthorizationTimer();
        updateEarnedCoins();

    }

    private void admobInterstital() {
        if (Ads.adNetwork.equals("admob")) {
            maxInterstitialAd = new MaxInterstitialAd(Ads.admobInterstitialId, this);
            maxInterstitialAd.setListener(this);
            maxInterstitialAd.loadAd();
        }
//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        com.google.android.gms.ads.interstitial.InterstitialAd.load(this,getResources().getString(R.string.admob_interstitial), adRequest, new InterstitialAdLoadCallback() {
//            @Override
//            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
//                mInterstitialAd = interstitialAd;
//                fullScreenContentCallback();
//                mInterstitialAd.show(WithdrawActivity.this);
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                Log.i(TAG, loadAdError.getMessage());
//                mInterstitialAd = null;
//            }
//        });
    }
    private void fullScreenContentCallback() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
            @Override
            public void onAdDismissedFullScreenContent() {
//                admobInterstital();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {

            }

            @Override
            public void onAdShowedFullScreenContent() {
                mInterstitialAd = null;
            }
        });
    }
    private void AdsAuthorizationTimer()
    {
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AdsSetVisibility();
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                handler.post(runnable);
            }
        }, 3000);

    }
    private void AdsSetVisibility()
    {
        if(interstitialAd == null || !interstitialAd.isAdLoaded()) {
            return;
        }
        // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
        if(interstitialAd.isAdInvalidated()) {
            return;
        }
        // Show the ad
        interstitialAd.show();

    }


    private void updateEarnedCoins()
    {
        dialog.show();
        firebaseFirestore.collection("users")
                .document(firebaseUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            String totalCoins = task.getResult().getData().get("user_earned_coins").toString();
                            //String EarnedCoins_ = numberFormat.format(Float.parseFloat(TotalCoins));
                            totalCoinsTextView.setText(String.format("%.2f",Double.parseDouble(totalCoins)));
                            updateTotalEarnedCoins(Double.parseDouble(totalCoins));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Failed To Fetch Details, Try Again With Good Network",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateTotalEarnedCoins(double totalCoins)
    {
        WithdrawMainActivity.totalEarnedCoins = totalCoins;
    }

    @Override
    protected void onDestroy()
    {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        WithdrawMainActivity.this.finish();
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
        Log.e("MaxAds","Loaded");
        retryAttempt = 0;
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {

    }

    @Override
    public void onAdHidden(MaxAd ad) {
        maxInterstitialAd.loadAd();
    }

    @Override
    public void onAdClicked(MaxAd ad) {

    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        Log.e("Error",error.getMessage());
        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                maxInterstitialAd.loadAd();
            }
        }, delayMillis );
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        Log.e("Error->",error.getMessage());
        maxInterstitialAd.loadAd();
    }
}
