package com.kingleader.cashybird;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.kingleader.cashybird.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class InformationSupport extends Activity
{
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseRemoteConfig firebaseRemoteConfig;
    private HashMap<String, Object> firebaseDefaults;
    String subject = "";
    String body = "";
    String email= "";
    String uiD = "";

    TextView faq_dynamic_add1;
    TextView faq_dynamic_add1_desc;
    TextView faq_dynamic_add2;
    TextView faq_dynamic_add2_desc;

    private final String Faq_dynamic_add1 = "faq_dynamic_add1";
    private final String Faq_dynamic_add1_desc = "faq_dynamic_add1_desc";
    private final String Faq_dynamic_add2 = "faq_dynamic_add2";
    private final String Faq_dynamic_add2_desc = "faq_dynamic_add2_desc";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_support);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        faq_dynamic_add1 = findViewById(R.id.faq_dynamic_add1);
        faq_dynamic_add1_desc = findViewById(R.id.faq_dynamic_add1_desc);
        faq_dynamic_add2 = findViewById(R.id.faq_dynamic_add2);
        faq_dynamic_add2_desc = findViewById(R.id.faq_dynamic_add2_desc);

    }

    public void openMailSupportDialog(View view)
    {
        if(firebaseUser!=null)
        {
           uiD = firebaseUser.getUid();
           email = firebaseUser.getEmail();
           subject = "Support";
           body = "Hi, \n\n Support ID: "+uiD+"\nEmail: "+email+"\n\nPlease type in your request / question / error / concern, we would be happy to answer / resolve as soon as possible..\n\n\nThank you, \nYour NAME\nYour COUNTRY\nYour EMAIL\nYour MobileNumber";
        }
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", getResources().getString(R.string.support_email) , null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void remoteConfigCheck()
    {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseDefaults = new HashMap<>();
        firebaseDefaults.put(Faq_dynamic_add1,"");
        firebaseDefaults.put(Faq_dynamic_add1_desc,"");
        firebaseDefaults.put(Faq_dynamic_add2,"");
        firebaseDefaults.put(Faq_dynamic_add2_desc,"");

        firebaseRemoteConfig.setDefaultsAsync(firebaseDefaults);
        firebaseRemoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600).build());

        firebaseRemoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    firebaseRemoteConfig.activate();
                    checkForUpdateParameters();
                }
            }
        });
    }

    private void checkForUpdateParameters()
    {
        String Faq_Head1 =  firebaseRemoteConfig.getString(Faq_dynamic_add1);
        String Faq_Head1_Desc =  firebaseRemoteConfig.getString(Faq_dynamic_add1_desc);
        String Faq_Head2 =  firebaseRemoteConfig.getString(Faq_dynamic_add2);
        String Faq_Head2_Desc =  firebaseRemoteConfig.getString(Faq_dynamic_add2_desc);

        faq_dynamic_add1.setText(Faq_Head1);
        faq_dynamic_add1_desc.setText(Faq_Head1_Desc);
        faq_dynamic_add2.setText(Faq_Head2);
        faq_dynamic_add2_desc.setText(Faq_Head2_Desc);

    }

    @Override
    protected void onResume() {
        remoteConfigCheck();
        super.onResume();
    }
}
