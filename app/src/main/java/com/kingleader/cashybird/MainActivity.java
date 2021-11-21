package com.kingleader.cashybird;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAppOptions;
import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.vungle.warren.AdConfig;
import com.vungle.warren.Banners;
import com.vungle.warren.InitCallback;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.VungleBanner;
import com.vungle.warren.error.VungleException;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPairGenerator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements MaxAdViewAdListener {

    private static final int PERSMISSION_REQ = 123;
    private final String LATEST_APP_VERSION_CODE = "latest_version_code";
    private final String REWARD_VIDEO_LIMIT = "reward_video_daily_limit";
    private final String TAG = "REWARDED_VIDEO_TAG";
    ImageView imageBird, rewardVideoImage;
    int[] bird, rewardVdImg;
    int currentFrame = 0, currentFrameRewardVDImg = 0;
    Dialog noInternetDialog, rewardedVideoDialog, coinsAddSuccessDialog;
    Button itsOkayButton;
    Button turnOnButton;
    Button noInternetTurnOnButtonWithdraw;
    Button closeButton;
    Button watchNowButton;
    TextView RVCurrentCountTextView;
    //AdsAuthorization adsAuthorization;
    LinearLayout adContainer;
    ProgressDialog dialog;
    int rewardVdCurrentCount = 0;
    int rewardVdLimit_RemoteConfig = 5; // update this variable from firebase remote configuration
    RewardedAd mRewardedAd;
    private AdView adView;
    private Timer timer;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private HashMap<String, Object> firebaseDefaults;
    private RewardedVideoAd rewardedVideoAd;
    private KeyPairGenerator MobileAds;
    private InterstitialAd mInterstitialAd;
    private com.facebook.ads.InterstitialAd interstitialAd;
    VungleBanner vungleBanner;
    private MaxAdView maxAdView;
    private MaxInterstitialAd maxInterstitialAd;
    private int retryAttempt;
    private MaxRewardedAd rewardedAd;
    private int retryRewardAttempt;
    RelativeLayout rlMain;
    public static final String LEADERBOARD = "leaderBoard";
    public static final String WITHDRAW = "withdraw";
    public static final String GAME = "game";
    private String fbInterAdScreen = "";
    private String maxInterAdScreen = "";
    AlertDialog progressDialog;

    ImageView share, profile, withdrawbtn, ratebtn, leaderboardbtn,playbtn,cashybird_text;


    private static Uri getImageUri(Context context, View view, String fileName) throws IOException {
        Bitmap bitmap = loadBitmapFromView(view);
        File pictureFile = new File(context.getExternalCacheDir(), fileName);
        FileOutputStream fos = new FileOutputStream(pictureFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        fos.close();
        return Uri.parse("file://" + pictureFile.getAbsolutePath());
    }

    private static Bitmap loadBitmapFromView(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation Nurhima I love You
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            v.setDrawingCacheEnabled(true);
            return v.getDrawingCache();
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.MainAppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_act);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        progressDialog = AppConstants.getDialogProgressBar(MainActivity.this, getResources().getString(R.string.please_wait)).create();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        rlMain = findViewById(R.id.rl_main1);
        // rlMain.setBackgroundResource(AppConstants.get());

//        requestPermission();
        // adsAuthorization = new AdsAuthorization(this);

        imageBird=findViewById(R.id.imageBird);
        cashybird_text=findViewById(R.id.cashybird_text);
        share= findViewById(R.id.imageButton);
        profile= findViewById(R.id.imageView2);
        withdrawbtn= findViewById(R.id.withdraw_moneybag);
        ratebtn= findViewById(R.id.rating_btn);
        leaderboardbtn= findViewById(R.id.leaderboardbtn);
        playbtn= findViewById(R.id.imageButton2);


        noInternetDialog = new Dialog(this);
        rewardedVideoDialog = new Dialog(this);
        coinsAddSuccessDialog = new Dialog(this);

        getCurrentUserVariables();

        rewardVideoImage = findViewById(R.id.rewardVideoImage);

        /*bird = new int[8];
        bird[0] = R.drawable.bird_frame1;
        bird[1] = R.drawable.bird_frame2;
        bird[2] = R.drawable.bird_frame3;
        bird[3] = R.drawable.bird_frame4;
        bird[4] = R.drawable.bird_frame5;
        bird[5] = R.drawable.bird_frame6;
        bird[6] = R.drawable.bird_frame7;
        bird[7] = R.drawable.bird_frame8;

        rewardVdImg = new int[4];
        rewardVdImg[0] = R.drawable.c1;
        rewardVdImg[1] = R.drawable.c2;
        rewardVdImg[2] = R.drawable.c3;
        rewardVdImg[3] = R.drawable.c4;

        AnimationHandlerInitiate();*/
        saveUserRecentOnline();
        isFacebookInstalled();
        int bg = AppConstants.getBg();
        AppConstants.initialization(this.getApplicationContext(), bg);

        com.google.android.gms.ads.MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                for (String adapterClass : statusMap.keySet()) {
                    AdapterStatus status = statusMap.get(adapterClass);
                    Log.d("MyApp", String.format(
                            "Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass, status.getDescription(), status.getLatency()));
                }

                // Start loading ads here...

            }
        });

        // ideally in launcher activity. In our case MainActivity.
        // There's no need to call it twice during a single execution of the app,
        // or in every single activity.
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        // Replace this with your app ID

        //Toast.makeText(this,AppConstants.SCREEN_WIDTH + " : " +AppConstants.SCREEN_HEIGHT ,Toast.LENGTH_SHORT).show();

        OnstartPriority();

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Loading Video...");
        dialog.setCancelable(false);

//        loadBannerAd();
        initializeVungelSDK();
        getAdsDataFromFirebase();

    }

    private void requestPermission() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            showSettingDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Storage Permission");
        builder.setMessage("Permission");
        builder.setPositiveButton("Go to Settings", (dialog, which) -> {
            dialog.cancel();
            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
            i.setData(uri);
            startActivityForResult(i, PERSMISSION_REQ);
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PERSMISSION_REQ) {
                requestPermission();
            }
        }
    }

    private void getAdsDataFromFirebase() {

        //DatabaseReference rootRefrence = FirebaseDatabase.getInstance().getReference("ads");
        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("ads");
        //rootRefrence.setValue("Hello, World!");

        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

//                DataSnapshot dataSnapshot = snapshot.child("ads");
                Ads.adFlag = Objects.requireNonNull(dataSnapshot.child("adFlag").getValue()).toString();
                Ads.adNetwork = Objects.requireNonNull(dataSnapshot.child("adnetwork").getValue()).toString();

                Ads.admobBannerId = Objects.requireNonNull(dataSnapshot.child("admobBanner").getValue()).toString();
                Ads.admobInterstitialId = Objects.requireNonNull(dataSnapshot.child("admobInterstitial").getValue()).toString();
                Ads.admobRewardedId = Objects.requireNonNull(dataSnapshot.child("admobRewarded").getValue()).toString();

                Ads.fbBannerId = Objects.requireNonNull(dataSnapshot.child("fbBanner").getValue()).toString();
                Ads.fbInterstitialId = Objects.requireNonNull(dataSnapshot.child("fbInterstitial").getValue()).toString();
                Ads.fbRewardedId = Objects.requireNonNull(dataSnapshot.child("fbRewarded").getValue()).toString();

                Ads.vungleAppId = Objects.requireNonNull(dataSnapshot.child("vungleAppId").getValue()).toString();
                Ads.VungleBannerId = Objects.requireNonNull(dataSnapshot.child("vungleBanner").getValue()).toString();
                Ads.VungleInterstitialId = Objects.requireNonNull(dataSnapshot.child("vungleInterstitial").getValue()).toString();
                Ads.VungleRewardedId = Objects.requireNonNull(dataSnapshot.child("vungleRewarded").getValue()).toString();

//                Log.d("firebasedata", "onDataChange method called");
                if (Ads.adFlag.equals("0")) {
                    if (Ads.adNetwork.equals("admob")) {
                        admobInterstital(Ads.admobInterstitialId, maxInterAdScreen);
                        loadAdmobBannerAd(Ads.admobBannerId);
                        loadRewardedAd();
                    }
                } else if (Ads.adFlag.equals("1")) {
                    if (Ads.adNetwork.equals("fb")) {
                        AudienceNetworkAds.initialize(MainActivity.this);
                        loadFbBannerAd(Ads.fbBannerId);
                        loadFbInterstitial(Ads.fbInterstitialId);
                    }
                } else if (Ads.adFlag.equals("2")) {
                    if (Ads.adNetwork.equals("vungle")) {

                        loadVungleBanner(Ads.VungleBannerId);
                        loadVungleInterstitial(Ads.VungleInterstitialId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "error:" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void loadRewardedAd() {
        rewardedAd = MaxRewardedAd.getInstance(Ads.admobRewardedId, this);
        rewardedAd.setListener(new MaxRewardedAdListener() {
            @Override
            public void onRewardedVideoStarted(MaxAd ad) {

            }

            @Override
            public void onRewardedVideoCompleted(MaxAd ad) {

            }

            @Override
            public void onUserRewarded(MaxAd ad, MaxReward reward) {
                giveReward();
                clearMemory();
            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                retryRewardAttempt = 0;
                dialog.dismiss();
                rewardedVideoDialog.dismiss();
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {
                rewardedAd.loadAd();
            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                retryRewardAttempt++;
                long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rewardedAd.loadAd();
                    }
                }, delayMillis);
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                rewardedAd.loadAd();
            }
        });

        rewardedAd.loadAd();
    }

    private void loadVungleInterstitial(String vungleInterstitialId) {

        // Load Ad Implementation
        if (Vungle.isInitialized()) {
            Vungle.loadAd(vungleInterstitialId, new LoadAdCallback() {
                @Override
                public void onAdLoad(String placementReferenceId) {

                }

                @Override
                public void onError(String placementReferenceId, VungleException exception) {
                }
            });
        }


    }

    private void loadVungleBanner(String vungleBannerId) {
        LinearLayout bungleBannerContainer = findViewById(R.id.vungle_banner_container);

        Banners.loadBanner(vungleBannerId, AdConfig.AdSize.BANNER, new LoadAdCallback() {
            @Override
            public void onAdLoad(String id) {
                if (Banners.canPlayAd(Ads.VungleBannerId, AdConfig.AdSize.BANNER)) {
                    vungleBanner = Banners.getBanner(Ads.VungleBannerId, AdConfig.AdSize.BANNER, null);
                    bungleBannerContainer.addView(vungleBanner);
                }
            }

            @Override
            public void onError(String id, VungleException exception) {
                exception.printStackTrace();
            }
        });

    }

    private void initializeVungelSDK() {
        Vungle.init(getResources().getString(R.string.vungleId), getApplicationContext(), new InitCallback() {
            @Override
            public void onSuccess() {
                // SDK has successfully initialized
                Log.e("success", "vungle ad");
            }

            @Override
            public void onError(VungleException exception) {
                // SDK has failed to initialize
                exception.printStackTrace();
            }

            @Override
            public void onAutoCacheAdAvailable(String placementId) {
                // Ad has become available to play for a cache optimized placement
                Log.e("placementId", placementId);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void loadAdmobBannerAd(String admobBannerId) {


        maxAdView = new MaxAdView(admobBannerId, this);
        maxAdView.setListener(this);

        // Stretch to the width of the screen for banners to be fully functional
        int width = ViewGroup.LayoutParams.MATCH_PARENT;

        // Banner height on phones and tablets is 50 and 90, respectively
        int heightPx = getResources().getDimensionPixelSize(R.dimen.banner_height);

        maxAdView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));

        // Set background or background color for banners to be fully functional
        maxAdView.setBackgroundColor(getResources().getColor(R.color.colorWhite));

        LinearLayout bannerContainer = findViewById(R.id.adViewMain);
        bannerContainer.addView(maxAdView);

        // Load the ad
        maxAdView.loadAd();

//        com.google.android.gms.ads.AdView adView = new com.google.android.gms.ads.AdView(MainActivity.this);
//        adView.setAdUnitId(admobBannerId);
//        adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
//
//        AdRequest adRequest = new AdRequest.Builder().build();
//        bannerContainer.addView(adView);
//        adView.loadAd(adRequest);


    }


    private void loadFbBannerAd(String fbBannerId) {
        adView = new AdView(this, fbBannerId, AdSize.BANNER_HEIGHT_50);
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

    private void admobInterstital(String maxInterstitialId, String screen) {

        maxInterstitialAd = new MaxInterstitialAd(maxInterstitialId, this);
        maxInterstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.e("MaxAds", "Loaded");
                retryAttempt = 0;
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {
                screenLoad(maxInterAdScreen);
                maxInterstitialAd.loadAd();
            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                Log.e("Error", error.getMessage());
                screenLoad(maxInterAdScreen);

                retryAttempt++;

                long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        maxInterstitialAd.loadAd();
                    }
                }, delayMillis);
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                Log.e("Error->", error.getMessage());
                screenLoad(maxInterAdScreen);
                maxInterstitialAd.loadAd();
            }
        });
        maxInterstitialAd.loadAd();

//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        InterstitialAd.load(this, admobInterstitialId, adRequest, new InterstitialAdLoadCallback() {
//            @Override
//            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                mInterstitialAd = interstitialAd;
//                fullScreenContentCallback();
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
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
//                admobInterstital(Ads.admobInterstitialId);
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

    private void isFacebookInstalled() {
        String packageName = "com.facebook.katana";
        PackageManager pm = this.getPackageManager();
        try {
            pm.getPackageInfo(packageName, 0);
            updateFirebase_FB(true);
        } catch (PackageManager.NameNotFoundException e) {
            updateFirebase_FB(false);
        }
    }

    private void updateFirebase_FB(boolean b) {
        if (firebaseUser != null) {
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("user_facebook_installed", Boolean.toString(b));
            firebaseFirestore.collection("users")
                    .document(firebaseUser.getUid())
                    .update(userProfile);
        }
    }

    private void loadFbInterstitial(String fbInterstitialAd) {
        //load interstitial ad here
        interstitialAd = new com.facebook.ads.InterstitialAd(this, fbInterstitialAd);

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
                screenLoad(fbInterAdScreen);
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                screenLoad(fbInterAdScreen);
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


    private void getCurrentUserVariables() {
        if (firebaseUser != null) {
            firebaseFirestore.collection("users")
                    .document(firebaseUser.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                try {
                                    String temp = task.getResult().getString("daily_reward_count");
                                    if (temp == null) {
                                        Map<String, Object> updateUserProfile = new HashMap<>();
                                        updateUserProfile.put("daily_reward_count", "0");
                                        firebaseFirestore.collection("users")
                                                .document(firebaseUser.getUid())
                                                .update(updateUserProfile);
                                        // Toast.makeText(getApplicationContext(),"created",Toast.LENGTH_SHORT).show();
                                        getCurrentUserVariables();
                                    } else {
                                        int dailyRewardCurrentCount = Integer.parseInt(temp);
                                        updateVariableCurrentCount(dailyRewardCurrentCount);
                                        //Toast.makeText(getApplicationContext(),"Fetched Daily Count: "+ dailyRewardCurrentCount,Toast.LENGTH_SHORT).show();
                                    }
                                } catch (NullPointerException e) {
                                    Map<String, Object> updateUserProfile = new HashMap<>();
                                    updateUserProfile.put("daily_reward_count", "0");
                                    firebaseFirestore.collection("users")
                                            .document(firebaseUser.getUid())
                                            .update(updateUserProfile);
                                    //Toast.makeText(getApplicationContext(),"created",Toast.LENGTH_SHORT).show();
                                    getCurrentUserVariables();
                                } catch (Exception ignored) {
                                }

                            }
                        }
                    });
        }
    }

    private void updateVariableCurrentCount(int dailyRewardCurrentCount) {
        this.rewardVdCurrentCount = dailyRewardCurrentCount;
    }

    private void remoteConfigCheck() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseDefaults = new HashMap<>();
        firebaseDefaults.put(LATEST_APP_VERSION_CODE, getCurrentVersionCode());
        firebaseDefaults.put(REWARD_VIDEO_LIMIT, 5);
        firebaseRemoteConfig.setDefaultsAsync(firebaseDefaults);
        firebaseRemoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600).build());

        firebaseRemoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseRemoteConfig.activate();
                    checkForUpdates();
                }
            }
        });
    }

    private int getCurrentVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*private void AdsAuthorizationTimer()
    {
        final Handler handler = new Handler();

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
        }, 2000);

    }
    private void AdsSetVisibility()
    {
        if(!adsAuthorization.isBannerAdsEnabled())
        {
            adContainer.setVisibility(View.GONE);
        }
        else
        {
            adView.loadAd();
        }
    }*/

    private void checkForUpdates() {
        final String packageName = this.getPackageName();
        int fetchedVersionCode = (int) firebaseRemoteConfig.getDouble(LATEST_APP_VERSION_CODE);

        //variable updated with video limit set
        rewardVdLimit_RemoteConfig = (int) firebaseRemoteConfig.getDouble(REWARD_VIDEO_LIMIT);

        if (getCurrentVersionCode() < fetchedVersionCode) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("New Update Available...");
            builder1.setMessage("Update to the latest version of the game is strongly recommended.");
            builder1.setCancelable(false);

            builder1.setPositiveButton(
                    "Update Now",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                            } catch (Exception e) {
                            }
                        }
                    });

            builder1.setNegativeButton(
                    "Dismiss",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            MainActivity.this.finish();
                            System.exit(0);
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    private void saveUserRecentOnline() {
        if (haveNetworkConnection() && firebaseUser != null) {
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("user_recent_online", Timestamp.now());
            firebaseFirestore.collection("users")
                    .document(firebaseUser.getUid())
                    .update(userProfile);
        }

    }

    private void OnstartPriority() {
        if (firebaseUser == null) {

            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

   /* private void AnimationHandlerInitiate() {
        final Handler handler = new Handler(Looper.getMainLooper());

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                FlyBirdAnimationStart();
                rewardVideoImageAnimationStart();
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                handler.post(runnable);
            }
        }, 10, 100);

    }

    private void FlyBirdAnimationStart() {
        imageBird.setBackgroundResource(bird[currentFrame]);
        currentFrame++;
        if (currentFrame == 7)
            currentFrame = 0;
    }

    private void rewardVideoImageAnimationStart() {
        rewardVideoImage.setBackgroundResource(rewardVdImg[currentFrameRewardVDImg]);
        currentFrameRewardVDImg++;
        if (currentFrameRewardVDImg == 3)
            currentFrameRewardVDImg = 0;
    }*/

    private void playSound(){
        MediaPlayer mp = MediaPlayer.create(this,R.raw.click_sound);
        mp.start();
        mp.setOnCompletionListener(mp1 -> {
            mp1.stop();
            mp1.release();
        });
    }
    public void startGame(View view) {
        playSound();
        if (!haveNetworkConnection()) // If Not Connected to Internet
        {
            noInternetDialog.setContentView(R.layout.popup_nointernet);
            itsOkayButton = noInternetDialog.findViewById(R.id.okayButton);
            turnOnButton = noInternetDialog.findViewById(R.id.turnOnButton);
            turnOnButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noInternetDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Turn On Internet Service", Toast.LENGTH_LONG).show();
                }
            });
            itsOkayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noInternetDialog.dismiss();
                    gameInit();
                }
            });
            noInternetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            noInternetDialog.show();
            noInternetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Toast.makeText(getApplicationContext(), "Turn On Internet Service", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            CountDownTimer countDownTimer = new CountDownTimer(3000,1000){

                @Override
                public void onTick(long millisUntilFinished) {
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);
                }

                @Override
                public void onFinish() {
                    progressDialog.dismiss();
                    gameInit();
                }
            };
            countDownTimer.start();
        }

    }

    public void gameInit() {
        //Log.i("ImageButton","clicked");
//        Intent intent = new Intent(this, GameActivity.class);
//        startActivity(intent);
//        finish();


        if (Ads.adNetwork.equals("fb")) {
            if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                fbInterAdScreen = GAME;
                interstitialAd.show();
            } else {

                Intent intent = new Intent(this, GameActivity.class);
                startActivity(intent);
                finish();
            }
//            progressDialog.dismiss();
        } else if (Ads.adNetwork.equals("admob")) {
            if (maxInterstitialAd.isReady()) {
                maxInterAdScreen = GAME;
                maxInterstitialAd.showAd();
            } else {
                Intent intent = new Intent(this, GameActivity.class);
                startActivity(intent);
                finish();
            }
//            progressDialog.dismiss();
//            if (mInterstitialAd != null) {
//                mInterstitialAd.show(MainActivity.this);
//            }
        } else if (Ads.adNetwork.equals("vungle")) {
            vungleInterRewardAd(Ads.VungleInterstitialId, GAME);

//            if(Vungle.canPlayAd(Ads.VungleInterstitialId)){
//                Vungle.playAd(Ads.VungleInterstitialId, null, new PlayAdCallback() {
//                    @Override
//                    public void onAdStart(String id) {
//
//                    }
//
//                    @Override
//                    public void onAdEnd(String id, boolean completed, boolean isCTAClicked) {
//
//                    }
//
//                    @Override
//                    public void onAdEnd(String id) {
//
//                    }
//
//
//                    @Override
//                    public void onAdClick(String id) {
//
//                    }
//
//                    @Override
//                    public void onAdRewarded(String id) {
//
//                    }
//
//                    @Override
//                    public void onAdLeftApplication(String id) {
//
//                    }
//
//                    @Override public void onError(String placementReferenceId, VungleException exception) { }
//
//                    @Override
//                    public void onAdViewed(String id) {
//
//                    }
//                });
//            }
        }

    }

    private void vungleRewardAd(String adId) {
        if (Vungle.canPlayAd(adId)) {
            Vungle.playAd(adId, null, new PlayAdCallback() {
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

                @Override
                public void onError(String placementReferenceId, VungleException exception) {
                    exception.printStackTrace();
                }

                @Override
                public void onAdViewed(String id) {

                }
            });
        }
    }

    private void vungleInterRewardAd(String adId, String screen) {
        if (Vungle.canPlayAd(adId)) {
            Vungle.playAd(adId, null, new PlayAdCallback() {
                @Override
                public void onAdStart(String id) {

                }

                @Override
                public void onAdEnd(String id, boolean completed, boolean isCTAClicked) {
                    screenLoad(screen);
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

                @Override
                public void onError(String placementReferenceId, VungleException exception) {
                    exception.printStackTrace();
                }

                @Override
                public void onAdViewed(String id) {

                }
            });
        } else {
            screenLoad(screen);
        }
//        progressDialog.dismiss();
    }

    private void screenLoad(String screen) {
        if (screen.equals(GAME)) {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        } else if (screen.equals(LEADERBOARD)) {
            Intent intent = new Intent(MainActivity.this, LeaderBoard.class);
            startActivity(intent);
        }
        else if (screen.equals(WITHDRAW)) {
            Intent intent = new Intent(MainActivity.this, WithdrawPaytmActivity.class);
            startActivity(intent);
        }
    }

    public void informationSupportActivityInit(View view) {
        Intent intent = new Intent(this, InformationSupport.class);
        startActivity(intent);
    }

    public void rateGame(View view) {
        playSound();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + this.getPackageName())));
        } catch (Exception e) {
        }
    }

    /*   public void shopItems(View view)
       {
           Toast.makeText(MainActivity.this, "Shop Items development in-Progress", Toast.LENGTH_SHORT).show();
       }
   */
    public void withdraw(View view) {
        playSound();
        if (!haveNetworkConnection()) // If Not Connected to Internet
        {
            noInternetDialog.setContentView(R.layout.popup_nointernet_withdraw);
            noInternetTurnOnButtonWithdraw = noInternetDialog.findViewById(R.id.noInternetTurnOnButtonWithdraw);
            closeButton = noInternetDialog.findViewById(R.id.closeButton);
            noInternetTurnOnButtonWithdraw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noInternetDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Turn On Internet Service", Toast.LENGTH_LONG).show();
                }
            });
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    noInternetDialog.dismiss();
                }
            });
            noInternetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            noInternetDialog.show();
            noInternetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Toast.makeText(getApplicationContext(), "Turn On Internet Service", Toast.LENGTH_LONG).show();
                }
            });
        } else {

            if (Ads.adNetwork.equals("fb")) {
                if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                    fbInterAdScreen = WITHDRAW;
                    interstitialAd.show();
                } else {
                    Intent intent = new Intent(this, WithdrawPaytmActivity.class);
                    startActivity(intent);
                }
            } else if (Ads.adNetwork.equals("admob")) {
                if (maxInterstitialAd.isReady()) {
                    maxInterAdScreen = WITHDRAW;
                    maxInterstitialAd.showAd();
                } else {
                    Intent intent = new Intent(this, WithdrawPaytmActivity.class);
                    startActivity(intent);
                }
//                if (mInterstitialAd != null) {
//                    mInterstitialAd.show(MainActivity.this);
//                }
            } else if (Ads.adNetwork.equals("vungle")) {
                vungleInterRewardAd(Ads.VungleInterstitialId, WITHDRAW);
            }
        }

    }

    public void shareGame(View view) {
        playSound();
        try {
            openShareIntent(this, null, "Hey, look what I found, an exciting game. Download, play and earn!!\n" + Uri.parse("https://play.google.com/store/apps/details?id=" + this.getPackageName()));
        } catch (Exception e) {
        }
    }

    public void openShareIntent(Context context, @Nullable View itemview, String shareText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (itemview != null) {
            try {
                Uri imageUri = getImageUri(context, itemview, "postBitmap.jpeg");
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (IOException e) {
                intent.setType("text/plain");
                e.printStackTrace();
            }
        } else {
            intent.setType("text/plain");
        }
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(intent, "Share Via:"));
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


    @SuppressLint("SetTextI18n")
    public void displayRewardedVideo(View view) {

        //show WatchNow Dialog
        rewardedVideoDialog.setContentView(R.layout.popup_rewarded_video);
        watchNowButton = rewardedVideoDialog.findViewById(R.id.watchNowButton);
        RVCurrentCountTextView = rewardedVideoDialog.findViewById(R.id.RVCurrentCountTextView);

        RVCurrentCountTextView.setText(rewardVdCurrentCount + "/" + rewardVdLimit_RemoteConfig);

        watchNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckRVLimit();
            }
        });

        rewardedVideoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        rewardedVideoDialog.show();

    }

    private void CheckRVLimit() {
        // if limit not reached Load and display ad
        if (rewardVdCurrentCount < rewardVdLimit_RemoteConfig) {
            dialog.setMessage("Loading Video...");
            dialog.setCancelable(false);
            dialog.show();
            //loadRewardedVideoAd();
            //adMobRewardAd();
            switch (Ads.adFlag) {
                case "0":
                    if (Ads.adNetwork.equals("admob")) {
                        adMobRewardAd();
                    }
                    break;
                case "1":
                    if (Ads.adNetwork.equals("fb")) {
                        setUpUserConsent();

                        loadRewardedVideoAd();
                    }
                    break;
                case "2":
                    if (Ads.adNetwork.equals("vungle")) {
                        vungleRewardAd(Ads.VungleRewardedId);
                    }
                    break;
            }
        } else {
            //limit reached - display dialog
            Toast.makeText(getApplicationContext(), "Limit Reached, Try again Later!", Toast.LENGTH_LONG).show();
        }

    }

    private void setUpUserConsent() {
        SharedPreferences s = getSharedPreferences("DollarBird", Context.MODE_PRIVATE);
        if (s.getString("agreed", "2").contentEquals("2")) {
            androidx.appcompat.app.AlertDialog a = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this).create();
            a.setMessage("Agree to.......");
            a.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "Yes", (dialog1, which) -> {
                SharedPreferences.Editor e = s.edit();
                e.putString("agreed", "1");
                e.apply();
                dialog1.dismiss();
            });
            a.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, "No", (dialog1, which) -> {
                SharedPreferences.Editor e = s.edit();
                e.putString("agreed", "0");
                e.apply();
                dialog1.dismiss();
            });
            a.show();
        } else {
            initializeAPp(Ads.bannerZoneId);
            initializeAPp(Ads.interstitialZoneId);
            initializeAPp(Ads.RewardedZoneId);

        }
    }
    private  void initializeAPp(String zoneId){

        // Your user's consent String. In this case, the user has given consent to store
        // and process personal information. This value may be either O, 1, or an IAB consent string.
        String consent = "1";

        // The value passed via setPrivacyFrameworkRequired() will determine the GDPR requirement of
        // the user. If it's set to true, the user is subject to the GDPR laws.
        AdColonyAppOptions options = new AdColonyAppOptions()
                .setPrivacyFrameworkRequired(AdColonyAppOptions.GDPR, true)
                .setPrivacyConsentString(AdColonyAppOptions.GDPR, consent);

        // Pass options object to AdColony in configure call, or later in the session via
        // AdColony.setAppOptions().
        AdColony.configure(MainActivity.this, options, BuildConfig.APPLICATION_ID, zoneId);
    }

    private void loadRewardedVideoAd() {
        // Instantiate a RewardedVideoAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        rewardedVideoAd = new RewardedVideoAd(this, getResources().getString(R.string.facebook_rewarded_video));


        rewardedVideoAd.buildLoadAdConfig().withAdListener(new RewardedVideoAdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                // Rewarded video ad failed to load
                Log.e(TAG, "Rewarded video ad failed to load: " + error.getErrorMessage());
                dialog.dismiss();
                rewardedVideoDialog.dismiss();
                Toast.makeText(getApplicationContext(), "No Video Available Currently, Try Later!", Toast.LENGTH_LONG).show();
                clearMemory();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Rewarded video ad is loaded and ready to be displayed
                Log.d(TAG, "Rewarded video ad is loaded and ready to be displayed!");
                dialog.dismiss();
                rewardedVideoDialog.dismiss();
                rewardedVideoAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Rewarded video ad clicked
                Log.d(TAG, "Rewarded video ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Rewarded Video ad impression - the event will fire when the
                // video starts playing
                Log.d(TAG, "Rewarded video ad impression logged!");
            }

            @Override
            public void onRewardedVideoCompleted() {
                // Rewarded Video View Complete - the video has been played to the end.
                // You can use this event to initialize your reward
                Log.d(TAG, "Rewarded video completed!");
                // Call method to give reward
                giveReward();
                clearMemory();
            }

            @Override
            public void onRewardedVideoClosed() {
                // The Rewarded Video ad was closed - this can occur during the video
                // by closing the app, or closing the end card.
                Log.d(TAG, "Rewarded video ad closed!");

            }
        }).build();
        rewardedVideoAd.loadAd();

//        adMobRewardAd();
    }

    private void adMobRewardAd() {

        if (rewardedAd.isReady()) {
            rewardedAd.showAd();
        }

//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
//            @Override
//            public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
//                Log.d("TAG", "ad Failed to show full screen");
//            }
//
//            @Override
//            public void onAdShowedFullScreenContent() {
//                mRewardedAd = null;
//                Log.d("TAG", "ad Showed");
//            }
//
//            @Override
//            public void onAdDismissedFullScreenContent() {
//                Log.d("TAG", "ad dismissed");
//            }
//        };
//        RewardedAd.load(this, getResources().getString(R.string.admob_rewarded_video),
//                adRequest, new RewardedAdLoadCallback() {
//                    @Override
//                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
//                        mRewardedAd = rewardedAd;
//                        mRewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
//                        dialog.dismiss();
//                        rewardedVideoDialog.dismiss();
//                        Log.d("TAG", "ad loaded");
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        mRewardedAd = null;
//                        dialog.dismiss();
//                        rewardedVideoDialog.dismiss();
//                        Toast.makeText(getApplicationContext(), "No Video Available Currently, Try Later!", Toast.LENGTH_LONG).show();
//                        clearMemory();
//                    }
//                });
//
//
//        if (mRewardedAd != null) {
//            mRewardedAd.show(this, new OnUserEarnedRewardListener() {
//                @Override
//                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
//                    giveReward();
//                    clearMemory();
//                }
//            });
//        }
    }

    private void giveReward() {
        dialog.setMessage("Processing Coins...");
        dialog.setCancelable(false);
        dialog.show();

        rewardVdCurrentCount += 1;


        //getting user coins
        firebaseFirestore.collection("users")
                .document(firebaseUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            try {
                                String coins_temp = task.getResult().getData().get("user_earned_coins").toString();
                                if (coins_temp != null) {
                                    float earned_coins = Float.parseFloat(coins_temp);
                                    earned_coins += 1;

                                    Map<String, Object> userProfile_updateCoins = new HashMap<>();
                                    userProfile_updateCoins.put("user_earned_coins", Float.toString(earned_coins));
                                    userProfile_updateCoins.put("daily_reward_count", Integer.toString(rewardVdCurrentCount));

                                    //updating with latest coins and video reward count
                                    firebaseFirestore.collection("users")
                                            .document(firebaseUser.getUid())
                                            .update(userProfile_updateCoins);

                                    displayConfirmationUpdateUI();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void displayConfirmationUpdateUI() {
        dialog.dismiss();

        //show WatchNow Dialog
        coinsAddSuccessDialog.setContentView(R.layout.popup_rewardvideo_coins_success);
        Button okay = coinsAddSuccessDialog.findViewById(R.id.okayButton);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coinsAddSuccessDialog.dismiss();
            }
        });

        coinsAddSuccessDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        coinsAddSuccessDialog.setCancelable(false);
        coinsAddSuccessDialog.show();

    }

    private void clearMemory() {
        if (rewardedVideoAd != null) {
            rewardedVideoAd.destroy();
            rewardedVideoAd = null;
        }
    }

    public void leaderBoardClicked(View view) {
        playSound();
        if (haveNetworkConnection()) {
//            Intent intent = new Intent(MainActivity.this, LeaderBoard.class);
//            startActivity(intent);

            if (Ads.adNetwork.equals("fb")) {
                if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                    fbInterAdScreen = LEADERBOARD;
                    interstitialAd.show();
                } else {
                    Intent intent = new Intent(MainActivity.this, LeaderBoard.class);
                    startActivity(intent);
                }
            } else if (Ads.adNetwork.equals("admob")) {
                if (maxInterstitialAd.isReady()) {
                    maxInterAdScreen = LEADERBOARD;
                    maxInterstitialAd.showAd();
                } else {
                    Intent intent = new Intent(MainActivity.this, LeaderBoard.class);
                    startActivity(intent);
                }
//                if (mInterstitialAd != null) {
//                    mInterstitialAd.show(MainActivity.this);
//                }
            } else if (Ads.adNetwork.equals("vungle")) {
                vungleInterRewardAd(Ads.VungleInterstitialId, LEADERBOARD);
            }

        } else {
            Toast.makeText(getApplicationContext(), "Turn On Internet Service!", Toast.LENGTH_LONG).show();
        }
    }

    public void ProfileEditActivityInit(View view) {
        if (haveNetworkConnection()) {
            Intent intent = new Intent(MainActivity.this, ProfileEdit.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Turn On Internet Service!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        remoteConfigCheck();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (rewardedVideoAd != null) {
            rewardedVideoAd.destroy();
            rewardedVideoAd = null;
        }

        super.onDestroy();

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
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


//    @Override
//    public void onRewardedVideoStarted(MaxAd ad) {
//
//    }
//
//    @Override
//    public void onRewardedVideoCompleted(MaxAd ad) {
//
//    }
//
//    @Override
//    public void onUserRewarded(MaxAd ad, MaxReward reward) {
//        giveReward();
//        clearMemory();
//    }
}
