package com.kingleader.cashybird;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WithdrawPaytmInitActivity extends Activity
{

    TextView totalCoinsWDInitTextView;
    EditText editTextPayPalEmail;
    EditText editTextPayPalConfirmEmail;
    EditText withdrawCoins;
    double available_coins;
    ProgressDialog dialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    Dialog noInternetDialog;
    Button closeButton,turnOnButton;
    ImageView ivPaymentImg;


    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_paytm_init);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ivPaymentImg = findViewById(R.id.iv_payment_img);
        totalCoinsWDInitTextView = findViewById(R.id.totalCoinsWDInitTextView);
        editTextPayPalEmail = findViewById(R.id.editTextPayPalEmail);
        // editTextPayPalConfirmEmail = findViewById(R.id.editTextPayPalConfirmEmail);
        withdrawCoins = findViewById(R.id.withdrawCoins);

        Bundle bundle= getIntent().getExtras();
        available_coins = bundle.getDouble("available_coins");
        totalCoinsWDInitTextView.setText(String.format("%.2f",available_coins));

        noInternetDialog = new Dialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        dialog = new ProgressDialog(WithdrawPaytmInitActivity.this);
        dialog.setMessage("Processing Request...");
        dialog.setCancelable(false);

    }

    public void withdrawConfirmButton(View view)
    {
        if(haveNetworkConnection())
        {
            if(checkForFieldClearance())
            {
                double withdrawCoinsEntered = Double.parseDouble(withdrawCoins.getText().toString());
                if(withdrawCoinsEntered>available_coins)
                {
                    Toast.makeText(this,"Can't withdraw more than available coins!",Toast.LENGTH_LONG).show();
                }
                else if(withdrawCoinsEntered> 0 && withdrawCoinsEntered<=available_coins)
                {
                    //Initiate Final Withdraw and Update Database
                    DeductAndSaveToDB(withdrawCoinsEntered,available_coins);
                }
                else
                {
                    Toast.makeText(this,"Invalid Request Amount.",Toast.LENGTH_LONG).show();
                }
            }
        }
        else
        {
            // display No Internet Connected
            showNoInternetToUser();
        }

    }

    private void showNoInternetToUser()
    {
        noInternetDialog.setContentView(R.layout.popup_nointernet_withdraw);
        closeButton = noInternetDialog.findViewById(R.id.closeButton);
        turnOnButton = noInternetDialog.findViewById(R.id.noInternetTurnOnButtonWithdraw);
        turnOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noInternetDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Turn On Internet Service",Toast.LENGTH_LONG).show();
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                noInternetDialog.dismiss();
            }
        });
        noInternetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        noInternetDialog.setCancelable(false);
        noInternetDialog.show();
    }

    private void DeductAndSaveToDB(double withdrawCoinsEntered, double available_coins)
    {
        dialog.show();
        double updatedTotalCoins = Math.abs(available_coins - withdrawCoinsEntered);

        // NumberFormat numberFormat = NumberFormat.getNumberInstance();
        // numberFormat.setMinimumFractionDigits(2);
        // numberFormat.setMaximumFractionDigits(2);
        // final String updatedTotalCoins_ = numberFormat.format(updatedTotalCoins);

        Map<String, Object> userProfile_update = new HashMap<>();
        userProfile_update.put("user_earned_coins", Double.valueOf(updatedTotalCoins));

        final Map<String, Object> userProfile_Add_ToWithdrawRequest = new HashMap<>();
        userProfile_Add_ToWithdrawRequest.put("user_uid", firebaseUser.getUid());
        userProfile_Add_ToWithdrawRequest.put("user_email", editTextPayPalEmail.getText().toString());
        userProfile_Add_ToWithdrawRequest.put("user_withdraw_coins", Double.toString(withdrawCoinsEntered));
        userProfile_Add_ToWithdrawRequest.put("request_timestamp", Timestamp.now());
        userProfile_Add_ToWithdrawRequest.put("status", "in_progress");

        firebaseFirestore.collection("users")
                .document(firebaseUser.getUid())
                .update(userProfile_update)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            String unique_id = firebaseFirestore.collection("withdraw_requests").document().getId();
                            firebaseFirestore.collection("withdraw_requests")
                                    .document(unique_id)
                                    .set(userProfile_Add_ToWithdrawRequest);
                            dialog.dismiss();
                            showConfirmationToUser();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        showErrorWhileProcessing();
                    }
                });
    }

    private void showErrorWhileProcessing() // Withdraw Failed
    {
        noInternetDialog.setContentView(R.layout.popup_withdraw_failed);
        Button okayButton = noInternetDialog.findViewById(R.id.okayButton);
        okayButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                noInternetDialog.dismiss();
                Intent intent= new Intent(WithdrawPaytmInitActivity.this, MainActivity.class);
                startActivity(intent);
                WithdrawPaytmInitActivity.this.finish();
            }
        });
        noInternetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        noInternetDialog.setCancelable(false);
        noInternetDialog.show();

    }

    private void showConfirmationToUser() // Withdraw Success
    {
        noInternetDialog.setContentView(R.layout.popup_withdraw_success);
        Button okayButton = noInternetDialog.findViewById(R.id.okayButton);
        okayButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                noInternetDialog.dismiss();
                Intent intent= new Intent(WithdrawPaytmInitActivity.this, MainActivity.class);
                startActivity(intent);
                WithdrawPaytmInitActivity.this.finish();
            }
        });
        noInternetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        noInternetDialog.setCancelable(false);
        noInternetDialog.show();
    }

    private boolean checkForFieldClearance()
    {
        if(editTextPayPalEmail.getText().toString().isEmpty())
        {
            Toast.makeText(this,"Email ID field Can't be Empty",Toast.LENGTH_SHORT).show();
            return false;
        }
       /* if(editTextPayPalConfirmEmail.getText().toString().isEmpty())
        {
            Toast.makeText(this,"Confirm Email ID field Can't be Empty",Toast.LENGTH_SHORT).show();
            return false;
        }*/
        if(!editTextPayPalEmail.getText().toString().matches(emailPattern))
        {
            Toast.makeText(this,"Incorrect Email ID",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!editTextPayPalEmail.getText().toString().matches(emailPattern))
        {
            Toast.makeText(this,"Incorrect Confirm Email ID",Toast.LENGTH_SHORT).show();
            return false;
        }
       /* if(!editTextPayPalEmail.getText().toString().equalsIgnoreCase(editTextPayPalConfirmEmail.getText().toString()))
        {
            Toast.makeText(this,"Email and Confirm Email Doesn't Match",Toast.LENGTH_LONG).show();
            return false;
        }*/
        if(withdrawCoins.getText().toString().isEmpty())
        {
            Toast.makeText(this,"Enter Number of Coins to Withdraw",Toast.LENGTH_LONG).show();
            return false;
        }
        // return editTextPayPalEmail.getText().toString().equalsIgnoreCase(editTextPayPalConfirmEmail.getText().toString());
        return true;
    }


    private boolean haveNetworkConnection()
    {
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

}
