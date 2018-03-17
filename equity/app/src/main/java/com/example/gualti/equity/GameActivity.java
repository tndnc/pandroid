package com.example.gualti.equity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;

public class GameActivity extends AppCompatActivity {

    GameApplication app;
    Intent intentExtras;
    int actualLevel;
    int nextLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        intentExtras = getIntent();
        app = (GameApplication) this.getApplication();
        actualLevel = intentExtras.getIntExtra("ActualXMLLevel", 0);
        nextLevel = actualLevel + 1;
        onWin();
    }

    public void onWin() {
        if (app.getGameModel().endOfGame()) {
            AlertDialog.Builder myAlert = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            myAlert.setView(inflater.inflate(R.layout.popup_start_rating, null));
            myAlert.setMessage("You finished the level with "+ app.getGameModel().getNbmoves() +" moves in "+ app.getGameModel().getGameTime()+ " seconds, please rate this level difficulty")
                    .setTitle("Level " + nextLevel + " Accomplished")
                    .setNeutralButton("Select Level", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("Ok..", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            if (actualLevel != 11) {

                myAlert.setPositiveButton("Next Level", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        app.getGameModel().setNbmoves(0);
                        app.setPartie(nextLevel);
                        nextLevel++;

                    }
                })
                        .create();

                myAlert.show();

            } else {
                myAlert.create()
                        .show();
            }
        }
    }
}
