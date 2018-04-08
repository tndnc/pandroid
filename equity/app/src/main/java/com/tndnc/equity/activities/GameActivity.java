package com.tndnc.equity.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.tndnc.equity.googleSheets.GoogleSheetsWriteUtil;
import com.tndnc.equity.GameApplication;
import com.tndnc.equity.models.Level;
import com.tndnc.equity.models.Model;
import com.tndnc.equity.R;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

public class GameActivity extends AppCompatActivity {

    GameApplication app;
    private RatingBar ratingBar;
    private float levelrating;
    private int resTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resTime = 0;
        setContentView(R.layout.activity_game);
//        intentExtras = getIntent();
        app = (GameApplication) this.getApplication();
        //actualLevel = intentExtras.getIntExtra("ActualXMLLevel", 0);
        //nextLevel = actualLevel + 1;
        onWin();
    }

    public void onWin() {
        Model gameModel = app.getGameModel();
        if (gameModel.endOfGame()) {
            // Set level completion
            SharedPreferences prefs = getSharedPreferences("level_completion", Context.MODE_PRIVATE);
            prefs.edit().putBoolean(gameModel.getLevelName(), true).apply();
            // TODO: only update relevant item
            app.getLevelListAdapter(gameModel.getNbActors()).notifyDataSetChanged();

            if(resTime == 0)
                resTime = app.getGameModel().getGameTime();

            // Show level complete screen
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.popup_start_rating, null));
            builder.setMessage("You finished the level with "+ gameModel.getNbmoves() +" moves in "
                    + gameModel.getGameTime()+ " seconds, please rate this level difficulty");
            builder.setTitle("Level Accomplished");
            builder.setNegativeButton("level menu", new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int which) {
                     rateMe();
                     finish();
                 }});

//            builder.setNegativeButton("Next level", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    rateMe();
//                    startNewGame(app.getGameModel().getNextLevel());
//                    finish();
//                }});
            builder.setCancelable(false);
            final AlertDialog alert = builder.create();
            alert.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    final Button menuButton = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
                    menuButton.setEnabled(false);
                    ratingBar = alert.findViewById(R.id.ratingBar);
                    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                            menuButton.setEnabled(true);
                        }
                    });
                }
            });

            alert.setCanceledOnTouchOutside(false);
            alert.show();
        }
    }

    public void writeGoogledocs(){
        try {
            app.getSheetsWriteUtil().writeUserEvaluation(
                    app.getUniqueId(),
                    app.getGameModel().getLevelName(),
                    String.valueOf(resTime),
                    String.valueOf(app.getGameModel().getNbmoves()),
                    String.valueOf((int)levelrating),
                    new Date().toString()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(app.getUniqueId());
//        System.out.println(app.getUserAge());
//        System.out.println(app.getUserFormation());
        System.out.println(String.valueOf(app.getGameModel().getGameTime()));
        System.out.println(app.getGameModel().getNbmoves());
        System.out.println(app.getGameModel().getMoveSequence());
        System.out.println(levelrating);
    }

    public void rateMe(){
        levelrating = ratingBar.getRating();
        writeGoogledocs();
    }

    private void startNewGame(Level l) {
        Model m = new Model(l);
        app.setPartie(m);
        Intent intent = new Intent(this, GameActivity.class);
        this.startActivity(intent);
    }
}
