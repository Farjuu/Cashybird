package com.kingleader.cashybird;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vungle.warren.AdConfig;
import com.vungle.warren.Banners;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.PlayAdCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.VungleBanner;
import com.vungle.warren.error.VungleException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.facebook.ads.AudienceNetworkAds.TAG;

public class ProfileEdit extends Activity implements MaxAdViewAdListener, MaxAdListener {
    EditText editTextFirstName;
    EditText editTextLastName;
    EditText editTextUserEmail;
    EditText editTextLoginPassword,editTextLoginPassword2;
    // AutoCompleteTextView editTextUserCountry;
    RadioButton radioButtonUserMale;
    RadioButton radioButtonUserFemale;
    Button buttonSave;
    ProgressDialog dialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private InterstitialAd interstitialAd;
    private com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAd;
    private AdView adView;
    private LinearLayout adContainer;
    private VungleBanner vungleBanner;
    private MaxAdView maxAdView;
    private MaxInterstitialAd maxInterstitialAd;
    private int retryAttempt;

    /*
    String profile_firstName;
    String profile_lastname;
    String profile_email;
    String profile_mobile;
    String profile_country;
    String profile_gender;
    private boolean changesMade = false;
    */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextUserEmail = findViewById(R.id.editTextRegisterEmail);
        editTextLoginPassword = findViewById(R.id.editTextRegisterPassword);
        editTextLoginPassword2 = findViewById(R.id.editTextRegisterPassword2);
        radioButtonUserMale = findViewById(R.id.radioButtonUserMale);
        radioButtonUserFemale = findViewById(R.id.radioButtonUserFemale);

        buttonSave=findViewById(R.id.savebtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();


/*

        String[] countries_list = getResources().getStringArray(R.array.countries_array);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries_list);
        editTextUserCountry.setAdapter(adapter);*/

        dialog = new ProgressDialog(ProfileEdit.this);
        dialog.setMessage("Loading Profile...");
        dialog.setCancelable(false);
        dialog.show();

        if (Ads.adNetwork.equals("fb")) {
            AudienceNetworkAds.initialize(this);
            loadFbBannerAd();
            loadFbInterstitial();
        } else if (Ads.adNetwork.equals("admob")) {
            loadAdmobBannerAd();
            admobInterstital();
        }else if(Ads.adNetwork.equals("vungle")){
            loadVungleBanner();
            loadVungleInterstitial();
        }
        firebaseConnectAndUpdate();
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
//        com.google.android.gms.ads.AdView adView = new com.google.android.gms.ads.AdView(ProfileEdit.this);
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

    private void loadFbInterstitial() {
        //load interstitial ad here
        interstitialAd = new InterstitialAd(this, getResources().getString(R.string.facebook_InterstitialProfileEdit));

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
                //interstitialAd.show();
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
        maxInterstitialAd = new MaxInterstitialAd(Ads.admobInterstitialId, this );
        maxInterstitialAd.setListener(this);
        maxInterstitialAd.loadAd();
//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        com.google.android.gms.ads.interstitial.InterstitialAd.load(this, getResources().getString(R.string.admob_interstitial), adRequest, new InterstitialAdLoadCallback() {
//            @Override
//            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
//                mInterstitialAd = interstitialAd;
//                fullScreenContentCallback();
//                //mInterstitialAd.show(ProfileEdit.this);
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
                admobInterstital();
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

    private void firebaseConnectAndUpdate() {
        firebaseFirestore.collection("users")
                .document(firebaseUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            String firstName = task.getResult().getString("user_firstname");
                            String lastname = task.getResult().getString("user_lastname");
                            String email = task.getResult().getString("user_email");
                            String mobile = task.getResult().getString("user_mobile");
                            String country = task.getResult().getString("user_country");
                             String gender = task.getResult().getString("user_gender");

                            onLoadUpdateUI(firstName, lastname, email, mobile, country, gender);


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Something went wrong, Try again!", Toast.LENGTH_LONG).show();
                        ProfileEdit.this.finish();
                    }
                });
    }

    private void onLoadUpdateUI(String firstName, String lastname, String email, String mobile, String country, String gender) {
/*
        profile_firstName = firstName;
        profile_lastname = lastname;
        profile_email = email;
        profile_mobile = mobile;
        profile_country = country;
        profile_gender = gender;
*/
        if (firstName != null) {
            editTextFirstName.setText(firstName);
        }
        if (lastname != null) {
            editTextLastName.setText(lastname);
        }
        if (email != null) {
            editTextUserEmail.setText(email);
        }
       /* if (mobile != null) {
            //  editTextUserMobile.setText(mobile);
        }
        if (country != null) {
            //  editTextUserCountry.setText(country);
        }*/
        if (gender != null) {
            if (gender.equalsIgnoreCase("male")) {
                radioButtonUserMale.setChecked(true);
            } else if (gender.equalsIgnoreCase("female")) {
                radioButtonUserFemale.setChecked(true);
            }
        }
        dialog.dismiss();
    }

    public void saveButtonClicked(View view) {
        if (!checkForPrimaryFieldNullEntries()) // FirstName, LastName, Email
        {
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            if (editTextUserEmail.getText().toString().matches(emailPattern)) {
                dialog.setMessage("Updating Profile...");
                dialog.show();

                //   String updatedMobileNumber = editTextUserMobile.getText().toString();
                //   String updatedCountry = editTextUserCountry.getText().toString();
                String updatedGender = "";
                if (radioButtonUserMale.isChecked()) {
                    updatedGender = "male";
                }
                if (radioButtonUserFemale.isChecked()) {
                    updatedGender = "female";
                }

                Map<String, Object> userProfile = new HashMap<>();
                userProfile.put("user_firstname", editTextFirstName.getText().toString());
                userProfile.put("user_lastname", editTextLastName.getText().toString());
                userProfile.put("user_email", editTextUserEmail.getText().toString());
                //  userProfile.put("user_mobile", updatedMobileNumber);
                //   userProfile.put("user_country", updatedCountry);
                userProfile.put("user_gender", updatedGender);

                firebaseFirestore.collection("users")
                        .document(firebaseUser.getUid())
                        .update(userProfile)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    if (Ads.adNetwork.equals("fb")) {
                                        if(interstitialAd !=null && interstitialAd.isAdLoaded()){
                                            interstitialAd.show();
                                        }
                                    } else if (Ads.adNetwork.equals("admob")) {
                                        if ( maxInterstitialAd.isReady() )
                                        {
                                            maxInterstitialAd.showAd();
                                        }
//                                        if (mInterstitialAd != null) {
//                                            mInterstitialAd.show(ProfileEdit.this);
//                                        }
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
                                    }

                                    Toast.makeText(getApplicationContext(), "Profile Updated Successfully!", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    ProfileEdit.this.finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Something went wrong, check your internet & try again.", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Something went wrong, check your internet & try again.", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), "Invalid Email Address..", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkForPrimaryFieldNullEntries() {
        if (editTextFirstName.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "FirstName Can't be Empty", Toast.LENGTH_LONG).show();
            return true;
        }
        if (editTextLastName.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "LastName Can't be Empty", Toast.LENGTH_LONG).show();
            return true;
        }
        if (editTextUserEmail.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Primary Email Can't be Empty", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        ProfileEdit.this.finish();
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
