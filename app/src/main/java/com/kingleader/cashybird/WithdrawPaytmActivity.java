package com.kingleader.cashybird;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

public class WithdrawPaytmActivity extends Activity implements MaxAdViewAdListener, MaxAdListener
{

    private static int ELIGIBLE_LIMIT = 1000;
    Button withdrawCheckButton;
    Button okayButtonNoCoins;
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
    ImageView ivPaymentImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.withdraw_paytm);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        totalCoinsTextView= findViewById(R.id.totalCoinsTextView);
        ivPaymentImg = findViewById(R.id.iv_payment_img);
        withdrawCheckButton = findViewById(R.id.withdrawCheckButton);
        noCoinsDialog = new Dialog(this);
        adsAuthorization= new AdsAuthorization(this);

        withdrawOption1 = findViewById(R.id.withdrawOption1);
        withdrawOption2 = findViewById(R.id.withdrawOption2);
        withdrawOption3 = findViewById(R.id.withdrawOption3);
        withdrawOption4 = findViewById(R.id.withdrawOption4);
        withdrawOption5 = findViewById(R.id.withdrawOption5);

        withdrawOption1_value = findViewById(R.id.withdrawOption1_value);
        withdrawOption2_value = findViewById(R.id.withdrawOption2_value);
        withdrawOption3_value = findViewById(R.id.withdrawOption3_value);
        withdrawOption4_value = findViewById(R.id.withdrawOption4_value);
        withdrawOption5_value = findViewById(R.id.withdrawOption5_value);

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);

        //  numberFormat = NumberFormat.getNumberInstance();
        // numberFormat.setMinimumFractionDigits(2);
        //  numberFormat.setMaximumFractionDigits(2);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        dialog = new ProgressDialog(WithdrawPaytmActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);


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
                            updateWithdrawOptions();
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
        WithdrawPaytmActivity.totalEarnedCoins = totalCoins;
    }

    private void updateWithdrawOptions()
    {
        firebaseFirestore.collection("admin")
                .document("withdraw_options")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            String option1 = task.getResult().getString("option1");
                            String option2 = task.getResult().getString("option2");
                            String option3 = task.getResult().getString("option3");
                            String option4 = task.getResult().getString("option4");
                            String option5 = task.getResult().getString("option5");

                            String option1_value = task.getResult().getString("option1_value");
                            String option2_value = task.getResult().getString("option2_value");
                            String option3_value = task.getResult().getString("option3_value");
                            String option4_value = task.getResult().getString("option4_value");
                            String option5_value = task.getResult().getString("option5_value");

                            withdrawOption1.setText(option1.concat(" coins =>"));
                            withdrawOption2.setText(option2.concat(" coins =>"));
                            withdrawOption3.setText(option3.concat(" coins =>"));
                            withdrawOption4.setText(option4.concat(" coins =>"));
                            withdrawOption5.setText(option5.concat(" coins =>"));

                            withdrawOption1_value.setText(option1_value.concat("$ USD"));
                            withdrawOption2_value.setText(option2_value.concat("$ USD"));
                            withdrawOption3_value.setText(option3_value.concat("$ USD"));
                            withdrawOption4_value.setText(option4_value.concat("$ USD"));
                            withdrawOption5_value.setText(option5_value.concat("$ USD"));
                            updateEligibleLimit();
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

    private void updateEligibleLimit()
    {
        firebaseFirestore.collection("admin")
                .document("withdraw_eligibility")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            String eligible_limit = task.getResult().getString("limit");
                            updateStaticVariable(eligible_limit);
                            dialog.dismiss();
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

    private void updateStaticVariable(String eligible_limit)
    {
        ELIGIBLE_LIMIT = Integer.parseInt(eligible_limit);

        // Toast.makeText(getApplicationContext(),""+ELIGIBLE_LIMIT,Toast.LENGTH_SHORT).show();
    }

    public void checkIsWithdrawEligible(View view)
    {
        double totalCoins = totalEarnedCoins;
        if(totalCoins>=ELIGIBLE_LIMIT)
        {

            Intent intent = new Intent(this, WithdrawPaytmInitActivity.class);
            intent.putExtra("available_coins",totalCoins);
            startActivity(intent);
        }
        else
        {
            //Show Dialog (Not Enough Coins to Cash Out)
            noCoinsDialog.setContentView(R.layout.popup_nocoins_towithdraw);
            okayButtonNoCoins = noCoinsDialog.findViewById(R.id.okayButtonNoCoins);
            okayButtonNoCoins.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    noCoinsDialog.dismiss();
                }
            });
            noCoinsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            noCoinsDialog.setCancelable(false);
            noCoinsDialog.show();
            //Toast.makeText(this,"No Enough Coins To Cash Out.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy()
    {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        WithdrawPaytmActivity.this.finish();
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
