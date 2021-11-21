package com.kingleader.cashybird;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundBank {

    Context context;
    MediaPlayer swoosh, point, hit, wing;

    public SoundBank(Context context){
        this.context = context;
        swoosh = MediaPlayer.create(context, R.raw.swoosh);
        point = MediaPlayer.create(context, R.raw.point);
        hit = MediaPlayer.create(context, R.raw.hit);
        wing = MediaPlayer.create(context, R.raw.wing);
    }

    public void playSwoosh(){
        if(swoosh != null){
            swoosh.start();
        }
    }
    public void releaseSwoosh() {
            swoosh.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
    }


    public void playPoint(){
        if(point != null){
            point.start();
        }
    }
    public void releasePoint() {
            point.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
    }


    public void playHit() {
        if (hit != null) {
            hit.start();
        }
    }
    public void releaseHit() {
            hit.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
        }

    public void playWing(){
        if(wing != null){
            wing.start();
        }
    }
    public void releaseWing() {
            wing.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
    }

}
