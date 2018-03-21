package com.tndnc.equity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class MainMenuActivity extends AppCompatActivity {

    private TextView ageTextView;
    private TextView formationTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Dialog alert = onCreateDialog();
        SharedPreferences mPrefs = getSharedPreferences("user_info_f",0);
        String mString = mPrefs.getString("age", "-1");
        if(mString == "-1"){
            alert.show();
            ageTextView = alert.findViewById(R.id.age);
            formationTextView = alert.findViewById(R.id.formation);
        }
    }

    public void levelSelect(View _){
        Intent levelSelect = new Intent(this, LevelSelectActivity.class);
        startActivity(levelSelect);
    }
    public Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.popup_user_info, null));
        builder.setMessage("Please input user data");
        builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                submitUserInfo();
            }});
        AlertDialog alert = builder.create();

        return alert;
    }
    public void submitUserInfo(){
        GameApplication app = (GameApplication) this.getApplication();
        app.setUserAge(ageTextView.getText().toString());
        app.setUserFormation(formationTextView.getText().toString());
    }

}
