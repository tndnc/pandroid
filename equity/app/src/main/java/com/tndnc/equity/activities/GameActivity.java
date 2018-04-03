package com.tndnc.equity.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;

import com.tndnc.equity.GameApplication;
import com.tndnc.equity.models.Model;
import com.tndnc.equity.R;

public class GameActivity extends AppCompatActivity {

    GameApplication app;
    Intent intentExtras;
    int actualLevel;
    int nextLevel;
    private RatingBar ratingBar;
    private float levelrating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        intentExtras = getIntent();
        app = (GameApplication) this.getApplication();
//        actualLevel = intentExtras.getIntExtra("ActualXMLLevel", 0);
//        nextLevel = actualLevel + 1;
        onWin();
    }

    public void onWin() {
        Model gameModel = app.getGameModel();
        if (gameModel.endOfGame()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.popup_start_rating, null));
            builder.setMessage("You finished the level with "+ gameModel.getNbmoves() +" moves in "
                    + gameModel.getGameTime()+ " seconds, please rate this level difficulty");
            builder.setTitle("Level " + nextLevel + " Accomplished");
            builder.setNeutralButton("back", new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int which) {
                     finish();
                 }});
            builder.setNegativeButton("Ok..", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }});
            AlertDialog alert = builder.create();
            alert.show();
            ratingBar = alert.findViewById(R.id.ratingBar);
        }
    }

    public void writeGoogledocs(){
        System.out.println(app.getUniqueId());
//        System.out.println(app.getUserAge());
//        System.out.println(app.getUserFormation());
        System.out.println(app.getGameModel().getGameTime());
        System.out.println(app.getGameModel().getNbmoves());
        System.out.println(app.getGameModel().getMoveSequence());
        System.out.println(levelrating);
    }

    public void rateMe(View view){
        levelrating = ratingBar.getRating();
        writeGoogledocs();
    }
}
