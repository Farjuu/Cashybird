package com.kingleader.cashybird;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kingleader.cashybird.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends Activity
{
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    ImageView cashybirdtext;
    Button imageBirdRegister;
    int[] bird;
    private Timer timer;
    int currentFrame = 0;
    ProgressDialog dialog;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    TextView back_login;
    EditText editTextFirstName;
    EditText editTextLastName;
    EditText editTextRegisterEmail;
    EditText editTextRegisterPassword;
    CheckBox checkBox;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        imageBirdRegister = findViewById(R.id.signup);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName= findViewById(R.id.editTextLastName);
        editTextRegisterEmail = findViewById(R.id.editTextRegisterEmail);
        editTextRegisterPassword= findViewById(R.id.editTextRegisterPassword);
        cashybirdtext= findViewById(R.id.cashybirdtext);
        back_login= findViewById(R.id.back_login);
        //  checkBox= findViewById(R.id.checkBox);

       /* bird = new int[8];
        bird[0] = R.drawable.bird_frame1;
        bird[1] = R.drawable.bird_frame2;
        bird[2] = R.drawable.bird_frame3;
        bird[3] = R.drawable.bird_frame4;
        bird[4] = R.drawable.bird_frame5;
        bird[5] = R.drawable.bird_frame6;
        bird[6] = R.drawable.bird_frame7;
        bird[7] = R.drawable.bird_frame8;

        AnimationHandlerInitiate();*/

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        dialog = new ProgressDialog(RegisterActivity.this);
        dialog.setMessage("Processing...");
        dialog.setCancelable(false);

    }

  /*  private void AnimationHandlerInitiate()
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
        imageBirdRegister.setBackgroundResource(bird[currentFrame]);
        currentFrame++;
        if(currentFrame==7)
            currentFrame=0;
    }*/

    public void registerButtonClicked(View view)
    {
        if(!checkForEmptyFields()) // returns false = no empty fields and email pattern is correct
        {
           /* if(checkBox.isChecked())
            {*/
            dialog.show();

            firebaseAuth.createUserWithEmailAndPassword(editTextRegisterEmail.getText().toString(),editTextRegisterPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful())
                    {

                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUser = firebaseUser.getUid();

                        Map<String, Object> userProfile = new HashMap<>();
                        userProfile.put("user_firstname", editTextFirstName.getText().toString());
                        userProfile.put("user_lastname", editTextLastName.getText().toString());
                        userProfile.put("user_email", editTextRegisterEmail.getText().toString());
                        userProfile.put("user_epass", editTextRegisterPassword.getText().toString());
                        userProfile.put("user_earned_coins",0);
                        userProfile.put("user_personal_best","0");
                        userProfile.put("user_uid",firebaseUser.getUid());
                        userProfile.put("user_joined", Timestamp.now());

                        firebaseFirestore.collection("users")
                                .document(currentUser)
                                .set(userProfile)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if(task.isSuccessful())
                                                            {
                                                                Toast.makeText(RegisterActivity.this, "Verify your e-mail!", Toast.LENGTH_LONG).show();
                                                            }

                                                        }
                                                    });
                                            startActivity(intent);
                                            dialog.dismiss();
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(RegisterActivity.this, "Something went wrong! Please try later!", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    }
                                });
                    }
                    else
                    {
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });

                    }
                }
            });

            /*}
            else
            {
                Toast.makeText(RegisterActivity.this, "Please Tick the CheckBox", Toast.LENGTH_LONG).show();
            }*/
        }
    }


    private boolean checkForEmptyFields()
    {
        if(editTextFirstName.getText().toString().isEmpty())
        {
            Toast.makeText(this,"First Name Can't be Empty",Toast.LENGTH_SHORT).show();
            return true;
        }
        if(editTextLastName.getText().toString().isEmpty())
        {
            Toast.makeText(this,"Last Name Can't be Empty",Toast.LENGTH_SHORT).show();
            return true;
        }
        if(editTextRegisterEmail.getText().toString().isEmpty())
        {
            Toast.makeText(this,"Email Can't be Empty",Toast.LENGTH_SHORT).show();
            return true;
        }
        if(editTextRegisterPassword.getText().toString().isEmpty())
        {
            Toast.makeText(this,"Password Can't be Empty",Toast.LENGTH_SHORT).show();
            return true;
        }
        if(!editTextFirstName.getText().toString().isEmpty() &&
                !editTextLastName.getText().toString().isEmpty() &&
                !editTextRegisterEmail.getText().toString().isEmpty() &&
                !editTextRegisterPassword.getText().toString().isEmpty())
        {
            return !editTextRegisterEmail.getText().toString().matches(emailPattern);
        }

        return true;
    }

    public void back_loginClicked(View view)
    {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }
}
