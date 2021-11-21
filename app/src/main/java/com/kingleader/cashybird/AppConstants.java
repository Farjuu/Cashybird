package com.kingleader.cashybird;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Random;

public class AppConstants {

    static BitmapBank bitmapBank; // Bitmap object reference
    static GameEngine gameEngine; // GameEngine object reference
    static int SCREEN_WIDTH, SCREEN_HEIGHT;
    static int gravity;
    static int VELOCITY_WHEN_JUMPED;
    static int gapBetweenTopAndBottomTubes;
    static int numberOfTubes;
    static int tubeVelocity;
    static int minTubeOffsetY;
    static int maxTubeOffsetY;
    static int distanceBetweenTubes;
    static SoundBank soundBank;
    static Context gameActivityContext;
    static int[] backgroundArray = {R.drawable.frame1,R.drawable.frame2,R.drawable.frame3,R.drawable.frame4,};
    static int[] gameoverbackArray = {R.drawable.gameoverback,};


    public static void initialization(Context context,int bg){
        setScreenSize(context);
        bitmapBank = new BitmapBank(context.getResources(), bg);
        setGameConstants();
        gameEngine = new GameEngine();
        soundBank = new SoundBank(context);
    }

    public static int getBg(){
        Random random = new Random();
        return backgroundArray[random.nextInt(backgroundArray.length)];
    }

    public static int get(){
        Random random = new Random();
        return gameoverbackArray[random.nextInt(gameoverbackArray.length)];
    }
    public static SoundBank getSoundBank(){
        return soundBank;
    }

    public static void setGameConstants(){
        AppConstants.gravity = 3;
        AppConstants.VELOCITY_WHEN_JUMPED = -32; //-40
        gapBetweenTopAndBottomTubes = 430; //600
        AppConstants.numberOfTubes = 2;
        AppConstants.tubeVelocity = 12;
        AppConstants.minTubeOffsetY = (int)(AppConstants.gapBetweenTopAndBottomTubes / 2.0);
        AppConstants.maxTubeOffsetY = AppConstants.SCREEN_HEIGHT - AppConstants.minTubeOffsetY - AppConstants.gapBetweenTopAndBottomTubes;
//        AppConstants.minTubeOffsetY = (int)(AppConstants.SCREEN_HEIGHT * 0.0976828);
//        AppConstants.maxTubeOffsetY = (int) (AppConstants.SCREEN_HEIGHT * 0.28); //0.7069513
//        AppConstants.gapBetweenTopAndBottomTubes = maxTubeOffsetY-minTubeOffsetY;

 //       AppConstants.distanceBetweenTubes = AppConstants.SCREEN_WIDTH * 3 /4;
        AppConstants.distanceBetweenTubes = (int) (AppConstants.SCREEN_WIDTH * 0.84375) ;
    }

    // Return BitmapBank instance
    public static BitmapBank getBitmapBank(){
        return bitmapBank;
    }

    // Return GameEngine instance
    public static GameEngine getGameEngine(){
        return gameEngine;
    }

    private static void setScreenSize(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        AppConstants.SCREEN_WIDTH = width;
        AppConstants.SCREEN_HEIGHT = height;
    }

    public static AlertDialog.Builder getDialogProgressBar(Activity activity, String msg) {
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(activity);
        builder.setTitle(null);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.loading, null);
        TextView tvMsg = dialogView.findViewById(R.id.pbText);
        tvMsg.setText(msg);
        builder.setView(dialogView);
        return builder;
    }

}
