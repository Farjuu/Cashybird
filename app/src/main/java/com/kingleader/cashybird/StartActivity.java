package com.kingleader.cashybird;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.kingleader.cashybird.R;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends Activity
{
    ImageButton imageBirdStart;
    int[] bird;
    private Timer timer;
    int currentFrame = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        imageBirdStart= findViewById(R.id.imageBirdStart);

        bird = new int[8];
        bird[0] = R.drawable.bird_frame1;
        bird[1] = R.drawable.bird_frame2;
        bird[2] = R.drawable.bird_frame3;
        bird[3] = R.drawable.bird_frame4;
        bird[4] = R.drawable.bird_frame5;
        bird[5] = R.drawable.bird_frame6;
        bird[6] = R.drawable.bird_frame7;
        bird[7] = R.drawable.bird_frame8;

        AnimationHandlerInitiate();

    }
    private void AnimationHandlerInitiate()
    {
        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                FlyBirdAnimationStart();
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
    private void FlyBirdAnimationStart()
    {
        imageBirdStart.setBackgroundResource(bird[currentFrame]);
        currentFrame++;
        if(currentFrame==7)
            currentFrame=0;
    }


    public void startActivityLogin(View view)
    {
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void startActivitySignup(View view)
    {
        Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
