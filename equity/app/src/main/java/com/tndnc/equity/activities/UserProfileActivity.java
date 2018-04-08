package com.tndnc.equity.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.tndnc.equity.GameApplication;
import com.tndnc.equity.R;
import com.tndnc.equity.googleSheets.GoogleSheetsWriteUtil;

public class UserProfileActivity extends AppCompatActivity {

    private boolean userProfileSaved;
    private EditText ageEditText;
    private EditText formationEditText;
    private SharedPreferences userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userProfile = getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        this.userProfileSaved = userProfile.getBoolean("user_profile_saved", false);
        String age = userProfile.getString("age", "");
        String formation = userProfile.getString("formation", "");
        String range = userProfile.getString( "userPos", "NONE");

        ageEditText = this.findViewById(R.id.age);
        formationEditText = this.findViewById(R.id.formation);

        ageEditText.setText(age);
        formationEditText.setText(formation);
    }

    public void saveUserProfile(View v) {
        String age = ageEditText.getText().toString();
        String formation = formationEditText.getText().toString();

        SharedPreferences.Editor editor = userProfile.edit();
        editor.putString("age", age);
        editor.putString("formation", formation);
        editor.putBoolean("user_profile_saved", true);
        editor.apply();

        if (!userProfileSaved) {
            ((GameApplication) this.getApplication()).getSheetsWriteUtil().writeUserInfo(
                    userProfile.getString("uuid", "ANON"),
                    age,
                    formation
            );
        } else {
            ((GameApplication) this.getApplication()).getSheetsWriteUtil().ModifyUserProfile(
                    userProfile.getString("uuid", "ANON"),
                    age,
                    formation
            );
        }

        this.userProfileSaved = true;
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (this.canGoBack()) {
            super.onBackPressed();
        }
    }

    private boolean canGoBack() {
        return this.userProfileSaved;
    }
}
