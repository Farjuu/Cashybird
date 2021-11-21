package com.kingleader.cashybird;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.vungle.warren.AdConfig;
import com.vungle.warren.Banners;
import com.vungle.warren.InitCallback;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.VungleBanner;
import com.vungle.warren.error.VungleException;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.facebook.ads.AudienceNetworkAds.TAG;

//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;

public class GameOver extends Activity implements MaxAdViewAdListener {

    private AdView adView;
    private InterstitialAd interstitialAd;
    TextView tvScore, tvPersonalBest, earnedCoinsHeadingTextView, earnedCoinsTextView;
    int score;
    float earnedCoins;
    double EarnedCoins_ = 0;
    private Timer timer;
    boolean retry_enable_Ads;
    boolean firebaseSaved_retry_enable;
    AsyncTaskRunner asyncTaskRunner;
    //NumberFormat numberFormat;
    Dialog noInternetDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private AdsAuthorization adsAuthorization;
    static int scoreLimit = 8; //if score below this then interstitial ad won't show
    LinearLayout adContainer;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private HashMap<String, Object> firebaseDefaults;
    private final String EARNEED_COINS_HEADING_TEXT = "no_earned_coins_heading";
    private com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    private VungleBanner vungleBanner;
    private MaxAdView maxAdView;
    private RelativeLayout rlGameOver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameover);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        retry_enable_Ads = false;
        firebaseSaved_retry_enable = false;

        adsAuthorization = new AdsAuthorization(this);

        asyncTaskRunner = new AsyncTaskRunner();



        if (Ads.adNetwork.equals("fb")) {
            AudienceNetworkAds.initialize(this);
            loadFbBannerAd();
            //loadFbInterstitial();
        } else if (Ads.adNetwork.equals("admob")) {
            loadAdmobBannerAd();
            //admobInterstital();
        }else if(Ads.adNetwork.equals("vungle")){
            initializeVungelSDK();
            loadVungleBanner();
            //loadVungleInterstitial();
        }



        noInternetDialog = new Dialog(this);

       /* if (!adsAuthorization.isBannerAdsEnabled()) {
            adView.setVisibility(View.GONE);
        }*/


        score = getIntent().getExtras().getInt("score");
//        AdsHandlerInitiate();
        SharedPreferences pref = getSharedPreferences("MyPref", 0);
        int scoreSP = pref.getInt("scoreSP", 0);
        SharedPreferences.Editor editor = pref.edit();
        if (score > scoreSP) {
            scoreSP = score;
            editor.putInt("scoreSP", scoreSP);
            editor.commit();
        }


        calculateEarnedCoins(score);

        tvScore = findViewById(R.id.tvScore);
        tvPersonalBest = findViewById(R.id.tvPersonalBest);
        // earnedCoinsHeadingTextView = findViewById(R.id.earnedCoinsHeadingTextView);
        // earnedCoinsTextView = findViewById(R.id.earnedCoinsTextView);
        rlGameOver = findViewById(R.id.rl_game_over1);

        // rlGameOver.setBackgroundResource(AppConstants.get());

        tvScore.setText("" + score);
        tvPersonalBest.setText("" + scoreSP);
       // earnedCoinsTextView.setText(String.format("%.2f", EarnedCoins_));

        if (score != 0)
        {
            if (score > 5) {
                if (haveNetworkConnection())
                {
                    saveScoreAndCoins(scoreSP, EarnedCoins_);
                    //earnedCoinsHeadingTextView.setText("Earned Coins");
                }
                else {
                    displayCoinsNotSavedDialog();
                    // earnedCoinsHeadingTextView.setVisibility(View.GONE);
                    // earnedCoinsTextView.setVisibility(View.GONE);
                }
            }
            else // score <= 5
            {
                //earnedCoinsHeadingTextView.setTextSize(12);
                // earnedCoinsHeadingTextView.setPadding(1, 5, 1, 5);
                // earnedCoinsHeadingTextView.setText(" Coins get counted when score >5 ");
                //earnedCoinsHeadingTextView.setVisibility(View.GONE);
                // earnedCoinsTextView.setVisibility(View.GONE);
            }
        }
        else // score == 0
        {/*
            earnedCoinsHeadingTextView.setVisibility(View.GONE);
            earnedCoinsTextView.setVisibility(View.GONE);*/
        }
    }

    private void remoteConfigCheck()
    {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseDefaults = new HashMap<>();
        firebaseDefaults.put(EARNEED_COINS_HEADING_TEXT, " Coins get counted when score >5 ");

        firebaseRemoteConfig.setDefaultsAsync(firebaseDefaults);
        firebaseRemoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600).build());

        firebaseRemoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseRemoteConfig.activate();
                    // checkForUpdateParameters();
                }
            }
        });
    }

    /*private void checkForUpdateParameters()
    {
        String text = firebaseRemoteConfig.getString(EARNEED_COINS_HEADING_TEXT);
        if (score <= 5)
            earnedCoinsHeadingTextView.setText(text);
        else
            earnedCoinsHeadingTextView.setText("Earned Coins");
    }*/

    private void displayCoinsNotSavedDialog() {
        noInternetDialog.setContentView(R.layout.popup_nointernet_nocoins_saved);
        Button okayButton = noInternetDialog.findViewById(R.id.okayButton);
        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noInternetDialog.dismiss();
            }
        });
        noInternetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        noInternetDialog.setCancelable(false);
        noInternetDialog.show();
        noInternetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Toast.makeText(getApplicationContext(), "Turn On Internet Service", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void calculateEarnedCoins(int score){
        if(score<50) {
            earnedCoins = (float) score / 100;
            EarnedCoins_ = earnedCoins;
        } else if(score == 50){
            earnedCoins = 1f;
            EarnedCoins_ = earnedCoins;
        }else if(score<=100){
            float score_50 = 1f; //3.125
            float temp_holder_50_100 = score - 50;
            earnedCoins = score_50 + (temp_holder_50_100/100);
            EarnedCoins_ = earnedCoins;
        }else{
            float score_100 = 2f; //3.125
            float temp_holder_above100 = score - 100;
            earnedCoins = score_100 + (temp_holder_above100/100);
            EarnedCoins_ = earnedCoins;
        }
    }
//    private void calculateEarnedCoins(int score) {
//        if (score <= 5) {
//            earnedCoins = 0;
//            EarnedCoins_ = earnedCoins;
//        } else if (score <= 50) {
//            earnedCoins = (float) score / 16;
//            EarnedCoins_ = earnedCoins;
//        } else if (score <= 100) {
//            float score_till_50 = (float) 50 / 16; //3.125
//
//            float temp_holder_50_100 = score - 50;
//            temp_holder_50_100 = temp_holder_50_100 / 8;
//
//            earnedCoins = score_till_50 + temp_holder_50_100;
//
//            EarnedCoins_ = earnedCoins;
//
//        } else {
//            float score_till_50 = (float) 50 / 16; //3.125
//            float score_till_50_100 = (float) 50 / 8; //6.25
//
//            float temp_holder_above100_score = score - 100;
//            temp_holder_above100_score = temp_holder_above100_score / 4;
//
//            earnedCoins = score_till_50 + score_till_50_100 + temp_holder_above100_score;
//
//            EarnedCoins_ = earnedCoins;
//
//        }
//    }

    private void saveScoreAndCoins(int personalBest, double earnedCoins) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        asyncTaskRunner.execute(Integer.toString(personalBest), Double.toString(earnedCoins));

    }

    private void AdsHandlerInitiate() {
        final Handler handler = new Handler(Looper.getMainLooper());

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                displayInterstitialAd(score);
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                handler.post(runnable);
            }
        }, 2000);

    }

    public void displayInterstitialAd(int score)
    {
        if (adsAuthorization.getScore_limit() != 0)
        {
            scoreLimit = adsAuthorization.getScore_limit();
        }
        if (score >= scoreLimit)
        {
            retry_enable_Ads = true;

            if (adsAuthorization.isInterstitialAdsEnabled())
            {

               /* if (Ads.adNetwork.equals("fb")) {
                    if(interstitialAd !=null && interstitialAd.isAdLoaded()){
                        interstitialAd.show();
                    }
                } else if (Ads.adNetwork.equals("admob")) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(GameOver.this);
                    }
                }else if(Ads.adNetwork.equals("vungle")){
                    if(Vungle.canPlayAd(Ads.VungleInterstitialId)){
                        Vungle.playAd(Ads.VungleInterstitialId, null, new PlayAdCallback() {
                            @Override
                            public void onAdStart(String id) {

                            }

                            @Override
                            public void onAdEnd(String id, boolean completed, boolean isCTAClicked) {

                            }

                            @Override
                            public void onAdEnd(String id) {

                            }


                            @Override
                            public void onAdClick(String id) {

                            }

                            @Override
                            public void onAdRewarded(String id) {

                            }

                            @Override
                            public void onAdLeftApplication(String id) {

                            }

                            @Override public void onError(String placementReferenceId, VungleException exception) { }

                            @Override
                            public void onAdViewed(String id) {

                            }
                        });
                    }
                }*/

            } else {
//                retry_enable_Ads = true;
            }
        }
       /* if (!adsAuthorization.isBannerAdsEnabled())
        {
            adView.setVisibility(View.GONE);
        }
        else {
            adView.loadAd();
        }*/
    }

    private void loadVungleInterstitial() {

        // Load Ad Implementation
        if (Vungle.isInitialized()) {
            Vungle.loadAd(Ads.VungleInterstitialId, new LoadAdCallback() {
                @Override
                public void onAdLoad(String placementReferenceId) {

                }

                @Override
                public void onError(String placementReferenceId, VungleException exception) { }
            });
        }


    }

    private void loadVungleBanner() {
        LinearLayout bungleBannerContainer = findViewById(R.id.vungle_banner_container);

        Banners.loadBanner(Ads.VungleBannerId, AdConfig.AdSize.BANNER, new LoadAdCallback() {
            @Override
            public void onAdLoad(String id) {
                if(Banners.canPlayAd(Ads.VungleBannerId,AdConfig.AdSize.BANNER)){
                    vungleBanner = Banners.getBanner(Ads.VungleBannerId, AdConfig.AdSize.BANNER,null);
                    bungleBannerContainer.addView(vungleBanner);
                }
            }

            @Override
            public void onError(String id, VungleException exception) {

            }
        });

    }

    private void initializeVungelSDK() {
        Vungle.init(Ads.vungleAppId, getApplicationContext(), new InitCallback() {
            @Override
            public void onSuccess() {
                // SDK has successfully initialized
            }

            @Override
            public void onError(VungleException exception) {
                // SDK has failed to initialize
            }

            @Override
            public void onAutoCacheAdAvailable(String placementId) {
                // Ad has become available to play for a cache optimized placement
            }
        });
    }


    private void loadFbInterstitial() {
        //load interstitial ad here
        interstitialAd = new InterstitialAd(this, Ads.fbInterstitialId);

        interstitialAd.buildLoadAdConfig().withAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        }).build();

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();
    }

    private void admobInterstital() {
        AdRequest adRequest = new AdRequest.Builder().build();

        com.google.android.gms.ads.interstitial.InterstitialAd.load(this, getResources().getString(R.string.admob_interstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                fullScreenContentCallback();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
                retry_enable_Ads = true;
            }
        });
    }

    private void fullScreenContentCallback() {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
//                admobInterstital();
                retry_enable_Ads = true;
            }

            @Override
            public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                retry_enable_Ads = true;
            }

            @Override
            public void onAdShowedFullScreenContent() {
                mInterstitialAd = null;
            }
        });
    }

    public void home(View view) {
        Intent intent = new Intent(GameOver.this, MainActivity.class);
        startActivity(intent);
        GameOver.this.finish();
    }

    public void retry(View view) {
        if (firebaseSaved_retry_enable) // if Internet Connected and coins save successful
        {
//            if (score < scoreLimit) {
            int bg = AppConstants.getBg();
            AppConstants.initialization(this.getApplicationContext(), bg);
            Intent intent = new Intent(GameOver.this, GameActivity.class);
            startActivity(intent);
            GameOver.this.finish();

               /* if (Ads.adNetwork.equals("fb")) {
                    if(interstitialAd !=null && interstitialAd.isAdLoaded()){
                        interstitialAd.show();
                    }
                } else if (Ads.adNetwork.equals("admob")) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(GameOver.this);
                    }
                }else if(Ads.adNetwork.equals("vungle")){
                    if(Vungle.canPlayAd(Ads.VungleInterstitialId)){
                        Vungle.playAd(Ads.VungleInterstitialId, null, new PlayAdCallback() {
                            @Override
                            public void onAdStart(String id) {

                            }

                            @Override
                            public void onAdEnd(String id, boolean completed, boolean isCTAClicked) {

                            }

                            @Override
                            public void onAdEnd(String id) {

                            }


                            @Override
                            public void onAdClick(String id) {

                            }

                            @Override
                            public void onAdRewarded(String id) {

                            }

                            @Override
                            public void onAdLeftApplication(String id) {

                            }

                            @Override public void onError(String placementReferenceId, VungleException exception) { }

                            @Override
                            public void onAdViewed(String id) {

                            }
                        });
                    }
                }*/

//            } else if (retry_enable_Ads) {
//                int bg = AppConstants.getBg();
//                AppConstants.initialization(this.getApplicationContext(), bg);
//                Intent intent = new Intent(GameOver.this, GameActivity.class);
//                startActivity(intent);
//                GameOver.this.finish();
                /*if (Ads.adNetwork.equals("fb")) {
                    if(interstitialAd !=null && interstitialAd.isAdLoaded()){
                        interstitialAd.show();
                    }
                } else if (Ads.adNetwork.equals("admob")) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(GameOver.this);
                    }
                }else if(Ads.adNetwork.equals("vungle")){
                    if(Vungle.canPlayAd(Ads.VungleInterstitialId)){
                        Vungle.playAd(Ads.VungleInterstitialId, null, new PlayAdCallback() {
                            @Override
                            public void onAdStart(String id) {

                            }

                            @Override
                            public void onAdEnd(String id, boolean completed, boolean isCTAClicked) {

                            }

                            @Override
                            public void onAdEnd(String id) {

                            }


                            @Override
                            public void onAdClick(String id) {

                            }

                            @Override
                            public void onAdRewarded(String id) {

                            }

                            @Override
                            public void onAdLeftApplication(String id) {

                            }

                            @Override public void onError(String placementReferenceId, VungleException exception) { }

                            @Override
                            public void onAdViewed(String id) {

                            }
                        });
                    }
                }*/

//            }
        }
//        else if (score < scoreLimit || retry_enable_Ads) // If No Internet
        else{
            int bg = AppConstants.getBg();
            AppConstants.initialization(this.getApplicationContext(), bg);
            Intent intent = new Intent(GameOver.this, GameActivity.class);
            startActivity(intent);
            GameOver.this.finish();

           /* if (Ads.adNetwork.equals("fb")) {
                if(interstitialAd !=null && interstitialAd.isAdLoaded()){
                    interstitialAd.show();
                }
            } else if (Ads.adNetwork.equals("admob")) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(GameOver.this);
                }
            }else if(Ads.adNetwork.equals("vungle")){
                if(Vungle.canPlayAd(Ads.VungleInterstitialId)){
                    Vungle.playAd(Ads.VungleInterstitialId, null, new PlayAdCallback() {
                        @Override
                        public void onAdStart(String id) {

                        }

                        @Override
                        public void onAdEnd(String id, boolean completed, boolean isCTAClicked) {

                        }

                        @Override
                        public void onAdEnd(String id) {

                        }


                        @Override
                        public void onAdClick(String id) {

                        }

                        @Override
                        public void onAdRewarded(String id) {

                        }

                        @Override
                        public void onAdLeftApplication(String id) {

                        }

                        @Override public void onError(String placementReferenceId, VungleException exception) { }

                        @Override
                        public void onAdViewed(String id) {

                        }
                    });
                }
            }*/

        }

    }

    public void exit(View view) {

/*        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
 */
        finish();
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

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            //publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                final int personalBest = Integer.parseInt(params[0]);
                final double EarnedCoins = Double.parseDouble(params[1]);

                firebaseFirestore.collection("users")
                        .document(firebaseUser.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    // int fireBasePersonal = Integer.parseInt(task.getResult().getString("user_personal_best"));
//                                    double fireBaseEarnedCoins = task.getResult().getDouble("user_earned_coins");
                                    String fireBaseEarnedCoins = task.getResult().getData().get("user_earned_coins").toString();

                                    double totalEarned = Double.parseDouble(fireBaseEarnedCoins) + EarnedCoins;

                                    final Map<String, Object> userProfile_update = new HashMap<>();
                                    userProfile_update.put("user_personal_best", Integer.toString(personalBest));
                                    userProfile_update.put("user_earned_coins", Double.valueOf(totalEarned));

                                    firebaseFirestore.collection("users")
                                            .document(firebaseUser.getUid())
                                            .update(userProfile_update).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                resp = "success";
                                            }
                                        }
                                    });

                                }
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();
            firebaseSaved_retry_enable = true;
            // Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
            // finalResult.setText(result);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(GameOver.this);
            progressDialog.setMessage("Processing...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... text) {
            //finalResult.setText(text[0]);
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    protected void onResume() {
        remoteConfigCheck();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("MissingPermission")
    private void loadAdmobBannerAd() {

        maxAdView = new MaxAdView( Ads.admobBannerId, this );
        maxAdView.setListener(this);

        // Stretch to the width of the screen for banners to be fully functional
        int width = ViewGroup.LayoutParams.MATCH_PARENT;

        // Banner height on phones and tablets is 50 and 90, respectively
        int heightPx = getResources().getDimensionPixelSize( R.dimen.banner_height );

        maxAdView.setLayoutParams( new FrameLayout.LayoutParams( width, heightPx ) );

        // Set background or background color for banners to be fully functional
        maxAdView.setBackgroundColor(getResources().getColor(R.color.colorWhite));

        LinearLayout bannerContainer = findViewById(R.id.adViewMain);
        bannerContainer.addView(maxAdView);

        // Load the ad
        maxAdView.loadAd();

//        LinearLayout bannerContainer = findViewById(R.id.adViewMain);
//        com.google.android.gms.ads.AdView adView = new com.google.android.gms.ads.AdView(GameOver.this);
//        adView.setAdUnitId(Ads.admobBannerId);
//        adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
//
//        AdRequest adRequest = new AdRequest.Builder().build();
//        bannerContainer.addView(adView);
//        adView.loadAd(adRequest);


    }


    private void loadFbBannerAd() {
        adView = new AdView(this, Ads.fbBannerId, AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        adContainer = findViewById(R.id.banner_container);
        // Add the ad view to your activity layout
        adContainer.addView(adView);
        // Request an ad
        adView.loadAd();
        adView.buildLoadAdConfig().withAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                try {
                    adView.loadAd();
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

}
