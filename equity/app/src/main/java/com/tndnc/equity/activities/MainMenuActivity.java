package com.tndnc.equity.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tndnc.equity.R;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        SharedPreferences prefs = getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        boolean userProfileSaved = prefs.getBoolean("user_profile_saved", false);

        if (!userProfileSaved) {
            startUserProfile(null);
        }

    }

    public void levelSelect(View v){
        Intent levelSelect = new Intent(this, LevelSelectActivity.class);
        startActivity(levelSelect);
    }

    public void startUserProfile(View v) {
        Intent userProfile = new Intent(this, UserProfileActivity.class);
        startActivity(userProfile);
    }

}
