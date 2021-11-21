package com.kingleader.cashybird;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kingleader.cashybird.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends Activity
{
    ImageView casybird;
    ImageButton imageBirdLogin;
    int[] bird;
    private Timer timer;
    int currentFrame = 0;
    ProgressDialog dialog;
    private FirebaseAuth userAuth;
    EditText editTextLoginEmail;
    EditText editTextLoginPassword;
    TextView forgotTxt, orTxt;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        userAuth = FirebaseAuth.getInstance();
/*
        imageBirdLogin= findViewById(R.id.imageBirdLogin);*/
        casybird=findViewById(R.id.imageView20);
        editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword);
        forgotTxt=findViewById(R.id.forgotTxt);
        orTxt=findViewById(R.id.orTxt);

        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
/*
        bird = new int[8];
        bird[0] = R.drawable.bird_frame1;
        bird[1] = R.drawable.bird_frame2;
        bird[2] = R.drawable.bird_frame3;
        bird[3] = R.drawable.bird_frame4;
        bird[4] = R.drawable.bird_frame5;
        bird[5] = R.drawable.bird_frame6;
        bird[6] = R.drawable.bird_frame7;
        bird[7] = R.drawable.bird_frame8;

        AnimationHandlerInitiate();*/

    }

    /* private void AnimationHandlerInitiate()
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
        imageBirdLogin.setBackgroundResource(bird[currentFrame]);
        currentFrame++;
        if(currentFrame==7)
            currentFrame=0;
    }*/


    public void forgotPasswordClicked(View view)
    {
        dialog.setMessage("Sending e-mail...");
        dialog.setCancelable(false);

        if(!TextUtils.isEmpty(editTextLoginEmail.getText().toString()))
        {
            dialog.show();
            userAuth.sendPasswordResetEmail(editTextLoginEmail.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this, "Reset password link was sent to your email!", Toast.LENGTH_LONG).show();
                                editTextLoginEmail.setText("");
                                editTextLoginPassword.setText("");
                                editTextLoginPassword.requestFocus();
                                dialog.dismiss();
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "E-mail doesn't match with any account!", Toast.LENGTH_LONG).show();
                                editTextLoginEmail.setText("");
                                editTextLoginPassword.setText("");
                                editTextLoginEmail.requestFocus();
                                dialog.dismiss();
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(LoginActivity.this, "Please enter your email ID.", Toast.LENGTH_LONG).show();
            editTextLoginEmail.setText("");
            editTextLoginPassword.setText("");
            editTextLoginEmail.requestFocus();
        }

    }

    public void loginButtonClicked(View view)
    {
        String userEmail = editTextLoginEmail.getText().toString();
        String userPass = editTextLoginPassword.getText().toString();

        if (!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty((userPass))) {

            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();

            userAuth.signInWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        dialog.dismiss();
                        finish();
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Either e-mail/password is incorrect!", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "Please enter your email and password.", Toast.LENGTH_SHORT).show();
        }
    }

    public void registerButtonClicked(View view)
    {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();

    }
}
