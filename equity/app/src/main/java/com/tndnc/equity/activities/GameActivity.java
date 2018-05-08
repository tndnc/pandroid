package com.tndnc.equity.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.tndnc.equity.googleSheets.GoogleSheetsWriteUtil;
import com.tndnc.equity.GameApplication;
import com.tndnc.equity.models.Level;
import com.tndnc.equity.models.Model;
import com.tndnc.equity.R;
import com.tndnc.equity.views.GameView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    GameApplication app;
    private RatingBar ratingBar;
    private float levelrating;
    private int resTime;
    private String selecedPrefs;
    private SharedPreferences prefs;
    private boolean finished;
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        app = (GameApplication) this.getApplication();
        ((TextView)findViewById(R.id.level_title)).setText("Level " + app.getGameModel().getLevelName());
        gameView = findViewById(R.id.surfaceView2);
        prefs = getSharedPreferences("level_completion", Context.MODE_PRIVATE);
        resTime = prefs.getInt(app.getGameModel().getLevelName() + "_res_time", 0);
        selecedPrefs = prefs.getString(app.getGameModel().getLevelName() + "_sel_pref","0");
        if(selecedPrefs.length()>1){
            System.err.println(selecedPrefs);
            String[] aselectedPrefs = selecedPrefs.split("");
            List<String> list = new LinkedList<String>(Arrays.asList(aselectedPrefs));
            list.remove(0);
            List<String> lselectedPrefs = new ArrayList<>(list);
            System.out.println(lselectedPrefs);
            app.getGameModel().setSelectedPieces(lselectedPrefs);
        }
        finished = false;
        Log.d("GameActivity", "Loaded previous resolution time: " + resTime);
        onWin();
    }

    public void onWin() {
        Model gameModel = app.getGameModel();
        if (gameModel.endOfGame()) {
            // Set level completion
            finished = true;
            prefs.edit().putBoolean(gameModel.getLevelName(), true).apply();
            // TODO: only update relevant item
            app.getLevelListAdapter(gameModel.getNbActors()).notifyDataSetChanged();

            resTime += app.getGameModel().getGameTime();

            // Show level complete screen
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.popup_start_rating, null));
            builder.setMessage("You finished the level with "+ gameModel.getNbmoves() +" moves in "
                    + resTime + " seconds, please rate this level difficulty");
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

//    @Override
//    protected void onPause() {
//        super.onPause();
//        saveResTime();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        loadResTime();
//    }

    @Override
    protected void onStop() {
        super.onStop();
        savePositions();
        saveResTime();
    }

    private void saveResTime() {
        if(finished) {
            resTime = 0;
            Log.d("GameActivity", "Reset resolution time after victory.");
        }
        else resTime += app.getGameModel().getGameTime();

        prefs.edit().putInt(app.getGameModel().getLevelName()+ "_res_time", resTime).apply();
        Log.d("GameActivity", "Saved resolution time: " + resTime);
    }

    private void savePositions() {
        if(finished) {
            selecedPrefs = "0";
            Log.d("GameActivity", "Reset selected pieces after victory.");
        }else{
            selecedPrefs = app.getGameModel().getSelectedPieces();
        }
        prefs.edit().putString(app.getGameModel().getLevelName()+ "_sel_pref", selecedPrefs).apply();
        Log.d("GameActivity", "Saved selected Pieces: " + selecedPrefs);
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
        System.out.println(String.valueOf(resTime));
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

    public void setHelp(View v) {
        Drawable d;
        if(gameView.isNoob()) {
            d = getResources().getDrawable(R.drawable.circle_background_off);
            gameView.setNoob(false);
        }
        else {
            d = getResources().getDrawable(R.drawable.circle_background_on);
            gameView.setNoob(true);
        }
        Log.d("GameView", "gameview toggle to " + gameView.isNoob() + ". Drawable="+d);
        v.setBackground(d);
    }
}
