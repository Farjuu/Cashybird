package com.kingleader.cashybird;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.vungle.warren.AdConfig;
import com.vungle.warren.Banners;
import com.vungle.warren.LoadAdCallback;
import com.vungle.warren.VungleBanner;
import com.vungle.warren.error.VungleException;

import java.util.ArrayList;

public class LeaderBoard extends Activity implements MaxAdViewAdListener
{
    private FirebaseRemoteConfig firebaseRemoteConfig;
    ListView mListView;
    TextView leaderboardtext,scnd_position_text,scnd_position_score,first_position_text,first_position_score,third_position_text,third_position_score;
    TextView leaderBoardFooter;
    ProgressDialog dialog;
    LinearLayout adContainer,titlelistview;
    ScrollView scrollViewParent;


    private MaxAdView maxAdView;

    // private final String LeaderBoardHeading = "leader_board_heading";
    private final String LeaderBoardFooter = "leader_board_footer";

    private final String player1_name = "player1_name";
    private final String player1_score = "player1_score";
    private final String player1_payouts = "player1_payouts";

    private final String player2_name = "player2_name";
    private final String player2_score = "player2_score";
    private final String player2_payouts = "player2_payouts";

    private final String player3_name = "player3_name";
    private final String player3_score = "player3_score";
    private final String player3_payouts = "player3_payouts";

    private final String player4_name = "player4_name";
    private final String player4_score = "player4_score";
    private final String player4_payouts = "player4_payouts";

    private final String player5_name = "player5_name";
    private final String player5_score = "player5_score";
    private final String player5_payouts = "player5_payouts";

    private final String player6_name = "player6_name";
    private final String player6_score = "player6_score";
    private final String player6_payouts = "player6_payouts";

    private final String player7_name = "player7_name";
    private final String player7_score = "player7_score";
    private final String player7_payouts = "player7_payouts";

    private final String player8_name = "player8_name";
    private final String player8_score = "player8_score";
    private final String player8_payouts = "player8_payouts";

    private final String player9_name = "player9_name";
    private final String player9_score = "player9_score";
    private final String player9_payouts = "player9_payouts";

    private final String player10_name = "player10_name";
    private final String player10_score = "player10_score";
    private final String player10_payouts = "player10_payouts";
    private AdView adView;
    private VungleBanner vungleBanner;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_new);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mListView = findViewById(R.id.gemUserList);
        leaderboardtext = findViewById(R.id.leaderboardtext);
        leaderBoardFooter = findViewById(R.id.leaderBoardFooter);

        scnd_position_text = findViewById(R.id.scnd_position_text);
        scnd_position_score = findViewById(R.id.scnd_position_score);
        first_position_text = findViewById(R.id.first_position_text);
        first_position_score = findViewById(R.id.first_position_score);
        third_position_text= findViewById(R.id.third_position_text);
        third_position_score= findViewById(R.id.third_position_score);



        if (Ads.adNetwork.equals("fb")) {
            AudienceNetworkAds.initialize(this);
            loadFbBannerAd();
        } else if (Ads.adNetwork.equals("admob")) {
            loadAdmobBannerAd();
        }else if(Ads.adNetwork.equals("vungle")){
            loadVungleBanner();
        }

        dialog = new ProgressDialog(LeaderBoard.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();

       /* scrollViewParent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                findViewById(R.id.gemUserList).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });*/

        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        remoteConfigCheck();

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
//        com.google.android.gms.ads.AdView adView = new com.google.android.gms.ads.AdView(LeaderBoard.this);
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


    private void remoteConfigCheck()
    {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        firebaseRemoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3500).build());

        firebaseRemoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    firebaseRemoteConfig.activate();
                    checkForUpdates();
                }
            }
        });
    }


    private void checkForUpdates() {

        //  String heading = firebaseRemoteConfig.getString(LeaderBoardHeading);
        String footer = firebaseRemoteConfig.getString(LeaderBoardFooter);

        //  leaderBoardHeading.setText(heading);
        leaderBoardFooter.setText(footer);

        String userName1 = firebaseRemoteConfig.getString(player1_name);
        int score1 = (int) firebaseRemoteConfig.getDouble(player1_score);
        int checkout1 = (int) firebaseRemoteConfig.getDouble(player1_payouts);


        String firstScoreText =userName1+ "($"+checkout1+")\n"+score1;
        first_position_score.setText(firstScoreText);

        String userName2 = firebaseRemoteConfig.getString(player2_name);
        int score2 = (int) firebaseRemoteConfig.getDouble(player2_score);
        int checkout2 = (int) firebaseRemoteConfig.getDouble(player2_payouts);

        String scndScoreText = userName2+"($"+checkout2+")\n"+score2;
        scnd_position_score.setText(scndScoreText);

        String userName3 = firebaseRemoteConfig.getString(player3_name);
        int score3 = (int) firebaseRemoteConfig.getDouble(player3_score);
        int checkout3 = (int) firebaseRemoteConfig.getDouble(player3_payouts);

        String thirdScoreText = userName3+"($"+checkout3+")\n"+score3;
        third_position_score.setText(thirdScoreText);

        String userName4 = firebaseRemoteConfig.getString(player4_name);
        int score4 = (int) firebaseRemoteConfig.getDouble(player4_score);
        int checkout4 = (int) firebaseRemoteConfig.getDouble(player4_payouts);

        String userName5 = firebaseRemoteConfig.getString(player5_name);
        int score5 = (int) firebaseRemoteConfig.getDouble(player5_score);
        int checkout5 = (int) firebaseRemoteConfig.getDouble(player5_payouts);

        String userName6 = firebaseRemoteConfig.getString(player6_name);
        int score6 = (int) firebaseRemoteConfig.getDouble(player6_score);
        int checkout6 = (int) firebaseRemoteConfig.getDouble(player6_payouts);

        String userName7 = firebaseRemoteConfig.getString(player7_name);
        int score7 = (int) firebaseRemoteConfig.getDouble(player7_score);
        int checkout7 = (int) firebaseRemoteConfig.getDouble(player7_payouts);

        String userName8 = firebaseRemoteConfig.getString(player8_name);
        int score8 = (int) firebaseRemoteConfig.getDouble(player8_score);
        int checkout8 = (int) firebaseRemoteConfig.getDouble(player8_payouts);

        String userName9 = firebaseRemoteConfig.getString(player9_name);
        int score9 = (int) firebaseRemoteConfig.getDouble(player9_score);
        int checkout9 = (int) firebaseRemoteConfig.getDouble(player9_payouts);

        String userName10 = firebaseRemoteConfig.getString(player10_name);
        int score10 = (int) firebaseRemoteConfig.getDouble(player10_score);
        int checkout10 = (int) firebaseRemoteConfig.getDouble(player10_payouts);

        //Creating Objects of each player with Player Class

        Player player1 =new Player(1,userName1,score1,checkout1);
        Player player2 =new Player(2,userName2,score2,checkout2);
        Player player3 =new Player(3,userName3,score3,checkout3);
        Player player4 =new Player(4,userName4,score4,checkout4);
        Player player5 =new Player(5,userName5,score5,checkout5);
        Player player6 =new Player(6,userName6,score6,checkout6);
        Player player7 =new Player(7,userName7,score7,checkout7);
        Player player8 =new Player(8,userName8,score8,checkout8);
        Player player9 =new Player(9,userName9,score9,checkout9);
        Player player10 =new Player(10,userName10,score10,checkout10);

        //Add these objects to ArrayList Now

        ArrayList<Player> PlayerList = new ArrayList<>();
       /* PlayerList.add(player1);
        PlayerList.add(player2);
        PlayerList.add(player3);
      */
        PlayerList.add(player4);
        PlayerList.add(player5);
        PlayerList.add(player6);
        PlayerList.add(player7);
        PlayerList.add(player8);
        PlayerList.add(player9);
        PlayerList.add(player10);

        //Create Custom Adapter
        PlayerListAdapter adapter = new PlayerListAdapter(this,R.layout.adapter_view_layout,PlayerList);
        mListView.setAdapter(adapter);

        dialog.dismiss();
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
