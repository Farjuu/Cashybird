package com.kingleader.cashybird;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdsAuthorization
{

    private final FirebaseFirestore firebaseFirestore;
    private static boolean banner;
    private static boolean interstitial;
    private static boolean rewarded_video;
    private static int score_limit = 8;

    public AdsAuthorization(Context context)
    {
        banner =true;
        interstitial=true;
        rewarded_video= true;
        firebaseFirestore = FirebaseFirestore.getInstance();
        getDataFromFireBase();
    }

    private void getDataFromFireBase()
    {
        firebaseFirestore.collection("admin")
                .document("ads_display")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            boolean banner_status = task.getResult().getBoolean("banner");
                            boolean interstitial_status = task.getResult().getBoolean("interstitial");
                            boolean rewarded_video_status = task.getResult().getBoolean("rewarded_video");

                            updateVariables(banner_status, interstitial_status, rewarded_video_status);
                        }
                    }
                });
        firebaseFirestore.collection("admin")
                .document("game_over_ads")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            String score_Limit_FB = task.getResult().getString("score_limit");
                            updateVariableScoreLimit(score_Limit_FB);
                        }
                    }
                });
    }

    private void updateVariableScoreLimit(String score_limit_fb)
    {
        AdsAuthorization.score_limit = Integer.parseInt(score_limit_fb);
    }

    private void updateVariables(boolean banner_status, boolean interstitial_status, boolean rewarded_video_status)
    {
        AdsAuthorization.banner = banner_status;
        AdsAuthorization.interstitial = interstitial_status;
        AdsAuthorization.rewarded_video = rewarded_video_status;
    }

    public boolean isBannerAdsEnabled()
    {
        return AdsAuthorization.banner;
    }
    public boolean isInterstitialAdsEnabled()
    {
        return AdsAuthorization.interstitial;
    }
    public boolean isRewarded_videoAdsEnabled()
    {
        return AdsAuthorization.rewarded_video;
    }
    public int getScore_limit()
    {
        return AdsAuthorization.score_limit;
    }

}
